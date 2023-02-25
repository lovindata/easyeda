/**
 * Server exception dto.
 */
export interface AppException {
  kind: "AppException";
  message: string;
}

/**
 * Tokens dto.
 */
export interface TokenDtoOut {
  kind: "TokenDtoOut";
  accessToken: string;
  expireAt: string;
  refreshToken: string;
}

/**
 * User status dto.
 */
export interface UserStatusDtoOut {
  kind: "UserStatusDtoOut";
  id: number;
  email: string;
  username: string;
  createdAt: string;
  validatedAt: string;
  updatedAt: string;
  activeAt: string;
}
