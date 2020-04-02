import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { catchError } from 'rxjs/operators';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(
    private router: Router,
    private dialogRef: MatDialog,
  ) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(catchError((e) => {
      if (e.status === 401) {
        this.dialogRef.closeAll();
        localStorage.removeItem('LoggedInUser');
        this.router.navigate(['/error/unauthenticated']);
      } else if (e.status === 403) {
        this.dialogRef.closeAll();
        this.router.navigate(['/error/unauthorized']);
      }
      return throwError(e);
    }));
  }
}
