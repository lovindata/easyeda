import axios from "axios";
import { AxiosError } from "axios";
import { useQuery, useMutation } from "react-query";
import { TokenODto, BackendException, ODto } from "./ODto";
import { useToaster, ToastLevelEnum, useAuthContext } from "../context";
import { useNavigate } from "react-router-dom";
import { IDto } from "./IDto";

// Error toasts
const clientErrToast = {
  level: ToastLevelEnum.Warning,
  header: "Unreachable server",
  message: "Please verify your internet connection.",
};
const serverAppErrToast = (message: string) => ({
  level: ToastLevelEnum.Error,
  header: "Bad request",
  message: message,
});
const serverAuthErrToast = {
  level: ToastLevelEnum.Warning,
  header: "Not connected",
  message: "Connection lost or account not provided. Please reconnect.",
};

/**
 * Backend axios fetcher.
 */
function useBackend(authed: boolean, verbose: boolean) {
  // Get hooks
  const navigate = useNavigate();
  const { addToast } = useToaster();
  const { tokens, setTokens } = useAuthContext();

  // Build backend axios
  const backend = axios.create({
    baseURL: `http://${window.location.hostname}:8081/`,
  });

  // Pre-request process
  backend.interceptors.request.use(async (req) => {
    // Get fresh tokens if authed
    let freshTokens: TokenODto | undefined;
    if (authed && tokens && tokens.expireAt < Date.now()) {
      await axios
        .post<TokenODto>(
          `http://${window.location.hostname}:8081/user/refresh`,
          undefined,
          {
            headers: { "DataPiU-Refresh-Token": tokens.refreshToken },
          }
        )
        .then((res) => {
          setTokens(res.data);
          freshTokens = res.data;
        })
        .catch((err: AxiosError<BackendException>) => {
          verbose &&
            (err.response
              ? addToast(serverAuthErrToast)
              : addToast(clientErrToast));
          freshTokens = undefined;
        });
    } else freshTokens = tokens;

    // Do request or abort (authed and no fresh tokens)
    const controller = new AbortController();
    if (authed) {
      if (freshTokens)
        req.headers.setAuthorization(`Bearer ${freshTokens.accessToken}`);
      else {
        controller.abort();
        navigate("/login");
      }
    }
    return { ...req, signal: controller.signal };
  });

  // Post-response process
  backend.interceptors.response.use(
    (_) => _,
    (err: AxiosError<BackendException>) => {
      switch (err.response?.data.kind) {
        case "AppException":
          verbose && addToast(serverAppErrToast(err.response.data.message));
          break;
        case "AuthException":
          verbose && addToast(serverAuthErrToast);
          navigate("/login");
          break;
        case undefined:
          verbose && addToast(clientErrToast);
          break;
      }
      return err;
    }
  );

  // Return
  return backend;
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
  refetchInterval: number | false
) {
  // Hooks
  const backend = useBackend(authed, verbose);
  const { data, isLoading } = useQuery(
    queryKey,
    () =>
      backend.get<A>(subDirect, { headers: headers }).then((res) => res.data),
    { refetchInterval: refetchInterval }
  );

  // Return
  return { data, isLoading };
}

/**
 * Get request hook effect.
 */
export function useGetM<A extends ODto>(
  subDirect: string,
  authed: boolean,
  verbose: boolean
) {
  // Hooks
  const backend = useBackend(authed, verbose);
  const {
    mutate: getMutate,
    data,
    isLoading,
  } = useMutation((headers: object | undefined) =>
    backend.get<A>(subDirect, { headers: headers }).then((res) => res.data)
  );

  // Return
  const getM = (headers: object | undefined) => getMutate(headers);
  return { getM, data, isLoading };
}

/**
 * Post request hook effect.
 */
export function usePostM<A extends ODto>(
  subDirect: string,
  authed: boolean,
  verbose: boolean
) {
  // Hooks
  const backend = useBackend(authed, verbose);
  const {
    mutate: postMutate,
    data,
    isLoading,
  } = useMutation(
    (args: { body: IDto | undefined; headers: object | undefined }) =>
      backend
        .post<A>(subDirect, args.body, { headers: args.headers })
        .then((res) => res.data)
  );

  // Return
  const postM = (body: IDto | undefined, headers: object | undefined) =>
    postMutate({ body: body, headers: headers });
  return { postM, data, isLoading };
}
