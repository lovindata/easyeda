import { UserProvider } from "./UserCtx";
import { ToasterProvider } from "./ToasterCtx";

/**
 * All context providers.
 */
export default function CtxProvider({ children }: { children: React.ReactNode }) {
  return (
    <UserProvider>
      <ToasterProvider>{children}</ToasterProvider>
    </UserProvider>
  );
}

export * from "./UserCtx";
export * from "./ToasterCtx";
