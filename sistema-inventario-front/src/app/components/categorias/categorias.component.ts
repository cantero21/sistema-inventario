import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, Categoria } from '../../services/api.service';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-categorias',
  standalone: true,
  imports: [FormsModule, LucideAngularModule],
  template: `
    <div class="page-header">
      <h1 class="page-title">
        <lucide-icon name="folder-open" [size]="24"></lucide-icon>
        Categorías
      </h1>
      <button class="btn btn-primary" (click)="abrirModal()">
        <lucide-icon name="plus" [size]="16"></lucide-icon> Nueva categoría
      </button>
    </div>

    <div class="card table-container">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Descripción</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          @for (c of categorias; track c.id) {
            <tr>
              <td class="td-id">{{ c.id }}</td>
              <td><strong>{{ c.nombre }}</strong></td>
              <td class="td-desc">{{ c.descripcion || '-' }}</td>
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
              <td colspan="4" class="empty">
                <lucide-icon name="folder-open" [size]="32"></lucide-icon>
                <p>No hay categorías registradas</p>
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
              {{ editando ? 'Editar' : 'Nueva' }} categoría
            </h3>
            <button class="btn-icon" (click)="cerrarModal()">
              <lucide-icon name="x" [size]="18"></lucide-icon>
            </button>
          </div>
          <div class="form-group">
            <label>Nombre</label>
            <input type="text" [(ngModel)]="form.nombre" placeholder="Nombre de la categoría">
          </div>
          <div class="form-group">
            <label>Descripción</label>
            <input type="text" [(ngModel)]="form.descripcion" placeholder="Descripción breve">
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
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 20px;
      flex-wrap: wrap;
      gap: 12px;
    }
    .page-title { margin-bottom: 0; }
    .td-id { color: var(--text-light); font-size: 0.8rem; }
    .td-desc { color: var(--text-light); }
    .actions { display: flex; gap: 6px; }
    .btn-icon {
      width: 32px; height: 32px;
      border: none; border-radius: 8px;
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

    @media (max-width: 768px) {
      .page-header { flex-direction: column; align-items: flex-start; }
      .modal-content { margin: 16px; max-height: 90vh; overflow-y: auto; }
    }
  `]
})
export class CategoriasComponent implements OnInit {
  categorias: Categoria[] = [];
  showModal = false;
  editando = false;
  categoriaId: number | null = null;
  form: Categoria = { nombre: '', descripcion: '' };

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargar(); }

  cargar() {
    this.api.getAll<Categoria>('categorias').subscribe(data => this.categorias = data);
  }

  abrirModal() {
    this.form = { nombre: '', descripcion: '' };
    this.editando = false;
    this.categoriaId = null;
    this.showModal = true;
  }

  editar(c: Categoria) {
    this.form = { ...c };
    this.editando = true;
    this.categoriaId = c.id!;
    this.showModal = true;
  }

  guardar() {
    const req = this.editando
      ? this.api.update<Categoria>('categorias', this.categoriaId!, this.form)
      : this.api.create<Categoria>('categorias', this.form);
    req.subscribe(() => { this.cerrarModal(); this.cargar(); });
  }

  eliminar(id: number) {
    if (confirm('¿Eliminar esta categoría?')) {
      this.api.delete('categorias', id).subscribe(() => this.cargar());
    }
  }

  cerrarModal() { this.showModal = false; }
}
