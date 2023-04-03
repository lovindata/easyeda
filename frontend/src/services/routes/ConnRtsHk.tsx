import { useEffect } from "react";
import { useGet, usePostM } from "../BackendHk";
import { ConnStatusODto, ConnTestODto } from "../ODto";
import { useToaster, ToastLevelEnum } from "../../context";
import { ConnFormIDto } from "../IDto";

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
    false
  );
  return { connsStatus: data, isLoading };
}
