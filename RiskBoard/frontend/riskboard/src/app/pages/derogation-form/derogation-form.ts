import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  AsyncValidatorFn,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { catchError, map, of } from 'rxjs';
import { LimitType } from '../../models/risk-limit.model';
import { Counterparty } from '../../models/derogation-request.model';
import { CounterpartyService } from '../../services/counterparty.service';
import { DerogationRequestService } from '../../services/derogation-request.service';

function positiveAmountValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  if (value === null || value === undefined || value === '') {
    return null;
  }
  return Number(value) > 0 ? null : { notPositive: true };
}

@Component({
  selector: 'app-derogation-form',
  imports: [ReactiveFormsModule],
  templateUrl: './derogation-form.html',
  styleUrl: './derogation-form.scss',
})
export class DerogationForm {
  private readonly counterpartyService = inject(CounterpartyService);
  private readonly derogationRequestService = inject(DerogationRequestService);

  readonly limitTypes: LimitType[] = ['CREDIT', 'MARKET', 'LIQUIDITY'];
  readonly counterparties = signal<Counterparty[]>([]);
  readonly submitting = signal(false);
  readonly submitError = signal<string | null>(null);
  readonly submitSuccess = signal(false);

  // Runs as soon as counterparty + type are both picked, independent of the amount field.
  private readonly limitExistsValidator: AsyncValidatorFn = (group: AbstractControl) => {
    const counterpartyId = group.get('counterpartyId')?.value;
    const limitType = group.get('limitType')?.value;

    if (!counterpartyId || !limitType) {
      return of(null);
    }

    return this.derogationRequestService.checkLimit(counterpartyId, limitType).pipe(
      map((result) => (result.limitExists ? null : { limitNotFound: true })),
      catchError(() => of({ limitCheckFailed: true })),
    );
  };

  // Runs once the amount itself is a valid positive number, reusing the sibling counterparty/type values.
  private readonly amountThresholdValidator: AsyncValidatorFn = (control: AbstractControl) => {
    const amount = control.value;
    if (!amount || amount <= 0) {
      return of(null);
    }

    const limitSelection = control.parent?.get('limitSelection');
    const counterpartyId = limitSelection?.get('counterpartyId')?.value;
    const limitType = limitSelection?.get('limitType')?.value;
    if (!counterpartyId || !limitType) {
      return of(null);
    }

    return this.derogationRequestService.checkLimit(counterpartyId, limitType, amount).pipe(
      map((result) =>
        result.limitExists && !result.amountValid ? { amountExceedsThreshold: { maxAmount: result.maxAmount } } : null,
      ),
      catchError(() => of({ limitCheckFailed: true })),
    );
  };

  readonly form = new FormGroup({
    limitSelection: new FormGroup(
      {
        counterpartyId: new FormControl<number | null>(null, { validators: [Validators.required] }),
        limitType: new FormControl<LimitType | null>(null, { validators: [Validators.required] }),
      },
      { asyncValidators: [this.limitExistsValidator] },
    ),
    amount: new FormControl<number | null>(null, {
      validators: [Validators.required, positiveAmountValidator],
      asyncValidators: [this.amountThresholdValidator],
      updateOn: 'blur',
    }),
    reason: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(20)],
    }),
    requestedBy: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(6)],
    }),
  });

  constructor() {
    this.counterpartyService.getAll().subscribe((data) => this.counterparties.set(data));
  }

  get limitSelection() {
    return this.form.controls.limitSelection;
  }

  onSubmit(): void {
    if (this.form.invalid || this.submitting()) {
      return;
    }

    const { limitSelection, amount, reason, requestedBy } = this.form.getRawValue();
    this.submitting.set(true);
    this.submitError.set(null);
    this.submitSuccess.set(false);

    this.derogationRequestService
      .create({
        counterpartyId: limitSelection.counterpartyId!,
        limitType: limitSelection.limitType!,
        amount: amount!,
        reason,
        requestedBy,
      })
      .subscribe({
        next: () => {
          this.submitting.set(false);
          this.submitSuccess.set(true);
          this.form.reset();
        },
        error: (err) => {
          this.submitting.set(false);
          this.submitError.set(err?.error?.message ?? 'Erreur lors de la création de la demande');
        },
      });
  }
}