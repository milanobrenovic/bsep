import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { UserService } from '../services/user.service';
import { LoggedInUser } from 'app/models/loggedInUser';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  public loggedInUser: LoggedInUser;

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
      if (this.loggedInUser.role == "ROLE_ADMIN") {
        return true;
      } else {
        this.router.navigate(['/errors/unauthorized']);
        return false;
      }
    }

    this.router.navigate(['/errors/unauthenticated']);
    return false;
  }
  
}
