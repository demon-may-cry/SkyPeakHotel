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

    function formatBookingDate(date: string) {
        return new Date(date).toLocaleDateString("ru-RU", {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
        });
    }

    function getNights(checkIn: string, checkOut: string) {

        const start = new Date(checkIn);
        const end = new Date(checkOut);

        return Math.round(
            (end.getTime() - start.getTime()) /
            (1000 * 60 * 60 * 24)
        );
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

                            {/* Левая часть */}

                            <div className="flex items-start gap-5">

                                {/* Иконка */}

                                <div
                                    className="
                        flex
                        h-14
                        w-14
                        shrink-0
                        items-center
                        justify-center
                        rounded-2xl
                        bg-blue-600/10
                        text-blue-400
                    "
                                >
                                    🏨
                                </div>

                                <div>

                                    {/* Название + статус */}

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

                                    {/* Информация */}

                                    <div
                                        className="
                            mt-5
                            grid
                            grid-cols-1
                            gap-x-10
                            gap-y-4
                            sm:grid-cols-2
                        "
                                    >

                                        <div>

                                            <p className="text-xs uppercase tracking-wider text-zinc-500">
                                                Комната
                                            </p>

                                            <p className="mt-1 text-sm text-zinc-200">
                                                №{booking.roomNumber}
                                            </p>

                                        </div>

                                        <div>

                                            <p className="text-xs uppercase tracking-wider text-zinc-500">
                                                Даты проживания
                                            </p>

                                            <p className="mt-1 text-sm text-zinc-200">
                                                {formatBookingDate(booking.checkIn)}
                                                {" — "}
                                                {formatBookingDate(booking.checkOut)}
                                            </p>

                                            <p className="mt-1 text-xs text-zinc-500">
                                                {getNights(
                                                    booking.checkIn,
                                                    booking.checkOut
                                                )}{" "}
                                                {getNights(
                                                    booking.checkIn,
                                                    booking.checkOut
                                                ) === 1
                                                    ? "ночь"
                                                    : "ночей"}
                                            </p>

                                        </div>

                                        <div>

                                            <p className="text-xs uppercase tracking-wider text-zinc-500">
                                                Гостей
                                            </p>

                                            <p className="mt-1 text-sm text-zinc-200">
                                                {booking.guestsCount}
                                            </p>

                                        </div>

                                    </div>

                                </div>

                            </div>

                            {/* Правая часть */}

                            <div
                                className="
                    flex
                    shrink-0
                    flex-col
                    items-start
                    gap-4
                    border-t
                    border-zinc-800
                    pt-5
                    lg:items-end
                    lg:border-t-0
                    lg:border-l
                    lg:pl-8
                    lg:pt-0
                "
                            >

                                <div>

                                    <p className="text-xs uppercase tracking-wider text-zinc-500">
                                        Стоимость
                                    </p>

                                    <p className="mt-1 text-2xl font-bold text-white">
                                        {booking.totalPrice.toLocaleString("ru-RU")} ₽
                                    </p>

                                </div>

                                {booking.status === "PENDING" && (

                                    <button
                                        type="button"
                                        onClick={() =>
                                            setBookingToPay(booking)
                                        }
                                        className="
                            w-full
                            rounded-xl
                            bg-blue-600
                            px-6
                            py-3
                            font-medium
                            text-white
                            transition
                            hover:bg-blue-700
                            active:scale-[0.98]
                            lg:w-auto
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
                isOpen={bookingToPay !== null}
                loading={paymentLoading}
                title="Оплата бронирования"
                message={
                    bookingToPay
                        ? `Оплатить бронирование номера №${bookingToPay.roomNumber} на сумму ${bookingToPay.totalPrice.toLocaleString("ru-RU")} ₽?`
                        : ""
                }
                confirmText="Оплатить"
                cancelText="Отмена"
                loadingText="Оплата..."
                confirmButtonClassName="bg-blue-600 hover:bg-blue-700"
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