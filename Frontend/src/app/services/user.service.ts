import { map, catchError } from 'rxjs/operators';
import { UserTokenState } from './../models/userTokenState';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { UserLoginRequest } from './../models/userLoginRequest';
import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  
  url = environment.baseUrl + environment.auth;
  jwt_access_token = null;
  req: UserTokenState
  loggedInUserSubject: BehaviorSubject<UserTokenState>;
  loggedInUser: Observable<UserTokenState>;
  loggedInSuccess: BehaviorSubject<UserTokenState> = new BehaviorSubject<UserTokenState>(null);

  constructor(private httpClient: HttpClient, private router: Router) {
    this.loggedInUserSubject = new BehaviorSubject<UserTokenState>(
      JSON.parse(localStorage.getItem('LoggedInUser'))
    );
    this.loggedInUser = this.loggedInUserSubject.asObservable();
  }

  getLoggedInUser(): UserTokenState {
    return this.loggedInUserSubject.value;
  }

  login(user: UserLoginRequest) {
    return this.httpClient.post(this.url + "/login", user).pipe(map((res: UserTokenState) => {
      this.jwt_access_token = res.jwtAccessToken;
      localStorage.setItem('LoggedInUser', JSON.stringify(res));
      this.loggedInUserSubject.next(res);
    }));
  }

  getToken() {
    return this.jwt_access_token;
  }

  logout() {
    this.jwt_access_token = null;
    localStorage.removeItem('LoggedInUser');
    this.router.navigate(['/']);
  }

  isLoggedIn() {
    return localStorage.getItem('LoggedInUser') !== null;
  }

}
