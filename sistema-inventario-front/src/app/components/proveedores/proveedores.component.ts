import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, Proveedor } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { LucideAngularModule } from 'lucide-angular';
import { PaginadorComponent } from '../paginador/paginador.component';
import { PaginacionService } from '../../services/paginacion.service';

@Component({
  selector: 'app-proveedores',
  standalone: true,
  imports: [FormsModule, LucideAngularModule, PaginadorComponent],
  template: `
    <div class="page-header">
      <h1 class="page-title">
        <lucide-icon name="truck" [size]="24"></lucide-icon>
        Proveedores
      </h1>
      @if (auth.esAdmin()) {
        <button class="btn btn-primary" (click)="abrirModal()">
          <lucide-icon name="plus" [size]="16"></lucide-icon> Nuevo proveedor
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
              <th>Email</th>
              <th>Teléfono</th>
              <th>Dirección</th>
              @if (auth.esAdmin()) {
                <th>Acciones</th>
              }
            </tr>
          </thead>
          <tbody>
            @for (p of proveedoresPagina; track p.id) {
              <tr>
                <td class="td-id">{{ p.id }}</td>
                <td><strong>{{ p.nombre }}</strong></td>
                <td class="td-email">{{ p.email || '-' }}</td>
                <td>{{ p.telefono || '-' }}</td>
                <td class="td-desc">{{ p.direccion || '-' }}</td>
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
                <td [attr.colspan]="auth.esAdmin() ? 6 : 5" class="empty">
                  <lucide-icon name="truck" [size]="32"></lucide-icon>
                  <p>No hay proveedores registrados</p>
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>
      <app-paginador
        [totalItems]="proveedores.length"
        [page]="pagina"
        [pageSize]="pageSize"
        (pageChange)="cambiarPagina($event)"
        (pageSizeChange)="cambiarPageSize($event)" />
    </div>

    @if (showModal) {
      <div class="modal-overlay" (click)="cerrarModal()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>
              <lucide-icon [name]="editando ? 'pencil' : 'plus'" [size]="18"></lucide-icon>
              {{ editando ? 'Editar' : 'Nuevo' }} proveedor
            </h3>
            <button class="btn-icon" (click)="cerrarModal()">
              <lucide-icon name="x" [size]="18"></lucide-icon>
            </button>
          </div>
          <div class="form-group">
            <label>Nombre</label>
            <input type="text" [(ngModel)]="form.nombre" placeholder="Nombre del proveedor">
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Email</label>
              <input type="email" [(ngModel)]="form.email" placeholder="correo@ejemplo.com">
            </div>
            <div class="form-group">
              <label>Teléfono</label>
              <input type="text" [(ngModel)]="form.telefono" placeholder="300 123 4567">
            </div>
          </div>
          <div class="form-group">
            <label>Dirección</label>
            <input type="text" [(ngModel)]="form.direccion" placeholder="Dirección completa">
          </div>
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
    .td-email { color: var(--primary-dark); }
    .td-desc { color: var(--text-light); max-width: 180px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .actions { display: flex; gap: 6px; }
    .btn-icon {
      width: 32px; height: 32px; border: none; border-radius: 8px;
      display: flex; align-items: center; justify-content: center;
      cursor: pointer; transition: all 0.2s; background: transparent;
    }
    .btn-icon-edit { color: var(--primary); background: var(--primary-light); }
    .btn-icon-edit:hover { background: var(--primary); color: var(--text-inverse); }
    .btn-icon-delete { color: var(--danger); background: var(--danger-light); }
    .btn-icon-delete:hover { background: var(--danger); color: var(--text-inverse); }
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
      .td-desc { max-width: 120px; }
    }
  `]
})
export class ProveedoresComponent implements OnInit {
  proveedores: Proveedor[] = [];

  private paginacion = inject(PaginacionService);
  pagina = 1;
  pageSize = this.paginacion.getPageSize();

  showModal = false;
  editando = false;
  proveedorId: number | null = null;
  form: Proveedor = { nombre: '', direccion: '', telefono: '', email: '' };
  error = '';

  constructor(private api: ApiService, public auth: AuthService) {}

  get proveedoresPagina(): Proveedor[] {
    return this.paginacion.paginar(this.proveedores, this.pagina, this.pageSize);
  }

  cambiarPagina(page: number) { this.pagina = page; }
  cambiarPageSize(size: number) {
    this.pageSize = size;
    this.paginacion.setPageSize(size);
    this.pagina = 1;
  }

  ngOnInit() { this.cargar(); }

  cargar() {
    this.api.getAll<Proveedor>('proveedores').subscribe({
      next: d => {
        this.proveedores = d;
        this.pagina = this.paginacion.clampPage(this.pagina, d.length, this.pageSize);
      },
      error: (err) => {
        this.error = err?.error?.detail || 'No se pudieron cargar los proveedores.';
      }
    });
  }

  abrirModal() {
    this.error = '';
    this.form = { nombre: '', direccion: '', telefono: '', email: '' };
    this.editando = false; this.proveedorId = null; this.showModal = true;
  }

  editar(p: Proveedor) {
    this.error = '';
    this.form = { ...p }; this.editando = true; this.proveedorId = p.id!; this.showModal = true;
  }

  guardar() {
    this.error = '';
    const req = this.editando
      ? this.api.update<Proveedor>('proveedores', this.proveedorId!, this.form)
      : this.api.create<Proveedor>('proveedores', this.form);
    req.subscribe({
      next: () => { this.cerrarModal(); this.cargar(); },
      error: (err) => {
        this.error = err?.error?.detail || 'No se pudo guardar el proveedor.';
      }
    });
  }

  eliminar(id: number) {
    if (confirm('¿Eliminar este proveedor?')) {
      this.error = '';
      this.api.delete('proveedores', id).subscribe({
        next: () => this.cargar(),
        error: (err) => {
          this.error = err?.error?.detail || 'No se pudo eliminar el proveedor.';
        }
      });
    }
  }

  cerrarModal() {
    this.error = '';
    this.showModal = false;
  }
}
