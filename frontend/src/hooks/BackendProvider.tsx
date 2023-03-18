import { QueryClient, QueryClientProvider } from "react-query";

/**
 * Backend provider.
 */
export function BackendProvider({ children }: { children: React.ReactNode }) {
  return <QueryClientProvider client={new QueryClient()}>{children}</QueryClientProvider>;
}
