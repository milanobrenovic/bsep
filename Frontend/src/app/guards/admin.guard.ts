import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { UserTokenState } from '../models/userTokenState';
import { UserService } from '../services/user.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  loggedInUser: UserTokenState;

  constructor(
    private router: Router,
    private userService: UserService,
  ) { }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    this.loggedInUser = this.userService.getLoggedInUser();

    if (this.loggedInUser) {
      if (this.loggedInUser) {
        return true;
      } else {
        this.router.navigate(['/error/non-authorized']);
        return false;
      }
    }

    this.router.navigate(['/error/non-authenticated']);
    return false;
  }
  
}
