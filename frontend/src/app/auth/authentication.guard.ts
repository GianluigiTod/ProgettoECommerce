import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../service/auth.service';

export const authenticationGuard: CanActivateFn = (route, state) => {
  let auth = inject(AuthService);
  let router = inject(Router)
  if(auth.isAuthenticated()){
    return true
  }else{
    router.navigate(['login'])
    return false
  }
};
