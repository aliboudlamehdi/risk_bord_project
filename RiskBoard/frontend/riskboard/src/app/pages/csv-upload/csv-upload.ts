import { Component, ElementRef, ViewChild, inject, signal } from '@angular/core';
import { ImportSummary } from '../../models/import-summary.model';
import { RiskLimitImportService } from '../../services/risk-limit-import.service';

@Component({
  selector: 'app-csv-upload',
  imports: [],
  templateUrl: './csv-upload.html',
  styleUrl: './csv-upload.scss',
})
export class CsvUpload {
  private readonly importService = inject(RiskLimitImportService);

  @ViewChild('fileInput') private fileInputRef?: ElementRef<HTMLInputElement>;

  readonly selectedFile = signal<File | null>(null);
  readonly uploading = signal(false);
  readonly result = signal<ImportSummary | null>(null);
  readonly error = signal<string | null>(null);

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile.set(input.files?.[0] ?? null);
    this.result.set(null);
    this.error.set(null);
  }

  upload(): void {
    const file = this.selectedFile();
    if (!file) {
      return;
    }

    this.uploading.set(true);
    this.error.set(null);
    this.result.set(null);

    this.importService.importCsv(file).subscribe({
      next: (summary) => {
        this.result.set(summary);
        this.uploading.set(false);
        this.resetFileInput();
      },
      error: () => {
        this.error.set("Erreur lors de l'import du fichier");
        this.uploading.set(false);
      },
    });
  }

  private resetFileInput(): void {
    this.selectedFile.set(null);
    if (this.fileInputRef) {
      this.fileInputRef.nativeElement.value = '';
    }
  }
}