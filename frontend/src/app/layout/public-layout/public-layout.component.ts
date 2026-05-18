import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthModalComponent } from '../../shared/components/auth-model/auth-model.component';
import { AuthModelService } from '../../services/auth-model.service';

@Component({
  selector: 'app-public-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, AuthModalComponent],
  templateUrl: './public-layout.component.html',
  styleUrls: ['./public-layout.component.scss'],
})
export class PublicLayoutComponent implements OnInit {
  showAuthModal = false;
  authMode: 'LOGIN' | 'REGISTER' = 'LOGIN';

  constructor(public authService: AuthModelService) {}

  ngOnInit() {
    this.authService.showModal$.subscribe(
      (show) => (this.showAuthModal = show),
    );
    this.authService.mode$.subscribe((mode) => (this.authMode = mode));
  }
}
