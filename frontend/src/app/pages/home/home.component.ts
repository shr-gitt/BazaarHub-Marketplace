import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { ProductResponse } from '../../core/models/product.model';
import { ProductCardComponent } from '../products/product-card/product-card.component';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';
import { AuthModelService } from '../../services/auth-model.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ProductCardComponent,
    LoadingSpinnerComponent,
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  recommendedProducts: ProductResponse[] = [];
  isLoading = true;

  constructor(
    private productService: ProductService,
    public authService: AuthModelService,
  ) {}

  ngOnInit(): void {
    this.productService.getRecommended(0, 8).subscribe({
      next: (res) => {
        this.recommendedProducts = res.data?.content ?? [];
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  getImageSrc(product: ProductResponse): string {
    if (!product.imageUrl) return 'assets/no-image.png';
    if (product.imageUrl.startsWith('http')) return product.imageUrl;
    return `http://localhost:9000/bazaarhub/${product.imageUrl}`;
  }
}
