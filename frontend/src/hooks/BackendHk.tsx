import axios from "axios";
import { useQuery, useMutation } from "react-query";
import { AppExceptionODto } from "../data/BackendDto";
import { useToaster, ToastLevelEnum } from "../context";

// Error messages
const clientErr = {
  header: "Unreachable server",
  message: "Please verify your internet connection.",
};
const serverErr = {
  header: "Bad request",
};

/**
 * Get request hook.
 */
export function useGet<A>(queryKey: string, subDirect: string, headers?: object, verbose: boolean = true) {
  // Do query
  const { addToast } = useToaster();
  const { data, isLoading } = useQuery(queryKey, () =>
    axios
      .get(`http://${window.location.hostname}:8081${subDirect}`, { headers: headers })
      .then((res) => res.data as A)
      .catch((err) => {
        if (verbose) {
          err.response
            ? addToast(ToastLevelEnum.Error, serverErr.header, (err.response.data as AppExceptionODto).message)
            : addToast(ToastLevelEnum.Warning, clientErr.header, clientErr.message);
        }
        return undefined;
      })
  );

  // Return
  return { data, isLoading };
}

/**
 *  Post request hook.
 */
export function usePost<A>(
  queryKey: string,
  subDirect: string,
  body?: object,
  headers?: object,
  verbose: boolean = true
) {
  // Do query
  const { addToast } = useToaster();
  const { data, isLoading } = useQuery(queryKey, () =>
    axios
      .post(`http://${window.location.hostname}:8081${subDirect}`, body, { headers: headers })
      .then((res) => res.data as A)
      .catch((err) => {
        if (verbose) {
          err.response
            ? addToast(ToastLevelEnum.Error, serverErr.header, (err.response.data as AppExceptionODto).message)
            : addToast(ToastLevelEnum.Warning, clientErr.header, clientErr.message);
        }
        return undefined;
      })
  );

  // Return
  return { data, isLoading };
}

/**
 * Get request hook for effects.
 */
export function useGetM<A>(subDirect: string, verbose: boolean = true) {
  // Build mutate
  const { addToast } = useToaster();
  const {
    mutate: getMutate,
    data,
    isLoading,
  } = useMutation((headers?: object) =>
    axios
      .get(`http://${window.location.hostname}:8081${subDirect}`, { headers: headers })
      .then((res) => res.data as A)
      .catch((err) => {
        if (verbose) {
          err.response
            ? addToast(ToastLevelEnum.Error, serverErr.header, (err.response.data as AppExceptionODto).message)
            : addToast(ToastLevelEnum.Warning, clientErr.header, clientErr.message);
        }
        return undefined;
      })
  );

  // Return
  const getM = (headers: object = {}) => getMutate(headers);
  return { getM, data, isLoading };
}

/**
 * Post request hook for effects.
 */
export function usePostM<A>(subDirect: string, verbose: boolean = true) {
  // Build mutate
  const { addToast } = useToaster();
  const {
    mutate: postMutate,
    data,
    isLoading,
  } = useMutation((body?: object, headers?: object) =>
    axios
      .post(`http://${window.location.hostname}:8081${subDirect}`, body, { headers: headers })
      .then((res) => res.data as A)
      .catch((err) => {
        if (verbose) {
          err.response
            ? addToast(ToastLevelEnum.Error, serverErr.header, (err.response.data as AppExceptionODto).message)
            : addToast(ToastLevelEnum.Warning, clientErr.header, clientErr.message);
        }
        return undefined;
      })
  );

  // Return
  const postM = (body?: object, headers?: object) => postMutate(body, headers);
  return { postM, data, isLoading };
}
