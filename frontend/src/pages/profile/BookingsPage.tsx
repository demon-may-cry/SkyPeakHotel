import { useEffect, useState } from "react";

import { getMyBookings } from "../../api/bookingApi.ts";

import { cancelBooking } from "../../api/bookingApi.ts";

import { payBooking } from "../../api/bookingApi";

import type { Booking } from "../../types/booking.ts";

import { useNavigate } from "react-router-dom";

import ConfirmModal from "../../components/ConfirmModal.tsx";

import {
    CalendarDays,
    Users,
    Wallet,
    BedDouble
} from "lucide-react";

import toast from "react-hot-toast";

const STATUS = {
    PENDING: {
        text: "Ожидает",
        color: "bg-yellow-500/20 text-yellow-300 border-yellow-500/30",
    },
    CONFIRMED: {
        text: "Подтверждено",
        color: "bg-green-500/20 text-green-300 border-green-500/30",
    },
    CANCELLED: {
        text: "Отменено",
        color: "bg-red-500/20 text-red-300 border-red-500/30",
    },
};

function formatDate(date: string) {
    return new Date(date).toLocaleDateString("ru-RU");
}

function formatPrice(price: number) {
    return `${price.toLocaleString("ru-RU")} ₽`;
}

function getNights(checkIn: string, checkOut: string) {

    const start = new Date(checkIn);
    const end = new Date(checkOut);

    return Math.round(
        (end.getTime() - start.getTime()) /
        (1000 * 60 * 60 * 24)
    );
}

