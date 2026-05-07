import { DatePipe, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';

import {
  RequestState,
  BannerMessage,
  collectionState,
  errorState,
  extractErrorMessage,
  idleState,
  loadingState,
} from '../../../core/models/request-state';
import {
  MovementAuditRecord,
  MovementSummaryRecord,
  ReportProduct,
} from '../../../core/models/report.models';
import { ReportsApi } from '../../../core/services/reports-api';
import { EmptyState } from '../../../shared/components/empty-state/empty-state';
import { LoadingPanel } from '../../../shared/components/loading-panel/loading-panel';
import { NoticeBanner } from '../../../shared/components/notice-banner/notice-banner';

@Component({
  selector: 'app-reports-page',
  imports: [
    DatePipe,
    DecimalPipe,
    ReactiveFormsModule,
    EmptyState,
    LoadingPanel,
    NoticeBanner,
  ],
  templateUrl: './reports-page.html',
  styleUrl: './reports-page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportsPage implements OnInit {
  private readonly reportsApi = inject(ReportsApi);
  private readonly formBuilder = inject(NonNullableFormBuilder);

  readonly feedback = signal<BannerMessage | null>(null);
  readonly stockState = signal<RequestState<ReportProduct[]>>(idleState([]));
  readonly lowStockState = signal<RequestState<ReportProduct[]>>(idleState([]));
  readonly auditState = signal<RequestState<MovementAuditRecord[]>>(idleState([]));
  readonly summaryState = signal<RequestState<MovementSummaryRecord[]>>(idleState([]));
  readonly form = this.formBuilder.group({
    threshold: [20, [Validators.required, Validators.min(1)]],
    from: [toDateTimeLocal(new Date(Date.now() - 7 * 24 * 60 * 60 * 1000)), [Validators.required]],
    to: [toDateTimeLocal(new Date()), [Validators.required]],
  });

  ngOnInit(): void {
    void this.runReports();
  }

  async runReports(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.feedback.set(null);
    this.stockState.set(loadingState(this.stockState().data));
    this.lowStockState.set(loadingState(this.lowStockState().data));
    this.auditState.set(loadingState(this.auditState().data));
    this.summaryState.set(loadingState(this.summaryState().data));

    try {
      const { threshold, from, to } = this.form.getRawValue();
      const fromInstant = new Date(from).toISOString();
      const toInstant = new Date(to).toISOString();

      const [stockReport, lowStockReport, movementAudit, movementSummary] = await Promise.all([
        firstValueFrom(this.reportsApi.getStockReport()),
        firstValueFrom(this.reportsApi.getLowStockReport(threshold)),
        firstValueFrom(this.reportsApi.getMovementAudit(fromInstant, toInstant)),
        firstValueFrom(this.reportsApi.getMovementSummary(fromInstant, toInstant)),
      ]);

      this.stockState.set(collectionState(stockReport, 'No hay productos para el reporte general.'));
      this.lowStockState.set(
        collectionState(lowStockReport, 'No hay productos por debajo del threshold actual.'),
      );
      this.auditState.set(collectionState(movementAudit, 'No hay movimientos auditables en ese rango.'));
      this.summaryState.set(collectionState(movementSummary, 'No hay datos agregados para resumir.'));
    } catch (error) {
      const message = extractErrorMessage(error);
      this.feedback.set({
        tone: 'danger',
        title: 'No fue posible generar los reportes',
        message,
      });
      this.stockState.set(errorState([], message));
      this.lowStockState.set(errorState([], message));
      this.auditState.set(errorState([], message));
      this.summaryState.set(errorState([], message));
    }
  }
}

function toDateTimeLocal(date: Date): string {
  const timezoneOffset = date.getTimezoneOffset() * 60_000;
  return new Date(date.getTime() - timezoneOffset).toISOString().slice(0, 16);
}
