import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MatDialogModule } from "@angular/material/dialog";
import { MatCardModule } from "@angular/material/card";

import { UnauthenticatedErrorComponent } from './components/unauthenticated-error/unauthenticated-error.component';
import { UnauthorizedErrorComponent } from './components/unauthorized-error/unauthorized-error.component';
import { ErrorComponent } from './components/error/error.component';
import { LoginComponent } from './components/login/login.component';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { ErrorInterceptor } from './interceptors/error.interceptor';
import { AdminGuard } from './guards/admin.guard';

@NgModule({
  declarations: [
    AppComponent,
    UnauthenticatedErrorComponent,
    UnauthorizedErrorComponent,
    ErrorComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    
    // import HttpClientModule after BrowserModule.
    HttpClientModule,

    AppRoutingModule,
    BrowserAnimationsModule,
    MatDialogModule,
    MatCardModule,
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
  bootstrap: [AppComponent]
})
export class AppModule { }
