import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { environment } from '../../../environments/environment';
import { AuthSession, LoginRequest, RegisterRequest } from '../models/auth.models';

@Injectable({
  providedIn: 'root',
})
export class AuthApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/auth`;

  login(payload: LoginRequest) {
    return this.http.post<AuthSession>(`${this.baseUrl}/login`, payload);
  }

  register(payload: RegisterRequest) {
    return this.http.post<AuthSession>(`${this.baseUrl}/register`, payload);
  }
}
