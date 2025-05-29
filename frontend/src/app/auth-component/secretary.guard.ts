import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import {ROLES} from '../model/user.model.two';

export const canActivateSecretaryOnly: CanActivateFn = () => {
  const role = localStorage.getItem('role');
  const router = inject(Router);
  if (role !== ROLES.SECRETARY.name) {
    router.navigate(['/']);
    return false;
  }
  return true;
};
