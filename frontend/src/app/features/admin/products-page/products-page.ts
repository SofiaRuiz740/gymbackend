import { CurrencyPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';

import { Category } from '../../../core/models/category.models';
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
import { CategoriesApi } from '../../../core/services/categories-api';
import { ProductsApi } from '../../../core/services/products-api';
import { EmptyState } from '../../../shared/components/empty-state/empty-state';
import { LoadingPanel } from '../../../shared/components/loading-panel/loading-panel';
import { NoticeBanner } from '../../../shared/components/notice-banner/notice-banner';

@Component({
  selector: 'app-products-page',
  imports: [
    CurrencyPipe,
    ReactiveFormsModule,
    EmptyState,
    LoadingPanel,
    NoticeBanner,
  ],
  templateUrl: './products-page.html',
  styleUrl: './products-page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProductsPage implements OnInit {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly categoriesApi = inject(CategoriesApi);
  private readonly productsApi = inject(ProductsApi);

  readonly categories = signal<Category[]>([]);
  readonly productsState = signal<RequestState<Product[]>>(idleState([]));
  readonly selectedProductId = signal<string | null>(null);
  readonly feedback = signal<BannerMessage | null>(null);
  readonly submitting = signal(false);
  readonly busyProductId = signal<string | null>(null);
  readonly isEditing = computed(() => this.selectedProductId() !== null);
  readonly hasActiveCategories = computed(() => this.categories().some((category) => category.active));
  readonly productTypes = ['SUPPLEMENT', 'EQUIPMENT', 'ACCESSORY', 'DRINK', 'MERCH'];
  readonly form = this.formBuilder.group({
    sku: ['', [Validators.required, Validators.maxLength(50)]],
    name: ['', [Validators.required, Validators.maxLength(120)]],
    description: [''],
    categoryId: ['', [Validators.required]],
    unitPrice: [0, [Validators.required, Validators.min(0)]],
    brand: ['', [Validators.required, Validators.maxLength(80)]],
    productType: ['SUPPLEMENT', [Validators.required]],
  });

  ngOnInit(): void {
    void this.loadData();
  }

  async loadData(): Promise<void> {
    this.productsState.set(loadingState(this.productsState().data));

    try {
      const [categories, products] = await Promise.all([
        firstValueFrom(this.categoriesApi.listCategories()),
        firstValueFrom(this.productsApi.listProducts()),
      ]);

      this.categories.set(categories);
      this.productsState.set(collectionState(products, 'Todavia no hay productos registrados.'));
    } catch (error) {
      this.productsState.set(errorState([], extractErrorMessage(error)));
    }
  }

  editProduct(product: Product): void {
    this.selectedProductId.set(product.id);
    this.form.controls.sku.disable();
    this.form.setValue({
      sku: product.sku,
      name: product.name,
      description: product.description ?? '',
      categoryId: product.categoryId,
      unitPrice: Number(product.unitPrice),
      brand: product.brand,
      productType: product.productType,
    });
  }

  resetForm(): void {
    this.selectedProductId.set(null);
    this.form.reset({
      sku: '',
      name: '',
      description: '',
      categoryId: '',
      unitPrice: 0,
      brand: '',
      productType: 'SUPPLEMENT',
    });
    this.form.controls.sku.enable();
  }

  async submit(): Promise<void> {
    if (this.form.invalid || this.submitting()) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.feedback.set(null);

    try {
      const payload = this.form.getRawValue();

      if (this.selectedProductId()) {
        await firstValueFrom(
          this.productsApi.updateProduct(this.selectedProductId()!, {
            name: payload.name,
            description: payload.description,
            categoryId: payload.categoryId,
            unitPrice: payload.unitPrice,
            brand: payload.brand,
            productType: payload.productType,
          }),
        );
        this.feedback.set({
          tone: 'success',
          title: 'Producto actualizado',
          message: 'Los cambios quedaron sincronizados con product-service.',
        });
      } else {
        await firstValueFrom(this.productsApi.createProduct(payload));
        this.feedback.set({
          tone: 'success',
          title: 'Producto creado',
          message: 'El nuevo SKU ya esta listo para operaciones de inventario.',
        });
      }

      this.resetForm();
      await this.loadData();
    } catch (error) {
      this.feedback.set({
        tone: 'danger',
        title: 'No fue posible guardar el producto',
        message: extractErrorMessage(error),
      });
    } finally {
      this.submitting.set(false);
    }
  }

  async toggleStatus(product: Product): Promise<void> {
    this.busyProductId.set(product.id);
    this.feedback.set(null);

    try {
      await firstValueFrom(this.productsApi.updateStatus(product.id, !product.active));
      this.feedback.set({
        tone: 'success',
        title: 'Estado actualizado',
        message: `El producto ${product.name} fue ${product.active ? 'desactivado' : 'reactivado'}.`,
      });
      await this.loadData();
    } catch (error) {
      this.feedback.set({
        tone: 'danger',
        title: 'No fue posible actualizar el estado',
        message: extractErrorMessage(error),
      });
    } finally {
      this.busyProductId.set(null);
    }
  }
}
