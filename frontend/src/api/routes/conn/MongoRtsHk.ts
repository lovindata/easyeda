import useApi from "../GenericRtsHk";
import { useQuery } from "react-query";

/**
 * Mongo databases listing hook for route ("/conn/{id}/mongo/databases").
 */
export function useMongoRtsDbs(id: number | undefined, enabled: boolean) {
  const api = useApi(true, false);
  const { data } = useQuery(
    `/conn/${id}/mongo/databases`,
    () => api.get<string[]>(`/conn/${id}/mongo/databases`).then((_) => _.data),
    { enabled: enabled && !!id }
  );
  return data;
}

/**
 * Mongo collections listing hook for route ("/conn/{id}/mongo/{database}/collections").
 */
export function useMongoRtsColls(id: number | undefined, database: string | undefined, enabled: boolean) {
  const api = useApi(true, false);
  const { data } = useQuery(
    `/conn/${id}/mongo/${database}/collections`,
    () => api.get<string[]>(`/conn/${id}/mongo/${database}/collections`).then((_) => _.data),
    { enabled: enabled && !!id && !!database }
  );
  return data;
}
