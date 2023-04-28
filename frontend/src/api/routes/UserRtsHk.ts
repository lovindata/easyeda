import useAuth from "../../context/auth/AuthHk";
import { ToastLevelEnum } from "../../context/toaster/ToasterCtx";
import useToaster from "../../context/toaster/ToasterHk";
import { LoginFormIDto, UserFormIDto } from "../dto/IDto";
import { TokenODto, UserStatusODto } from "../dto/ODto";
import useApi from "./GenericRtsHk";
import { useEffect } from "react";
import { useQuery, useMutation } from "react-query";
import { useNavigate } from "react-router-dom";

/**
 * User create hook for route ("/user/create").
 */
export function useUserRtsCreate() {
  // Hooks
  const { addToast } = useToaster();
  const navigate = useNavigate();
  const api = useApi(false, true);
  const {
    mutate: postM,
    data,
    isLoading,
  } = useMutation((body: UserFormIDto) => api.post<UserStatusODto>("/user/create", body).then((_) => _.data));

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
    create: (body: UserFormIDto) => postM(body),
    isCreating: isLoading,
  };
}

/**
 * User login hook for route ("/user/login").
 */
export function useUserRtsLogin() {
  // Effect running on user connect
  const { tokens: oldTokens, setTokens } = useAuth();
  const apiLogin = useApi(false, true);
  const {
    mutate: postM,
    data: freshTokens,
    isLoading: isGettingTokens,
  } = useMutation((body: LoginFormIDto) => apiLogin.post<TokenODto>("/user/login", body).then((_) => _.data));
  useEffect(() => freshTokens && setTokens(freshTokens), [freshTokens]);
  useEffect(() => freshTokens && getUserStatus(), [oldTokens]);

  // Effect running on user get
  const apiStatus = useApi(true, true);
  const {
    mutate: getUserStatus,
    data: user,
    isLoading: isGettingUser,
  } = useMutation(() => apiStatus.get<UserStatusODto>("/user/status").then((_) => _.data));
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
    logIn: (body: LoginFormIDto) => postM(body),
    isLogingIn: isGettingTokens || isGettingUser,
  };
}

/**
 * User info hook for route ("/user/status").
 */
export function useUserRtsStatus() {
  const api = useApi(true, true);
  const { data } = useQuery(
    "/user/status",
    () => api.get<UserStatusODto>("/user/status", { headers: undefined }).then((_) => _.data),
    { refetchInterval: 10000 }
  );
  return data;
}
