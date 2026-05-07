import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { environment } from '../../../environments/environment';
import { CreateUserRequest, User } from '../models/user.models';

@Injectable({
  providedIn: 'root',
})
export class UsersApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/users`;

  listUsers() {
    return this.http.get<User[]>(this.baseUrl);
  }

  createUser(payload: CreateUserRequest) {
    return this.http.post<User>(this.baseUrl, payload);
  }

  updateStatus(userId: string, active: boolean) {
    return this.http.patch<User>(`${this.baseUrl}/${userId}/status`, { active });
  }
}
