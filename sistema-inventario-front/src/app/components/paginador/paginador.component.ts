import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-paginador',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="paginador">
      <div class="paginador-info">
        Mostrando {{ start }}–{{ end }} de {{ totalItems }}
      </div>

      <div class="paginador-controles">
        <label class="paginador-size">
          Filas:
          <select [ngModel]="pageSize" (ngModelChange)="cambiarPageSize($event)">
            @for (size of pageSizes; track size) {
              <option [ngValue]="size">{{ size }}</option>
            }
          </select>
        </label>

        <button
          class="btn btn-secondary btn-sm"
          [disabled]="page <= 1"
          (click)="irA(page - 1)">
          Anterior
        </button>

        <span class="paginador-page">Página {{ page }} de {{ totalPages }}</span>

        <button
          class="btn btn-secondary btn-sm"
          [disabled]="page >= totalPages"
          (click)="irA(page + 1)">
          Siguiente
        </button>
      </div>
    </div>
  `,
  styles: [`
    .paginador {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      justify-content: space-between;
      gap: 12px;
      margin-top: 16px;
      padding-top: 12px;
      border-top: 1px solid var(--border);
      color: var(--text-light);
      font-size: 0.85rem;
    }
    .paginador-controles {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      gap: 8px;
    }
    .paginador-size {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      color: var(--text-light);
    }
    .paginador-size select {
      padding: 5px 8px;
      border: 1px solid var(--border);
      border-radius: 6px;
      background: var(--bg-card);
      color: var(--text);
      font-family: inherit;
      font-size: 0.85rem;
    }
    .paginador-page {
      color: var(--text);
      font-weight: 600;
    }
    .paginador button:disabled {
      opacity: 0.45;
      cursor: not-allowed;
    }
  `]
})
export class PaginadorComponent {
  @Input() totalItems = 0;
  @Input() page = 1;
  @Input() pageSize = 10;
  @Output() pageChange = new EventEmitter<number>();
  @Output() pageSizeChange = new EventEmitter<number>();

  readonly pageSizes = [5, 10, 20, 50];

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.totalItems / this.pageSize));
  }

  get start(): number {
    return this.totalItems === 0 ? 0 : (this.page - 1) * this.pageSize + 1;
  }

  get end(): number {
    return Math.min(this.page * this.pageSize, this.totalItems);
  }

  irA(page: number) {
    const next = Math.min(Math.max(1, page), this.totalPages);
    if (next !== this.page) {
      this.pageChange.emit(next);
    }
  }

  cambiarPageSize(size: number) {
    if (size !== this.pageSize) {
      this.pageSizeChange.emit(size);
    }
  }
}
