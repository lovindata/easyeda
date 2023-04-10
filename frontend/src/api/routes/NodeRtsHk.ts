import { NodeStatusODto } from "../dto/ODto";
import { useGet } from "./GenericRtsHk";

/**
 * Node status hook for route ("/node/status").
 */
export function useNodeRtsStatus() {
  const { data, isLoading } = useGet<NodeStatusODto>(
    "useNodeRtsStatus",
    "/node/status",
    undefined,
    false,
    false,
    5000
  );
  return { node: data, isRetrieving: isLoading };
}
