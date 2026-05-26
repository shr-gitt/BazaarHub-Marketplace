import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { ProductResponse } from '../../core/models/product.model';
import { ProductCardComponent } from '../products/product-card/product-card.component';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';
import { AuthModelService } from '../../services/auth-model.service';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ProductCardComponent,
    LoadingSpinnerComponent,
    ButtonModule,
    ToastModule,
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  providers: [MessageService],
})
export class HomeComponent implements OnInit {
  recommendedProducts: ProductResponse[] = [];
  isLoading = true;

  constructor(
    private productService: ProductService,
    public authService: AuthModelService,
    public messageService: MessageService,
  ) {}

  ngOnInit(): void {
    this.productService.getAll(0, 8).subscribe({
      next: (res) => {
        this.recommendedProducts = res.data?.content ?? [];
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load products. Please try again.',
        });
      },
    });
  }

  openRegister() {
    this.authService.open('REGISTER');
  }
}
