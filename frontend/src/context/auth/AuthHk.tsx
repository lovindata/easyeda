import { useContext } from "react";
import { AuthContext, IAuthContext } from "./AuthCtx";

/**
 * Auth context hook.
 */
export function useAuthContext() {
  return useContext(AuthContext) as IAuthContext;
}
