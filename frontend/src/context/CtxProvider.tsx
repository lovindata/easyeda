import { ToasterProvider } from "./toaster/ToasterCtx";
import { AuthProvider } from "./auth/AuthCtx";

/**
 * All context providers.
 */
export function CtxProvider({ children }: { children: React.ReactNode }) {
  return (
    <ToasterProvider>
      <AuthProvider>{children}</AuthProvider>
    </ToasterProvider>
  );
}
