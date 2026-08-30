import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Counterparty } from '../models/derogation-request.model';

@Injectable({ providedIn: 'root' })
export class CounterpartyService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<Counterparty[]> {
    return this.http.get<Counterparty[]>('/api/counterparties');
  }
}