import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService, DashboardTotales, StockBajo } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { LucideAngularModule } from 'lucide-angular';
import { BaseChartDirective } from 'ng2-charts';
import type { ChartConfiguration, ChartData } from 'chart.js';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [LucideAngularModule, RouterLink, BaseChartDirective],
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

    <div class="charts-grid">
      <div class="chart-card">
        <div class="chart-header">
          <h2 class="chart-title">
            <lucide-icon name="trending-up" [size]="18"></lucide-icon>
            Ventas por día
          </h2>
          <span class="chart-subtitle">Últimos 30 días</span>
        </div>
        <div class="chart-body">
          <canvas baseChart
            [data]="ventasChartData"
            [options]="ventasChartOptions"
            [type]="'line'">
          </canvas>
        </div>
      </div>

      <div class="chart-card">
        <div class="chart-header">
          <h2 class="chart-title">
            <lucide-icon name="package" [size]="18"></lucide-icon>
            Stock bajo
          </h2>
          <span class="chart-subtitle">Menos de 10 unidades</span>
        </div>
        <div class="chart-body">
          @if (stockBajo.length === 0) {
            <div class="chart-empty">
              <lucide-icon name="package" [size]="28"></lucide-icon>
              <p>No hay productos con stock bajo.</p>
            </div>
          } @else {
            <canvas baseChart
              [data]="stockChartData"
              [options]="stockChartOptions"
              [type]="'bar'">
            </canvas>
          }
        </div>
      </div>
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
      background: var(--bg-card);
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
      box-shadow: var(--shadow-lg);
    }

    .stat-icon-wrapper {
      width: 48px; height: 48px; border-radius: 12px;
      display: flex; align-items: center; justify-content: center; flex-shrink: 0;
    }
    .productos-bg { background: var(--primary-light); color: var(--primary); }
    .clientes-bg { background: var(--accent-light); color: var(--accent); }
    .proveedores-bg { background: var(--success-light); color: var(--success); }
    .facturas-bg { background: var(--warning-light); color: var(--warning); }

    .stat-content { flex: 1; }
    .stat-number { font-size: 1.8rem; font-weight: 700; color: var(--text); line-height: 1; }
    .stat-label { font-size: 0.85rem; color: var(--text-light); margin-top: 4px; }

    .stat-trend {
      position: absolute; top: 12px; right: 12px;
      color: var(--success); opacity: 0.6;
    }

    .charts-grid {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 20px;
      margin-top: 20px;
    }
    .chart-card {
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      box-shadow: var(--shadow);
      padding: 20px;
    }
    .chart-header {
      display: flex;
      align-items: baseline;
      justify-content: space-between;
      gap: 12px;
      margin-bottom: 16px;
    }
    .chart-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 1rem;
      color: var(--text);
      margin: 0;
    }
    .chart-subtitle {
      font-size: 0.8rem;
      color: var(--text-light);
    }
    .chart-body {
      position: relative;
      height: 320px;
    }
    .chart-empty {
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 8px;
      color: var(--text-light);
      font-size: 0.9rem;
    }

    @media (max-width: 1200px) {
      .cards-grid { grid-template-columns: repeat(2, 1fr); }
      .charts-grid { grid-template-columns: 1fr; }
    }
    @media (max-width: 768px) {
      .cards-grid { grid-template-columns: 1fr; }
      .stat-card { padding: 20px; }
      .stat-number { font-size: 1.5rem; }
      .chart-body { height: 260px; }
      .chart-header { flex-direction: column; align-items: flex-start; gap: 4px; }
    }
    @media (max-width: 480px) {
      .page-title { font-size: 1.2rem; }
      .stat-icon-wrapper { width: 40px; height: 40px; }
    }
  `]
})
export class DashboardComponent implements OnInit {
  totales: DashboardTotales = { producto: 0, cliente: 0, proveedor: 0, factura: 0 };
  stockBajo: StockBajo[] = [];

  ventasChartData: ChartData<'line'> = { labels: [], datasets: [] };
  stockChartData: ChartData<'bar'> = { labels: [], datasets: [] };

  ventasChartOptions: ChartConfiguration<'line'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    interaction: { mode: 'index', intersect: false },
    plugins: {
      legend: { display: false },
      tooltip: {
        callbacks: {
          label: ctx => `$${Number(ctx.parsed.y).toLocaleString('es-CO')}`
        }
      }
    },
    scales: {
      x: {
        grid: { display: false },
        ticks: {
          color: this.cssVar('--text-light'),
          maxTicksLimit: 10,
          autoSkip: true
        }
      },
      y: {
        beginAtZero: true,
        grid: { color: this.cssVar('--border') },
        ticks: {
          color: this.cssVar('--text-light'),
          callback: value => `$${Number(value).toLocaleString('es-CO')}`
        }
      }
    }
  };

  stockChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    indexAxis: 'y',
    plugins: {
      legend: { display: false },
      tooltip: {
        callbacks: {
          label: ctx => `${ctx.parsed.x} uds`
        }
      }
    },
    scales: {
      x: {
        beginAtZero: true,
        grid: { color: this.cssVar('--border') },
        ticks: { color: this.cssVar('--text-light'), precision: 0 }
      },
      y: {
        reverse: true,
        grid: { display: false },
        ticks: { color: this.cssVar('--text') }
      }
    }
  };

  constructor(private api: ApiService, public auth: AuthService) {}

  ngOnInit() {
    this.api.getDashboardTotales().subscribe(data => this.totales = data);
    this.cargarVentas();
    this.cargarStockBajo();
  }

  private cargarVentas() {
    this.api.getVentasPorDia().subscribe(data => {
      this.ventasChartData = {
        labels: data.map(v => this.formatearDia(v.fecha)),
        datasets: [{
          data: data.map(v => v.total),
          label: 'Ventas',
          borderColor: this.cssVar('--primary'),
          backgroundColor: this.hexToRgba(this.cssVar('--primary'), 0.15),
          pointBackgroundColor: this.cssVar('--primary'),
          pointBorderColor: this.cssVar('--primary'),
          pointRadius: 3,
          pointHoverRadius: 5,
          borderWidth: 2,
          tension: 0.35,
          fill: true
        }]
      };
    });
  }

  private cargarStockBajo() {
    this.api.getStockBajo().subscribe(data => {
      this.stockBajo = data;
      this.stockChartData = {
        labels: data.map(p => p.nombre),
        datasets: [{
          data: data.map(p => p.stock),
          label: 'Stock',
          backgroundColor: data.map(p =>
            p.stock < 5 ? this.cssVar('--danger') : this.cssVar('--warning')
          ),
          borderRadius: 6,
          maxBarThickness: 28
        }]
      };
    });
  }

  private formatearDia(iso: string): string {
    const [, mes, dia] = iso.split('-');
    return `${dia}/${mes}`;
  }

  private cssVar(nombre: string): string {
    return getComputedStyle(document.documentElement).getPropertyValue(nombre).trim();
  }

  private hexToRgba(hex: string, alpha: number): string {
    const limpio = hex.replace('#', '');
    const r = parseInt(limpio.substring(0, 2), 16);
    const g = parseInt(limpio.substring(2, 4), 16);
    const b = parseInt(limpio.substring(4, 6), 16);
    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
  }
}
