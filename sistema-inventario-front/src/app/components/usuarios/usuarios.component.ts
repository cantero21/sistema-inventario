import { Component, OnInit } from '@angular/core';
import { ApiService, Usuario } from '../../services/api.service';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [LucideAngularModule],
  template: `
    <div class="page-header">
      <h1 class="page-title">
        <lucide-icon name="settings" [size]="24"></lucide-icon>
        Usuarios
      </h1>
    </div>

    <div class="card table-container">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Email</th>
            <th>Rol</th>
          </tr>
        </thead>
        <tbody>
          @for (u of usuarios; track u.id) {
            <tr>
              <td class="td-id">{{ u.id }}</td>
              <td><strong>{{ u.nombre }}</strong></td>
              <td class="td-email">{{ u.email }}</td>
              <td>
                <span class="badge-rol" [class.rol-admin]="u.rol === 'ADMIN'" [class.rol-user]="u.rol === 'USER'">
                  <lucide-icon [name]="u.rol === 'ADMIN' ? 'shield' : 'user-check'" [size]="13"></lucide-icon>
                  {{ u.rol }}
                </span>
              </td>
            </tr>
          } @empty {
            <tr>
              <td colspan="4" class="empty">
                <lucide-icon name="users" [size]="32"></lucide-icon>
                <p>No hay usuarios registrados</p>
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
    .td-email { color: var(--primary-dark); }

    .badge-rol {
      display: inline-flex;
      align-items: center;
      gap: 5px;
      padding: 4px 10px;
      border-radius: 20px;
      font-size: 0.75rem;
      font-weight: 600;
    }
    .rol-admin {
      background: var(--warning-light);
      color: #b7791f;
    }
    .rol-user {
      background: var(--primary-light);
      color: var(--primary-dark);
    }

    .empty { text-align: center; color: var(--text-light); padding: 48px 16px !important; }
    .empty lucide-icon { opacity: 0.3; margin-bottom: 8px; }
    .empty p { font-size: 0.9rem; }

    @media (max-width: 768px) {
      .page-header { flex-direction: column; align-items: flex-start; }
    }
  `]
})
export class UsuariosComponent implements OnInit {
  usuarios: Usuario[] = [];

  constructor(private api: ApiService) {}
  ngOnInit() { this.api.getAll<Usuario>('usuarios').subscribe(d => this.usuarios = d); }
}
