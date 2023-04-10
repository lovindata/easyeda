import { useEffect } from "react";
import { ToastLevelEnum } from "../../context/toaster/ToasterCtx";
import useToaster from "../../context/toaster/ToasterHk";
import { ConnFormIDto } from "../dto/IDto";
import { ConnStatusODto, ConnTestODto } from "../dto/ODto";
import { useGet, usePostM } from "./GenericRtsHk";

/**
 * Connection testing hook for route ("/conn/test").
 */
export function useConnRtsTest() {
  const { addToast } = useToaster();
  const { postM, data, isLoading } = usePostM<ConnTestODto>(
    "/conn/test",
    true,
    false
  );
  useEffect(
    () =>
      data
        ? addToast({
            level: ToastLevelEnum.Success,
            header: "Connection is UP.",
          })
        : addToast({
            level: ToastLevelEnum.Warning,
            header: "Connection is DOWN.",
          }),
    [isLoading]
  );
  return {
    test: (body: ConnFormIDto) => postM(body, undefined),
    isTesting: isLoading,
  };
}

/**
 * Connection listing hook for route ("/conn/list").
 */
export function useConnRtsList() {
  const { data, isLoading } = useGet<ConnStatusODto[]>(
    "useConnRtsList",
    "/conn/list",
    undefined,
    true,
    false,
    false
  );
  return { connsStatus: data, isLoading };
}
