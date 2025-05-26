import { Injectable } from '@angular/core';
import { Reservation } from './reservation.service';

@Injectable({ providedIn: 'root' })
export class QueueService {
  send(res: Reservation): void {
    console.log('[QUEUE] Reservation placed in queue:', res);

    // Plus tard, tu peux faire une vraie requête :
    // this.http.post('/api/queue', res).subscribe();
  }
}