export default function BookingsPage() {

    const navigate = useNavigate();

    const [bookingToCancel, setBookingToCancel] =
        useState<Booking | null>(null);

    const [bookingToPay, setBookingToPay] =
        useState<Booking | null>(null);

    const [paymentLoading, setPaymentLoading] =
        useState(false);

    const [bookings, setBookings] = useState<Booking[]>([]);
    const [loading, setLoading] = useState(true);

    const [cancelLoading, setCancelLoading] = useState(false);

    useEffect(() => {
        loadBookings();
    }, []);

    async function loadBookings() {

        try {

            setLoading(true);

            const page = await getMyBookings();

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

    async function handleCancelBooking() {

        if (!bookingToCancel) {
            return;
        }

        try {

            setCancelLoading(true);

            await cancelBooking(bookingToCancel.id);

            toast.success("Бронирование отменено.");

            setBookingToCancel(null);

            await loadBookings();

        } catch (error: any) {

            toast.error(
                error.response?.data?.message ??
                "Не удалось отменить бронирование."
            );

        } finally {

            setCancelLoading(false);

        }

    }

    async function handlePayBooking() {

        if (!bookingToPay) {
            return;
        }

        try {

            setPaymentLoading(true);

            await payBooking(bookingToPay.id);

            toast.success("Бронирование успешно оплачено.");

            setBookingToPay(null);

            await loadBookings();

        } catch (error: any) {

            toast.error(
                error.response?.data?.message ??
                "Не удалось оплатить бронирование."
            );

        } finally {

            setPaymentLoading(false);

        }

    }

    if (loading) {

        return (

            <div className="py-20 text-center">

                <div
                    className="
                    mx-auto
                    h-12
                    w-12
                    animate-spin
                    rounded-full
                    border-4
                    border-zinc-700
                    border-t-blue-500
                "
                />

                <p className="mt-6 text-zinc-400">
                    Загружаем бронирования...
                </p>

            </div>

        );

    }

    if (bookings.length === 0) {
        return (
            <div
                className="
        rounded-2xl
        border
        border-dashed
        border-zinc-700
        py-20
        text-center
    "
            >

                <h2 className="text-3xl font-bold text-white">
                    У вас пока нет бронирований
                </h2>

                <p className="text-zinc-400 mt-4">
                    Выберите номер и оформите первое бронирование.
                </p>

                <button
                    onClick={() => navigate("/rooms")}
                    className="
                mt-8
                rounded-xl
                bg-blue-600
                hover:bg-blue-700
                px-8
                py-3
                transition
            "
                >
                    Перейти к номерам
                </button>

            </div>
        );
    }

    return (
        <>

            <div className="mb-10">

                <h1 className="text-4xl font-bold text-white">
                    Мои бронирования
                </h1>

                <p className="text-zinc-400 mt-2">
                    Здесь отображаются все ваши текущие и прошлые бронирования.
                </p>

            </div>

            {bookings.map((booking) => (

                <div
                    key={booking.id}
                    className="
        rounded-3xl
        border
        border-zinc-800
        bg-zinc-900/60
        backdrop-blur-xl
        p-8
        mb-6
        transition-all
        duration-300
        hover:border-blue-500
        hover:shadow-2xl
        hover:scale-[1.01]
hover:-translate-y-1
    "
                >

                    <div className="flex justify-between items-start">

                        <div>

                            <h2 className="text-2xl font-bold text-white">
                                {booking.roomTypeName}
                            </h2>

                            <div className="flex items-center gap-2 text-zinc-400 mt-2">

                                <BedDouble size={18} />

                                <span>
                    Комната №{booking.roomNumber}
                </span>

                            </div>

                        </div>

                        <span
                            className={`
    inline-flex
    items-center
    rounded-full
    border
    px-4
    py-2
    text-sm
    font-semibold
    ${STATUS[booking.status].color}
`}
                        >
            {STATUS[booking.status].text}
        </span>

                    </div>

                    <div className="grid md:grid-cols-3 gap-8 mt-8">

                        <div className="flex items-start gap-3">

                            <CalendarDays
                                className="text-blue-400 mt-1"
                                size={22}
                            />

                            <div>

                                <p className="text-zinc-500 text-sm">
                                    Даты проживания
                                </p>

                                <p className="text-white">

                                    {formatDate(booking.checkIn)}
                                    {" — "}
                                    {formatDate(booking.checkOut)}

                                </p>

                                <p className="text-zinc-400 text-sm">

                                    {getNights(
                                        booking.checkIn,
                                        booking.checkOut
                                    )} ночей

                                </p>

                            </div>

                        </div>

                        <div className="flex items-start gap-3">

                            <Users
                                className="text-blue-400 mt-1"
                                size={22}
                            />

                            <div>

                                <p className="text-zinc-500 text-sm">
                                    Гостей
                                </p>

                                <p className="text-white">
                                    {booking.guestsCount}
                                </p>

                            </div>

                        </div>

                        <div className="flex items-start gap-3">

                            <Wallet
                                className="text-green-400 mt-1"
                                size={22}
                            />

                            <div>

                                <p className="text-zinc-500 text-sm">
                                    Стоимость
                                </p>

                                <p className="text-2xl font-bold text-white">

                                    {formatPrice(booking.totalPrice)}

                                </p>

                            </div>

                        </div>

                    </div>

                    <div className="flex flex-wrap gap-4 mt-10">

                        <button
                            onClick={() =>
                                navigate(`/rooms/${booking.roomTypeSlug}`)
                            }
                            className="
    rounded-xl
    bg-blue-600
    hover:bg-blue-700
    hover:shadow-lg
    px-6
    py-3
    font-medium
    transition-all
    duration-200
"
                        >
                            Подробнее о номере
                        </button>

                        {booking.status === "PENDING" && (

                            <button
                                onClick={() => setBookingToPay(booking)}
                                className="
            rounded-xl
            bg-green-600
            hover:bg-green-700
            hover:shadow-lg
            px-6
            py-3
            font-medium
            transition-all
            duration-200
        "
                            >
                                Оплатить
                            </button>

                        )}

                        {booking.status == "PENDING" && (

                            <button
                                onClick={() => setBookingToCancel(booking)}
                                className="
        rounded-xl
        border
        border-red-500
        px-6
        py-3
        text-red-400
        hover:bg-red-500/10
        transition-all
        duration-200
    "
                            >
                                Отменить бронирование
                            </button>

                        )}

                    </div>

                </div>

            ))}
            <ConfirmModal
                isOpen={bookingToCancel !== null}
                loading={cancelLoading}
                loadingText="Отмена..."

                title="Отменить бронирование?"

                message={
                    bookingToCancel && (
                        <>
                            Вы действительно хотите отменить бронирование
                            <br />
                            <span className="font-semibold text-white">
                    {bookingToCancel.roomTypeName}
                </span>
                            <br />
                            №{bookingToCancel.roomNumber}?
                        </>
                    )
                }

                confirmText="Да, отменить"
                cancelText="Нет"

                onCancel={() => setBookingToCancel(null)}
                onConfirm={handleCancelBooking}
            />

            <ConfirmModal
                isOpen={bookingToPay !== null}

                loading={paymentLoading}

                loadingText="Оплата..."

                title="Оплатить бронирование?"

                message={
                    bookingToPay && (
                        <>
                            Оплатить бронирование
                            <br />

                            <span className="font-semibold text-white">
                    {bookingToPay.roomTypeName}
                </span>

                            <br />

                            на сумму

                            <br />

                            <span className="text-green-400 text-xl font-bold">
                    {formatPrice(bookingToPay.totalPrice)}
                </span>
                        </>
                    )
                }

                confirmText="Оплатить"

                cancelText="Отмена"

                onCancel={() => setBookingToPay(null)}

                onConfirm={handlePayBooking}
            />
        </>

    );

}