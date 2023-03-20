/**
 * Server exception dto.
 */
export interface AppExceptionODto {
  message: string;
}

/**
 * Tokens dto.
 */
export interface TokenODto {
  accessToken: string;
  expireAt: string;
  refreshToken: string;
}

/**
 * User status dto.
 */
export interface UserStatusODto {
  id: number;
  email: string;
  username: string;
  createdAt: string;
  validatedAt: string | null;
  updatedAt: string;
  activeAt: string;
}
