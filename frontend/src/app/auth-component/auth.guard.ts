import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { map } from 'rxjs/operators';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.currentUser.pipe(
    map(user => {
      if (!user) {
        console.log('Redirection vers /login');
        return router.parseUrl('/login');
      }
      return true;
    })
  );
};
