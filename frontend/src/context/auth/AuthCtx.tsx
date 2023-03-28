import { useState, createContext, useEffect } from "react";

/**
 * User context type.
 */
export interface IAuthContext {
  tokens:
    | {
        accessToken: string;
        expireAt: number;
        refreshToken: string;
      }
    | undefined;
  setTokens: (
    tokens:
      | {
          accessToken: string;
          expireAt: number;
          refreshToken: string;
        }
      | undefined
  ) => void;
}

/**
 * Auth context with tokens.
 */
export const AuthContext = createContext<IAuthContext | undefined>(undefined);

/**
 * Auth context provider.
 */
export function AuthProvider({ children }: { children: React.ReactNode }) {
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
