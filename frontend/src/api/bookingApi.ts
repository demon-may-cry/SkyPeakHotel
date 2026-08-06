import api from "./axios";
import type { Booking } from "../types/booking";
import type { Page } from "../types/page";

export interface BookingRequest {
    roomTypeSlug: string;
    checkIn: string;
    checkOut: string;
    guestsCount: number;
}

export interface BookingResponse {
    id: string;
    roomNumber: string;
    roomType: string;
    checkIn: string;
    checkOut: string;
    guestsCount: number;
    totalPrice: number;
    status: string;
    createdAt: string;
}

export const createBooking = async (
    request: BookingRequest
): Promise<BookingResponse> => {

    const response = await api.post(
        "/bookings",
        request
    );

    return response.data;
};

export async function getMyBookings(
    page = 0,
    size = 10
): Promise<Page<Booking>> {

    const response = await api.get("/bookings/me", {
        params: {
            page,
            size
        }
    });

    return response.data;
}

export async function cancelBooking(id: string): Promise<void> {

    await api.delete(`/bookings/${id}`);
}

export async function payBooking(
    id: string
) {

    const response = await api.post(
        `/bookings/${id}/pay`
    );

    return response.data;

}