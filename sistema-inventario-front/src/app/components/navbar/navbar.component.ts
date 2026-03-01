import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, LucideAngularModule],
  template: `
    <!-- Botón hamburguesa (solo visible en móvil) -->
    <button class="menu-toggle" (click)="toggleMenu()">
      <lucide-icon [name]="menuAbierto ? 'x' : 'menu'" [size]="22"></lucide-icon>
    </button>

    <!-- Overlay oscuro al abrir sidebar en móvil -->
    @if (menuAbierto) {
      <div class="sidebar-overlay" (click)="cerrarMenu()"></div>
    }

    <nav class="sidebar" [class.sidebar-open]="menuAbierto">
      <div class="sidebar-header">
        <lucide-icon name="box" [size]="24" class="logo-icon"></lucide-icon>
        <h2>Inventario</h2>
      </div>

      <div class="user-info">
        <div class="user-avatar">{{ auth.getNombre().charAt(0).toUpperCase() }}</div>
        <div>
          <p class="user-name">{{ auth.getNombre() }}</p>
          <p class="user-role">{{ auth.getRol() }}</p>
        </div>
      </div>

      <ul class="nav-links">
        <li>
          <a routerLink="/dashboard" routerLinkActive="active" (click)="cerrarMenu()">
            <lucide-icon name="layout-dashboard" [size]="18"></lucide-icon> Dashboard
          </a>
        </li>
        <li>
          <a routerLink="/productos" routerLinkActive="active" (click)="cerrarMenu()">
            <lucide-icon name="package" [size]="18"></lucide-icon> Productos
          </a>
        </li>
        <li>
          <a routerLink="/categorias" routerLinkActive="active" (click)="cerrarMenu()">
            <lucide-icon name="folder-open" [size]="18"></lucide-icon> Categorías
          </a>
        </li>
        <li>
          <a routerLink="/clientes" routerLinkActive="active" (click)="cerrarMenu()">
            <lucide-icon name="users" [size]="18"></lucide-icon> Clientes
          </a>
        </li>
        <li>
          <a routerLink="/proveedores" routerLinkActive="active" (click)="cerrarMenu()">
            <lucide-icon name="truck" [size]="18"></lucide-icon> Proveedores
          </a>
        </li>
        <li>
          <a routerLink="/facturas" routerLinkActive="active" (click)="cerrarMenu()">
            <lucide-icon name="file-text" [size]="18"></lucide-icon> Facturas
          </a>
        </li>
        <li>
          <a routerLink="/movimientos" routerLinkActive="active" (click)="cerrarMenu()">
            <lucide-icon name="arrow-right-left" [size]="18"></lucide-icon> Movimientos
          </a>
        </li>
        <li>
          <a routerLink="/usuarios" routerLinkActive="active" (click)="cerrarMenu()">
            <lucide-icon name="settings" [size]="18"></lucide-icon> Usuarios
          </a>
        </li>
      </ul>

      <button class="logout-btn" (click)="logout()">
        <lucide-icon name="log-out" [size]="16"></lucide-icon> Cerrar sesión
      </button>
    </nav>
  `,
  styles: [`
    .sidebar {
      position: fixed;
      left: 0; top: 0; bottom: 0;
      width: 240px;
      background: white;
      border-right: 1px solid var(--border);
      display: flex;
      flex-direction: column;
      padding: 20px 0;
      z-index: 100;
      transition: transform 0.3s ease;
    }
    .sidebar-header {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 0 20px 20px;
      border-bottom: 1px solid var(--border);
    }
    .sidebar-header h2 {
      font-size: 1.1rem;
      color: var(--primary-dark);
    }
    :host ::ng-deep .logo-icon svg {
      color: var(--primary);
    }
    .user-info {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 16px 20px;
      border-bottom: 1px solid var(--border);
    }
    .user-avatar {
      width: 36px; height: 36px;
      background: var(--primary-light);
      color: var(--primary-dark);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 700;
      font-size: 0.9rem;
    }
    .user-name { font-weight: 600; font-size: 0.85rem; }
    .user-role { font-size: 0.75rem; color: var(--text-light); }
    .nav-links {
      list-style: none;
      flex: 1;
      padding: 12px 0;
      overflow-y: auto;
    }
    .nav-links a {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px 20px;
      color: var(--text-light);
      text-decoration: none;
      font-size: 0.9rem;
      font-weight: 500;
      transition: all 0.2s;
    }
    .nav-links a:hover {
      background: var(--primary-light);
      color: var(--primary-dark);
    }
    .nav-links a.active {
      background: var(--primary-light);
      color: var(--primary-dark);
      font-weight: 600;
      border-right: 3px solid var(--primary);
    }
    .logout-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      margin: 12px 20px 0;
      padding: 10px;
      background: var(--danger-light);
      color: var(--danger);
      border: none;
      border-radius: 8px;
      font-family: 'DM Sans', sans-serif;
      font-weight: 600;
      font-size: 0.85rem;
      cursor: pointer;
      transition: opacity 0.2s;
    }
    .logout-btn:hover { opacity: 0.8; }

    /* Botón hamburguesa - oculto en desktop */
    .menu-toggle {
      display: none;
      position: fixed;
      top: 16px;
      left: 16px;
      z-index: 200;
      width: 40px; height: 40px;
      border: none;
      border-radius: 10px;
      background: white;
      box-shadow: var(--shadow-md);
      cursor: pointer;
      align-items: center;
      justify-content: center;
      color: var(--text);
    }

    /* Overlay */
    .sidebar-overlay {
      display: none;
      position: fixed;
      top: 0; left: 0; right: 0; bottom: 0;
      background: rgba(0,0,0,0.3);
      z-index: 90;
    }

    /* RESPONSIVE */
    @media (max-width: 768px) {
      .menu-toggle {
        display: flex;
      }
      .sidebar {
        transform: translateX(-100%);
      }
      .sidebar.sidebar-open {
        transform: translateX(0);
        box-shadow: var(--shadow-md);
      }
      .sidebar-overlay {
        display: block;
      }
    }
  `]
})
export class NavbarComponent {
  menuAbierto = false;

  constructor(public auth: AuthService, private router: Router) {}

  toggleMenu() {
    this.menuAbierto = !this.menuAbierto;
  }

  cerrarMenu() {
    this.menuAbierto = false;
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
