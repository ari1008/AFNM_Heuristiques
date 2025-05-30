import {provideRouter} from '@angular/router';

import {ParkingLotComponent} from './parking-lot/parking-lot.component';
import {authGuard} from './auth-component/auth.guard';
import {LoginComponent} from './login-component/login.component';
import {canActivateSecretaryOnly} from './auth-component/secretary.guard';
import {provideHttpClient} from '@angular/common/http';
import {AdminUserComponent} from './admin-user-component/admin.user.component';
import {AdminReservationComponent} from './admin-reservation-component/admin.reservation.component';
import {ReservationComponent} from './reservation-component/reservation-component';
import {CheckinComponent} from './checkin-component/checkin.component';

export const appConfig = {
  providers: [
    provideHttpClient(),
    provideRouter([
      {path: 'admin/users', component: AdminUserComponent, canActivate: [canActivateSecretaryOnly]},
      {path: 'admin/reservations', component: AdminReservationComponent, canActivate: [canActivateSecretaryOnly]},
      {path: 'login', component: LoginComponent},
      {path: '', component: ParkingLotComponent, canActivate: [authGuard]},
      {path: 'reservation/:id', component: ReservationComponent, canActivate: [authGuard]},
      {path: 'checkin', component: CheckinComponent, canActivate: [authGuard]}
    ])
  ],

};
