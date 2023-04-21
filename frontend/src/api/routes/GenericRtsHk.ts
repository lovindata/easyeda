import useAuth from "../../context/auth/AuthHk";
import { ToastLevelEnum } from "../../context/toaster/ToasterCtx";
import useToaster from "../../context/toaster/ToasterHk";
import { IDto } from "../dto/IDto";
import { BackendException, ODto, TokenODto } from "../dto/ODto";
import axios, { AxiosError } from "axios";
import { useMutation, useQuery } from "react-query";
import { NavigateFunction, useNavigate } from "react-router-dom";

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
 * Refresh tokens syncer.
 */
let REFRESH_TOKENS_SYNCER: Promise<TokenODto | undefined> | undefined = undefined;

/**
 * Non concurrent refresh tokens.
 */
async function REFRESH_TOKENS_UNCONCURENT(
  tokens: {
    accessToken: string;
    expireAt: number;
    refreshToken: string;
  },
  setTokens: (tokens: { accessToken: string; expireAt: number; refreshToken: string } | undefined) => void
) {
  !REFRESH_TOKENS_SYNCER &&
    (REFRESH_TOKENS_SYNCER = axios
      .post<TokenODto>(`${SERVER_URL}user/refresh`, undefined, {
        headers: { "DataPiU-Refresh-Token": tokens.refreshToken },
      })
      .then((res) => {
        setTokens(res.data);
        return res.data;
      })
      .catch(() => {
        setTokens(undefined);
        return undefined;
      }));
  const freshTokens = await REFRESH_TOKENS_SYNCER; // Concurrent executions await the same global promise
  REFRESH_TOKENS_SYNCER && (REFRESH_TOKENS_SYNCER = undefined);
  return freshTokens;
}

/**
 * Redirect to "/login" and toast display syncer.
 */
let REDIRECT_LOGIN_TOAST_SYNCER: Promise<void> | undefined = undefined;

/**
 * Non concurrent redirect to "/login" and toast display.
 */
async function REDIRECT_LOGIN_TOAST_UNCONCURRENT(
  navigate: NavigateFunction,
  addToast: (toast: { level: ToastLevelEnum; header: string; message?: string | undefined }) => void,
  verbose: boolean
) {
  !REDIRECT_LOGIN_TOAST_SYNCER &&
    (REDIRECT_LOGIN_TOAST_SYNCER = new Promise((resolve) => {
      navigate("/login");
      verbose && addToast(AUTH_ERR_TOAST);
      return resolve();
    }));
  await REDIRECT_LOGIN_TOAST_SYNCER; // Concurrent executions await the same global promise
  REDIRECT_LOGIN_TOAST_SYNCER && (REDIRECT_LOGIN_TOAST_SYNCER = undefined);
}

/**
 * Axios fetcher hook.
 */
function useApi(authed: boolean, verbose: boolean) {
  // Get hooks & Build backend api
  const navigate = useNavigate();
  const { addToast } = useToaster();
  const { tokens, setTokens } = useAuth();
  const api = axios.create({ baseURL: SERVER_URL });

  // Pre-request process
  api.interceptors.request.use(async (req) => {
    if (!authed) return req;
    else {
      const controller = new AbortController();
      if (tokens) req.headers.setAuthorization(`Bearer ${tokens.accessToken}`);
      else {
        controller.abort();
        REDIRECT_LOGIN_TOAST_UNCONCURRENT(navigate, addToast, verbose);
      }
      return { ...req, signal: controller.signal };
    }
  });

  // Post-response process
  api.interceptors.response.use(
    (_) => _,
    async (err: AxiosError<BackendException>) => {
      // Retry case
      if (err.response?.data.kind === "AuthException" && tokens) {
        const freshTokens = await REFRESH_TOKENS_UNCONCURENT(tokens, setTokens);
        if (freshTokens && err.config) {
          err.config.headers.setAuthorization(`Bearer ${freshTokens.accessToken}`);
          return await axios.request(err.config);
        }
      }

      // No retry cases
      switch (err.response?.data.kind) {
        case "AppException":
          verbose && addToast(SERVER_APP_ERR_TOAST(err.response.data.message));
          break;
        case "AuthException":
          REDIRECT_LOGIN_TOAST_UNCONCURRENT(navigate, addToast, verbose);
          break;
        case undefined:
          verbose && err.message === "Network Error" && addToast(CLIENT_ERR_TOAST);
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
