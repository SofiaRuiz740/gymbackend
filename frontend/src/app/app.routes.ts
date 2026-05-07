import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth-guard';
import { guestGuard } from './core/guards/guest-guard';
import { roleGuard } from './core/guards/role-guard';
import { AdminShell } from './layouts/admin-shell/admin-shell';
import { UserShell } from './layouts/user-shell/user-shell';

export const routes: Routes = [
  {
    path: '',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/auth/login-page/login-page').then((module) => module.LoginPage),
  },
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/auth/login-page/login-page').then((module) => module.LoginPage),
  },
  {
    path: 'register',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/auth/register-page/register-page').then((module) => module.RegisterPage),
  },
  {
    path: 'admin',
    component: AdminShell,
    canActivate: [authGuard, roleGuard],
    data: {
      roles: ['ROLE_ADMIN'],
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard',
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/admin/dashboard-page/dashboard-page').then(
            (module) => module.DashboardPage,
          ),
      },
      {
        path: 'users',
        loadComponent: () =>
          import('./features/admin/users-page/users-page').then((module) => module.UsersPage),
      },
      {
        path: 'categories',
        loadComponent: () =>
          import('./features/admin/categories-page/categories-page').then(
            (module) => module.CategoriesPage,
          ),
      },
      {
        path: 'products',
        loadComponent: () =>
          import('./features/admin/products-page/products-page').then(
            (module) => module.ProductsPage,
          ),
      },
      {
        path: 'reports',
        loadComponent: () =>
          import('./features/admin/reports-page/reports-page').then(
            (module) => module.ReportsPage,
          ),
      },
    ],
  },
  {
    path: 'ops',
    component: UserShell,
    canActivate: [authGuard, roleGuard],
    data: {
      roles: ['ROLE_USER'],
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'overview',
      },
      {
        path: 'overview',
        loadComponent: () =>
          import('./features/operations/overview-page/overview-page').then(
            (module) => module.OverviewPage,
          ),
      },
      {
        path: 'inventory',
        loadComponent: () =>
          import('./features/operations/inventory-page/inventory-page').then(
            (module) => module.InventoryPage,
          ),
      },
      {
        path: 'movements',
        loadComponent: () =>
          import('./features/operations/movements-page/movements-page').then(
            (module) => module.MovementsPage,
          ),
      },
    ],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
