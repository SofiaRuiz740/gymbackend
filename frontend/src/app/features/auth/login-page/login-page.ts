import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, Validators, NonNullableFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { extractErrorMessage, BannerMessage } from '../../../core/models/request-state';
import { AuthApi } from '../../../core/services/auth-api';
import { AuthStore } from '../../../core/services/auth-store';
import { NoticeBanner } from '../../../shared/components/notice-banner/notice-banner';

@Component({
  selector: 'app-login-page',
  imports: [ReactiveFormsModule, RouterLink, NoticeBanner],
  templateUrl: './login-page.html',
  styleUrl: './login-page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginPage {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly authApi = inject(AuthApi);
  private readonly authStore = inject(AuthStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly feedback = signal<BannerMessage | null>(
    this.route.snapshot.queryParamMap.get('reason') === 'expired'
      ? {
          tone: 'warning',
          title: 'Sesion expirada',
          message: 'Tu token ya no es valido. Inicia sesion de nuevo para continuar.',
        }
      : null,
  );
  readonly submitting = signal(false);
  readonly form = this.formBuilder.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]],
  });

  async submit(): Promise<void> {
    if (this.form.invalid || this.submitting()) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.feedback.set(null);

    try {
      const session = await firstValueFrom(this.authApi.login(this.form.getRawValue()));
      this.authStore.setSession(session);

      const redirect = this.route.snapshot.queryParamMap.get('redirect');
      void this.router.navigateByUrl(redirect || this.authStore.homeUrl());
    } catch (error) {
      this.feedback.set({
        tone: 'danger',
        title: 'No fue posible iniciar sesion',
        message: extractErrorMessage(error),
      });
    } finally {
      this.submitting.set(false);
    }
  }
}
