import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const canActivateSecretaryOnly: CanActivateFn = () => {
  const role = localStorage.getItem('role');
  const router = inject(Router);
  if (role !== 'secretary') {
    router.navigate(['/']);
    return false;
  }
  return true;
};
