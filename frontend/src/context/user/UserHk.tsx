import { useContext, useState } from "react";
import { UserContext, IUserContext } from "./UserCtx";
import { useToaster } from "../toaster/ToasterHk";
import { TokenODto, UserStatusODto } from "../../data";
import { useGet as useGet, usePost as usePost, useGetM, usePostM } from "../../hooks";
import { useEffect } from "react";
import { ToastLevelEnum } from "../toaster/ToasterCtx";
import { useNavigate } from "react-router-dom";

/**
 * User context hook.
 */
function useUserContext() {
  return useContext(UserContext) as IUserContext;
}

/**
 * User connexion hook for effects.
 */
export function useUserConnectM() {
  // Effect running on connect
  const { setTokens } = useUserContext();
  const { postM, data: tokens, isLoading: isGettingTokens } = usePostM<TokenODto>("/user/login");
  useEffect(() => {
    if (tokens) {
      setTokens(tokens);
      getUserStatus({ Authorization: `Bearer ${tokens.accessToken}` });
    }
  }, [tokens]);

  // Effect running on user get
  const {
    getM: getUserStatus,
    data: user,
    isLoading: isGettingUser,
  } = useGetM<UserStatusODto>("/user/retrieve", false);
  const navigate = useNavigate();
  const { addToast } = useToaster();
  useEffect(() => {
    if (user) {
      addToast(ToastLevelEnum.Success, `Request success`, `${user.email} successfully connected.`);
      navigate("/app");
    }
  }, [user]);

  // Return
  return {
    connectM: (email: string, pwd: string) => postM({ email: email, pwd: pwd }),
    isConnecting: isGettingTokens || isGettingUser,
  };
}

/**
 * User registration hook for effects.
 */
export function useUserRegisterM() {
  // Pre-requisite(s)
  const { addToast } = useToaster();
  const navigate = useNavigate();
  const { postM: registerM, data: user, isLoading: isRegisting } = usePostM<UserStatusODto>("/user/create");

  // Effect on post registration
  useEffect(() => {
    if (user) {
      addToast(ToastLevelEnum.Success, `Request success`, `${user.email} ready to connect.`);
      navigate("/login");
    }
  }, [user]);

  // Return
  return {
    registerM: (email: string, username: string, pwd: string, birthDate: string) =>
      registerM({ email: email, username: username, pwd: pwd, birthDate: birthDate }),
    isRegisting: isRegisting,
  };
}

/**
 * User info hook.
 */
export function useUser() {
  // Get user state
  const [isDoomed, setIsDoomed] = useState(false);

  // Try get local tokens
  const { tokens, setTokens } = useUserContext();
  if (!tokens) return { user: undefined, isDoomed: true };

  // User & Tokens getter
  const { data: userGot, isLoading: isTryGetUser } = useGet<UserStatusODto>(
    "useUser",
    "/user/retrieve",
    { Authorization: `Bearer ${tokens.accessToken}` },
    false
  );
  const { postM, data: tokensGot } = usePostM<TokenODto>("/user/refresh");

  // Launch & Effect on requests
  useEffect(() => {
    if (!userGot && !isTryGetUser) postM({ Authorization: `Bearer ${tokens.refreshToken}` });
  }, [userGot]);
  useEffect(() => {
    if (tokensGot) setTokens(tokensGot);
    if (!isTryGetUser && !tokensGot) setIsDoomed(true);
  }, [tokensGot]);

  // Return
  return { user: userGot, isDoomed: isDoomed };
}
