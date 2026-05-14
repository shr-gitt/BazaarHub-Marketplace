import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="auth-layout">
      <div class="auth-card">
        <div class="logo">
          <h1>BazaarHub</h1>
        </div>
        <router-outlet></router-outlet>
      </div>
    </div>
  `,
  styles: [
    `
      .auth-layout {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: var(--bg-secondary);
        padding: 1rem;
      }
      .auth-card {
        background: var(--bg-primary);
        border-radius: var(--radius-lg);
        box-shadow: var(--shadow-md);
        width: 100%;
        max-width: 480px;
        padding: 2.5rem;
      }
      .logo {
        text-align: center;
        margin-bottom: 2rem;
      }
      .logo h1 {
        color: var(--primary-color);
        font-size: 2rem;
        font-weight: 700;
        margin: 0;
      }
    `,
  ],
})
export class AuthLayoutComponent {}
