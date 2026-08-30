import { DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LimitType, RiskLimit, SectorExposure } from '../../models/risk-limit.model';
import { RiskLimitService } from '../../services/risk-limit.service';

type SortColumn =
  | 'counterpartyName'
  | 'limitType'
  | 'sector'
  | 'maxAmount'
  | 'usedAmount'
  | 'usageRate'
  | 'alertLevel';

type SortDirection = 'asc' | 'desc';
type ViewMode = 'DETAIL' | LimitType;

const DEFAULT_SORT_CHAIN: { column: SortColumn; direction: SortDirection }[] = [
  { column: 'counterpartyName', direction: 'asc' },
  { column: 'limitType', direction: 'asc' },
  { column: 'sector', direction: 'asc' },
  { column: 'maxAmount', direction: 'desc' },
  { column: 'usedAmount', direction: 'desc' },
  { column: 'usageRate', direction: 'desc' },
  { column: 'alertLevel', direction: 'asc' },
];

const PAGE_SIZE = 10;

@Component({
  selector: 'app-dashboard',
  imports: [FormsModule, DecimalPipe],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  private readonly riskLimitService = inject(RiskLimitService);

  readonly limitTypes: LimitType[] = ['CREDIT', 'MARKET', 'LIQUIDITY'];
  readonly pageSize = PAGE_SIZE;

  readonly riskLimits = signal<RiskLimit[]>([]);
  readonly sectorExposures = signal<SectorExposure[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly nameFilter = signal('');
  readonly viewMode = signal<ViewMode>('DETAIL');
  readonly sortColumn = signal<SortColumn | null>(null);
  readonly sortDirection = signal<SortDirection>('asc');
  readonly currentPage = signal(1);

  readonly filteredAndSorted = computed(() => {
    const filter = this.nameFilter().trim().toLowerCase();
    const items = filter
      ? this.riskLimits().filter((item) => item.counterpartyName.toLowerCase().includes(filter))
      : this.riskLimits();
    return [...items].sort(this.comparator());
  });

  readonly totalPages = computed(() =>
    Math.max(1, Math.ceil(this.filteredAndSorted().length / this.pageSize)),
  );

  readonly pagedItems = computed(() => {
    const page = this.currentPage();
    const start = (page - 1) * this.pageSize;
    return this.filteredAndSorted().slice(start, start + this.pageSize);
  });

  ngOnInit(): void {
    this.loadRiskLimits();
  }

  loadRiskLimits(): void {
    this.loading.set(true);
    this.error.set(null);
    this.riskLimitService.getAll().subscribe({
      next: (data) => {
        this.riskLimits.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les limites de risque');
        this.loading.set(false);
      },
    });
  }

  onFilterChange(value: string): void {
    this.nameFilter.set(value);
    this.currentPage.set(1);
  }

  onViewModeChange(value: string): void {
    this.currentPage.set(1);
    if (value === 'DETAIL') {
      this.viewMode.set('DETAIL');
      return;
    }

    const limitType = value as LimitType;
    this.viewMode.set(limitType);
    this.riskLimitService.getSectorExposure(limitType).subscribe({
      next: (data) => this.sectorExposures.set(data),
      error: () => this.error.set("Impossible de charger l'exposition par secteur"),
    });
  }

  sortBy(column: SortColumn): void {
    if (this.sortColumn() === column) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortColumn.set(column);
      this.sortDirection.set('asc');
    }
    this.currentPage.set(1);
  }

  sortIndicator(column: SortColumn): string {
    if (this.sortColumn() !== column) {
      return '';
    }
    return this.sortDirection() === 'asc' ? '▲' : '▼';
  }

  goToPage(page: number): void {
    this.currentPage.set(Math.min(Math.max(1, page), this.totalPages()));
  }

  private comparator(): (a: RiskLimit, b: RiskLimit) => number {
    const column = this.sortColumn();
    if (column) {
      const direction = this.sortDirection();
      return (a, b) => this.compareValues(a, b, column, direction);
    }
    return (a, b) => {
      for (const step of DEFAULT_SORT_CHAIN) {
        const result = this.compareValues(a, b, step.column, step.direction);
        if (result !== 0) {
          return result;
        }
      }
      return 0;
    };
  }

  private compareValues(a: RiskLimit, b: RiskLimit, column: SortColumn, direction: SortDirection): number {
    const valueA = a[column];
    const valueB = b[column];
    const result =
      typeof valueA === 'number' && typeof valueB === 'number'
        ? valueA - valueB
        : String(valueA).localeCompare(String(valueB));
    return direction === 'asc' ? result : -result;
  }
}