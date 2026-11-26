import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, Producto, Categoria, Proveedor } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { DecimalPipe } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';
import { PaginadorComponent } from '../paginador/paginador.component';
import { PaginacionService } from '../../services/paginacion.service';

@Component({
  selector: 'app-productos',
  standalone: true,
  imports: [FormsModule, DecimalPipe, LucideAngularModule, PaginadorComponent],
  template: `
    <div class="page-header">
      <h1 class="page-title">
        <lucide-icon name="package" [size]="24"></lucide-icon>
        Productos
      </h1>
      @if (auth.esAdmin()) {
        <button class="btn btn-primary" (click)="abrirModal()">
          <lucide-icon name="plus" [size]="16"></lucide-icon> Nuevo producto
        </button>
      }
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
              <th>Nombre</th>
              <th>Descripción</th>
              <th>Precio</th>
              <th>Stock</th>
              <th>Categoría</th>
              <th>Proveedor</th>
              @if (auth.esAdmin()) {
                <th>Acciones</th>
              }
            </tr>
          </thead>
          <tbody>
            @for (p of productosPagina; track p.id) {
              <tr>
                <td class="td-id">{{ p.id }}</td>
                <td><strong>{{ p.nombre }}</strong></td>
                <td class="td-desc">{{ p.descripcion || '-' }}</td>
                <td class="td-price">\${{ p.precio | number:'1.0-0' }}</td>
                <td>
                  <span class="badge" [class.badge-entrada]="p.stock > 5" [class.badge-salida]="p.stock <= 5">
                    {{ p.stock }} uds
                  </span>
                </td>
                <td>{{ p.categoria?.nombre || '-' }}</td>
                <td>{{ p.proveedor?.nombre || '-' }}</td>
                @if (auth.esAdmin()) {
                  <td>
                    <div class="actions">
                      <button class="btn-icon btn-icon-edit" title="Editar" (click)="editar(p)">
                        <lucide-icon name="pencil" [size]="15"></lucide-icon>
                      </button>
                      <button class="btn-icon btn-icon-delete" title="Eliminar" (click)="eliminar(p.id!)">
                        <lucide-icon name="trash-2" [size]="15"></lucide-icon>
                      </button>
                    </div>
                  </td>
                }
              </tr>
            } @empty {
              <tr>
                <td [attr.colspan]="auth.esAdmin() ? 8 : 7" class="empty">
                  <lucide-icon name="package" [size]="32"></lucide-icon>
                  <p>No hay productos registrados</p>
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>
      <app-paginador
        [totalItems]="productos.length"
        [page]="pagina"
        [pageSize]="pageSize"
        (pageChange)="cambiarPagina($event)"
        (pageSizeChange)="cambiarPageSize($event)" />
    </div>

    <!-- Modal -->
    @if (showModal) {
      <div class="modal-overlay" (click)="cerrarModal()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>
              <lucide-icon [name]="editando ? 'pencil' : 'plus'" [size]="18"></lucide-icon>
              {{ editando ? 'Editar' : 'Nuevo' }} producto
            </h3>
            <button class="btn-icon" (click)="cerrarModal()">
              <lucide-icon name="x" [size]="18"></lucide-icon>
            </button>
          </div>

          <div class="form-group">
            <label>Nombre</label>
            <input type="text" [(ngModel)]="form.nombre" placeholder="Nombre del producto">
          </div>
          <div class="form-group">
            <label>Descripción</label>
            <input type="text" [(ngModel)]="form.descripcion" placeholder="Descripción breve">
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Precio</label>
              <input type="number" [(ngModel)]="form.precio" placeholder="0">
            </div>
            <div class="form-group">
              <label>Stock</label>
              <input type="number" [(ngModel)]="form.stock" placeholder="0" [disabled]="editando">
              @if (editando) {
                <small class="hint">El stock se ajusta con movimientos de entrada o salida.</small>
              }
            </div>
          </div>
          <div class="form-group">
            <label>Categoría</label>
            <select [(ngModel)]="categoriaSeleccionada">
              <option [ngValue]="null">-- Seleccionar --</option>
              @for (c of categorias; track c.id) {
                <option [ngValue]="c.id">{{ c.nombre }}</option>
              }
            </select>
          </div>
          <div class="form-group">
            <label>Proveedor</label>
            <select [(ngModel)]="proveedorSeleccionado">
              <option [ngValue]="null">-- Seleccionar --</option>
              @for (p of proveedores; track p.id) {
                <option [ngValue]="p.id">{{ p.nombre }}</option>
              }
            </select>
          </div>

          @if (error) {
            <div class="alert-error">{{ error }}</div>
          }

          <div class="modal-actions">
            <button class="btn btn-secondary" (click)="cerrarModal()">Cancelar</button>
            <button class="btn btn-primary" (click)="guardar()">
              <lucide-icon name="save" [size]="15"></lucide-icon> Guardar
            </button>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .page-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 20px;
      flex-wrap: wrap;
      gap: 12px;
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
    .hint { display: block; margin-top: 6px; color: var(--text-light); font-size: 0.78rem; }

    /* Columnas específicas */
    .td-id { color: var(--text-light); font-size: 0.8rem; }
    .td-desc { color: var(--text-light); max-width: 180px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .td-price { font-weight: 600; color: var(--primary-dark); }

    /* Botones de acción con icono */
    .actions { display: flex; gap: 6px; }
    .btn-icon {
      width: 32px; height: 32px;
      border: none;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.2s;
      background: transparent;
    }
    .btn-icon-edit {
      color: var(--primary);
      background: var(--primary-light);
    }
    .btn-icon-edit:hover {
      background: var(--primary);
      color: var(--text-inverse);
    }
    .btn-icon-delete {
      color: var(--danger);
      background: var(--danger-light);
    }
    .btn-icon-delete:hover {
      background: var(--danger);
      color: var(--text-inverse);
    }

    /* Empty state */
    .empty {
      text-align: center;
      color: var(--text-light);
      padding: 48px 16px !important;
    }
    .empty lucide-icon { opacity: 0.3; margin-bottom: 8px; }
    .empty p { font-size: 0.9rem; }

    /* Modal header */
    .modal-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 20px;
    }
    .modal-header h3 {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 0;
    }

    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }

    /* RESPONSIVE */
    @media (max-width: 768px) {
      .page-header {
        flex-direction: column;
        align-items: flex-start;
      }
      .form-row {
        grid-template-columns: 1fr;
      }
      .modal-content {
        margin: 16px;
        max-height: 90vh;
        overflow-y: auto;
      }
      .td-desc { max-width: 120px; }
    }
  `]
})
export class ProductosComponent implements OnInit {
  productos: Producto[] = [];
  categorias: Categoria[] = [];
  proveedores: Proveedor[] = [];

