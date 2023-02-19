import { useContext } from "react";
import { ToasterContext, IToasterContext } from "./ToasterCtx";

/**
 * Use toaster hook.
 */
export function useToaster() {
  return useContext(ToasterContext) as IToasterContext;
}
