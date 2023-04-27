import { NodeStatusODto } from "../dto/ODto";
import useApi from "./GenericRtsHk";
import { useQuery } from "react-query";

/**
 * Node status hook for route ("/node/status").
 */
export function useNodeRtsStatus() {
  const api = useApi(false, false);
  const { data } = useQuery(
    "/node/status",
    () => api.get<NodeStatusODto>("/node/status", { headers: undefined }).then((_) => _.data),
    { refetchInterval: 10000 }
  );
  return data;
}
