import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { environment } from '../../../environments/environment';
import { CreateProductRequest, Product, UpdateProductRequest } from '../models/product.models';

@Injectable({
  providedIn: 'root',
})
export class ProductsApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/products`;

  listProducts() {
    return this.http.get<Product[]>(this.baseUrl);
  }

  createProduct(payload: CreateProductRequest) {
    return this.http.post<Product>(this.baseUrl, payload);
  }

  updateProduct(productId: string, payload: UpdateProductRequest) {
    return this.http.put<Product>(`${this.baseUrl}/${productId}`, payload);
  }

  updateStatus(productId: string, active: boolean) {
    return this.http.patch<Product>(`${this.baseUrl}/${productId}/status`, { active });
  }
}
