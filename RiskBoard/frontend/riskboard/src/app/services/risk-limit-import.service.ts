import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ImportSummary } from '../models/import-summary.model';

@Injectable({ providedIn: 'root' })
export class RiskLimitImportService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/risk-limits';

  importCsv(file: File): Observable<ImportSummary> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ImportSummary>(`${this.baseUrl}/import`, formData);
  }
}