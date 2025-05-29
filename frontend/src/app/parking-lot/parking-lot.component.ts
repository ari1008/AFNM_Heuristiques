import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {AuthService} from '../mock/auth.service';

@Component({
  selector: 'app-parking-lot',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './parking-lot.component.html',
  styleUrls: ['./parking-lot.component.css']
})
export class ParkingLotComponent implements OnInit {
  rows = ['A', 'B', 'C', 'D', 'E', 'F'];
  columns = Array.from({length: 10}, (_, i) => i + 1);

  session: string | null = null;
  role: string | null = null;
  userEmail: string | null = null;
  isLoggedIn: boolean = true;

  constructor(
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe((logged) => {
      this.isLoggedIn = !!localStorage.getItem('session');
      this.userEmail = localStorage.getItem('email');
      this.role = localStorage.getItem('role');
    });
  }

  getSlot(row: string, col: number) {
    const id = `${row}${col.toString().padStart(2, '0')}`;
    return {id, occupied: this.isSlotReservedToday(id)};
  }

  isSlotReservedToday(slotId: string): boolean {
    const today = new Date().toISOString().split('T')[0];
    return false
  }

  isElectricRow(row: string): boolean {
    return row === 'A' || row === 'F';
  }

}
