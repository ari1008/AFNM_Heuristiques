export interface ReservationModel {
  slotId: {
    number: number;
    row: string;
  };
  reservedBy: string;
  startDate: Date;
  endDate: Date;
}


export interface ReservationRequest{
  userId: string,
  dates: string[],
  slotId: string,
}


export interface CheckInRequest{
  userId: string,
  slotId: string,
}


export interface ReservationResponse{
  id: string,
  userId: string,
  slotId: string,
  startDate: string,
  endDate: string,
}


export interface ReservationUpdateRequest{
  startDateTime: string,
  endDateTime: string,
}
