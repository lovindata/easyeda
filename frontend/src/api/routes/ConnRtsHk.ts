import { ConnFormIDto } from "../dto/IDto";
import { ConnODto, ConnTestODto } from "../dto/ODto";
import useApi from "./GenericRtsHk";
import { useQuery, useMutation, useQueries } from "react-query";

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
export function useConnRtsIdsTest(connIds: number[] | undefined) {
  const api = useApi(true, false);
  const data = useQueries(
    (connIds || []).map((id) => {
      return {
        queryKey: `/conn/${id}/test`,
        queryFn: () => api.post<ConnTestODto>(`/conn/${id}/test`, { headers: undefined }).then((_) => _.data),
        refetchInterval: 10000,
      };
    })
  );
  return connIds ? data.map((_) => _.data?.isUp) : undefined;
}
