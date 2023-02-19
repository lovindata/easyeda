import { ToasterProvider } from "./toaster/ToasterCtx";
import { UserProvider } from "./user/UserCtx";

/**
 * All context providers.
 */
export function CtxProvider({ children }: { children: React.ReactNode }) {
  return (
    <UserProvider>
      <ToasterProvider>{children}</ToasterProvider>
    </UserProvider>
  );
}
