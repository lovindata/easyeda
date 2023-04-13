import { AuthContext, IAuthContext } from "./AuthCtx";
import { useContext } from "react";

/**
 * Auth context hook.
 */
export default function useAuth() {
  return useContext(AuthContext) as IAuthContext;
}
