import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { ProductResponse } from '../../core/models/product.model';
import { ProductCardComponent } from '../products/product-card/product-card.component';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';
import { AuthModelService } from '../../services/auth-model.service';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { PageResponse } from '../../core/models/api-response.model';
import { FormsModule } from '@angular/forms';

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
    FormsModule,
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  providers: [MessageService],
})
export class HomeComponent implements OnInit {
  recommendedProducts: ProductResponse[] = [];
  isLoading = true;
  searchKeyword: string = '';
  searchResults: ProductResponse[] = [];
  isSearching: boolean = false;
  hasSearched: boolean = false;

  searchPage = 0;
  searchSize = 10;
  searchTotalPages = 0;
  searchTotalElements = 0;

  constructor(
    private router: Router,
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

  openRegister(): void {
    this.router.navigate(['/register']);
  }

  onSearch(page: number = 0): void {
    if (!this.searchKeyword.trim()) return;
    this.isSearching = true;
    this.hasSearched = true;
    this.searchPage = page;

    this.productService
      .searchProducts(this.searchKeyword, page, this.searchSize)
      .subscribe({
        next: (res) => {
          this.searchResults = res.data?.content ?? [];
          this.searchTotalPages = res.data?.totalPages ?? 0;
          this.searchTotalElements = res.data?.totalElements ?? 0;
          this.isSearching = false;
        },
        error: () => {
          this.isSearching = false;
        },
      });
  }

  clearSearch(): void {
    this.searchKeyword = '';
    this.hasSearched = false;
    this.searchResults = [];
    this.searchPage = 0;
    this.searchTotalPages = 0;
  }

  onSearchPageChange(page: number): void {
    this.onSearch(page);
  }
}
