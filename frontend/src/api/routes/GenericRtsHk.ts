import useAuth from "../../context/auth/AuthHk";
import { ToastLevelEnum } from "../../context/toaster/ToasterCtx";
import useToaster from "../../context/toaster/ToasterHk";
import { IDto } from "../dto/IDto";
import { BackendException, ODto, TokenODto } from "../dto/ODto";
import axios, { AxiosError } from "axios";
import { useEffect } from "react";
import { useMutation, useQuery } from "react-query";
import { useNavigate } from "react-router-dom";

/**
 * Server location.
 */
const SERVER_URL = `http://${window.location.hostname}:8081/`;

/**
 * Client side error toast.
 */
const CLIENT_ERR_TOAST = {
  level: ToastLevelEnum.Warning,
  header: "Unreachable server",
  message: "Please verify your internet connection.",
};

/**
 * Server side applicative error toast.
 */
const SERVER_APP_ERR_TOAST = (message: string) => ({
  level: ToastLevelEnum.Error,
  header: "Bad request",
  message: message,
});

/**
 * Authentication error toast.
 */
const AUTH_ERR_TOAST = {
  level: ToastLevelEnum.Warning,
  header: "Not connected",
  message: "Connection lost or account not provided. Please reconnect.",
};

/**
 * Is refreshing access token.
 */
let IS_REFRESHING_TOKENS = false;

/**
 * Use fresh access token hook.
 */
export function useFreshAuth() {
  // Get hooks
  const navigate = useNavigate();
  const { addToast } = useToaster();
  const { tokens, setTokens } = useAuth();

  // Launch refresh if necessary
  const isExpired = tokens && tokens.expireAt < Date.now();
  console.log(isExpired);
  useEffect(() => {
    if (!IS_REFRESHING_TOKENS && isExpired) {
      IS_REFRESHING_TOKENS = true;
      axios
        .post<TokenODto>(`${SERVER_URL}user/refresh`, undefined, {
          headers: { "DataPiU-Refresh-Token": tokens.refreshToken },
        })
        .then((res) => {
          setTokens(res.data);
          IS_REFRESHING_TOKENS = false;
        })
        .catch(() => {
          setTokens(undefined);
          addToast(AUTH_ERR_TOAST);
          navigate("/login");
          IS_REFRESHING_TOKENS = false;
        });
    }
  }, [tokens]);

  // Return access token
  return isExpired ? undefined : tokens?.accessToken;
}

/**
 * Axios fetcher hook.
 */
function useApi(authed: boolean, verbose: boolean) {
  // Get hooks & Build backend api
  const navigate = useNavigate();
  const { addToast } = useToaster();
  const freshAccessToken = useFreshAuth();
  const api = axios.create({ baseURL: SERVER_URL });

  // Pre-request process (request abort if authed but no fresh tokens)
  api.interceptors.request.use(async (req) => {
    const controller = new AbortController();
    authed && (freshAccessToken ? req.headers.setAuthorization(`Bearer ${freshAccessToken}`) : controller.abort());
    return { ...req, signal: controller.signal };
  });

  // Post-response process
  api.interceptors.response.use(
    (_) => _,
    (err: AxiosError<BackendException>) => {
      switch (err.response?.data.kind) {
        case "AppException":
          verbose && addToast(SERVER_APP_ERR_TOAST(err.response.data.message));
          break;
        case "AuthException":
          verbose && addToast(AUTH_ERR_TOAST);
          navigate("/login");
          break;
        case undefined:
          verbose && addToast(CLIENT_ERR_TOAST);
          break;
      }
      return err;
    }
  );

  // Return
  return api;
}

/**
 * Get request hook.
 */
export function useGet<A extends ODto>(
  queryKey: string,
  subDirect: string,
  headers: object | undefined,
  authed: boolean,
  verbose: boolean,
  refetchInterval: number | false // In second(s)
) {
  // Hooks
  const api = useApi(authed, verbose);
  const { data, isLoading } = useQuery(
    queryKey,
    () => api.get<A>(subDirect, { headers: headers }).then((_) => _.data),
    { refetchInterval: refetchInterval ? refetchInterval * 1000 : refetchInterval }
  );

  // Return
  return { data, isLoading };
}

/**
 * Get request hook effect.
 */
export function useGetM<A extends ODto>(subDirect: string, authed: boolean, verbose: boolean) {
  // Hooks
  const api = useApi(authed, verbose);
  const {
    mutate: getMutate,
    data,
    isLoading,
  } = useMutation((headers: object | undefined) => api.get<A>(subDirect, { headers: headers }).then((_) => _.data));

  // Return
  const getM = (headers: object | undefined) => getMutate(headers);
  return { getM, data, isLoading };
}

/**
 * Post request hook effect.
 */
export function usePostM<A extends ODto>(subDirect: string, authed: boolean, verbose: boolean) {
  // Hooks
  const api = useApi(authed, verbose);
  const {
    mutate: postMutate,
    data,
    isLoading,
  } = useMutation((args: { body: IDto | undefined; headers: object | undefined }) =>
    api.post<A>(subDirect, args.body, { headers: args.headers }).then((_) => _.data)
  );

  // Return
  const postM = (body: IDto | undefined, headers: object | undefined) => postMutate({ body: body, headers: headers });
  return { postM, data, isLoading };
}
