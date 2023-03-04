package com.ilovedatajjia
package api.controllers

import api.dto.input.ConnFormDtoIn
import api.dto.output.ConnStatusDtoOut
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
  def createConn(user: UserMod, form: ConnFormDtoIn): IO[ConnStatusDtoOut] =
    form.name.isValidName >> ConnSvc.createConn(user, form)

}
