package com.ilovedatajjia
package api.controllers

import api.dto.input.ConnFormIDto
import api.dto.output.ConnODto
import api.helpers.StringUtils._
import api.models.UserMod
import api.services.ConnSvc
import cats.effect.IO

/**
 * Controller layer for connections.
 */
trait ConnCtrl {

  /**
   * Verify form & create connection.
   * @param user
   *   Verified user
   * @param form
   *   Connection form
   * @return
   *   Connection status
   */
  def createConn(user: UserMod, form: ConnFormIDto)(implicit connSvc: ConnSvc): IO[ConnODto] =
    form.name.isValidName >> connSvc.createConn(user, form)

}

/**
 * Auto-DI on import.
 */
object ConnCtrl { implicit val impl: ConnCtrl = new ConnCtrl {} }
