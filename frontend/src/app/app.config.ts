
import { provideRouter } from '@angular/router';

import {ParkingLotComponent} from './parking-lot/parking-lot.component';
import {SlotDetailComponent} from './slot-detail/slot-detail.component';
import {authGuard} from './auth-component/auth.guard';
import {LoginComponent} from './login-component/login.component';
import {UserManagementComponent} from './user-component/user-management.component';
import {canActivateSecretaryOnly} from './auth-component/secretary.guard';
import {ReservationAdminComponent} from './reservation-history/reservation-admin.component';
import {CheckinComponent} from './checkin-component/checkin.component';

export const appConfig = {
  providers: [
    provideRouter([
      { path: 'check-in/:slotId', component: CheckinComponent, canActivate: [authGuard] },
      { path: 'admin/reservations', component: ReservationAdminComponent, canActivate: [canActivateSecretaryOnly] },
      { path: 'admin/users', component: UserManagementComponent, canActivate: [canActivateSecretaryOnly] },
      { path: 'login', component: LoginComponent },
      { path: '', component: ParkingLotComponent, canActivate: [authGuard] },
      { path: 'slot/:id', component: SlotDetailComponent, canActivate: [authGuard] },

      // ✅ Route standalone vers dashboard
      {
        path: 'dashboard',
        canActivate: [authGuard], // ou un guard manager si tu préfères
        loadComponent: () => import('./dashboard-manager-component/dashboard-manager.component')
          .then(m => m.DashboardManagerComponent)
      }
    ])
  ]
};
