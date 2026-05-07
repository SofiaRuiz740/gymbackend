import { Injectable, computed, signal } from '@angular/core';

import { AuthSession, UserRole } from '../models/auth.models';

@Injectable({
  providedIn: 'root',
})
export class AuthStore {
  private readonly storageKey = 'gym-inventory.session';
  private readonly sessionState = signal<AuthSession | null>(this.readStoredSession());

  readonly session = this.sessionState.asReadonly();
  readonly isAuthenticated = computed(() => this.sessionState() !== null);
  readonly role = computed<UserRole | null>(() => this.sessionState()?.role ?? null);
  readonly username = computed(() => this.sessionState()?.username ?? '');
  readonly homeUrl = computed(() => this.resolveHomeUrl(this.role()));

  setSession(session: AuthSession): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(this.storageKey, JSON.stringify(session));
    }

    this.sessionState.set(session);
  }

  clearSession(): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem(this.storageKey);
    }

    this.sessionState.set(null);
  }

  logout(): void {
    this.clearSession();
  }

  getAccessToken(): string | null {
    return this.sessionState()?.accessToken ?? null;
  }

  resolveHomeUrl(role: UserRole | null): string {
    return role === 'ROLE_ADMIN' ? '/admin/dashboard' : '/ops/overview';
  }

  private readStoredSession(): AuthSession | null {
    if (typeof localStorage === 'undefined') {
      return null;
    }

    const rawSession = localStorage.getItem(this.storageKey);
    if (!rawSession) {
      return null;
    }

    try {
      const session = JSON.parse(rawSession) as AuthSession;
      if (new Date(session.expiresAt).getTime() <= Date.now()) {
        localStorage.removeItem(this.storageKey);
        return null;
      }

      return session;
    } catch {
      localStorage.removeItem(this.storageKey);
      return null;
    }
  }
}
