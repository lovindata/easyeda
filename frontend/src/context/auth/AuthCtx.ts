import { createContext } from "react";

/**
 * User context type.
 */
export interface IAuthContext {
  tokens:
    | {
        accessToken: string;
        expireAt: number;
        refreshToken: string;
      }
    | undefined;
  setTokens: (
    tokens:
      | {
          accessToken: string;
          expireAt: number;
          refreshToken: string;
        }
      | undefined
  ) => void;
}

/**
 * Auth context with tokens.
 */
export const AuthContext = createContext<IAuthContext | undefined>(undefined);
