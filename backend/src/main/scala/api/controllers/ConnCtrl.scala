package com.ilovedatajjia
package api.controllers

import api.dto.input.ConnFormIDto
import api.dto.output.ConnODto
import api.helpers.StringUtils._
import api.models.UserMod
import api.services.ConnSvc
import cats.effect.IO
import api.helpers.BackendException.AppException

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
   *   Connection status OR
   *   - [[AppException]] if non valid name or not up connection
   */
  def createConn(user: UserMod, form: ConnFormIDto)(implicit connSvc: ConnSvc): IO[ConnODto] = for {
    _    <- form.name.isValidName
    _    <- connSvc
              .testConn(form)
              .flatMap(x =>
                IO.raiseUnless(x.isUp)(
                  AppException("Impossible to connect verify your external service and provided information.")))
    oDto <- connSvc.createConn(user, form)
  } yield oDto

}

/**
 * Auto-DI on import.
 */
object ConnCtrl { implicit val impl: ConnCtrl = new ConnCtrl {} }
