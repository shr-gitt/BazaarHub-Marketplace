import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="auth-layout">
      <div class="auth-left">
        <h1>BazaarHub</h1>
        <p>
          Connect customers, empower vendors, and simplify administration — all
          in one powerful platform.
        </p>
      </div>

      <div class="auth-right">
        <div class="auth-card">
          <router-outlet></router-outlet>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .auth-layout {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: space-between;

        padding: 2rem 4rem;
        background-color: var(--bg-secondary);
        gap: 4rem;
      }

      .auth-left {
        flex: 1;
        max-width: 500px;
      }

      .auth-left h1 {
        font-size: 4rem;
        color: var(--primary-color);
        margin-bottom: 1rem;
        font-weight: 700;
      }

      .auth-left p {
        font-size: 1.5rem;
        line-height: 1.5;
        color: var(--text-secondary);
      }

      .auth-right {
        flex: 1;
        display: flex;
        justify-content: flex-end;
      }

      .auth-card {
        border-radius: var(--radius-lg);
        box-shadow: var(--shadow-md);
        width: 100%;
        max-width: 560px;
        padding: 2rem;
      }

      /* Mobile */
      @media (max-width: 992px) {
        .auth-layout {
          flex-direction: column;
          justify-content: center;
          padding: 2rem;
          text-align: center;
        }

        .auth-left {
          max-width: 100%;
        }

        .auth-left h1 {
          font-size: 3rem;
        }

        .auth-left p {
          font-size: 1.2rem;
        }

        .auth-right {
          width: 100%;
          justify-content: center;
        }

        .auth-card {
          max-width: 480px;
        }
      }
    `,
  ],
})
export class AuthLayoutComponent {}
