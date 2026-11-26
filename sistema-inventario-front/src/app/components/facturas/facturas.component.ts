import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe, DecimalPipe } from '@angular/common';
import { ApiService, Factura } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { LucideAngularModule } from 'lucide-angular';
import { PaginadorComponent } from '../paginador/paginador.component';
import { PaginacionService } from '../../services/paginacion.service';

@Component({
  selector: 'app-facturas',
  standalone: true,
  imports: [RouterLink, DatePipe, DecimalPipe, LucideAngularModule, PaginadorComponent],
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

    @if (error) {
      <div class="alert-error">{{ error }}</div>
    }

    <div class="card">
      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Fecha</th>
              <th>Cliente</th>
              <th>Total</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            @for (f of facturasPagina; track f.id) {
              <tr [class.fila-anulada]="f.estado === 'ANULADA'">
                <td class="td-id">#{{ f.id }}</td>
                <td>{{ f.fecha | date:'dd/MM/yyyy' }}</td>
                <td><strong>{{ f.cliente?.nombre || '-' }}</strong></td>
                <td class="td-price">\${{ f.total | number:'1.0-0' }}</td>
                <td>
                  <span class="badge"
                        [class.badge-activa]="f.estado !== 'ANULADA'"
                        [class.badge-anulada]="f.estado === 'ANULADA'"
                        [title]="f.estado === 'ANULADA' && f.fechaAnulacion
                          ? 'Anulada el ' + (f.fechaAnulacion | date:'dd/MM/yyyy HH:mm')
                            + (f.usuarioAnulacion ? ' por ' + f.usuarioAnulacion : '')
                          : ''">
                    {{ f.estado === 'ANULADA' ? 'ANULADA' : 'ACTIVA' }}
                  </span>
                </td>
                <td>
                  <div class="actions">
                    <button class="btn-icon btn-icon-view" title="Ver detalle" (click)="abrirDetalle(f)">
                      <lucide-icon name="eye" [size]="15"></lucide-icon>
                    </button>
  
                    @if (auth.esAdmin()) {
                      @if (f.estado !== 'ANULADA') {
                        <button class="btn-icon btn-icon-delete" title="Anular factura" (click)="anular(f.id!)">
                          <lucide-icon name="ban" [size]="15"></lucide-icon>
                        </button>
                      }
                    }
                  </div>
                </td>
              </tr>
            } @empty {
              <tr>
                <td colspan="6" class="empty">
                  <lucide-icon name="file-text" [size]="32"></lucide-icon>
                  <p>No hay facturas registradas</p>
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>
      <app-paginador
        [totalItems]="facturas.length"
        [page]="pagina"
        [pageSize]="pageSize"
        (pageChange)="cambiarPagina($event)"
        (pageSizeChange)="cambiarPageSize($event)" />
    </div>

    @if (facturaSeleccionada; as f) {
      <div class="modal-overlay" (click)="cerrarDetalle()">
        <div class="modal-content modal-detalle" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>
              <lucide-icon name="eye" [size]="18"></lucide-icon>
              Detalle de factura #{{ f.id }}
            </h3>
            <button class="btn-icon" title="Cerrar" (click)="cerrarDetalle()">
              <lucide-icon name="x" [size]="18"></lucide-icon>
            </button>
          </div>

          <div class="detalle-resumen">
            <div>
              <span class="detalle-label">Cliente</span>
              <strong>{{ f.cliente?.nombre || '-' }}</strong>
            </div>
            <div>
              <span class="detalle-label">Fecha</span>
              <strong>{{ f.fecha | date:'dd/MM/yyyy' }}</strong>
            </div>
            <div>
              <span class="detalle-label">Estado</span>
              <span class="badge"
                    [class.badge-activa]="f.estado !== 'ANULADA'"
                    [class.badge-anulada]="f.estado === 'ANULADA'">
                {{ f.estado === 'ANULADA' ? 'ANULADA' : 'ACTIVA' }}
              </span>
            </div>
            <div>
              <span class="detalle-label">Total</span>
              <strong class="td-price">\${{ f.total | number:'1.0-0' }}</strong>
            </div>
          </div>

          <div class="table-container detalle-items">
            <table>
              <thead>
                <tr>
                  <th>Producto</th>
                  <th>Cantidad</th>
                  <th>Precio U.</th>
                  <th>Subtotal</th>
                </tr>
              </thead>
              <tbody>
                @for (item of f.items; track $index) {
                  <tr>
                    <td><strong>{{ item.producto?.nombre || '-' }}</strong></td>
                    <td>{{ item.cantidad }}</td>
                    <td>\${{ item.precioUnitario | number:'1.0-0' }}</td>
                    <td class="td-price">\${{ item.cantidad * item.precioUnitario | number:'1.0-0' }}</td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="4" class="empty">Esta factura no tiene ítems.</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>

          <div class="modal-actions">
            <button class="btn btn-secondary" (click)="cerrarDetalle()">Cerrar</button>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .page-header {
      display: flex; align-items: center; justify-content: space-between;
      margin-bottom: 20px; flex-wrap: wrap; gap: 12px;
    }
    .page-title { margin-bottom: 0; }

    .alert-error {
      background: var(--danger-light);
      color: var(--danger);
      border: 1px solid var(--danger);
      border-radius: 8px;
      padding: 10px 14px;
      margin-bottom: 16px;
      font-size: 0.9rem;
    }
    .td-id { color: var(--text-light); font-size: 0.8rem; }
    .td-price { font-weight: 600; color: var(--primary-dark); }
    .fila-anulada { opacity: 0.6; }
    .badge-activa { background: var(--success-light); color: var(--success-dark); }
    .badge-anulada { background: var(--danger-light); color: var(--danger-dark); }
    .actions { display: flex; gap: 6px; }
    .btn-icon {
      width: 32px; height: 32px; border: none; border-radius: 8px;
      display: flex; align-items: center; justify-content: center;
      cursor: pointer; transition: all 0.2s; background: transparent;
    }
    .btn-icon-delete { color: var(--danger); background: var(--danger-light); }
    .btn-icon-delete:hover { background: var(--danger); color: var(--text-inverse); }

    .btn-icon-view { color: var(--primary); background: var(--primary-light); }
    .btn-icon-view:hover { background: var(--primary); color: var(--text-inverse); }

    .modal-header {
      display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px;
    }
    .modal-header h3 { display: flex; align-items: center; gap: 8px; margin-bottom: 0; }

    .modal-detalle {
      max-width: 720px;
      max-height: 90vh;
      display: flex;
      flex-direction: column;
    }

    .detalle-resumen {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 12px 20px;
      margin-bottom: 20px;
      padding: 16px;
      background: var(--bg);
      border: 1px solid var(--border);
      border-radius: 8px;
    }
    .detalle-resumen > div { display: flex; flex-direction: column; gap: 2px; }
    .detalle-label {
      font-size: 0.75rem;
      color: var(--text-light);
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .detalle-items {
      overflow-y: auto;
      max-height: 45vh;
      border: 1px solid var(--border);
      border-radius: 8px;
    }
    .detalle-items table th { position: sticky; top: 0; z-index: 1; }

    .empty { text-align: center; color: var(--text-light); padding: 48px 16px !important; }
    .empty lucide-icon { opacity: 0.3; margin-bottom: 8px; }
    .empty p { font-size: 0.9rem; }

    @media (max-width: 768px) {
      .page-header { flex-direction: column; align-items: flex-start; }
      .modal-detalle { margin: 16px; }
      .detalle-resumen { grid-template-columns: 1fr; }
      .detalle-items { max-height: 40vh; }
    }
  `]
})
export class FacturasComponent implements OnInit {
  facturas: Factura[] = [];
  error = '';
  facturaSeleccionada: Factura | null = null;

  private paginacion = inject(PaginacionService);
  pagina = 1;
  pageSize = this.paginacion.getPageSize();

  constructor(private api: ApiService, public auth: AuthService) {}

  get facturasPagina(): Factura[] {
    return this.paginacion.paginar(this.facturas, this.pagina, this.pageSize);
  }

  cambiarPagina(page: number) { this.pagina = page; }
  cambiarPageSize(size: number) {
    this.pageSize = size;
    this.paginacion.setPageSize(size);
    this.pagina = 1;
  }

  ngOnInit() { this.cargar(); }

  abrirDetalle(f: Factura) {
    this.facturaSeleccionada = f;
  }

  cerrarDetalle() {
    this.facturaSeleccionada = null;
  }

  cargar() {
    this.api.getAll<Factura>('facturas').subscribe({
      next: d => {
        this.facturas = d;
        this.pagina = this.paginacion.clampPage(this.pagina, d.length, this.pageSize);
      },
      error: (err) => {
        this.error = err?.error?.detail || 'No se pudieron cargar las facturas.';
      }
    });
  }

  anular(id: number) {
    if (confirm('¿Anular esta factura? Se devolverá el stock.')) {
      this.error = '';
      this.api.anularFactura(id).subscribe({
        next: () => this.cargar(),
        error: (err) => {
          this.error = err?.error?.detail || 'No se pudo anular la factura.';
        }
      });
    }
  }
}
