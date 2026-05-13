import { Routes } from '@angular/router';
import { LoginComponent } from './pages/auth/login/login.component';
import { VendorDashboardComponent } from './pages/vendor/vendor-dashboard/vendor-dashboard.component';
import { CustomerDashboardComponent } from './pages/customer/customer-dashboard/customer-dashboard.component';
import { AdminDashboardComponent } from './pages/admin/admin-dashboard/admin-dashboard.component';
import { RegisterComponent } from './pages/auth/register/register.component';
import { CustomerProfileSetupComponent } from './pages/customer/customer-profile-setup/customer-profile-setup.component';
import { VendorProfileSetupComponent } from './pages/vendor/vendor-profile-setup/vendor-profile-setup.component';
import { VendorSelectionComponent } from './pages/vendor/vendor-selection/vendor-selection.component';
import { VendorProfileComponent } from './pages/vendor/vendor-profile/vendor-profile.component';
import { ProfileComponent } from './pages/profile/profile.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full',
  },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  { path: 'customer/profile-setup', component: CustomerProfileSetupComponent },
  { path: 'vendor/profile-setup', component: VendorProfileSetupComponent },

  { path: 'vendor/selection', component: VendorSelectionComponent },
  { path: 'vendor/profile', component: VendorProfileComponent },

  { path: 'profile', component: ProfileComponent },

  { path: 'admin/dashboard', component: AdminDashboardComponent },
  { path: 'vendor/dashboard', component: VendorDashboardComponent },
  { path: 'customer/dashboard', component: CustomerDashboardComponent },
  { path: '**', redirectTo: 'login' },
];
