import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, SidebarComponent],
  template: `
    <div class="main-layout">
      <app-navbar (toggleSidebar)="sidebarOpen = !sidebarOpen"></app-navbar>

      <div class="layout-body">
        <app-sidebar (close)="sidebarOpen = false"></app-sidebar>

        <main class="content-area" [class.sidebar-open]="sidebarOpen">
          <div class="content-wrapper">
            <router-outlet></router-outlet>
          </div>
        </main>
      </div>
    </div>
  `,
  styles: [
    `
      .main-layout {
        height: 100vh;
        display: flex;
        flex-direction: column;
        background-color: var(--bg-secondary);
        overflow: hidden;
      }

      .layout-body {
        flex: 1;
        display: flex;
        min-height: 0;
      }

      .content-area {
        flex: 1;

        overflow-y: auto;
        overflow-x: hidden;

        padding: 2rem;
        padding-bottom: 120px;

        min-height: 0;

        transition: margin-left 0.3s ease;

        -webkit-overflow-scrolling: touch;
      }

      .content-wrapper {
        width: 100%;
        max-width: 1200px;
        margin: 0 auto;
      }

      @media (min-width: 992px) {
        .content-area {
          margin-left: 250px;
        }
      }

      @media (max-width: 991px) {
        .content-area {
          padding: 1rem;
          margin-left: 0;
        }
      }
    `,
  ],
})
export class MainLayoutComponent {
  sidebarOpen = false;
}
