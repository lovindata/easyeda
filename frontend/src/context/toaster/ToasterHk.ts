import { IToasterContext, ToasterContext } from "./ToasterCtx";
import { useContext } from "react";

/**
 * Use toaster hook.
 */
export default function useToaster() {
  return useContext(ToasterContext) as IToasterContext;
}
