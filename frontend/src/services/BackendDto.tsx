/**
 * Server exception dto.
 */
export type BackendException = AppException | AuthException;

/**
 * Server application exception dto.
 */
export interface AppException {
  kind: "AppException";
  message: string;
}

/**
 * Server authentication exception dto.
 */
export interface AuthException {
  kind: "AuthException";
  message: string;
}

/**
 * Tokens dto.
 */
export interface TokenODto {
  accessToken: string;
  expireAt: number;
  refreshToken: string;
}

/**
 * User status dto.
 */
export interface UserStatusODto {
  id: number;
  email: string;
  username: string;
  createdAt: number;
  validatedAt: number | null;
  updatedAt: number;
  activeAt: number;
}
