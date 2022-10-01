package com.ilovedatajjia
package api.helpers

/**
 * Enumeration for session states.
 */
object SessionStateEnum extends Enumeration {
  type SessionStateType = Value
  val Active, Terminated: SessionStateType = Value
}
