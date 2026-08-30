import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { LimitType } from '../models/risk-limit.model';
import {
  CreateDerogationRequest,
  DerogationRequestDto,
  DerogationStatus,
  LimitCheck,
} from '../models/derogation-request.model';

@Injectable({ providedIn: 'root' })
export class DerogationRequestService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/derogation-requests';

  checkLimit(counterpartyId: number, limitType: LimitType, amount?: number | null): Observable<LimitCheck> {
    const params: Record<string, string | number> = { counterpartyId, limitType };
    if (amount !== undefined && amount !== null && amount > 0) {
      params['amount'] = amount;
    }
    return this.http.get<LimitCheck>(`${this.baseUrl}/limit-check`, { params });
  }

  create(dto: CreateDerogationRequest): Observable<DerogationRequestDto> {
    return this.http.post<DerogationRequestDto>(this.baseUrl, dto);
  }

  findAll(status?: DerogationStatus): Observable<DerogationRequestDto[]> {
    const params: Record<string, string> = {};
    if (status) {
      params['status'] = status;
    }
    return this.http.get<DerogationRequestDto[]>(this.baseUrl, { params });
  }

  approve(id: number): Observable<DerogationRequestDto> {
    return this.http.patch<DerogationRequestDto>(`${this.baseUrl}/${id}/approve`, {});
  }

  reject(id: number): Observable<DerogationRequestDto> {
    return this.http.patch<DerogationRequestDto>(`${this.baseUrl}/${id}/reject`, {});
  }
}