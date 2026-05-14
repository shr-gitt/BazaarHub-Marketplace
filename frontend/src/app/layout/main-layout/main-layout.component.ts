import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar1/navbar.component';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, SidebarComponent],
  template: `
    <div class="main-layout">
      <app-navbar (toggleSidebar)="sidebarOpen = !sidebarOpen"></app-navbar>

      <div class="layout-body">
        <app-sidebar
          [isOpen]="sidebarOpen"
          (close)="sidebarOpen = false"
        ></app-sidebar>

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
        min-height: 100vh;
        display: flex;
        flex-direction: column;
        background-color: var(--bg-secondary);
      }
      .layout-body {
        display: flex;
        flex: 1;
        position: relative;
        overflow: hidden;
      }
      .content-area {
        flex: 1;
        padding: 2rem;
        overflow-y: auto;
        transition: margin-left 0.3s ease;
        height: calc(100vh - 70px); /* 70px navbar height */
      }
      .content-wrapper {
        max-width: 1200px;
        margin: 0 auto;
      }

      /* Desktop layout */
      @media (min-width: 992px) {
        .content-area {
          margin-left: 250px; /* sidebar width */
        }
      }

      /* Mobile layout */
      @media (max-width: 991px) {
        .content-area {
          padding: 1rem;
        }
      }
    `,
  ],
})
export class MainLayoutComponent {
  sidebarOpen = false;
}
