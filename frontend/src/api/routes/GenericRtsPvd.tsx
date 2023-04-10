import { QueryClient, QueryClientProvider } from "react-query";

/**
 * Backend provider.
 */
export default function GenericRtsPvd({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <QueryClientProvider client={new QueryClient()}>
      {children}
    </QueryClientProvider>
  );
}
