import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';

import { Category } from '../../../core/models/category.models';
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
import { EmptyState } from '../../../shared/components/empty-state/empty-state';
import { LoadingPanel } from '../../../shared/components/loading-panel/loading-panel';
import { NoticeBanner } from '../../../shared/components/notice-banner/notice-banner';

@Component({
  selector: 'app-categories-page',
  imports: [DatePipe, ReactiveFormsModule, EmptyState, LoadingPanel, NoticeBanner],
  templateUrl: './categories-page.html',
  styleUrl: './categories-page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CategoriesPage implements OnInit {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly categoriesApi = inject(CategoriesApi);

  readonly selectedCategoryId = signal<string | null>(null);
  readonly submitting = signal(false);
  readonly busyCategoryId = signal<string | null>(null);
  readonly feedback = signal<BannerMessage | null>(null);
  readonly categoriesState = signal<RequestState<Category[]>>(idleState([]));
  readonly isEditing = computed(() => this.selectedCategoryId() !== null);
  readonly form = this.formBuilder.group({
    code: ['', [Validators.required, Validators.maxLength(50)]],
    name: ['', [Validators.required, Validators.maxLength(120)]],
    description: [''],
  });

  ngOnInit(): void {
    void this.loadCategories();
  }

  async loadCategories(): Promise<void> {
    this.categoriesState.set(loadingState(this.categoriesState().data));

    try {
      const categories = await firstValueFrom(this.categoriesApi.listCategories());
      this.categoriesState.set(collectionState(categories, 'Aun no hay categorias creadas.'));
    } catch (error) {
      this.categoriesState.set(errorState([], extractErrorMessage(error)));
    }
  }

  editCategory(category: Category): void {
    this.selectedCategoryId.set(category.id);
    this.form.controls.code.disable();
    this.form.setValue({
      code: category.code,
      name: category.name,
      description: category.description ?? '',
    });
  }

  resetForm(): void {
    this.selectedCategoryId.set(null);
    this.form.reset({
      code: '',
      name: '',
      description: '',
    });
    this.form.controls.code.enable();
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
      if (this.selectedCategoryId()) {
        await firstValueFrom(
          this.categoriesApi.updateCategory(this.selectedCategoryId()!, {
            name: payload.name,
            description: payload.description,
          }),
        );
        this.feedback.set({
          tone: 'success',
          title: 'Categoria actualizada',
          message: 'Los cambios se guardaron en category-service.',
        });
      } else {
        await firstValueFrom(this.categoriesApi.createCategory(payload));
        this.feedback.set({
          tone: 'success',
          title: 'Categoria creada',
          message: 'La nueva categoria ya esta disponible para productos.',
        });
      }

      this.resetForm();
      await this.loadCategories();
    } catch (error) {
      this.feedback.set({
        tone: 'danger',
        title: 'No fue posible guardar la categoria',
        message: extractErrorMessage(error),
      });
    } finally {
      this.submitting.set(false);
    }
  }

  async toggleStatus(category: Category): Promise<void> {
    this.busyCategoryId.set(category.id);
    this.feedback.set(null);

    try {
      await firstValueFrom(this.categoriesApi.updateStatus(category.id, !category.active));
      this.feedback.set({
        tone: 'success',
        title: 'Estado actualizado',
        message: `La categoria ${category.name} fue ${category.active ? 'desactivada' : 'reactivada'}.`,
      });
      await this.loadCategories();
    } catch (error) {
      this.feedback.set({
        tone: 'danger',
        title: 'No fue posible actualizar el estado',
        message: extractErrorMessage(error),
      });
    } finally {
      this.busyCategoryId.set(null);
    }
  }
}
