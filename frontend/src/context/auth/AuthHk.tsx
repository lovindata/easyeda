import { useContext } from "react";
import { AuthContext, IAuthContext } from "./AuthCtx";
import { useToaster } from "../toaster/ToasterHk";
import { useGet, useGetM, usePostM, TokenODto, UserStatusODto } from "../../services";
import { useEffect } from "react";
import { ToastLevelEnum } from "../toaster/ToasterCtx";
import { useNavigate } from "react-router-dom";

/**
 * Auth context hook.
 */
export function useAuthContext() {
  return useContext(AuthContext) as IAuthContext;
}

/**
 * User connexion hook effect.
 */
export function useUserConnectM() {
  // Effect running on user connect
  const { tokens: oldTokens, setTokens } = useAuthContext();
  const { postM, data: freshTokens, isLoading: isGettingTokens } = usePostM<TokenODto>("/user/login", false, true);
  useEffect(() => freshTokens && setTokens(freshTokens), [freshTokens]);
  useEffect(() => freshTokens && getUserStatus(undefined), [oldTokens]);

  // Effect running on user get
  const {
    getM: getUserStatus,
    data: user,
    isLoading: isGettingUser,
  } = useGetM<UserStatusODto>("/user/retrieve", true, true);
  const navigate = useNavigate();
  const { addToast } = useToaster();
  useEffect(() => {
    if (user) {
      addToast({
        level: ToastLevelEnum.Success,
        header: `Request success`,
        message: `${user.email} successfully connected.`,
      });
      navigate("/app");
    }
  }, [user]);

  // Return
  return {
    connectM: (email: string, pwd: string) => postM({ email: email, pwd: pwd }, undefined),
    isConnecting: isGettingTokens || isGettingUser,
  };
}

/**
 * User registration hook effect.
 */
export function useUserRegisterM() {
  // Hooks
  const { addToast } = useToaster();
  const navigate = useNavigate();
  const {
    postM: registerM,
    data: user,
    isLoading: isRegisting,
  } = usePostM<UserStatusODto>("/user/create", false, true);

  // Effect on post registration
  useEffect(() => {
    if (user) {
      addToast({
        level: ToastLevelEnum.Success,
        header: `Request success`,
        message: `${user.email} ready to connect.`,
      });
      navigate("/login");
    }
  }, [user]);

  // Return
  return {
    registerM: (email: string, username: string, pwd: string, birthDate: number, isTermsAccepted: boolean) =>
      registerM(
        { email: email, username: username, pwd: pwd, birthDate: birthDate, isTermsAccepted: isTermsAccepted },
        undefined
      ),
    isRegisting: isRegisting,
  };
}

/**
 * User info hook.
 */
export function useUser() {
  const { data, isLoading } = useGet<UserStatusODto>("useUser", "/user/retrieve", undefined, true, true);
  return { user: data, isRetrieving: isLoading };
}
