import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserTokenState } from '../models/userTokenState';
import { UserService } from '../services/user.service';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  
  userTokenState: UserTokenState;

  constructor(
    public userService: UserService,
  ) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.userTokenState = JSON.parse(localStorage.getItem("LoggedInUser"));

    if (this.userTokenState) {
      if (this.userTokenState.jwtAccessToken) {
        request = request.clone(
          {
            setHeaders: {
              Authorization: `Bearer ${this.userTokenState.jwtAccessToken}`
            }
          }
        );
      }
    }

    return next.handle(request);
  }
  
}
