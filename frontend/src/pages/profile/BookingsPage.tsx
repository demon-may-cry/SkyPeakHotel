import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import toast from "react-hot-toast";

import {
    getMyBookings,
    payBooking,
} from "../../api/bookingApi";

import type { Booking } from "../../types/booking";

import ConfirmModal from "../../components/ConfirmModal";

import { useBalance } from "../../context/BalanceContext";

export default function BookingsPage() {

    const { refreshBalance } = useBalance();

    const [bookings, setBookings] =
        useState<Booking[]>([]);

    const [loading, setLoading] =
        useState(true);

    const [bookingToPay, setBookingToPay] =
        useState<Booking | null>(null);

    const [paymentLoading, setPaymentLoading] =
        useState(false);

    useEffect(() => {

        loadBookings();

    }, []);

    async function loadBookings() {

        try {

            setLoading(true);

            const page =
                await getMyBookings();

            setBookings(page.content);

        } catch (error: any) {

            toast.error(
                error.response?.data?.message ??
                "Не удалось загрузить бронирования."
            );

        } finally {

            setLoading(false);

        }

    }

    async function handlePayBooking() {

        if (!bookingToPay) {
            return;
        }

        try {

            setPaymentLoading(true);

            await payBooking(
                bookingToPay.id
            );

            toast.success(
                "Бронирование успешно оплачено."
            );

            setBookingToPay(null);

            await Promise.all([
                loadBookings(),
                refreshBalance(),
            ]);

        } catch (error: any) {

            toast.error(
                error.response?.data?.message ??
                "Не удалось оплатить бронирование."
            );

        } finally {

            setPaymentLoading(false);

        }

    }

    function getStatusLabel(
        status: Booking["status"]
    ) {

        switch (status) {

            case "PENDING":
                return "Ожидает оплаты";

            case "CONFIRMED":
                return "Подтверждено";

            case "CANCELLED":
                return "Отменено";

            case "COMPLETED":
                return "Завершено";

            default:
                return status;

        }

    }

    function getStatusClass(
        status: Booking["status"]
    ) {

        switch (status) {

            case "PENDING":
                return "bg-yellow-500/10 text-yellow-400 border-yellow-500/20";

            case "CONFIRMED":
                return "bg-green-500/10 text-green-400 border-green-500/20";

            case "CANCELLED":
                return "bg-red-500/10 text-red-400 border-red-500/20";

            case "COMPLETED":
                return "bg-blue-500/10 text-blue-400 border-blue-500/20";

            default:
                return "bg-zinc-800 text-zinc-400 border-zinc-700";

        }

    }

    if (loading) {

        return (

            <div className="py-20 text-center text-zinc-400">

                Загрузка бронирований...

            </div>

        );

    }

    if (bookings.length === 0) {

        return (

            <div className="py-10">

                <div className="mb-10">

                    <p className="text-xs uppercase tracking-[0.4em] text-zinc-500">
                        Личный кабинет
                    </p>

                    <h1 className="mt-3 text-4xl font-bold text-white">
                        Мои бронирования
                    </h1>

                </div>

                <div
                    className="
                        rounded-3xl
                        border
                        border-dashed
                        border-zinc-700
                        p-16
                        text-center
                    "
                >

                    <div className="text-6xl">
                        🏨
                    </div>

                    <h2 className="mt-6 text-2xl font-semibold text-white">
                        У вас пока нет бронирований
                    </h2>

                    <p className="mt-3 text-zinc-400">
                        Выберите номер и забронируйте его,
                        чтобы увидеть его здесь.
                    </p>

                    <Link
                        to="/rooms"
                        className="
                            mt-8
                            inline-flex
                            rounded-xl
                            bg-blue-600
                            px-6
                            py-3
                            font-medium
                            text-white
                            transition
                            hover:bg-blue-700
                        "
                    >
                        Выбрать номер
                    </Link>

                </div>

            </div>

        );

    }

    return (

        <>

            <div className="mb-10">

                <p className="text-xs uppercase tracking-[0.4em] text-zinc-500">
                    Личный кабинет
                </p>

                <h1 className="mt-3 text-4xl font-bold text-white">
                    Мои бронирования
                </h1>

                <p className="mt-3 text-zinc-400">
                    Управляйте своими бронированиями
                    и оплачивайте выбранные номера.
                </p>

            </div>

            <div className="space-y-5">

                {bookings.map((booking) => (

                    <div
                        key={booking.id}
                        className="
                            rounded-3xl
                            border
                            border-zinc-800
                            bg-zinc-900/50
                            p-6
                            transition
                            hover:border-zinc-700
                        "
                    >

                        <div
                            className="
                                flex
                                flex-col
                                gap-6
                                lg:flex-row
                                lg:items-center
                                lg:justify-between
                            "
                        >

                            <div>

                                <div
                                    className="
                                        flex
                                        flex-wrap
                                        items-center
                                        gap-3
                                    "
                                >

                                    <h2 className="text-xl font-semibold text-white">

                                        {booking.roomTypeName}

                                    </h2>

                                    <span
                                        className={`
                                            rounded-full
                                            border
                                            px-3
                                            py-1
                                            text-xs
                                            font-medium
                                            ${getStatusClass(
                                            booking.status
                                        )}
                                        `}
                                    >
                                        {getStatusLabel(
                                            booking.status
                                        )}
                                    </span>

                                </div>

                                <div className="mt-4 space-y-2 text-sm text-zinc-400">

                                    <p>
                                        Комната №
                                        <span className="text-zinc-200">
                                            {booking.roomNumber}
                                        </span>
                                    </p>

                                    <p>
                                        {booking.checkIn}
                                        {" — "}
                                        {booking.checkOut}
                                    </p>

                                    <p>
                                        Гостей:{" "}
                                        <span className="text-zinc-200">
                                            {booking.guestsCount}
                                        </span>
                                    </p>

                                </div>

                            </div>

                            <div
                                className="
                                    flex
                                    flex-col
                                    items-start
                                    gap-4
                                    lg:items-end
                                "
                            >

                                <div>

                                    <p className="text-sm text-zinc-500">
                                        Стоимость
                                    </p>

                                    <p className="mt-1 text-2xl font-bold text-white">

                                        {booking.totalPrice.toLocaleString(
                                            "ru-RU"
                                        )} ₽

                                    </p>

                                </div>

                                {booking.status === "PENDING" && (

                                    <button
                                        type="button"
                                        onClick={() =>
                                            setBookingToPay(
                                                booking
                                            )
                                        }
                                        className="
                                            rounded-xl
                                            bg-blue-600
                                            px-6
                                            py-3
                                            font-medium
                                            text-white
                                            transition
                                            hover:bg-blue-700
                                            active:scale-[0.98]
                                        "
                                    >
                                        Оплатить
                                    </button>

                                )}

                                {booking.status === "CONFIRMED" && (

                                    <span className="text-sm text-green-400">
                                        ✓ Оплачено
                                    </span>

                                )}

                            </div>

                        </div>

                    </div>

                ))}

            </div>

            <ConfirmModal
                open={bookingToPay !== null}
                loading={paymentLoading}
                title="Оплата бронирования"
                description={
                    bookingToPay
                        ? `Оплатить бронирование номера №${bookingToPay.roomNumber} на сумму ${bookingToPay.totalPrice.toLocaleString("ru-RU")} ₽?`
                        : ""
                }
                confirmText="Оплатить"
                cancelText="Отмена"
                onConfirm={handlePayBooking}
                onCancel={() => {

                    if (!paymentLoading) {
                        setBookingToPay(null);
                    }

                }}
            />

        </>

    );

}