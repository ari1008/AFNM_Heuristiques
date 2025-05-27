import {Injectable} from '@angular/core';

export interface Reservation {
  slotId: string;
  reservedBy: string;
  startDate: string; // format: 'YYYY-MM-DD'
  endDate: string;
  checkedIn: boolean;
  status: 'active' | 'used' | 'expired' | 'canceled';
}

@Injectable({ providedIn: 'root' })
export class ReservationService {
  private mockData: Reservation[] = [
    {
      slotId: 'A01',
      reservedBy: 'alice@company.com',
      startDate: '2025-05-25',
      endDate: '2025-05-30',
      checkedIn: false,
      status: 'active'
    },
    {
      slotId: 'F05',
      reservedBy: 'bob@company.com',
      startDate: '2025-05-26',
      endDate: '2025-05-26',
      checkedIn: true,
      status: 'used'
    }
  ];

  getReservationsForSlot(slotId: string): Reservation[] {
    return this.mockData.filter(r => r.slotId === slotId);
  }

  makeReservation(res: Reservation): void {
    this.mockData.push(res);
  }

  isCheckinLate(reservation: Reservation): boolean {
    const now = new Date();
    const limit = new Date(reservation.startDate);
    limit.setHours(11, 0, 0, 0);
    return !reservation.checkedIn && now > limit;
  }

  getAllReservations(): Reservation[] {
    return this.mockData;
  }

  getAllHistory(): Reservation[] {
    return this.mockData;
  }
}

