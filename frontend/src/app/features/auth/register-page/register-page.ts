import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { BannerMessage, extractErrorMessage } from '../../../core/models/request-state';
import { AuthApi } from '../../../core/services/auth-api';
import { AuthStore } from '../../../core/services/auth-store';
import { NoticeBanner } from '../../../shared/components/notice-banner/notice-banner';

@Component({
  selector: 'app-register-page',
  imports: [ReactiveFormsModule, RouterLink, NoticeBanner],
  templateUrl: './register-page.html',
  styleUrl: './register-page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterPage {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly authApi = inject(AuthApi);
  private readonly authStore = inject(AuthStore);
  private readonly router = inject(Router);

  readonly feedback = signal<BannerMessage | null>(null);
  readonly submitting = signal(false);
  readonly form = this.formBuilder.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required, Validators.minLength(8)]],
  });

  async submit(): Promise<void> {
    if (this.form.invalid || this.submitting()) {
      this.form.markAllAsTouched();
      return;
    }

    const { username, email, password, confirmPassword } = this.form.getRawValue();
    if (password !== confirmPassword) {
      this.feedback.set({
        tone: 'warning',
        title: 'Las contrasenas no coinciden',
        message: 'Confirma la misma contrasena para crear tu cuenta.',
      });
      return;
    }

    this.submitting.set(true);
    this.feedback.set(null);

    try {
      const session = await firstValueFrom(this.authApi.register({ username, email, password }));
      this.authStore.setSession(session);
      void this.router.navigateByUrl(this.authStore.homeUrl());
    } catch (error) {
      this.feedback.set({
        tone: 'danger',
        title: 'No fue posible crear la cuenta',
        message: extractErrorMessage(error),
      });
    } finally {
      this.submitting.set(false);
    }
  }
}
