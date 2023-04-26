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
object ConnCtrl {

  /**
   * Verify form & create connection.
   * @param user
   *   Verified user
   * @param form
   *   Connection form
   * @return
   *   Connection status
   */
  def createConn(user: UserMod, form: ConnFormIDto): IO[ConnODto] =
    form.name.isValidName >> ConnSvc.createConn(user, form)

}
