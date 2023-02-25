import axios from "axios";
import { useMutation, QueryClient, QueryClientProvider } from "react-query";
import { AppException } from "../data";
import { useToaster, ToastLevelEnum } from "../context";
import { useEffect } from "react";

/**
 * Client exception on request.
 */
interface ClientException {
  kind: "ClientException";
  header: string;
  message: string;
}

/**
 * Default client exception.
 */
const DefaultException = {
  kind: "ClientException",
  header: "🧐 Hmm, is your internet down?",
  message: "Connection refused impossible to reach the server.",
} as ClientException;

/**
 * Post request hook.
 */
export function usePost<A extends { kind: string }>(subDirect: string, dtoOutKind: string) {
  // Build mutate
  const {
    mutate: post,
    isLoading,
    data,
  } = useMutation((body: object) =>
    axios
      .post(`http://${window.location.hostname}:8081${subDirect}`, body)
      .then((res) => ({ kind: dtoOutKind, ...res.data } as A))
      .catch((err) =>
        err.response ? ({ kind: "AppException", ...err.response.data } as AppException) : DefaultException
      )
  );

  // Effect on reaching server error & Return
  const { addToast } = useToaster();
  useEffect(() => {
    data == DefaultException && addToast(ToastLevelEnum.Warning, data.header, data.message);
  }, [data]);
  return { post, isLoading, data };
}

/**
 * Get request hook.
 */
export function useGet<A extends { kind: string }>(subDirect: string, dtoOutKind: string) {
  // Build mutate
  const {
    mutate: get,
    isLoading,
    data,
  } = useMutation((headers: object) =>
    axios
      .get(`http://${window.location.hostname}:8081${subDirect}`, { headers: headers })
      .then((res) => ({ kind: dtoOutKind, ...res.data } as A))
      .catch((err) =>
        err.response ? ({ kind: "AppException", ...err.response.data } as AppException) : DefaultException
      )
  );

  // Effect on reaching server error & Return
  const { addToast } = useToaster();
  useEffect(() => {
    data == DefaultException && addToast(ToastLevelEnum.Warning, data.header, data.message);
  }, [data]);
  return { get, isLoading, data };
}

/**
 * Http provider.
 */
export function HttpProvider({ children }: { children: React.ReactNode }) {
  return <QueryClientProvider client={new QueryClient()}>{children}</QueryClientProvider>;
}
