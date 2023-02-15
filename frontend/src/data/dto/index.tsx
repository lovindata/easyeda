export interface AppException {
  kind: "AppException";
  message: string;
}

export interface TokenDtoOut {
  kind: "TokenDtoOut";
  accessToken: string;
  expireAt: string;
  refreshToken: string;
}
