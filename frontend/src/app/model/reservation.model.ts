export interface ReservationModel {
  slotId: {
    number: number;
    row: string;
  };
  reservedBy: string;
  startDate: Date;
  endDate: Date;
}