  private paginacion = inject(PaginacionService);
  pagina = 1;
  pageSize = this.paginacion.getPageSize();

  showModal = false;
  editando = false;
  productoId: number | null = null;

  form: Producto = { nombre: '', descripcion: '', precio: 0, stock: 0 };
  categoriaSeleccionada: number | null = null;
  proveedorSeleccionado: number | null = null;
  error = '';

  constructor(private api: ApiService, public auth: AuthService) {}

  get productosPagina(): Producto[] {
    return this.paginacion.paginar(this.productos, this.pagina, this.pageSize);
  }

  cambiarPagina(page: number) { this.pagina = page; }
  cambiarPageSize(size: number) {
    this.pageSize = size;
    this.paginacion.setPageSize(size);
    this.pagina = 1;
  }

  ngOnInit() {
    this.cargarDatos();
  }

  cargarDatos() {
    this.api.getAll<Producto>('productos').subscribe(data => {
      this.productos = data;
      this.pagina = this.paginacion.clampPage(this.pagina, data.length, this.pageSize);
    });
    this.api.getAll<Categoria>('categorias').subscribe(data => this.categorias = data);
    this.api.getAll<Proveedor>('proveedores').subscribe(data => this.proveedores = data);
  }

  abrirModal() {
    this.error = '';
    this.form = { nombre: '', descripcion: '', precio: 0, stock: 0 };
    this.categoriaSeleccionada = null;
    this.proveedorSeleccionado = null;
    this.editando = false;
    this.productoId = null;
    this.showModal = true;
  }

  editar(p: Producto) {
    this.error = '';
    this.form = { ...p };
    this.categoriaSeleccionada = p.categoria?.id || null;
    this.proveedorSeleccionado = p.proveedor?.id || null;
    this.editando = true;
    this.productoId = p.id!;
    this.showModal = true;
  }

  guardar() {
    this.error = '';
    const data: any = {
      nombre: this.form.nombre,
      descripcion: this.form.descripcion,
      precio: this.form.precio,
      stock: this.form.stock,
      categoriaId: this.categoriaSeleccionada,
      proveedorId: this.proveedorSeleccionado
    };

    const request = this.editando
      ? this.api.update<Producto>('productos', this.productoId!, data)
      : this.api.create<Producto>('productos', data);

    request.subscribe({
      next: () => {
        this.cerrarModal();
        this.cargarDatos();
      },
      error: (err) => {
        this.error = err?.error?.detail || 'No se pudo guardar el producto.';
      }
    });
  }

  eliminar(id: number) {
    if (confirm('¿Estás seguro de eliminar este producto?')) {
      this.error = '';
      this.api.delete('productos', id).subscribe({
        next: () => this.cargarDatos(),
        error: (err) => {
          this.error = err?.error?.detail || 'No se pudo eliminar el producto.';
        }
      });
    }
  }

  cerrarModal() {
    this.error = '';
    this.showModal = false;
  }
}
