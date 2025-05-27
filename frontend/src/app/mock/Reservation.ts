export interface Reservation {
  slotId: {
    number: number;
    row: string;
  };
  reservedBy: string;
  startDate: Date;
  endDate: Date;
}
