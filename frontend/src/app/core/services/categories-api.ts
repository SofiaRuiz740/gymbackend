import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { environment } from '../../../environments/environment';
import { Category, CreateCategoryRequest, UpdateCategoryRequest } from '../models/category.models';

@Injectable({
  providedIn: 'root',
})
export class CategoriesApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/categories`;

  listCategories() {
    return this.http.get<Category[]>(this.baseUrl);
  }

  createCategory(payload: CreateCategoryRequest) {
    return this.http.post<Category>(this.baseUrl, payload);
  }

  updateCategory(categoryId: string, payload: UpdateCategoryRequest) {
    return this.http.put<Category>(`${this.baseUrl}/${categoryId}`, payload);
  }

  updateStatus(categoryId: string, active: boolean) {
    return this.http.patch<Category>(`${this.baseUrl}/${categoryId}/status`, { active });
  }
}
