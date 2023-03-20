import { useContext, useState } from "react";
import { UserContext, IUserContext } from "./UserCtx";
import { useToaster } from "../toaster/ToasterHk";
import { TokenODto, UserStatusODto } from "../../data";
import { useGetM, usePostM } from "../../hooks";
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

  // Effect running on connect
  const { setTokens } = useUserContext();
  const { postM, data: tokens, isLoading: isGettingTokens } = usePostM<TokenODto>("/user/login");
  useEffect(() => {
    if (tokens) {
      setTokens(tokens);
      getUserStatus({ Authorization: `Bearer ${tokens.accessToken}` });
    }
  }, [tokens]);

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
 * User info hook with auto redirect to login page if connection is doomed.
 */
export function useUser() {
  // Pre-requisite(s)
  const navigate = useNavigate();
  const { addToast } = useToaster();
  const { tokens, setTokens } = useUserContext();

  // Internal states
  const [isDoomed, setIsDoomed] = useState(tokens.refreshToken === undefined);
  const { getM, data: userGot, isLoading: isTryGetUser } = useGetM<UserStatusODto>("/user/retrieve");
  const [hasLaunchedGetUser, setHasLaunchedGetUser] = useState(false);
  const { postM, data: tokensGot, isLoading: isTryGetTokens } = usePostM<TokenODto>("/user/refresh");
  const [hasLaunchedGetTokens, setHasLaunchedGetTokens] = useState(false);

  // Effect on mounting or updating `userGot`
  useEffect(() => {
    if (isDoomed) {
      console.log("DOOMED");
      addToast(ToastLevelEnum.Warning, "Not connected", "Connection lost or account not provided.");
      navigate("/login");
    }
    if (!hasLaunchedGetUser && tokens.accessToken && !isTryGetUser) {
      console.log("FIRST TRY GET USER");
      setHasLaunchedGetUser(true);
      getM({ Authorization: `Bearer ${tokens.accessToken}` });
    }
    if (hasLaunchedGetUser && !userGot && !hasLaunchedGetTokens && tokens.refreshToken) {
      console.log("TRY GET TOKENS");
      setHasLaunchedGetTokens(true);
      postM(undefined, { Authorization: `Bearer ${tokens.refreshToken}` });
    }
  }, [isTryGetUser]);

  // Effect on mounting or updating `tokensGot`
  useEffect(() => {
    if (hasLaunchedGetTokens && tokensGot && !isTryGetTokens) {
      setTokens(tokensGot);
      getM({ Authorization: `Bearer ${tokensGot.accessToken}` });
    }
    if (hasLaunchedGetTokens && !tokensGot) {
      setIsDoomed(true);
    }
  }, [isTryGetTokens]);

  // Return
  return userGot;
}
