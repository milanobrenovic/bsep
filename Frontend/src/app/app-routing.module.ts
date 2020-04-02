import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UnauthenticatedErrorComponent } from './components/unauthenticated-error/unauthenticated-error.component';
import { UnauthorizedErrorComponent } from './components/unauthorized-error/unauthorized-error.component';
import { ErrorComponent } from './components/error/error.component';
import { LoginComponent } from './components/login/login.component';

const routes: Routes = [
  {
    path: '',
    component: LoginComponent,
  },
  {
    path: 'error/unauthenticated',
    component: UnauthenticatedErrorComponent,
  },
  {
    path: 'error/unauthorized',
    component: UnauthorizedErrorComponent,
  },
  {
    path: '**',
    component: ErrorComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
