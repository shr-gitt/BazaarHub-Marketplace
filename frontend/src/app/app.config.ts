import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import {
  provideHttpClient,
  withFetch,
  withInterceptors,
} from '@angular/common/http';
import { authInterceptor } from './core/interceptors/auth.interceptor';

import { providePrimeNG } from 'primeng/config';
import { definePreset } from '@primeng/themes';
import Aura from '@primeng/themes/aura';
import { provideAnimations } from '@angular/platform-browser/animations';

const Preset = definePreset(Aura, {
  semantic: {
    primary: {
      50: '#f0faeb',
      100: '#d9f2cc',
      200: '#b3e699',
      300: '#8cd966',
      400: '#60BB46',
      500: '#60BB46',
      600: '#4ea336',
      700: '#3d8229',
      800: '#2c611d',
      900: '#1b4112',
      950: '#0d2009',
    },
  },
});

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withFetch(), withInterceptors([authInterceptor])),
    provideAnimations(),
    providePrimeNG({
      theme: {
        preset: Preset,
        options: {
          darkModeSelector: 'none',
        },
      },
    }),
  ],
};
