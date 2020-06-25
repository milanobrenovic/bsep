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
import { LoggedInUser } from 'app/models/loggedInUser';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  
  public loggedInUser: LoggedInUser;

  constructor(
    public userService: UserService,
  ) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.loggedInUser = this.userService.getLoggedInUser();

    console.log(this.loggedInUser);

    if (this.loggedInUser) {
      if (this.loggedInUser.userTokenState.jwtAccessToken) {
        request = request.clone(
          {
            setHeaders: {
              Authorization: `Bearer ${this.loggedInUser.userTokenState.jwtAccessToken}`
            }
          }
        );
      }
    }

    return next.handle(request);
  }
  
}
