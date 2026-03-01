import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService, DashboardTotales } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [LucideAngularModule, RouterLink],
  template: `
    <div class="dashboard-header">
      <div>
        <h1 class="page-title">
          <lucide-icon name="layout-dashboard" [size]="24"></lucide-icon>
          Dashboard
        </h1>
        <p class="welcome">Hola, {{ auth.getNombre() }}. Aquí tienes el resumen general.</p>
      </div>
    </div>

    <div class="cards-grid">
      <a routerLink="/productos" class="stat-card productos">
        <div class="stat-icon-wrapper productos-bg">
          <lucide-icon name="package" [size]="24"></lucide-icon>
        </div>
        <div class="stat-content">
          <p class="stat-number">{{ totales.producto }}</p>
          <p class="stat-label">Productos</p>
        </div>
        <div class="stat-trend">
          <lucide-icon name="trending-up" [size]="14"></lucide-icon>
        </div>
      </a>

      <a routerLink="/clientes" class="stat-card clientes">
        <div class="stat-icon-wrapper clientes-bg">
          <lucide-icon name="user-check" [size]="24"></lucide-icon>
        </div>
        <div class="stat-content">
          <p class="stat-number">{{ totales.cliente }}</p>
          <p class="stat-label">Clientes</p>
        </div>
        <div class="stat-trend">
          <lucide-icon name="trending-up" [size]="14"></lucide-icon>
        </div>
      </a>

      <a routerLink="/proveedores" class="stat-card proveedores">
        <div class="stat-icon-wrapper proveedores-bg">
          <lucide-icon name="truck" [size]="24"></lucide-icon>
        </div>
        <div class="stat-content">
          <p class="stat-number">{{ totales.proveedor }}</p>
          <p class="stat-label">Proveedores</p>
        </div>
        <div class="stat-trend">
          <lucide-icon name="trending-up" [size]="14"></lucide-icon>
        </div>
      </a>

      <a routerLink="/facturas" class="stat-card facturas">
        <div class="stat-icon-wrapper facturas-bg">
          <lucide-icon name="clipboard-list" [size]="24"></lucide-icon>
        </div>
        <div class="stat-content">
          <p class="stat-number">{{ totales.factura }}</p>
          <p class="stat-label">Facturas</p>
        </div>
        <div class="stat-trend">
          <lucide-icon name="trending-up" [size]="14"></lucide-icon>
        </div>
      </a>
    </div>
  `,
  styles: [`
    .dashboard-header { margin-bottom: 28px; }
    .page-title {
      display: flex; align-items: center; gap: 10px;
      font-size: 1.4rem; color: var(--text); margin-bottom: 6px;
    }
    .welcome { color: var(--text-light); font-size: 0.95rem; }

    .cards-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;
    }

    .stat-card {
      background: white;
      border-radius: var(--radius);
      padding: 24px;
      display: flex;
      align-items: center;
      gap: 16px;
      box-shadow: var(--shadow);
      position: relative;
      overflow: hidden;
      transition: transform 0.2s, box-shadow 0.2s;
      text-decoration: none;
      color: inherit;
      cursor: pointer;
    }
    .stat-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 24px rgba(0,0,0,0.08);
    }

    .stat-icon-wrapper {
      width: 48px; height: 48px; border-radius: 12px;
      display: flex; align-items: center; justify-content: center; flex-shrink: 0;
    }
    .productos-bg { background: var(--primary-light); color: var(--primary); }
    .clientes-bg { background: var(--accent-light, #e8f4fd); color: var(--accent, #2196F3); }
    .proveedores-bg { background: var(--success-light); color: var(--success); }
    .facturas-bg { background: var(--warning-light); color: var(--warning); }

    .stat-content { flex: 1; }
    .stat-number { font-size: 1.8rem; font-weight: 700; color: var(--text); line-height: 1; }
    .stat-label { font-size: 0.85rem; color: var(--text-light); margin-top: 4px; }

    .stat-trend {
      position: absolute; top: 12px; right: 12px;
      color: var(--success); opacity: 0.6;
    }

    @media (max-width: 1200px) { .cards-grid { grid-template-columns: repeat(2, 1fr); } }
    @media (max-width: 768px) {
      .cards-grid { grid-template-columns: 1fr; }
      .stat-card { padding: 20px; }
      .stat-number { font-size: 1.5rem; }
    }
    @media (max-width: 480px) {
      .page-title { font-size: 1.2rem; }
      .stat-icon-wrapper { width: 40px; height: 40px; }
    }
  `]
})
export class DashboardComponent implements OnInit {
  totales: DashboardTotales = { producto: 0, cliente: 0, proveedor: 0, factura: 0 };

  constructor(private api: ApiService, public auth: AuthService) {}

  ngOnInit() {
    this.api.getDashboardTotales().subscribe(data => this.totales = data);
  }
}
