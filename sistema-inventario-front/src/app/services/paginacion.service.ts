import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class PaginacionService {
  readonly pageSizes: number[] = [5, 10, 20, 50];
  private readonly storageKey = 'pageSize';

  getPageSize(): number {
    const value = Number(localStorage.getItem(this.storageKey));
    return this.pageSizes.includes(value) ? value : 10;
  }

  setPageSize(size: number): void {
    if (this.pageSizes.includes(size)) {
      localStorage.setItem(this.storageKey, String(size));
    }
  }

  paginar<T>(items: T[], page: number, pageSize: number): T[] {
    const start = (page - 1) * pageSize;
    return items.slice(start, start + pageSize);
  }

  totalPages(totalItems: number, pageSize: number): number {
    return Math.max(1, Math.ceil(totalItems / pageSize));
  }

  clampPage(page: number, totalItems: number, pageSize: number): number {
    return Math.min(Math.max(1, page), this.totalPages(totalItems, pageSize));
  }
}
