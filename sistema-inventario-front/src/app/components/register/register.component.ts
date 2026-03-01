import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink, LucideAngularModule],
  template: `
    <div class="login-page">
      <div class="login-card">
        <div class="login-header">
          <div class="login-logo">
            <lucide-icon name="user-check" [size]="32"></lucide-icon>
          </div>
          <h1>Crear cuenta</h1>
          <p>Registra tus datos para acceder</p>
        </div>

        <div class="form-group">
          <label>Nombre</label>
          <div class="input-icon">
            <lucide-icon name="user" [size]="16" class="field-icon"></lucide-icon>
            <input type="text" [(ngModel)]="nombre" placeholder="Tu nombre completo">
          </div>
        </div>

        <div class="form-group">
          <label>Email</label>
          <div class="input-icon">
            <lucide-icon name="mail" [size]="16" class="field-icon"></lucide-icon>
            <input type="email" [(ngModel)]="email" placeholder="tu@email.com">
          </div>
        </div>

        <div class="form-group">
          <label>Contraseña</label>
          <div class="input-icon">
            <lucide-icon name="lock" [size]="16" class="field-icon"></lucide-icon>
            <input [type]="showPassword ? 'text' : 'password'"
                   [(ngModel)]="password"
                   placeholder="Mínimo 6 caracteres"
                   (keyup.enter)="register()">
            <button class="toggle-pass" (click)="showPassword = !showPassword">
              <lucide-icon [name]="showPassword ? 'eye-off' : 'eye'" [size]="16"></lucide-icon>
            </button>
          </div>
        </div>

        @if (error) {
          <div class="error-msg">
            <lucide-icon name="x" [size]="14"></lucide-icon>
            {{ error }}
          </div>
        }

        @if (success) {
          <div class="success-msg">
            <lucide-icon name="user-check" [size]="14"></lucide-icon>
            {{ success }}
          </div>
        }

        <button class="btn btn-primary btn-full" (click)="register()" [disabled]="loading">
          @if (loading) {
            Registrando...
          } @else {
            <lucide-icon name="user-check" [size]="16"></lucide-icon> Registrarme
          }
        </button>

        <p class="register-link">
          ¿Ya tienes cuenta? <a routerLink="/login">Inicia sesión</a>
        </p>
      </div>
    </div>
  `,
  styles: [`
    .login-page {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #e8eef6 0%, #f5f6fa 50%, #fdf0e8 100%);
      padding: 16px;
    }
    .login-card {
      background: white;
      padding: 40px;
      border-radius: 16px;
      box-shadow: 0 8px 30px rgba(0,0,0,0.08);
      width: 100%;
      max-width: 420px;
    }
    .login-header { text-align: center; margin-bottom: 32px; }
    .login-logo {
      width: 60px; height: 60px;
      background: var(--primary-light);
      color: var(--primary);
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 12px;
    }
    .login-header h1 { font-size: 1.3rem; color: var(--text); }
    .login-header p { color: var(--text-light); font-size: 0.9rem; margin-top: 4px; }

    .input-icon {
      position: relative;
      display: flex;
      align-items: center;
    }
    .input-icon input {
      width: 100%;
      padding: 10px 40px 10px 46px;
      border: 1px solid var(--border);
      border-radius: 8px;
      font-family: 'DM Sans', sans-serif;
      font-size: 0.9rem;
      color: var(--text);
      transition: border-color 0.2s;
    }
    .input-icon input:focus {
      outline: none;
      border-color: var(--primary);
      box-shadow: 0 0 0 3px var(--primary-light);
    }
:host ::ng-deep .field-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-light);
  pointer-events: none;

    }
    .toggle-pass {
      position: absolute;
      right: 10px;
      background: none;
      border: none;
      cursor: pointer;
      color: var(--text-light);
      display: flex;
      align-items: center;
      padding: 4px;
    }
    .toggle-pass:hover { color: var(--primary); }

    .btn-full {
      width: 100%;
      padding: 12px;
      font-size: 0.95rem;
      margin-top: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    }
    .error-msg {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
      color: var(--danger);
      background: var(--danger-light);
      padding: 10px;
      border-radius: 8px;
      font-size: 0.85rem;
      margin-bottom: 10px;
    }
    .success-msg {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
      color: #2f855a;
      background: var(--success-light);
      padding: 10px;
      border-radius: 8px;
      font-size: 0.85rem;
      margin-bottom: 10px;
    }
    .register-link {
      text-align: center;
      margin-top: 20px;
      font-size: 0.85rem;
      color: var(--text-light);
    }
    .register-link a {
      color: var(--primary);
      font-weight: 600;
      text-decoration: none;
    }
    .register-link a:hover { text-decoration: underline; }

    @media (max-width: 480px) {
      .login-card { padding: 28px 20px; }
      .login-header h1 { font-size: 1.1rem; }
    }
  `]
})
export class RegisterComponent {
  nombre = '';
  email = '';
  password = '';
  error = '';
  success = '';
  loading = false;
  showPassword = false;

  constructor(private auth: AuthService, private router: Router) {}

  register() {
    this.loading = true;
    this.error = '';
    this.success = '';

    this.auth.register(this.nombre, this.email, this.password).subscribe({
      next: () => {
        this.success = 'Cuenta creada. Redirigiendo al login...';
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: () => {
        this.error = 'Error al registrar. Verifica los datos.';
        this.loading = false;
      }
    });
  }
}
