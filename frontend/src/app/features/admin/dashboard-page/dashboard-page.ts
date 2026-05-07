import { DatePipe, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';

import { InventoryMovement } from '../../../core/models/inventory.models';
import {
  RequestState,
  BannerMessage,
  collectionState,
  errorState,
  extractErrorMessage,
  idleState,
  loadingState,
} from '../../../core/models/request-state';
import { ReportProduct } from '../../../core/models/report.models';
import { CategoriesApi } from '../../../core/services/categories-api';
import { InventoryApi } from '../../../core/services/inventory-api';
import { ProductsApi } from '../../../core/services/products-api';
import { ReportsApi } from '../../../core/services/reports-api';
import { UsersApi } from '../../../core/services/users-api';
import { EmptyState } from '../../../shared/components/empty-state/empty-state';
import { LoadingPanel } from '../../../shared/components/loading-panel/loading-panel';
import { NoticeBanner } from '../../../shared/components/notice-banner/notice-banner';

interface DashboardMetrics {
  users: number;
  activeCategories: number;
  activeProducts: number;
  stockUnits: number;
  lowStock: number;
}

@Component({
  selector: 'app-dashboard-page',
  imports: [DatePipe, DecimalPipe, EmptyState, LoadingPanel, NoticeBanner],
  templateUrl: './dashboard-page.html',
  styleUrl: './dashboard-page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardPage implements OnInit {
  private readonly usersApi = inject(UsersApi);
  private readonly categoriesApi = inject(CategoriesApi);
  private readonly productsApi = inject(ProductsApi);
  private readonly inventoryApi = inject(InventoryApi);
  private readonly reportsApi = inject(ReportsApi);

  readonly loading = signal(true);
  readonly feedback = signal<BannerMessage | null>(null);
  readonly metrics = signal<DashboardMetrics>({
    users: 0,
    activeCategories: 0,
    activeProducts: 0,
    stockUnits: 0,
    lowStock: 0,
  });
  readonly lowStockState = signal<RequestState<ReportProduct[]>>(idleState([]));
  readonly movementsState = signal<RequestState<InventoryMovement[]>>(idleState([]));

  ngOnInit(): void {
    void this.loadDashboard();
  }

  async loadDashboard(): Promise<void> {
    this.loading.set(true);
    this.feedback.set(null);
    this.lowStockState.set(loadingState(this.lowStockState().data));
    this.movementsState.set(loadingState(this.movementsState().data));

    try {
      const from = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString();
      const to = new Date().toISOString();

      const [users, categories, products, stock, lowStock, recentMovements] = await Promise.all([
        firstValueFrom(this.usersApi.listUsers()),
        firstValueFrom(this.categoriesApi.listCategories()),
        firstValueFrom(this.productsApi.listProducts()),
        firstValueFrom(this.inventoryApi.getStock()),
        firstValueFrom(this.reportsApi.getLowStockReport(20)),
        firstValueFrom(this.inventoryApi.getMovements({ from, to })),
      ]);

      this.metrics.set({
        users: users.length,
        activeCategories: categories.filter((category) => category.active).length,
        activeProducts: products.filter((product) => product.active).length,
        stockUnits: stock.reduce((total, item) => total + item.availableStock, 0),
        lowStock: lowStock.length,
      });

      this.lowStockState.set(collectionState(lowStock, 'No hay productos con bajo stock.'));
      this.movementsState.set(
        collectionState(
          [...recentMovements]
            .sort(
              (left, right) =>
                new Date(right.occurredAt).getTime() - new Date(left.occurredAt).getTime(),
            )
            .slice(0, 8),
          'No hay movimientos recientes dentro del rango operativo.',
        ),
      );
    } catch (error) {
      const message = extractErrorMessage(error);
      this.feedback.set({
        tone: 'danger',
        title: 'No fue posible cargar el dashboard',
        message,
      });
      this.lowStockState.set(errorState([], message));
      this.movementsState.set(errorState([], message));
    } finally {
      this.loading.set(false);
    }
  }
}
