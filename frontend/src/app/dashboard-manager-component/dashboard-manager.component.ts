import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReservationService, Reservation } from '../mock/reservation.service';
import { ChartData, ChartOptions } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';

@Component({
  selector: 'app-dashboard-manager',
  standalone: true,
  imports: [CommonModule, NgChartsModule],
  templateUrl: './dashboard-manager.component.html',
  styleUrls: ['./dashboard-manager.component.css']
})
export class DashboardManagerComponent implements OnInit {
  reservations: Reservation[] = [];

  totalReservations = 0;
  activeToday = 0;
  noShows = 0;
  electricUsed = 0;

  usageChartData: ChartData<'doughnut'> = { labels: [], datasets: [] };
  chargerChartData: ChartData<'doughnut'> = { labels: [], datasets: [] };
  chartOptions: ChartOptions = {
    responsive: true,
    plugins: { legend: { position: 'bottom' } }
  };

  constructor(private reservationService: ReservationService) {}

  ngOnInit(): void {
    this.reservations = this.reservationService.getAllHistory();
    this.computeStats();
  }

  computeStats(): void {
    const today = new Date().toISOString().split('T')[0];

    this.totalReservations = this.reservations.length;
    this.activeToday = this.reservations.filter(r => r.startDate <= today && r.endDate >= today).length;
    this.noShows = this.reservations.filter(r =>
      r.startDate === today && !r.checkedIn && r.status === 'expired'
    ).length;
    this.electricUsed = this.reservations.filter(r => r.slotId.startsWith('A') || r.slotId.startsWith('F')).length;

    this.usageChartData = {
      labels: ['Utilisées aujourd\'hui', 'Non utilisées'],
      datasets: [
        {
          data: [this.activeToday, this.totalReservations - this.activeToday],
          backgroundColor: ['#4caf50', '#ddd']
        }
      ]
    };

    this.chargerChartData = {
      labels: ['Avec borne ⚡', 'Sans borne'],
      datasets: [
        {
          data: [this.electricUsed, this.totalReservations - this.electricUsed],
          backgroundColor: ['#2196f3', '#bbb']
        }
      ]
    };
  }
}
