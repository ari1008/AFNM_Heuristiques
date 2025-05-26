
import { provideRouter } from '@angular/router';

import {ParkingLotComponent} from './parking-lot/parking-lot.component';
import {SlotDetailComponent} from './slot-detail/slot-detail.component';
import {authGuard} from './auth-component/auth.guard';
import {LoginComponent} from './login-component/login.component';

export const appConfig = {
  providers: [
    provideRouter([
      { path: 'login', component: LoginComponent },
      { path: '', component: ParkingLotComponent, canActivate: [authGuard] },
      { path: 'slot/:id', component: SlotDetailComponent, canActivate: [authGuard] }
    ])
  ]
};
