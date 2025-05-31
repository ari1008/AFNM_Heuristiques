import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SelectDateComponent } from '../select-date/select-date.component';
import { ParkingLotComponent } from '../parking-lot/parking-lot.component';

@Component({
  selector: 'app-reservation-viewer',
  standalone: true,
  imports: [CommonModule, SelectDateComponent, ParkingLotComponent],
  templateUrl: './reservation-viewer.component.html',
  styleUrls: ['./reservation-viewer.component.css']
})
export class ReservationViewerComponent {
  selectedDateRange: { start: Date, end: Date } | null = null;

  onDateRangeSelected(range: { start: Date, end: Date }) {
    console.log('Date range received from SelectDateComponent:', range);
    this.selectedDateRange = range;
  }
}
