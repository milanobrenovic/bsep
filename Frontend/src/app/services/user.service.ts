import { map } from 'rxjs/operators';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserLoginRequest } from './../models/userLoginRequest';
import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { LoggedInUser } from 'app/models/loggedInUser';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  
  public url = environment.baseUrl + environment.auth;
  public jwtAccessToken = null;
  public req: LoggedInUser;
  public loggedInUserSubject: BehaviorSubject<LoggedInUser>;
  public loggedInUser: Observable<LoggedInUser>;
  public loggedInSuccess: BehaviorSubject<LoggedInUser> = new BehaviorSubject<LoggedInUser>(null);

  constructor(private httpClient: HttpClient, private router: Router) {
    this.loggedInUserSubject = new BehaviorSubject<LoggedInUser>(
      JSON.parse(localStorage.getItem('LoggedInUser'))
    );
    this.loggedInUser = this.loggedInUserSubject.asObservable();
  }

  public getLoggedInUser(): LoggedInUser {
    return this.loggedInUserSubject.value;
  }

  public login(user: UserLoginRequest) {
    return this.httpClient.post(this.url, user).pipe(map((res: LoggedInUser) => {
      this.jwtAccessToken = res.userTokenState.jwtAccessToken;
      localStorage.setItem('LoggedInUser', JSON.stringify(res));
      this.loggedInUserSubject.next(res);
    }));
  }

  public getToken() {
    return this.jwtAccessToken;
  }

  public logout() {
    this.jwtAccessToken = null;
    localStorage.removeItem('LoggedInUser');
    this.router.navigate(['/pages/login']);
  }

  public isLoggedIn() {
    return localStorage.getItem('LoggedInUser') !== null;
  }
  
}
