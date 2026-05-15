import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarComponent, NavbarComponent],
  templateUrl: './dashboard-layout.component.html',
  styleUrls: ['./dashboard-layout.component.scss'],
})
export class DashboardLayoutComponent {}
