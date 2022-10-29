package com.ilovedatajjia
package api.models

/**
 * Enumeration for session states.
 */
object SessionStateEnum extends Enumeration {

  // Possible states
  type SessionStateType = Value
  val Active, Terminated: SessionStateType = Value

}
