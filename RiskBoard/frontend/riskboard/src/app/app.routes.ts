import { Routes } from '@angular/router';
import { Dashboard } from './pages/dashboard/dashboard';
import { DerogationForm } from './pages/derogation-form/derogation-form';
import { DerogationValidation } from './pages/derogation-validation/derogation-validation';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'dashboard', component: Dashboard },
    { path: 'derogations/new', component: DerogationForm },
      { path: 'derogations/pending', component: DerogationValidation },
  { path: '**', redirectTo: 'dashboard' },
];
