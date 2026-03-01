import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe, DecimalPipe } from '@angular/common';
import { ApiService, Movimiento, Producto } from '../../services/api.service';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-movimientos',
  standalone: true,
  imports: [FormsModule, DatePipe, DecimalPipe, LucideAngularModule],
  template: `
    <div class="page-header">
      <h1 class="page-title">
        <lucide-icon name="arrow-right-left" [size]="24"></lucide-icon>
        Movimientos de inventario
      </h1>
      <div class="toolbar-actions">
        <button class="btn btn-success" (click)="abrirModal('ENTRADA')">
          <lucide-icon name="arrow-down-to-line" [size]="16"></lucide-icon> Entrada
        </button>
        <button class="btn btn-danger" (click)="abrirModal('SALIDA')">
          <lucide-icon name="arrow-up-from-line" [size]="16"></lucide-icon> Salida
        </button>
      </div>
    </div>

    <div class="card table-container">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Fecha</th>
            <th>Tipo</th>
            <th>Producto</th>
            <th>Cantidad</th>
            <th>Precio U.</th>
          </tr>
        </thead>
        <tbody>
          @for (m of movimientos; track m.id) {
            <tr>
              <td class="td-id">{{ m.id }}</td>
              <td>{{ m.fecha | date:'dd/MM/yyyy HH:mm' }}</td>
              <td>
                <span class="badge-tipo" [class.tipo-entrada]="m.tipo === 'ENTRADA'" [class.tipo-salida]="m.tipo === 'SALIDA'">
                  <lucide-icon [name]="m.tipo === 'ENTRADA' ? 'arrow-down-to-line' : 'arrow-up-from-line'" [size]="13"></lucide-icon>
                  {{ m.tipo }}
                </span>
              </td>
              <td><strong>{{ m.producto?.nombre || '-' }}</strong></td>
              <td>{{ m.cantidad }}</td>
              <td class="td-price">\${{ m.precioUnitario | number:'1.0-0' }}</td>
            </tr>
          } @empty {
            <tr>
              <td colspan="6" class="empty">
                <lucide-icon name="arrow-right-left" [size]="32"></lucide-icon>
                <p>No hay movimientos registrados</p>
              </td>
            </tr>
          }
        </tbody>
      </table>
    </div>

    @if (showModal) {
      <div class="modal-overlay" (click)="cerrarModal()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>
              <lucide-icon [name]="tipoMovimiento === 'ENTRADA' ? 'arrow-down-to-line' : 'arrow-up-from-line'" [size]="18"></lucide-icon>
              Nueva {{ tipoMovimiento }}
            </h3>
            <button class="btn-icon" (click)="cerrarModal()">
              <lucide-icon name="x" [size]="18"></lucide-icon>
            </button>
          </div>
          <div class="form-group">
            <label>Producto</label>
            <select [(ngModel)]="productoId">
              <option [ngValue]="null">-- Seleccionar --</option>
              @for (p of productos; track p.id) {
                <option [ngValue]="p.id">{{ p.nombre }} (Stock: {{ p.stock }})</option>
              }
            </select>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Cantidad</label>
              <input type="number" [(ngModel)]="cantidad" min="1">
            </div>
            <div class="form-group">
              <label>Precio unitario</label>
              <input type="number" [(ngModel)]="precioUnitario">
            </div>
          </div>
          <div class="modal-actions">
            <button class="btn btn-secondary" (click)="cerrarModal()">Cancelar</button>
            <button class="btn btn-primary" (click)="guardar()">
              <lucide-icon name="save" [size]="15"></lucide-icon> Registrar
            </button>
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
    .toolbar-actions { display: flex; gap: 10px; }
    .td-id { color: var(--text-light); font-size: 0.8rem; }
    .td-price { font-weight: 600; color: var(--primary-dark); }

    /* Badges de tipo con icono */
    .badge-tipo {
      display: inline-flex;
      align-items: center;
      gap: 5px;
      padding: 4px 10px;
      border-radius: 20px;
      font-size: 0.75rem;
      font-weight: 600;
    }
    .tipo-entrada {
      background: var(--success-light);
      color: #2f855a;
    }
    .tipo-salida {
      background: var(--danger-light);
      color: #c53030;
    }

    .actions { display: flex; gap: 6px; }
    .btn-icon {
      width: 32px; height: 32px; border: none; border-radius: 8px;
      display: flex; align-items: center; justify-content: center;
      cursor: pointer; transition: all 0.2s; background: transparent;
    }
    .empty { text-align: center; color: var(--text-light); padding: 48px 16px !important; }
    .empty lucide-icon { opacity: 0.3; margin-bottom: 8px; }
    .empty p { font-size: 0.9rem; }
    .modal-header {
      display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px;
    }
    .modal-header h3 { display: flex; align-items: center; gap: 8px; margin-bottom: 0; }
    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }

    @media (max-width: 768px) {
      .page-header { flex-direction: column; align-items: flex-start; }
      .form-row { grid-template-columns: 1fr; }
      .modal-content { margin: 16px; max-height: 90vh; overflow-y: auto; }
    }
  `]
})
export class MovimientosComponent implements OnInit {
  movimientos: Movimiento[] = [];
  productos: Producto[] = [];

  showModal = false;
  tipoMovimiento: 'ENTRADA' | 'SALIDA' = 'ENTRADA';
  productoId: number | null = null;
  cantidad = 1;
  precioUnitario = 0;

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargar(); }

  cargar() {
    this.api.getAll<Movimiento>('movimientos').subscribe(d => this.movimientos = d);
    this.api.getAll<Producto>('productos').subscribe(d => this.productos = d);
  }

  abrirModal(tipo: 'ENTRADA' | 'SALIDA') {
    this.tipoMovimiento = tipo;
    this.productoId = null;
    this.cantidad = 1;
    this.precioUnitario = 0;
    this.showModal = true;
  }

  guardar() {
    if (!this.productoId) return;

    const request = this.tipoMovimiento === 'ENTRADA'
      ? this.api.crearEntrada(this.productoId, this.cantidad, this.precioUnitario)
      : this.api.crearSalida(this.productoId, this.cantidad, this.precioUnitario);

    request.subscribe(() => { this.cerrarModal(); this.cargar(); });
  }

  cerrarModal() { this.showModal = false; }
}
