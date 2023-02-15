import { useState, createContext, useEffect } from "react";

/**
 * User context type.
 */
export interface IUserContext {
  accessToken?: string;
  setAccessToken: React.Dispatch<React.SetStateAction<string | undefined>>;
  expireAt?: string;
  setExpireAt: React.Dispatch<React.SetStateAction<string | undefined>>;
  refreshToken?: string;
  setRefreshToken: React.Dispatch<React.SetStateAction<string | undefined>>;
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

  // Render
  return (
    <UserContext.Provider
      value={{
        accessToken: accessToken,
        setAccessToken: setAccessToken,
        expireAt: expireAt,
        setExpireAt: setExpireAt,
        refreshToken: refreshToken,
        setRefreshToken: setRefreshToken,
      }}>
      {children}
    </UserContext.Provider>
  );
}
