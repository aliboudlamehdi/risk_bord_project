export type LimitType = 'CREDIT' | 'MARKET' | 'LIQUIDITY';
export type AlertLevel = 'GREEN' | 'ORANGE' | 'RED';

export interface RiskLimit {
  id: number;
  counterpartyId: number;
  counterpartyName: string;
  sector: string;
  country: string;
  limitType: LimitType;
  maxAmount: number;
  usedAmount: number;
  currency: string;
  usageRate: number;
  alertLevel: AlertLevel;
  lastUpdated: string;
}

export interface SectorExposure {
  limitType: LimitType;
  sector: string;
  totalUsed: number;
}