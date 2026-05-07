import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { environment } from '../../../environments/environment';
import {
  MovementAuditRecord,
  MovementSummaryRecord,
  ReportProduct,
} from '../models/report.models';

@Injectable({
  providedIn: 'root',
})
export class ReportsApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/reports`;

  getStockReport() {
    return this.http.get<ReportProduct[]>(`${this.baseUrl}/stock`);
  }

  getLowStockReport(threshold = 10) {
    const params = new HttpParams().set('threshold', threshold);
    return this.http.get<ReportProduct[]>(`${this.baseUrl}/low-stock`, { params });
  }

  getMovementAudit(from: string, to: string) {
    const params = new HttpParams().set('from', from).set('to', to);
    return this.http.get<MovementAuditRecord[]>(`${this.baseUrl}/movements`, { params });
  }

  getMovementSummary(from: string, to: string) {
    const params = new HttpParams().set('from', from).set('to', to);
    return this.http.get<MovementSummaryRecord[]>(`${this.baseUrl}/movements/summary`, {
      params,
    });
  }
}
