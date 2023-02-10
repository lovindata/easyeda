export interface AppException {
  message: string;
}

export interface TokenDtoOut {
  accessToken: string;
  expireAt: string;
  refreshToken: string;
}
