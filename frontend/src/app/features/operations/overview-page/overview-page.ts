import { DatePipe, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';

import { InventoryMovement, StockItem } from '../../../core/models/inventory.models';
import {
  BannerMessage,
  RequestState,
  collectionState,
  errorState,
  extractErrorMessage,
  idleState,
  loadingState,
} from '../../../core/models/request-state';
import { InventoryApi } from '../../../core/services/inventory-api';
import { EmptyState } from '../../../shared/components/empty-state/empty-state';
import { LoadingPanel } from '../../../shared/components/loading-panel/loading-panel';
import { NoticeBanner } from '../../../shared/components/notice-banner/notice-banner';
import { SectionShell } from '../../../shared/components/section-shell/section-shell';

interface OverviewMetrics {
  trackedProducts: number;
  availableUnits: number;
  entriesToday: number;
  exitsToday: number;
}

@Component({
  selector: 'app-overview-page',
  imports: [DatePipe, DecimalPipe, EmptyState, LoadingPanel, NoticeBanner, SectionShell],
  templateUrl: './overview-page.html',
  styleUrl: './overview-page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverviewPage implements OnInit {
  private readonly inventoryApi = inject(InventoryApi);

  readonly feedback = signal<BannerMessage | null>(null);
  readonly metrics = signal<OverviewMetrics>({
    trackedProducts: 0,
    availableUnits: 0,
    entriesToday: 0,
    exitsToday: 0,
  });
  readonly stockState = signal<RequestState<StockItem[]>>(idleState([]));
  readonly movementsState = signal<RequestState<InventoryMovement[]>>(idleState([]));

  ngOnInit(): void {
    void this.loadOverview();
  }

  async loadOverview(): Promise<void> {
    this.feedback.set(null);
    this.stockState.set(loadingState(this.stockState().data));
    this.movementsState.set(loadingState(this.movementsState().data));

    try {
      const from = new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString();
      const to = new Date().toISOString();
      const [stock, movements] = await Promise.all([
        firstValueFrom(this.inventoryApi.getStock()),
        firstValueFrom(this.inventoryApi.getMovements({ from, to })),
      ]);

      this.metrics.set({
        trackedProducts: stock.length,
        availableUnits: stock.reduce((total, item) => total + item.availableStock, 0),
        entriesToday: movements
          .filter((movement) => movement.movementType === 'ENTRY')
          .reduce((total, movement) => total + movement.quantity, 0),
        exitsToday: movements
          .filter((movement) => movement.movementType === 'EXIT')
          .reduce((total, movement) => total + movement.quantity, 0),
      });

      this.stockState.set(
        collectionState(
          stock.filter((item) => item.availableStock <= 10).slice(0, 8),
          'No hay productos con stock sensible en este momento.',
        ),
      );
      this.movementsState.set(
        collectionState(
          [...movements]
            .sort(
              (left, right) =>
                new Date(right.occurredAt).getTime() - new Date(left.occurredAt).getTime(),
            )
            .slice(0, 8),
          'Todavia no se registran movimientos recientes.',
        ),
      );
    } catch (error) {
      const message = extractErrorMessage(error);
      this.feedback.set({
        tone: 'danger',
        title: 'No fue posible cargar el resumen operativo',
        message,
      });
      this.stockState.set(errorState([], message));
      this.movementsState.set(errorState([], message));
    }
  }
}
