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
  const { postM, data, isLoading } = usePostM<UserStatusODto>(
    "/user/create",
    false,
    true
  );

  // Effect on post registration
  useEffect(() => {
    if (data) {
      addToast({
        level: ToastLevelEnum.Success,
        header: `Request success`,
        message: `${data.email} ready to connect.`,
      });
      navigate("/login");
    }
  }, [data]);

  // Return
  return {
    create: (body: UserFormIDto) => postM(body, undefined),
    isCreating: isLoading,
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
 * User info hook for route ("/user/status").
 */
export function useUserRtsStatus() {
  const { data, isLoading } = useGet<UserStatusODto>(
    "useUser",
    "/user/status",
    undefined,
    true,
    true,
    false
  );
  return { user: data, isRetrieving: isLoading };
}
