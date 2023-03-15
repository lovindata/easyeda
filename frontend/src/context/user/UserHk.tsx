import { useContext } from "react";
import { UserContext, IUserContext } from "./UserCtx";
import { usePost, useGet } from "../../hooks";
import { TokenDtoOut, UserStatusDtoOut } from "../../data";
import { useEffect } from "react";

/**
 * User context hook.
 */
export function useUserContext() {
  return useContext(UserContext) as IUserContext;
}

/**
 * User connexion hook.
 */
export function useUserConnect() {
  // Pre-requisites
  const { accessToken, expireAt, refreshToken, setAccessToken, setExpireAt, setRefreshToken } = useUserContext();
  const { post, isLoading, data } = usePost<TokenDtoOut>("/user/login", "TokenDtoOut");

  // Effect running on log in changes
  useEffect(() => {
    switch (data?.kind) {
      case "TokenDtoOut":
        setAccessToken(data.accessToken);
        setExpireAt(data.expireAt);
        setRefreshToken(data.refreshToken);
        break;
      case "AppException":
      default:
        break;
    }
  }, [data]);

  // Return
  return {
    connect: (email: string, pwd: string) => post({ email: email, pwd: pwd }),
    isConnecting: isLoading,
    isConnected: accessToken !== undefined && expireAt !== undefined && refreshToken !== undefined,
  };
}

/**
 * User info hook.
 */
export function useUser() {
  // Pre-requisites
  const { accessToken, expireAt, refreshToken } = useUserContext();
  const { get: get, data: dataUserInfo } = useGet<UserStatusDtoOut>("/user/retrieve", "UserStatusDtoOut");

  // TODO HERE
}
