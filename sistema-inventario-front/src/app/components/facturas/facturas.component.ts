import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe, DecimalPipe } from '@angular/common';
import { ApiService, Factura } from '../../services/api.service';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-facturas',
  standalone: true,
  imports: [RouterLink, DatePipe, DecimalPipe, LucideAngularModule],
  template: `
    <div class="page-header">
      <h1 class="page-title">
        <lucide-icon name="file-text" [size]="24"></lucide-icon>
        Facturas
      </h1>
      <a routerLink="/facturas/nueva" class="btn btn-primary">
        <lucide-icon name="plus" [size]="16"></lucide-icon> Nueva factura
      </a>
    </div>

    <div class="card table-container">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Fecha</th>
            <th>Cliente</th>
            <th>Vendedor</th>
            <th>Total</th>
            <th>Items</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          @for (f of facturas; track f.id) {
            <tr>
              <td class="td-id">#{{ f.id }}</td>
              <td>{{ f.fecha | date:'dd/MM/yyyy' }}</td>
              <td><strong>{{ f.cliente?.nombre || '-' }}</strong></td>
              <td>{{ f.usuario?.nombre || '-' }}</td>
              <td class="td-price">\${{ f.total | number:'1.0-0' }}</td>
              <td>
                <span class="badge badge-entrada">{{ f.items.length }} items</span>
              </td>
              <td>
                <div class="actions">
                  <button class="btn-icon btn-icon-delete" title="Anular factura" (click)="anular(f.id!)">
                    <lucide-icon name="ban" [size]="15"></lucide-icon>
                  </button>
                </div>
              </td>
            </tr>
          } @empty {
            <tr>
              <td colspan="7" class="empty">
                <lucide-icon name="file-text" [size]="32"></lucide-icon>
                <p>No hay facturas registradas</p>
              </td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .page-header {
      display: flex; align-items: center; justify-content: space-between;
      margin-bottom: 20px; flex-wrap: wrap; gap: 12px;
    }
    .page-title { margin-bottom: 0; }
    .td-id { color: var(--text-light); font-size: 0.8rem; }
    .td-price { font-weight: 600; color: var(--primary-dark); }
    .actions { display: flex; gap: 6px; }
    .btn-icon {
      width: 32px; height: 32px; border: none; border-radius: 8px;
      display: flex; align-items: center; justify-content: center;
      cursor: pointer; transition: all 0.2s; background: transparent;
    }
    .btn-icon-delete { color: var(--danger); background: var(--danger-light); }
    .btn-icon-delete:hover { background: var(--danger); color: white; }
    .empty { text-align: center; color: var(--text-light); padding: 48px 16px !important; }
    .empty lucide-icon { opacity: 0.3; margin-bottom: 8px; }
    .empty p { font-size: 0.9rem; }

    @media (max-width: 768px) {
      .page-header { flex-direction: column; align-items: flex-start; }
    }
  `]
})
export class FacturasComponent implements OnInit {
  facturas: Factura[] = [];

  constructor(private api: ApiService) {}
  ngOnInit() { this.cargar(); }

  cargar() { this.api.getAll<Factura>('facturas').subscribe(d => this.facturas = d); }

  anular(id: number) {
    if (confirm('¿Anular esta factura? Se devolverá el stock.')) {
      this.api.anularFactura(id).subscribe(() => this.cargar());
    }
  }
}
