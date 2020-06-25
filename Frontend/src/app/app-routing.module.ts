import { Routes, RouterModule } from "@angular/router";
import { LoginComponent } from "./main/login/login.component";
import { HomeComponent } from "./main/home/home.component";
import { AdminGuard } from "./guards/admin.guard";
import { ErrorUnauthenticatedComponent } from "./main/error-unauthenticated/error-unauthenticated.component";
import { ErrorUnauthorizedComponent } from "./main/error-unauthorized/error-unauthorized.component";
import { ErrorNotFoundComponent } from "./main/error-not-found/error-not-found.component";
import { ErrorInternalServerComponent } from "./main/error-internal-server/error-internal-server.component";
import { ErrorComponent } from "./main/error/error.component";
import { NgModule } from '@angular/core';
import { CreateCertificateComponent } from './main/create-certificate/create-certificate.component';
import { ListCertificatesComponent } from './main/list-certificates/list-certificates.component';
import { CreateSubjectComponent } from './main/create-subject/create-subject.component';
import { CreateRootCertificateComponent } from './main/create-root-certificate/create-root-certificate.component';

const appRoutes: Routes = [


    //==============================================================================//
    // REDIRECTIONS
    //==============================================================================//
    {
        path        : '',
        redirectTo  : 'pages/home',
        pathMatch   : 'full',
    },


    //==============================================================================//
    // INITIAL
    //==============================================================================//
    {
        path        : 'pages/login',
        component   : LoginComponent,
    },


    //==============================================================================//
    // PAGES
    //==============================================================================//
    {
        path        : 'pages/home',
        component   : HomeComponent,
        canActivate : [AdminGuard],
    },
    {
        path        : 'pages/create-subject',
        component   : CreateSubjectComponent,
        // canActivate : [AdminGuard],
    },
    {
        path        : 'pages/create-root-certificate',
        component   : CreateRootCertificateComponent,
        canActivate : [AdminGuard],
    },
    {
        path        : 'pages/create-certificate',
        component   : CreateCertificateComponent,
        canActivate : [AdminGuard],
    },
    {
        path        : 'pages/list-certificates',
        component   : ListCertificatesComponent,
        canActivate : [AdminGuard],
    },
    {
        path        : 'pages/list-certificates',
        component   : ListCertificatesComponent,
        canActivate : [AdminGuard],
    },


    //==============================================================================//
    // ERRORS
    //==============================================================================//
    {
        // 401
        path        : 'errors/unauthenticated',
        component   : ErrorUnauthenticatedComponent,
    },
    {
        // 403
        path        : 'errors/unauthorized',
        component   : ErrorUnauthorizedComponent,
    },
    {
        // 404
        path        : 'errors/page-not-found',
        component   : ErrorNotFoundComponent,
    },
    {
        // 500
        path        : 'errors/internal-server',
        component   : ErrorInternalServerComponent,
    },
    {
        // Unrecognized endpoint
        path        : '**',
        component   : ErrorComponent,
    },
];

@NgModule({
    imports: [RouterModule.forRoot(appRoutes)],
    exports: [RouterModule],
})
export class AppRoutingModule { }