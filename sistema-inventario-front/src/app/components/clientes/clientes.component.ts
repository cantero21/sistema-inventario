import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, Cliente } from '../../services/api.service';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [FormsModule, LucideAngularModule],
  template: `
    <div class="page-header">
      <h1 class="page-title">
        <lucide-icon name="users" [size]="24"></lucide-icon>
        Clientes
      </h1>
      <button class="btn btn-primary" (click)="abrirModal()">
        <lucide-icon name="plus" [size]="16"></lucide-icon> Nuevo cliente
      </button>
    </div>

    <div class="card table-container">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Email</th>
            <th>Teléfono</th>
            <th>Dirección</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          @for (c of clientes; track c.id) {
            <tr>
              <td class="td-id">{{ c.id }}</td>
              <td><strong>{{ c.nombre }}</strong></td>
              <td class="td-email">{{ c.email || '-' }}</td>
              <td>{{ c.telefono || '-' }}</td>
              <td class="td-desc">{{ c.direccion || '-' }}</td>
              <td>
                <div class="actions">
                  <button class="btn-icon btn-icon-edit" title="Editar" (click)="editar(c)">
                    <lucide-icon name="pencil" [size]="15"></lucide-icon>
                  </button>
                  <button class="btn-icon btn-icon-delete" title="Eliminar" (click)="eliminar(c.id!)">
                    <lucide-icon name="trash-2" [size]="15"></lucide-icon>
                  </button>
                </div>
              </td>
            </tr>
          } @empty {
            <tr>
              <td colspan="6" class="empty">
                <lucide-icon name="users" [size]="32"></lucide-icon>
                <p>No hay clientes registrados</p>
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
              <lucide-icon [name]="editando ? 'pencil' : 'plus'" [size]="18"></lucide-icon>
              {{ editando ? 'Editar' : 'Nuevo' }} cliente
            </h3>
            <button class="btn-icon" (click)="cerrarModal()">
              <lucide-icon name="x" [size]="18"></lucide-icon>
            </button>
          </div>
          <div class="form-group">
            <label>Nombre</label>
            <input type="text" [(ngModel)]="form.nombre" placeholder="Nombre del cliente">
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
    .btn-icon-edit:hover { background: var(--primary); color: white; }
    .btn-icon-delete { color: var(--danger); background: var(--danger-light); }
    .btn-icon-delete:hover { background: var(--danger); color: white; }
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
export class ClientesComponent implements OnInit {
  clientes: Cliente[] = [];
  showModal = false;
  editando = false;
  clienteId: number | null = null;
  form: Cliente = { nombre: '', direccion: '', telefono: '', email: '' };

  constructor(private api: ApiService) {}
  ngOnInit() { this.cargar(); }

  cargar() { this.api.getAll<Cliente>('clientes').subscribe(d => this.clientes = d); }

  abrirModal() {
    this.form = { nombre: '', direccion: '', telefono: '', email: '' };
    this.editando = false; this.clienteId = null; this.showModal = true;
  }

  editar(c: Cliente) {
    this.form = { ...c }; this.editando = true; this.clienteId = c.id!; this.showModal = true;
  }

  guardar() {
    const req = this.editando
      ? this.api.update<Cliente>('clientes', this.clienteId!, this.form)
      : this.api.create<Cliente>('clientes', this.form);
    req.subscribe(() => { this.cerrarModal(); this.cargar(); });
  }

  eliminar(id: number) {
    if (confirm('¿Eliminar este cliente?')) this.api.delete('clientes', id).subscribe(() => this.cargar());
  }

  cerrarModal() { this.showModal = false; }
}
