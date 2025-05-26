import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-parking-lot',
  imports: [CommonModule, RouterModule],
  template: `
    <h2>Parking Lot Map</h2>
    <div class="row" *ngFor="let row of rows">
      <ng-container *ngFor="let col of columns">
        <ng-container *ngIf="!getSlot(row, col).occupied; else locked">
          <a [routerLink]="['/slot', getSlot(row, col).id]" class="slot free">
            {{ getSlot(row, col).id }}
          </a>
        </ng-container>
        <ng-template #locked>
          <div class="slot occupied">
            {{ getSlot(row, col).id }} 🔒
          </div>
        </ng-template>
      </ng-container>
    </div>
  `,
  styles: [`
    .row { display: flex; justify-content: center; margin-bottom: 10px; }
    .slot {
      width: 60px;
      height: 60px;
      display: flex;
      justify-content: center;
      align-items: center;
      border-radius: 8px;
      margin: 4px;
      font-weight: bold;
    }
    .free {
      background-color: #c8f7c5;
      border: 2px solid #4caf50;
      cursor: pointer;
    }
    .occupied {
      background-color: #ddd;
      border: 2px solid #aaa;
      color: #555;
    }
  `]
})
export class ParkingLotComponent {
  rows = ['A', 'B', 'C'];
  columns = Array.from({ length: 10 }, (_, i) => i + 1);
  occupiedSlots = new Set(['A01', 'A02', 'A03']);

  getSlot(row: string, col: number) {
    const id = `${row}${col.toString().padStart(2, '0')}`;
    return { id, occupied: this.occupiedSlots.has(id) };
  }

  isElectricRow(row: string): boolean {
    return row === 'A' || row === 'F';
  }
}
