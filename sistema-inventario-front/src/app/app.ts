import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent],
  template: `
    <!-- Solo muestra el navbar si el usuario está logueado -->
    @if (auth.isLoggedIn()) {
      <app-navbar />
    }
    <main class="main-content" [class.no-nav]="!auth.isLoggedIn()">
      <router-outlet />
    </main>
  `,
  styles: [`
    .main-content {
      margin-left: 240px;
      padding: 32px;
      min-height: 100vh;
      background: #f5f6fa;
    }
    .main-content.no-nav {
      margin-left: 0;
    }
  `]
})
export class AppComponent {
  constructor(public auth: AuthService) {}
}
