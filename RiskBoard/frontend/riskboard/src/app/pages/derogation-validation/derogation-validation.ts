import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { DerogationRequestDto } from '../../models/derogation-request.model';
import { DerogationRequestService } from '../../services/derogation-request.service';

@Component({
  selector: 'app-derogation-validation',
  imports: [DatePipe, DecimalPipe],
  templateUrl: './derogation-validation.html',
  styleUrl: './derogation-validation.scss',
})
export class DerogationValidation implements OnInit {
  private readonly derogationRequestService = inject(DerogationRequestService);

  readonly requests = signal<DerogationRequestDto[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly processingIds = signal<Set<number>>(new Set());

  ngOnInit(): void {
    this.loadPending();
  }

  loadPending(): void {
    this.loading.set(true);
    this.error.set(null);
    this.derogationRequestService.findAll('PENDING').subscribe({
      next: (data) => {
        this.requests.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les demandes en attente');
        this.loading.set(false);
      },
    });
  }

  isProcessing(id: number): boolean {
    return this.processingIds().has(id);
  }

  approve(id: number): void {
    this.setProcessing(id, true);
    this.derogationRequestService.approve(id).subscribe({
      next: () => this.removeFromList(id),
      error: () => {
        this.error.set('Impossible de valider la demande');
        this.setProcessing(id, false);
      },
    });
  }

  reject(id: number): void {
    this.setProcessing(id, true);
    this.derogationRequestService.reject(id).subscribe({
      next: () => this.removeFromList(id),
      error: () => {
        this.error.set('Impossible de rejeter la demande');
        this.setProcessing(id, false);
      },
    });
  }

  private setProcessing(id: number, processing: boolean): void {
    const next = new Set(this.processingIds());
    if (processing) {
      next.add(id);
    } else {
      next.delete(id);
    }
    this.processingIds.set(next);
  }

  private removeFromList(id: number): void {
    this.requests.set(this.requests().filter((request) => request.id !== id));
    this.setProcessing(id, false);
  }
}