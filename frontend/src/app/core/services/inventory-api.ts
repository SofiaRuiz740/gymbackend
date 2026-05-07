import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { environment } from '../../../environments/environment';
import {
  InventoryMovement,
  MovementRange,
  RegisterInventoryMovementRequest,
  StockItem,
} from '../models/inventory.models';

@Injectable({
  providedIn: 'root',
})
export class InventoryApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiBaseUrl;

  getStock() {
    return this.http.get<StockItem[]>(`${this.baseUrl}/stock`);
  }

  getMovements(range: MovementRange = {}) {
    let params = new HttpParams();

    if (range.from) {
      params = params.set('from', range.from);
    }

    if (range.to) {
      params = params.set('to', range.to);
    }

    return this.http.get<InventoryMovement[]>(`${this.baseUrl}/movements`, { params });
  }

  registerEntry(payload: RegisterInventoryMovementRequest) {
    return this.http.post<InventoryMovement>(`${this.baseUrl}/movements/entries`, payload);
  }

  registerExit(payload: RegisterInventoryMovementRequest) {
    return this.http.post<InventoryMovement>(`${this.baseUrl}/movements/exits`, payload);
  }
}
