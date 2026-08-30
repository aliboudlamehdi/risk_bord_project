import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { LimitType, RiskLimit, SectorExposure } from '../models/risk-limit.model';

@Injectable({ providedIn: 'root' })
export class RiskLimitService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/risk-limits';

  getAll(): Observable<RiskLimit[]> {
    return this.http.get<RiskLimit[]>(this.baseUrl);
  }

  getSectorExposure(limitType: LimitType): Observable<SectorExposure[]> {
    return this.http.get<SectorExposure[]>(`${this.baseUrl}/sector-exposure`, {
      params: { limitType },
    });
  }
}