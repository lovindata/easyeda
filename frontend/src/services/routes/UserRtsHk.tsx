import { useEffect } from "react";
import { useAuthContext } from "../../context";
import { useGet, usePostM, useGetM } from "../BackendHk";
import { TokenODto, UserStatusODto } from "../ODto";
import { useNavigate } from "react-router-dom";
import { useToaster, ToastLevelEnum } from "../../context";
import { LoginFormIDto, UserFormIDto } from "../IDto";

/**
 * User create hook for route ("/user/create").
 */
export function useUserRtsCreate() {
  // Hooks
  const { addToast } = useToaster();
  const navigate = useNavigate();
  const {
    postM: registerM,
    data: user,
    isLoading: isCreating,
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
    registerM: (body: UserFormIDto) => registerM(body, undefined),
    isCreating: isCreating,
  };
}

/**
 * User login hook for route ("/user/login").
 */
export function useUserRtsLogin() {
  // Effect running on user connect
  const { tokens: oldTokens, setTokens } = useAuthContext();
  const {
    postM,
    data: freshTokens,
    isLoading: isGettingTokens,
  } = usePostM<TokenODto>("/user/login", false, true);
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
    logIn: (body: LoginFormIDto) => postM(body, undefined),
    isLogingIn: isGettingTokens || isGettingUser,
  };
}

/**
 * User info hook for route ("/user/retrieve").
 */
export function useUserRtsRetrieve() {
  const { data, isLoading } = useGet<UserStatusODto>(
    "useUser",
    "/user/retrieve",
    undefined,
    true,
    true
  );
  return { user: data, isRetrieving: isLoading };
}
