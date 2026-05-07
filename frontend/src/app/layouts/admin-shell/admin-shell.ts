import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { AuthStore } from '../../core/services/auth-store';

interface NavItem {
  label: string;
  path: string;
  iconPath: string;
}

@Component({
  selector: 'app-admin-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './admin-shell.html',
  styleUrl: './admin-shell.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminShell {
  private readonly authStore = inject(AuthStore);
  private readonly router = inject(Router);

  readonly username = this.authStore.username;
  readonly navigation: NavItem[] = [
    {
      label: 'Dashboard',
      path: '/admin/dashboard',
      iconPath: 'M3 13h8V3H3v10Zm10 8h8V3h-8v18Zm-10 0h8v-6H3v6Z',
    },
    {
      label: 'Usuarios',
      path: '/admin/users',
      iconPath:
        'M16 21v-1a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v1m17-11a4 4 0 1 1-8 0a4 4 0 0 1 8 0Zm6 11v-1a4 4 0 0 0-3-3.87m-3-7.13a4 4 0 0 1 0-7.75',
    },
    {
      label: 'Categorias',
      path: '/admin/categories',
      iconPath: 'M4 7h16M4 12h16M4 17h10',
    },
    {
      label: 'Productos',
      path: '/admin/products',
      iconPath:
        'M3 7.5 12 3l9 4.5v9L12 21l-9-4.5v-9Zm9-4.5v18m9-13.5-9 4.5-9-4.5',
    },
    {
      label: 'Reportes',
      path: '/admin/reports',
      iconPath: 'M4 19h16M7 16V8M12 16V5M17 16v-3',
    },
  ];

  readonly todayLabel = new Intl.DateTimeFormat('es-CO', {
    dateStyle: 'full',
  }).format(new Date());

  logout(): void {
    this.authStore.logout();
    void this.router.navigateByUrl('/login');
  }
}
