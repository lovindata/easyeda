import useApi from "../GenericRtsHk";
import { useQuery } from "react-query";

/**
 * Postgres databases listing hook for route ("/conn/{id}/postgres/databases").
 */
export function usePostgresRtsDbs(id: number | undefined, enabled: boolean) {
  const api = useApi(true, false);
  const { data } = useQuery(
    `/conn/${id}/postgres/databases`,
    () => api.get<string[]>(`/conn/${id}/postgres/databases`).then((_) => _.data),
    { enabled: enabled && !!id }
  );
  return data;
}

/**
 * Postgres schemas listing hook for route ("/conn/{id}/postgres/{database}/schemas").
 */
export function usePostgresRtsSchs(id: number | undefined, database: string | undefined, enabled: boolean) {
  const api = useApi(true, false);
  const { data } = useQuery(
    `/conn/${id}/postgres/${database}/schemas`,
    () => api.get<string[]>(`/conn/${id}/postgres/${database}/schemas`).then((_) => _.data),
    { enabled: enabled && !!id && !!database }
  );
  return data;
}
/**
 * Postgres tables listing hook for route ("/conn/{id}/postgres/{database}/{schema}/tables").
 */
export function usePostgresRtsTabs(
  id: number | undefined,
  database: string | undefined,
  schema: string | undefined,
  enabled: boolean
) {
  const api = useApi(true, false);
  const { data } = useQuery(
    `/conn/${id}/postgres/${database}/${schema}/tables`,
    () => api.get<string[]>(`/conn/${id}/postgres/${database}/${schema}/tables`).then((_) => _.data),
    { enabled: enabled && !!id && !!database && !!schema }
  );
  return data;
}
