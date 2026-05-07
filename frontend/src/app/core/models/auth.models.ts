export type UserRole = 'ROLE_ADMIN' | 'ROLE_USER';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthSession {
  accessToken: string;
  tokenType: string;
  expiresAt: string;
  username: string;
  role: UserRole;
}
