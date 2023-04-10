import { useContext } from "react";
import { IToasterContext, ToasterContext } from "./ToasterCtx";

/**
 * Use toaster hook.
 */
export default function useToaster() {
  return useContext(ToasterContext) as IToasterContext;
}
