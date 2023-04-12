import { useContext } from "react";
import { AuthContext, IAuthContext } from "./AuthCtx";

/**
 * Auth context hook.
 */
export default function useAuth() {
  return useContext(AuthContext) as IAuthContext;
}
