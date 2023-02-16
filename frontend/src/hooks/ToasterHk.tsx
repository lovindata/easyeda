import { useContext } from "react";
import { ToasterContext, IToasterContext } from "../context";

/**
 * Use toaster hook.
 */
export function useToaster() {
  return useContext(ToasterContext) as IToasterContext;
}
