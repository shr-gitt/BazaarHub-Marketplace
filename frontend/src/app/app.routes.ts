import { Routes } from '@angular/router';

// Guards
import { authGuard } from './core/guards/auth.guard';

// Auth Pages
import { LoginComponent } from './pages/auth/login/login.component';
import { RegisterComponent } from './pages/auth/register/register.component';

// Profile Setup
import { CustomerProfileSetupComponent } from './pages/customer/customer-profile-setup/customer-profile-setup.component';
import { VendorProfileSetupComponent } from './pages/vendor/vendor-profile-setup/vendor-profile-setup.component';

// Dashboards
import { AdminDashboardComponent } from './pages/admin/admin-dashboard/admin-dashboard.component';
import { VendorDashboardComponent } from './pages/vendor/vendor-dashboard/vendor-dashboard.component';
import { CustomerDashboardComponent } from './pages/customer/customer-dashboard/customer-dashboard.component';

// Features (Products)
import { RecommendedProductsComponent } from './pages/products/recommended-products/recommended-products.component';

// Pages
import { CartComponent } from './pages/cart/cart.component';
import { CategoryListComponent } from './pages/categories/category-list/category-list.component';
import { CategoryFormComponent } from './pages/categories/category-form/category-form.component';
import { NotificationsComponent } from './pages/notifications/notifications.component';
import { AuthLayoutComponent } from './layout/auth-layout/auth-layout.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { DashboardLayoutComponent } from './layout/dashboard-layout/dashboard-layout.component';
import { roleGuard } from './core/guards/role.guard';
import { ProductListComponent } from './pages/products/product-list/product-list.component';
import { ProductDetailComponent } from './pages/products/product-detail/product-detail.component';
import { VendorSelectionComponent } from './pages/vendor/vendor-selection/vendor-selection.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { PublicLayoutComponent } from './layout/public-layout/public-layout.component';
import { HomeComponent } from './pages/home/home.component';
import { EditProductComponent } from './pages/products/edit-product/edit-product.component';
import { CreateProductComponent } from './pages/products/create-product/create-product.component';
import { OrderListComponent } from './pages/orders/order-list/order-list.component';
import { OrderCheckoutComponent } from './pages/orders/order-checkout/order-checkout.component';
import { OrderPaymentComponent } from './pages/payments/order-payment/order-payment.component';

export const routes: Routes = [
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },
    ],
  },
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      {
        path: 'customer/profile-setup',
        component: CustomerProfileSetupComponent,
        canActivate: [authGuard],
      },
      {
        path: 'vendor/profile-setup',
        component: VendorProfileSetupComponent,
        canActivate: [authGuard],
      },

      {
        path: 'vendor/selection',
        component: VendorSelectionComponent,
        canActivate: [roleGuard],
        data: { expectedRole: 'VENDOR' },
      },
    ],
  },

  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'products', component: ProductListComponent },
      { path: 'products/recommended', component: RecommendedProductsComponent },
      { path: 'product/:id', component: ProductDetailComponent },
      {
        path: 'cart',
        component: CartComponent,
        canActivate: [roleGuard],
        data: { expectedRole: 'CUSTOMER' },
      },

      {
        path: 'orders',
        component: OrderListComponent,
        canActivate: [roleGuard],
        data: { expectedRole: 'CUSTOMER' },
      },
      {
        path: 'checkout',
        component: OrderCheckoutComponent,
        canActivate: [roleGuard],
        data: { expectedRole: 'CUSTOMER' },
      },
      {
        path: 'payment',
        component: OrderPaymentComponent,
        canActivate: [roleGuard],
        data: { expectedRole: 'CUSTOMER' },
      },

      //ToDO: Add customer orders flow

      // {
      //   path: 'customer/orders',
      //   component: OrderListComponent,
      //   canActivate: [roleGuard],
      //   data: { expectedRole: 'CUSTOMER' },
      // },
      {
        path: 'customer/dashboard',
        component: CustomerDashboardComponent,
        canActivate: [roleGuard],
        data: { expectedRole: 'CUSTOMER' },
      },
      { path: 'notifications', component: NotificationsComponent },
      {
        path: 'profile',
        component: ProfileComponent,
        canActivate: [roleGuard],
        data: { expectedRole: ['VENDOR', 'CUSTOMER', 'ADMIN'] },
      },
    ],
  },

  // Dashboard Routes (Vendor & Admin)
  {
    path: '',
    component: DashboardLayoutComponent,
    canActivate: [authGuard],
    children: [
      // Vendor

      {
        path: 'vendor/dashboard',
        component: VendorDashboardComponent,
        canActivate: [roleGuard],
        data: { expectedRole: 'VENDOR' },
      },

      {
        path: 'vendor/products',
        component: ProductListComponent,
        canActivate: [roleGuard],
        data: { expectedRole: 'VENDOR' },
      },
      {
        path: 'vendor/products/create',
        component: CreateProductComponent,
        canActivate: [roleGuard],
        data: { expectedRole: 'VENDOR' },
      },
      {
        path: 'vendor/products/edit/:id',
        component: EditProductComponent,
        canActivate: [roleGuard],
        data: { expectedRole: 'VENDOR' },
      },

      //ToDO: Add vendor Order route

      // {
      //   path: 'vendor/orders',
      //   component: OrderListComponent,
      //   canActivate: [roleGuard],
      //   data: { expectedRole: 'VENDOR' },
      // },

      // Admin
      {
        path: 'admin/dashboard',
        component: AdminDashboardComponent,
        canActivate: [roleGuard],
        data: { expectedRole: 'ADMIN' },
      },
      {
        path: 'admin/categories',
        component: CategoryListComponent,
        canActivate: [roleGuard],
        data: { expectedRole: 'ADMIN' },
      },
      {
        path: 'admin/categories/create',
        component: CategoryFormComponent,
        canActivate: [roleGuard],
        data: { expectedRole: 'ADMIN' },
      },
      {
        path: 'admin/categories/edit/:id',
        component: CategoryFormComponent,
        canActivate: [roleGuard],
        data: { expectedRole: 'ADMIN' },
      },
      //ToDO: Admin order

      // {
      //   path: 'admin/orders',
      //   component: OrderListComponent,
      //   canActivate: [roleGuard],
      //   data: { expectedRole: 'ADMIN' },
      // },
    ],
  },

  { path: '**', redirectTo: 'home' },
];
