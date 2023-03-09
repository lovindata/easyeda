package com.ilovedatajjia
package api.models

import api.helpers.AppException
import api.helpers.DoobieUtils._
import api.helpers.JdbcUtils._
import api.helpers.StringUtils._
import cats.effect.IO
import config._
import io.circe.Json
import java.sql.Date
import java.sql.Timestamp
import scala.annotation.tailrec

/**
 * Repository to extends on the companion object for DB methods. Requirements are
 *   - Case class name matches `^(.+)Mod$`
 *   - Case class name are in pascal case with only letters
 *   - Case class has an attribute `id` in first position in [[Long]]
 *   - Attributes are in camel case with only letters
 * @tparam A
 *   Repository applied on type
 * @note
 *   An example of usage
 *   {{{
 *   // DB already initialized with
 *   // - Table "person"
 *   // - Column "id" and "name"
 *
 *   // Then
 *   import api.helpers.DoobieUtils._
 *   case class PersonMod(id: Long, name: String)
 *   object PersonMod extends GenericRep[PersonMod]
 *   PersonMod.insert(PersonMod(-1, "Elorine"))
 *   }}}
 */
trait GenericMod[A <: Product] {

  /**
   * Build table DB path [[Fragment]].
   * @return
   *   Table DB path [[Fragment]]
   */
  private def tableFrag: Fragment = {
    val pattern            = "^(.+)Mod\\$$".r
    val pattern(tableName) = getClass.getSimpleName
    Fragment.const(s"\"${ConfigLoader.dbSchName}\".\"${tableName.toSnakeCase}\"")
  }

  /**
   * Convert [[Any]] to [[Fragment]].
   * @param x
   *   Applied on
   * @return
   *   [[Fragment]]
   */
  @tailrec
  private def anyToFrag(x: Any): Fragment = x match {
    case x: Short                   => fr0"$x"
    case x: Int                     => fr0"$x"
    case x: Long                    => fr0"$x"
    case x: String                  => fr0"$x"
    case x: Array[Byte]             => fr0"$x"
    case x: Timestamp               => fr0"$x"
    case x: Date                    => fr0"$x"
    case x: Json                    => fr0"$x"
    case x: List[Json]              => fr0"$x"
    case Some(x)                    => anyToFrag(x)
    case None                       => Fragment.const0("null")
    case x: scala.Enumeration#Value => fr0"${x.toString}"
    case _                          => throw AppException(s"Implementation error, fragment impossible for type `${x.getClass.getTypeName}`.")
  }

  /**
   * Insert entity into database.
   * @param entity
   *   Entity to insert
   * @return
   *   Up-to-date entity
   */
  def insert(entity: A)(implicit read: Read[A]): IO[A] = DBDriver.run {
    // Prepare fragments
    val attributesName  =
      Fragment.const(entity.productElementNames.drop(1).map(_.toSnakeCase.nameB()).mkString("(", ", ", ")"))
    val attributesValue = entity.productIterator.drop(1).map(anyToFrag).reduceLeft(_ ++ fr"," ++ _)

    // Start execution
    for {
      id     <- (fr"insert into" ++ tableFrag ++ attributesName ++ fr0"values (" ++ attributesValue ++ fr")").update
                  .withUniqueGeneratedKeys[Long]("id")
      entity <- (fr"select * from" ++ tableFrag ++ fr"where id = $id").query[A].unique
    } yield entity
  }

  /**
   * Select entity from database.
   * @param id
   *   Entity id
   * @return
   *   Up-to-date entity
   */
  def select(id: Long)(implicit read: Read[A]): IO[A] = DBDriver.run {
    val idValue: Fragment = anyToFrag(id)
    (fr"select * from" ++ tableFrag ++ fr"where id =" ++ idValue).query[A].unique
  }

  /**
   * Select entities from database.
   * @param condition
   *   SQL condition as doobie [[Fragment]]
   * @return
   *   Up-to-date entities
   * @note
   *   An example of usage
   *   {{{
   *   case class PersonMod(id: Long, name: String)
   *   object PersonMod extends GenericRep[PersonMod]
   *
   *   import api.helpers.DoobieUtils._   // To use fragment interpolator
   *   Person.select(fr"id = ${1}")       // List[PersonMod]
   *   }}}
   */
  def select(condition: Fragment)(implicit read: Read[A]): IO[List[A]] = DBDriver.run {
    (fr"select * from" ++ tableFrag ++ fr"where" ++ condition).query[A].to[List]
  }

  /**
   * Update entity in database using column `id`.
   * @param entity
   *   Entity to update
   * @return
   *   Up-to-date entity
   */
  def update(entity: A)(implicit read: Read[A]): IO[A] = DBDriver.run {
    // Prepare fragments
    val idValue: Fragment                   = anyToFrag(entity.productIterator.next())
    val attributesName: Iterator[Fragment]  =
      entity.productElementNames.drop(1).map(_.toSnakeCase).map(Fragment.const(_))
    val attributesValue: Iterator[Fragment] = entity.productIterator.drop(1).map(anyToFrag)
    val attributesFrag: Fragment            = attributesName
      .zip(attributesValue)
      .map { case (name, value) => name ++ fr"=" ++ value }
      .reduceLeft(_ ++ fr"," ++ _)

    // Start execution
    for {
      nbAffected <- (fr"update" ++ tableFrag ++ fr"set" ++ attributesFrag ++ fr" where id =" ++ idValue).update.run
      _           = if (nbAffected != 1) throw AppException(s"Update failed because affects $nbAffected rows (!= 1)")
      entity     <-
        (fr"select * from" ++ tableFrag ++ fr"where id =" ++ idValue).query[A].unique
    } yield entity
  }

  /**
   * Delete entity from database using column `id`.
   * @param entity
   *   Entity to delete
   */
  def delete(entity: A): IO[Unit] = DBDriver.run {
    // Prepare fragments
    val idValue: Fragment = anyToFrag(entity.productIterator.next())

    // Start execution
    for {
      nbAffected <- (fr"delete from" ++ tableFrag ++ fr"where id =" ++ idValue).update.run
      _           = nbAffected match {
                      case 0 | 1 =>
                      case _     => throw AppException(s"Deletion failed because affects $nbAffected rows (> 1)")
                    }
    } yield ()
  }

}
