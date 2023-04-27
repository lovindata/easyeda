import { ConnFormIDto } from "../dto/IDto";
import { ConnODto, ConnTestODto } from "../dto/ODto";
import useApi from "./GenericRtsHk";
import { useQuery, useMutation } from "react-query";

/**
 * Connection testing hook for route ("/conn/test").
 */
export function useConnRtsTest() {
  // Hooks
  const api = useApi(true, true);
  const { mutate: postM, data } = useMutation((body: ConnFormIDto) =>
    api.post<ConnTestODto>("/conn/test", body, { headers: undefined }).then((_) => _.data)
  );
  return {
    test: (body: ConnFormIDto) => postM(body),
    data,
  };
}

/**
 * Connection listing hook for route ("/conn/list").
 */
export function useConnRtsList() {
  const api = useApi(true, false);
  const { data } = useQuery(
    "/conn/list",
    () => api.get<ConnODto[]>("/conn/list", { headers: undefined }).then((_) => _.data),
    { refetchInterval: 10000 }
  );
  return data;
}

/**
 * Test known connection ("/conn/{id}/test").
 */
export function useConnRtsIdTest() {
  const api = useApi(true, false);
  const { mutate: test, data: isUp } = useMutation((connId: number) =>
    api.post<ConnTestODto>(`/conn/${connId}/test`).then((_) => _.data.isUp)
  );
  return { test, isUp };
}
