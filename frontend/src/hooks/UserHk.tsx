import { useContext } from "react";
import { UserContext, IUserContext } from "../context";

/**
 * Use user hook.
 */
export function useUser() {
  return useContext(UserContext) as IUserContext;
}
