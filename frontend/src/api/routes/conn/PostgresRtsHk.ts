import useApi from "../GenericRtsHk";
import { useQuery } from "react-query";

/**
 * Postgres databases listing hook for route ("/conn/{id}/postgres/databases").
 */
export function usePostgresRtsDbs(connId: number, enabled: boolean) {
  const api = useApi(true, false);
  const { data } = useQuery(
    `/conn/${connId}/postgres/databases`,
    () => api.get<string[]>(`/conn/${connId}/postgres/databases`).then((_) => _.data),
    { enabled: enabled }
  );
  return data;
}

/**
 * Postgres schemas listing hook for route ("/conn/{id}/postgres/{database}/schemas").
 */
export function usePostgresRtsSchs(connId: number | undefined, database: string | undefined, enabled: boolean) {
  const api = useApi(true, false);
  const { data } = useQuery(
    `/conn/${connId}/postgres/${database}/schemas`,
    () => api.get<string[]>(`/conn/${connId}/postgres/${database}/schemas`).then((_) => _.data),
    { enabled: enabled && !!connId && !!database }
  );
  return data;
}
/**
 * Postgres tables listing hook for route ("/conn/{id}/postgres/{database}/{schema}/tables").
 */
export function usePostgresRtsTabs(
  connId: number | undefined,
  database: string | undefined,
  schema: string | undefined,
  enabled: boolean
) {
  const api = useApi(true, false);
  const { data } = useQuery(
    `/conn/${connId}/postgres/${database}/${schema}/tables`,
    () => api.get<string[]>(`/conn/${connId}/postgres/${database}/${schema}/tables`).then((_) => _.data),
    { enabled: enabled && !!connId && !!database && !!schema }
  );
  return data;
}
