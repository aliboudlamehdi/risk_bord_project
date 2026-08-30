import { LimitType } from './risk-limit.model';

export type DerogationStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface Counterparty {
  id: number;
  name: string;
  ricosCode: string;
  country: string;
  sector: string;
}

export interface LimitCheck {
  limitExists: boolean;
  maxAmount: number | null;
  amountValid: boolean;
}

export interface CreateDerogationRequest {
  counterpartyId: number;
  limitType: LimitType;
  amount: number;
  reason: string;
  requestedBy: string;
}

export interface DerogationRequestDto {
  id: number;
  counterpartyId: number;
  counterpartyName: string;
  limitType: LimitType;
  amount: number;
  reason: string;
  requestedBy: string;
  status: DerogationStatus;
  createdAt: string;
}