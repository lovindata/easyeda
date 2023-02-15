import axios from "axios";
import { useMutation, QueryClient, QueryClientProvider } from "react-query";
import { AppException } from "../data/dto";

/**
 * Post request hook.
 */
export function usePost<A extends object>(subDirect: string, body: object) {
  // Initialize
  const bodyJsonified = {
    method: "POST",
    headers: {
      "content-type": "application/json;charset=UTF-8",
    },
    body: JSON.stringify(body),
  };

  // Shot request & Return data
  const { mutate, data, isLoading, isError } = useMutation(() =>
    axios
      .post(`http://${window.location.hostname}:8081${subDirect}`, bodyJsonified)
      .then((res) => res as A)
      .catch((err) => err as AppException)
  );
  return { data, isLoading, isError };
}

/**
 * Http provider.
 */
export function HttpProvider({ children }: { children: React.ReactNode }) {
  return <QueryClientProvider client={new QueryClient()}>{children}</QueryClientProvider>;
}
