import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { AuthStore } from '../../core/services/auth-store';

interface NavItem {
  label: string;
  path: string;
  iconPath: string;
}

@Component({
  selector: 'app-user-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './user-shell.html',
  styleUrl: './user-shell.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserShell {
  private readonly authStore = inject(AuthStore);
  private readonly router = inject(Router);

  readonly username = this.authStore.username;
  readonly navigation: NavItem[] = [
    {
      label: 'Resumen',
      path: '/ops/overview',
      iconPath: 'M3 12 12 4l9 8m-2 0v8H5v-8',
    },
    {
      label: 'Inventario',
      path: '/ops/inventory',
      iconPath:
        'M4 7.5 12 4l8 3.5v9L12 20l-8-3.5v-9Zm8-3.5v16m8-12.5L12 11 4 7.5',
    },
    {
      label: 'Movimientos',
      path: '/ops/movements',
      iconPath: 'M7 7h10m0 0-3-3m3 3-3 3M17 17H7m0 0 3-3m-3 3 3 3',
    },
  ];

  logout(): void {
    this.authStore.logout();
    void this.router.navigateByUrl('/login');
  }
}
