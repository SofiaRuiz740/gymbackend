import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';

import { UserRole } from '../../../core/models/auth.models';
import {
  BannerMessage,
  RequestState,
  collectionState,
  errorState,
  extractErrorMessage,
  idleState,
  loadingState,
} from '../../../core/models/request-state';
import { User } from '../../../core/models/user.models';
import { UsersApi } from '../../../core/services/users-api';
import { EmptyState } from '../../../shared/components/empty-state/empty-state';
import { LoadingPanel } from '../../../shared/components/loading-panel/loading-panel';
import { NoticeBanner } from '../../../shared/components/notice-banner/notice-banner';

@Component({
  selector: 'app-users-page',
  imports: [DatePipe, ReactiveFormsModule, EmptyState, LoadingPanel, NoticeBanner],
  templateUrl: './users-page.html',
  styleUrl: './users-page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UsersPage implements OnInit {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly usersApi = inject(UsersApi);

  readonly roles: UserRole[] = ['ROLE_USER', 'ROLE_ADMIN'];
  readonly submitting = signal(false);
  readonly busyUserId = signal<string | null>(null);
  readonly feedback = signal<BannerMessage | null>(null);
  readonly usersState = signal<RequestState<User[]>>(idleState([]));
  readonly form = this.formBuilder.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    role: ['ROLE_USER' as UserRole, [Validators.required]],
  });

  ngOnInit(): void {
    void this.loadUsers();
  }

  async loadUsers(): Promise<void> {
    this.usersState.set(loadingState(this.usersState().data));

    try {
      const users = await firstValueFrom(this.usersApi.listUsers());
      this.usersState.set(collectionState(users, 'Todavia no hay usuarios creados.'));
    } catch (error) {
      this.usersState.set(errorState([], extractErrorMessage(error)));
    }
  }

  async submit(): Promise<void> {
    if (this.form.invalid || this.submitting()) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.feedback.set(null);

    try {
      await firstValueFrom(this.usersApi.createUser(this.form.getRawValue()));
      this.feedback.set({
        tone: 'success',
        title: 'Usuario creado',
        message: 'La cuenta quedo disponible en el servicio de usuarios.',
      });
      this.form.reset({
        username: '',
        email: '',
        password: '',
        role: 'ROLE_USER',
      });
      await this.loadUsers();
    } catch (error) {
      this.feedback.set({
        tone: 'danger',
        title: 'No fue posible crear el usuario',
        message: extractErrorMessage(error),
      });
    } finally {
      this.submitting.set(false);
    }
  }

  async toggleStatus(user: User): Promise<void> {
    this.busyUserId.set(user.id);
    this.feedback.set(null);

    try {
      await firstValueFrom(this.usersApi.updateStatus(user.id, !user.active));
      this.feedback.set({
        tone: 'success',
        title: 'Estado actualizado',
        message: `La cuenta ${user.username} fue ${user.active ? 'bloqueada' : 'reactivada'}.`,
      });
      await this.loadUsers();
    } catch (error) {
      this.feedback.set({
        tone: 'danger',
        title: 'No fue posible actualizar el estado',
        message: extractErrorMessage(error),
      });
    } finally {
      this.busyUserId.set(null);
    }
  }
}
