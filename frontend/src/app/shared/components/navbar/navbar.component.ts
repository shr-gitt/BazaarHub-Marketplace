import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent {
  constructor(
    public auth: AuthService,
    private router: Router,
  ) {}

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
