import { useState, createContext, useEffect, useCallback } from "react";

/**
 * User context type.
 */
export interface IUserContext {
  tokens:
    | {
        accessToken: string;
        expireAt: string;
        refreshToken: string;
      }
    | undefined;
  setTokens: (
    tokens:
      | {
          accessToken: string;
          expireAt: string;
          refreshToken: string;
        }
      | undefined
  ) => void;
}

/**
 * User context with tokens.
 */
export const UserContext = createContext<IUserContext | undefined>(undefined);

/**
 * User context provider.
 */
export function UserProvider({ children }: { children: React.ReactNode }) {
  // Initial states
  const accessTokenStore = localStorage.getItem("accessToken");
  const expireAtStore = localStorage.getItem("expireAt");
  const refreshTokenStore = localStorage.getItem("accessToken");

  // States
  const [accessToken, setAccessToken] = useState<string | undefined>(accessTokenStore ? accessTokenStore : undefined);
  const [expireAt, setExpireAt] = useState<string | undefined>(expireAtStore ? expireAtStore : undefined);
  const [refreshToken, setRefreshToken] = useState<string | undefined>(
    refreshTokenStore ? refreshTokenStore : undefined
  );

  // Effects
  useEffect(() => {
    accessToken ? localStorage.setItem("accessToken", accessToken) : localStorage.removeItem("accessToken");
  }, [accessToken]);
  useEffect(() => {
    expireAt ? localStorage.setItem("expireAt", expireAt) : localStorage.removeItem("expireAt");
  }, [expireAt]);
  useEffect(() => {
    refreshToken ? localStorage.setItem("refreshToken", refreshToken) : localStorage.removeItem("refreshToken");
  }, [refreshToken]);

  // Tokens
  const tokens =
    accessToken && expireAt && refreshToken
      ? { accessToken: accessToken, expireAt: expireAt, refreshToken: refreshToken }
      : undefined;
  const setTokens = useCallback(
    (tokens: { accessToken: string; expireAt: string; refreshToken: string } | undefined) => {
      setAccessToken(tokens?.accessToken);
      setExpireAt(tokens?.expireAt);
      setRefreshToken(tokens?.refreshToken);
    },
    []
  );

  // Render
  return (
    <UserContext.Provider
      value={{
        tokens: tokens,
        setTokens: setTokens,
      }}
    >
      {children}
    </UserContext.Provider>
  );
}
