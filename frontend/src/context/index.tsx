import { UserProvider } from "./UserCtx";

/**
 * All global context.
 */
export default function CtxProvider({ children }: { children: React.ReactNode }) {
  return <UserProvider>{children}</UserProvider>;
}

export * from "./UserCtx";
