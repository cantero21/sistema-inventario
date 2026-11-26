import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink, LucideAngularModule],
  template: `
    <div class="login-page">
      <div class="login-card">
        <div class="login-header">
          <div class="login-logo">
            <lucide-icon name="box" [size]="32"></lucide-icon>
          </div>
          <h1>Fercell</h1>
          <p>Ingresa tus credenciales</p>
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
                   placeholder="••••••••"
                   (keyup.enter)="login()">
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

        <button class="btn btn-primary btn-full" (click)="login()" [disabled]="loading">
          @if (loading) {
            Ingresando...
          } @else {
            <lucide-icon name="log-out" [size]="16"></lucide-icon> Iniciar sesión
          }
        </button>

        <p class="register-link">
          ¿No tienes cuenta? <a routerLink="/register">Regístrate</a>
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
      background: linear-gradient(135deg, var(--primary-light) 0%, var(--bg) 50%, var(--accent-light) 100%);
      padding: 16px;
    }
    .login-card {
      background: var(--bg-card);
      padding: 40px;
      border-radius: 16px;
      box-shadow: var(--shadow-lg);
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
    .login-header h1 {
      font-size: 1.3rem;
      color: var(--text);
    }
    .login-header p {
      color: var(--text-light);
      font-size: 0.9rem;
      margin-top: 4px;
    }

    /* Input con icono */
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
export class LoginComponent {
  email = '';
  password = '';
  error = '';
  loading = false;
  showPassword = false;

  constructor(private auth: AuthService, private router: Router) {}

  login() {
    this.loading = true;
    this.error = '';

    this.auth.login(this.email, this.password).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.error = 'Email o contraseña incorrectos';
        this.loading = false;
      }
    });
  }
}
