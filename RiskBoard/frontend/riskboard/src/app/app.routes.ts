import { Routes } from '@angular/router';
import { Dashboard } from './pages/dashboard/dashboard';
import { DerogationForm } from './pages/derogation-form/derogation-form';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'dashboard', component: Dashboard },
    { path: 'derogations/new', component: DerogationForm },
  { path: '**', redirectTo: 'dashboard' },
];
