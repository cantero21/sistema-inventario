import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { ApiService, Cliente, Producto } from '../../services/api.service';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-factura-form',
  standalone: true,
  imports: [FormsModule, DecimalPipe, LucideAngularModule],
  template: `
    <div class="page-header">
      <h1 class="page-title">
        <lucide-icon name="file-text" [size]="24"></lucide-icon>
        Nueva factura
      </h1>
      <button class="btn btn-secondary" (click)="router.navigate(['/facturas'])">
        <lucide-icon name="x" [size]="16"></lucide-icon> Cancelar
      </button>
    </div>

    <div class="card">
      <div class="form-group">
        <label>
          <lucide-icon name="user-check" [size]="14"></lucide-icon> Cliente
        </label>
        <select [(ngModel)]="clienteId">
          <option [ngValue]="null">-- Seleccionar cliente --</option>
          @for (c of clientes; track c.id) {
            <option [ngValue]="c.id">{{ c.nombre }}</option>
          }
        </select>
      </div>

      <div class="section-header">
        <h3 class="section-title">
          <lucide-icon name="shopping-bag" [size]="16"></lucide-icon> Productos
        </h3>
        <button class="btn btn-sm btn-primary" (click)="agregarItem()">
          <lucide-icon name="plus" [size]="14"></lucide-icon> Agregar
        </button>
      </div>

      @for (item of items; track $index; let i = $index) {
        <div class="item-row">
          <div class="form-group item-producto">
            <label>Producto</label>
            <select [(ngModel)]="item.productoId" (ngModelChange)="actualizarPrecio(i)">
              <option [ngValue]="null">-- Seleccionar --</option>
              @for (p of productos; track p.id) {
                <option [ngValue]="p.id">{{ p.nombre }} (Stock: {{ p.stock }})</option>
              }
            </select>
          </div>
          <div class="form-group item-mini">
            <label>Cantidad</label>
            <input type="number" [(ngModel)]="item.cantidad" min="1" (ngModelChange)="calcularTotal()">
          </div>
          <div class="form-group item-mini">
            <label>Precio</label>
            <input type="number" [(ngModel)]="item.precioUnitario" (ngModelChange)="calcularTotal()">
          </div>
          <div class="form-group item-subtotal">
            <label>Subtotal</label>
            <p class="subtotal-value">\${{ item.cantidad * item.precioUnitario | number:'1.0-0' }}</p>
          </div>
          <button class="btn-icon btn-icon-delete item-remove" title="Quitar" (click)="quitarItem(i)">
            <lucide-icon name="trash-2" [size]="15"></lucide-icon>
          </button>
        </div>
      }

      @if (items.length === 0) {
        <div class="empty-items">
          <lucide-icon name="shopping-bag" [size]="28"></lucide-icon>
          <p>Agrega productos a la factura</p>
        </div>
      }

      <div class="total-section">
        <span>Total:</span>
        <strong>\${{ total | number:'1.0-0' }}</strong>
      </div>

      <div class="modal-actions">
        <button class="btn btn-secondary" (click)="router.navigate(['/facturas'])">Cancelar</button>
        <button class="btn btn-primary" (click)="guardar()" [disabled]="!clienteId || items.length === 0">
          <lucide-icon name="save" [size]="15"></lucide-icon> Guardar factura
        </button>
      </div>
    </div>
  `,
  styles: [`
    .page-header {
      display: flex; align-items: center; justify-content: space-between;
      margin-bottom: 20px; flex-wrap: wrap; gap: 12px;
    }
    .page-title { margin-bottom: 0; }

    .form-group label {
      display: flex;
      align-items: center;
      gap: 5px;
    }

    .section-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin: 24px 0 16px;
      padding-bottom: 10px;
      border-bottom: 1px solid var(--border);
    }
    .section-title {
      font-size: 1rem;
      color: var(--text);
      display: flex;
      align-items: center;
      gap: 8px;
      margin: 0;
    }

    .item-row {
      display: grid;
      grid-template-columns: 2fr 100px 100px 100px 40px;
      gap: 12px;
      align-items: end;
      margin-bottom: 12px;
      padding: 12px;
      background: var(--bg);
      border-radius: 8px;
    }

    .subtotal-value {
      font-weight: 700;
      color: var(--primary-dark);
      font-size: 0.9rem;
      padding: 10px 0;
    }

    .item-remove {
      margin-bottom: 16px;
    }

    .btn-icon {
      width: 32px; height: 32px; border: none; border-radius: 8px;
      display: flex; align-items: center; justify-content: center;
      cursor: pointer; transition: all 0.2s; background: transparent;
    }
    .btn-icon-delete { color: var(--danger); background: var(--danger-light); }
    .btn-icon-delete:hover { background: var(--danger); color: white; }

    .empty-items {
      text-align: center;
      padding: 32px;
      color: var(--text-light);
    }
    .empty-items lucide-icon { opacity: 0.3; margin-bottom: 8px; }
    .empty-items p { font-size: 0.9rem; }

    .total-section {
      display: flex;
      justify-content: flex-end;
      align-items: center;
      gap: 12px;
      font-size: 1.3rem;
      margin-top: 20px;
      padding-top: 16px;
      border-top: 2px solid var(--border);
    }
    .total-section strong {
      color: var(--primary-dark);
    }

    @media (max-width: 768px) {
      .page-header { flex-direction: column; align-items: flex-start; }
      .item-row {
        grid-template-columns: 1fr 1fr;
        gap: 10px;
      }
      .item-producto {
        grid-column: 1 / -1;
      }
      .item-remove {
        margin-bottom: 0;
        grid-column: 2;
        justify-self: end;
      }
      .item-subtotal {
        display: none;
      }
    }
    @media (max-width: 480px) {
      .item-row {
        grid-template-columns: 1fr;
      }
      .item-remove {
        grid-column: 1;
        justify-self: start;
      }
    }
  `]
})
export class FacturaFormComponent implements OnInit {
  clientes: Cliente[] = [];
  productos: Producto[] = [];
  clienteId: number | null = null;
  total = 0;

  items: { productoId: number | null; cantidad: number; precioUnitario: number }[] = [];

  constructor(private api: ApiService, public router: Router) {}

  ngOnInit() {
    this.api.getAll<Cliente>('clientes').subscribe(d => this.clientes = d);
    this.api.getAll<Producto>('productos').subscribe(d => this.productos = d);
    this.agregarItem();
  }

  agregarItem() {
    this.items.push({ productoId: null, cantidad: 1, precioUnitario: 0 });
  }

  quitarItem(i: number) {
    this.items.splice(i, 1);
    this.calcularTotal();
  }

  actualizarPrecio(i: number) {
    const prod = this.productos.find(p => p.id === this.items[i].productoId);
    if (prod) {
      this.items[i].precioUnitario = prod.precio;
      this.calcularTotal();
    }
  }

  calcularTotal() {
    this.total = this.items.reduce((sum, item) => sum + (item.cantidad * item.precioUnitario), 0);
  }

  guardar() {
    const factura = {
      fecha: new Date().toISOString().split('T')[0],
      cliente: { id: this.clienteId },
      total: this.total,
      items: this.items
        .filter(i => i.productoId)
        .map(i => ({
          producto: { id: i.productoId },
          cantidad: i.cantidad,
          precioUnitario: i.precioUnitario
        }))
    };

    this.api.create('facturas', factura).subscribe(() => {
      this.router.navigate(['/facturas']);
    });
  }
}
