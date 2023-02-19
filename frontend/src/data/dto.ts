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
