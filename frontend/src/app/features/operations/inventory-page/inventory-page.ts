import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';

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
import { StockItem } from '../../../core/models/inventory.models';
import { InventoryApi } from '../../../core/services/inventory-api';
import { ProductsApi } from '../../../core/services/products-api';
import { EmptyState } from '../../../shared/components/empty-state/empty-state';
import { LoadingPanel } from '../../../shared/components/loading-panel/loading-panel';
import { NoticeBanner } from '../../../shared/components/notice-banner/notice-banner';
import { SectionShell } from '../../../shared/components/section-shell/section-shell';

@Component({
  selector: 'app-inventory-page',
  imports: [DatePipe, ReactiveFormsModule, EmptyState, LoadingPanel, NoticeBanner, SectionShell],
  templateUrl: './inventory-page.html',
  styleUrl: './inventory-page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InventoryPage implements OnInit {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly inventoryApi = inject(InventoryApi);
  private readonly productsApi = inject(ProductsApi);

  readonly products = signal<Product[]>([]);
  readonly feedback = signal<BannerMessage | null>(null);
  readonly submitting = signal(false);
  readonly stockState = signal<RequestState<StockItem[]>>(idleState([]));
  readonly form = this.formBuilder.group({
    productId: ['', [Validators.required]],
    movementType: ['ENTRY', [Validators.required]],
    quantity: [1, [Validators.required, Validators.min(1)]],
    reference: [''],
    notes: [''],
  });
  readonly hasProducts = computed(() => this.products().length > 0);

  ngOnInit(): void {
    void this.loadData();
  }

  async loadData(): Promise<void> {
    this.stockState.set(loadingState(this.stockState().data));

    try {
      const [products, stock] = await Promise.all([
        firstValueFrom(this.productsApi.listProducts()),
        firstValueFrom(this.inventoryApi.getStock()),
      ]);

      this.products.set(products.filter((product) => product.active));
      this.stockState.set(collectionState(stock, 'Todavia no hay registros de stock.'));
    } catch (error) {
      this.stockState.set(errorState([], extractErrorMessage(error)));
    }
  }

  async submitMovement(): Promise<void> {
    if (this.form.invalid || this.submitting()) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.feedback.set(null);

    try {
      const payload = this.form.getRawValue();
      if (payload.movementType === 'ENTRY') {
        await firstValueFrom(
          this.inventoryApi.registerEntry({
            productId: payload.productId,
            quantity: payload.quantity,
            reference: payload.reference,
            notes: payload.notes,
          }),
        );
      } else {
        await firstValueFrom(
          this.inventoryApi.registerExit({
            productId: payload.productId,
            quantity: payload.quantity,
            reference: payload.reference,
            notes: payload.notes,
          }),
        );
      }

      this.feedback.set({
        tone: 'success',
        title: 'Movimiento registrado',
        message:
          payload.movementType === 'ENTRY'
            ? 'La entrada quedo registrada correctamente.'
            : 'La salida quedo registrada correctamente.',
      });
      this.form.patchValue({
        quantity: 1,
        reference: '',
        notes: '',
      });
      await this.loadData();
    } catch (error) {
      this.feedback.set({
        tone: 'danger',
        title: 'No fue posible registrar el movimiento',
        message: extractErrorMessage(error),
      });
    } finally {
      this.submitting.set(false);
    }
  }
}
