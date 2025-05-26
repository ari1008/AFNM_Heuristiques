import { Injectable } from '@angular/core';

export interface Reservation {
  slotId: string;
  reservedBy: string;
  startDate: string;
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
    }
  ];
  getWorkingDaysCount(start: Date, end: Date): number {
    let count = 0;
    const date = new Date(start);
    while (date <= end) {
      const day = date.getDay();
      if (day !== 0 && day !== 6) count++;
      date.setDate(date.getDate() + 1);
    }
    return count;
  }

  isCheckinLate(reservation: Reservation): boolean {
    const now = new Date();
    const deadline = new Date(reservation.startDate);
    deadline.setHours(11, 0, 0, 0);
    return !reservation.checkedIn && now > deadline;
  }

  getHistory(userEmail: string): Reservation[] {
    return this.mockData.filter(r => r.reservedBy === userEmail);
  }



  getReservationsForSlot(slotId: string): Reservation[] {
    return this.mockData.filter(r => r.slotId === slotId);
  }

  makeReservation(res: Reservation) {
    this.mockData.push({ ...res, checkedIn: false, status: 'active' });
  }
}
