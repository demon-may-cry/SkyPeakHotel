export type BookingStatus =
    | "PENDING"
    | "CONFIRMED"
    | "CANCELLED";

export interface Booking {

    id: string;

    roomId: string;

    roomNumber: string;

    roomTypeSlug: string;

    roomTypeName: string;

    checkIn: string;

    checkOut: string;

    guestsCount: number;

    totalPrice: number;

    status: BookingStatus;

    createdAt: string;

}