import { useState, createContext, useEffect } from "react";

/**
 * User context type.
 */
export interface IUserContext {
  tokens: {
    accessToken: string | undefined;
    expireAt: string | undefined;
    refreshToken: string | undefined;
  };
  setTokens: (tokens: {
    accessToken: string | undefined;
    expireAt: string | undefined;
    refreshToken: string | undefined;
  }) => void;
}

/**
 * User context with tokens.
 */
export const UserContext = createContext<IUserContext | undefined>(undefined);

/**
 * User context provider.
 */
export function UserProvider({ children }: { children: React.ReactNode }) {
  // States
  const accessTokenStore = localStorage.getItem("accessToken") || undefined;
  const expireAtStore = localStorage.getItem("expireAt") || undefined;
  const refreshTokenStore = localStorage.getItem("refreshToken") || undefined;
  const [tokens, setTokens] = useState({
    accessToken: accessTokenStore,
    expireAt: expireAtStore,
    refreshToken: refreshTokenStore,
  });

  // Effects
  useEffect(() => {
    tokens.accessToken
      ? localStorage.setItem("accessToken", tokens.accessToken)
      : localStorage.removeItem("accessToken");
    tokens.expireAt ? localStorage.setItem("expireAt", tokens.expireAt) : localStorage.removeItem("expireAt");
    tokens.refreshToken
      ? localStorage.setItem("refreshToken", tokens.refreshToken)
      : localStorage.removeItem("refreshToken");
  }, [tokens]);

  // Render
  return <UserContext.Provider value={{ tokens: tokens, setTokens: setTokens }}>{children}</UserContext.Provider>;
}
