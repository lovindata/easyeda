import axios from "axios";
import { useMutation, QueryClient, QueryClientProvider } from "react-query";
import { AppException } from "../data";

/**
 * Post request hook.
 */
export function usePost<A extends object>(subDirect: string, dtoOutKind: string) {
  const {
    mutate: post,
    isLoading,
    data,
  } = useMutation((body: object) =>
    axios
      .post(`http://${window.location.hostname}:8081${subDirect}`, body)
      .then((res) => ({ kind: dtoOutKind, ...res.data } as A))
      .catch((err) => ({ kind: "AppException", ...err.response.data } as AppException))
  );
  return { post, data, isLoading };
}

/**
 * Http provider.
 */
export function HttpProvider({ children }: { children: React.ReactNode }) {
  return <QueryClientProvider client={new QueryClient()}>{children}</QueryClientProvider>;
}
