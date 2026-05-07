import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';

import { InventoryMovement } from '../../../core/models/inventory.models';
import { Product } from '../../../core/models/product.models';
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
import { ProductsApi } from '../../../core/services/products-api';
import { EmptyState } from '../../../shared/components/empty-state/empty-state';
import { LoadingPanel } from '../../../shared/components/loading-panel/loading-panel';
import { NoticeBanner } from '../../../shared/components/notice-banner/notice-banner';
import { SectionShell } from '../../../shared/components/section-shell/section-shell';

@Component({
  selector: 'app-movements-page',
  imports: [DatePipe, ReactiveFormsModule, EmptyState, LoadingPanel, NoticeBanner, SectionShell],
  templateUrl: './movements-page.html',
  styleUrl: './movements-page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MovementsPage implements OnInit {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly inventoryApi = inject(InventoryApi);
  private readonly productsApi = inject(ProductsApi);

  readonly products = signal<Product[]>([]);
  readonly feedback = signal<BannerMessage | null>(null);
  readonly historyState = signal<RequestState<InventoryMovement[]>>(idleState([]));
  readonly submittingEntry = signal(false);
  readonly submittingExit = signal(false);
  readonly entryForm = this.formBuilder.group({
    productId: ['', [Validators.required]],
    quantity: [1, [Validators.required, Validators.min(1)]],
    reference: [''],
    notes: [''],
  });
  readonly exitForm = this.formBuilder.group({
    productId: ['', [Validators.required]],
    quantity: [1, [Validators.required, Validators.min(1)]],
    reference: [''],
    notes: [''],
  });
  readonly rangeForm = this.formBuilder.group({
    from: [toDateTimeLocal(new Date(Date.now() - 7 * 24 * 60 * 60 * 1000)), [Validators.required]],
    to: [toDateTimeLocal(new Date()), [Validators.required]],
  });

  ngOnInit(): void {
    void this.loadProducts();
    void this.loadHistory();
  }

  async loadProducts(): Promise<void> {
    try {
      const products = await firstValueFrom(this.productsApi.listProducts());
      this.products.set(products.filter((product) => product.active));
    } catch (error) {
      this.feedback.set({
        tone: 'danger',
        title: 'No fue posible cargar productos',
        message: extractErrorMessage(error),
      });
    }
  }

  async loadHistory(): Promise<void> {
    if (this.rangeForm.invalid) {
      return;
    }

    this.historyState.set(loadingState(this.historyState().data));

    try {
      const { from, to } = this.rangeForm.getRawValue();
      const movements = await firstValueFrom(
        this.inventoryApi.getMovements({
          from: new Date(from).toISOString(),
          to: new Date(to).toISOString(),
        }),
      );
      this.historyState.set(collectionState(movements, 'No hay movimientos en el rango indicado.'));
    } catch (error) {
      this.historyState.set(errorState([], extractErrorMessage(error)));
    }
  }

  async submitEntry(): Promise<void> {
    if (this.entryForm.invalid || this.submittingEntry()) {
      this.entryForm.markAllAsTouched();
      return;
    }

    this.submittingEntry.set(true);
    this.feedback.set(null);

    try {
      await firstValueFrom(this.inventoryApi.registerEntry(this.entryForm.getRawValue()));
      this.feedback.set({
        tone: 'success',
        title: 'Entrada registrada',
        message: 'La entrada quedo reflejada en inventario.',
      });
      this.entryForm.patchValue({ quantity: 1, reference: '', notes: '' });
      await this.loadHistory();
    } catch (error) {
      this.feedback.set({
        tone: 'danger',
        title: 'No fue posible registrar la entrada',
        message: extractErrorMessage(error),
      });
    } finally {
      this.submittingEntry.set(false);
    }
  }

  async submitExit(): Promise<void> {
    if (this.exitForm.invalid || this.submittingExit()) {
      this.exitForm.markAllAsTouched();
      return;
    }

    this.submittingExit.set(true);
    this.feedback.set(null);

    try {
      await firstValueFrom(this.inventoryApi.registerExit(this.exitForm.getRawValue()));
      this.feedback.set({
        tone: 'success',
        title: 'Salida registrada',
        message: 'La salida quedo reflejada en inventario.',
      });
      this.exitForm.patchValue({ quantity: 1, reference: '', notes: '' });
      await this.loadHistory();
    } catch (error) {
      this.feedback.set({
        tone: 'danger',
        title: 'No fue posible registrar la salida',
        message: extractErrorMessage(error),
      });
    } finally {
      this.submittingExit.set(false);
    }
  }
}

function toDateTimeLocal(date: Date): string {
  const timezoneOffset = date.getTimezoneOffset() * 60_000;
  return new Date(date.getTime() - timezoneOffset).toISOString().slice(0, 16);
}
