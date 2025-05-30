import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { ParkingService } from '../service/parking.service';
import { Slot } from '../model/parking.model';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-parking-lot',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './parking-lot.component.html',
  styleUrls: ['./parking-lot.component.css']
})
export class ParkingLotComponent implements OnInit {
  rows = ['A', 'B', 'C', 'D', 'E', 'F'];
  columns = Array.from({ length: 10 }, (_, i) => i + 1);

  isLoggedIn = false;
  userEmail: string | null = null;
  role: string | null = null;
  isMineCarIsElectrified = false;
  parking: Slot[] = [];

  constructor(
    private authService: AuthService,
    private parkingService: ParkingService
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = !!localStorage.getItem('session');
    this.userEmail = localStorage.getItem('email');
    this.role = localStorage.getItem('role');
    const electricFlag = localStorage.getItem('isElectricOrHybrid');
    this.isMineCarIsElectrified = electricFlag ? JSON.parse(electricFlag) : false;

    this.parkingService.getAll().subscribe(slots => {
      this.parking = slots;
    });
  }

  getSlotId(row: string, col: number): string {
    return `${row}${col.toString().padStart(2, '0')}`;
  }

  getSlotBlockReason(row: string, col: number): Observable<string | null> {
    const id = this.getSlotId(row, col);
    const today = new Date().toISOString().split('T')[0];

    return this.parkingService.getByCode(id).pipe(
      map(slot => {
        if (!slot) return 'Données indisponibles';
       // if (slot.reservedDate === today) return 'Réservée aujourd’hui';
        if (slot.has_charger && !this.isMineCarIsElectrified) return 'Véhicule non électrifié';
        return null;
      })
    );
  }

  isElectricSlot(row: string, col: number): Observable<boolean> {
    const id = this.getSlotId(row, col);
    return this.parkingService.getByCode(id).pipe(
      map(slot => slot?.has_charger ?? false)
    );
  }
}
