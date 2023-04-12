import { useEffect, useState } from "react";
import { AuthContext } from "./AuthCtx";

/**
 * Auth context provider.
 */
export default function AuthPvd({ children }: { children: React.ReactNode }) {
  // States
  const tokensStore = localStorage.getItem("tokens") || undefined;
  const [tokens, setTokens] = useState<
    | {
        accessToken: string;
        expireAt: number;
        refreshToken: string;
      }
    | undefined
  >(tokensStore ? JSON.parse(tokensStore) : undefined);

  // Effects
  useEffect(
    () => (tokens ? localStorage.setItem("tokens", JSON.stringify(tokens)) : localStorage.removeItem("tokens")),
    [tokens]
  );

  // Render
  return <AuthContext.Provider value={{ tokens: tokens, setTokens: setTokens }}>{children}</AuthContext.Provider>;
}
