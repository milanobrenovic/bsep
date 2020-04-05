import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, Routes } from '@angular/router';
import { MatMomentDateModule } from '@angular/material-moment-adapter';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { TranslateModule } from '@ngx-translate/core';
import 'hammerjs';

import { FuseModule } from '@fuse/fuse.module';
import { FuseSharedModule } from '@fuse/shared.module';
import { FuseProgressBarModule, FuseSidebarModule, FuseThemeOptionsModule } from '@fuse/components';

import { fuseConfig } from 'app/fuse-config';

import { AppComponent } from 'app/app.component';
import { LayoutModule } from 'app/layout/layout.module';
import { LoginComponent } from './main/login/login.component';
import { HomeComponent } from './main/home/home.component';
import { ErrorComponent } from './main/error/error.component';
import { ErrorUnauthenticatedComponent } from './main/error-unauthenticated/error-unauthenticated.component';
import { ErrorUnauthorizedComponent } from './main/error-unauthorized/error-unauthorized.component';
import { ErrorNotFoundComponent } from './main/error-not-found/error-not-found.component';
import { ErrorInternalServerComponent } from './main/error-internal-server/error-internal-server.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { ErrorInterceptor } from './interceptors/error.interceptor';
import { AdminGuard } from './guards/admin.guard';

const appRoutes: Routes = [


    //==============================================================================//
    // HOME
    //==============================================================================//
    {
        path        : '',
        component   : HomeComponent,
    },


    //==============================================================================//
    // PAGES
    //==============================================================================//
    {
        path        : 'pages/login',
        component   : LoginComponent,
    },


    //==============================================================================//
    // ERRORS
    //==============================================================================//
    {
        path        : 'errors/unauthenticated',
        component   : ErrorUnauthenticatedComponent,
    },
    {
        path        : 'errors/unauthorized',
        component   : ErrorUnauthorizedComponent,
    },
    {
        path        : 'errors/page-not-found',
        component   : ErrorNotFoundComponent,
    },
    {
        path        : 'errors/internal-server',
        component   : ErrorInternalServerComponent,
    },
    {
        path        : '**',
        component   : ErrorComponent,
    }
];

@NgModule({
    declarations: [
        AppComponent,
        HomeComponent,
        LoginComponent,
        ErrorComponent,
        ErrorUnauthenticatedComponent,
        ErrorUnauthorizedComponent,
        ErrorNotFoundComponent,
        ErrorInternalServerComponent,
    ],
    imports     : [
        BrowserModule,
        BrowserAnimationsModule,
        HttpClientModule,

        RouterModule.forRoot(appRoutes),
        TranslateModule.forRoot(),

        // Material
        MatMomentDateModule,
        MatButtonModule,
        MatIconModule,
        MatCheckboxModule,
        MatFormFieldModule,
        MatInputModule,

        // Fuse modules
        FuseModule.forRoot(fuseConfig),
        FuseProgressBarModule,
        FuseSharedModule,
        FuseSidebarModule,
        FuseThemeOptionsModule,

        // App modules
        LayoutModule,
    ],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: TokenInterceptor,
            multi: true,
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorInterceptor,
            multi: true,
        },
        AdminGuard,
    ],
    bootstrap   : [
        AppComponent
    ]
})
export class AppModule { }
