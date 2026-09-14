import {useEffect, useState} from "react";
import {BedDouble, CalendarDays, Users, Wallet,} from "lucide-react";
import {useNavigate} from "react-router-dom";
import toast from "react-hot-toast";

import {cancelBooking, getMyBookings, payBooking,} from "../../api/bookingApi";

import type {Booking} from "../../types/booking";
import ConfirmModal from "../../components/ConfirmModal";

const STATUS = {
    PENDING: {
        text: "Ожидает",
        color: "bg-yellow-500/10 text-yellow-300 border-yellow-500/20",
    },

    CONFIRMED: {
        text: "Подтверждено",
        color: "bg-green-500/10 text-green-300 border-green-500/20",
    },

    CANCELLED: {
        text: "Отменено",
        color: "bg-red-500/10 text-red-300 border-red-500/20",
    },
};

function formatDate(date: string) {
    return new Date(date).toLocaleDateString("ru-RU", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
    });
}

function formatPrice(price: number) {
    return `${price.toLocaleString("ru-RU")} ₽`;
}

function getNights(
    checkIn: string,
    checkOut: string
) {
    const start = new Date(checkIn);
    const end = new Date(checkOut);

    return Math.round(
        (end.getTime() - start.getTime()) /
        (1000 * 60 * 60 * 24)
    );
}

export default function BookingsPage() {

    const navigate = useNavigate();

    const [bookings, setBookings] =
        useState<Booking[]>([]);

    const [loading, setLoading] =
        useState(true);

    const [bookingToCancel, setBookingToCancel] =
        useState<Booking | null>(null);

    const [bookingToPay, setBookingToPay] =
        useState<Booking | null>(null);

    const [cancelLoading, setCancelLoading] =
        useState(false);

    const [paymentLoading, setPaymentLoading] =
        useState(false);

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

            await cancelBooking(
                bookingToCancel.id
            );

            toast.success(
                "Бронирование отменено."
            );

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

            await payBooking(
                bookingToPay.id
            );

            toast.success(
                "Бронирование успешно оплачено."
            );

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
            <div className="py-20 text-center text-zinc-400">
                Загрузка...
            </div>
        );

    }

    return (
        <>

            {/* Заголовок */}

            <div className="mb-10">

                <p className="
                    text-xs
                    uppercase
                    tracking-[0.35em]
                    text-zinc-500
                ">
                    Личный кабинет
                </p>

                <h1 className="
                    mt-3
                    text-4xl
                    font-semibold
                    tracking-tight
                    text-white
                    sm:text-5xl
                ">
                    Мои бронирования
                </h1>

                <p className="
                    mt-3
                    text-zinc-400
                ">
                    Здесь отображаются все ваши
                    текущие и прошлые бронирования.
                </p>

            </div>


            {/* Бронирования */}

            {bookings.length === 0 ? (

                <div className="
                    rounded-3xl
                    border
                    border-dashed
                    border-zinc-700
                    p-16
                    text-center
                ">

                    <div className="
                        mx-auto
                        flex
                        h-16
                        w-16
                        items-center
                        justify-center
                        rounded-2xl
                        bg-blue-500/10
                        text-blue-400
                    ">
                        <BedDouble size={30}/>
                    </div>

                    <h2 className="
                        mt-6
                        text-2xl
                        font-semibold
                        text-white
                    ">
                        Бронирований пока нет
                    </h2>

                    <p className="
                        mx-auto
                        mt-3
                        max-w-md
                        text-zinc-400
                    ">
                        Выберите подходящий номер
                        и создайте своё первое бронирование.
                    </p>

                    <button
                        type="button"
                        onClick={() => navigate("/rooms")}
                        className="
                            mt-8
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
                    </button>

                </div>

            ) : (

                <div className="space-y-5">

                    {bookings.map((booking) => {

                        const status =
                            STATUS[booking.status];

                        return (

                            <div
                                key={booking.id}
                                className="
                                    rounded-3xl
                                    border
                                    border-zinc-800
                                    bg-zinc-900/60
                                    p-6
                                    backdrop-blur-xl
                                    transition-all
                                    duration-300
                                    hover:border-zinc-700
                                    hover:shadow-xl
                                    sm:p-7
                                "
                            >

                                <div className="
                                    flex
                                    flex-col
                                    gap-7
                                    lg:flex-row
                                    lg:items-center
                                    lg:justify-between
                                ">


                                    {/* ЛЕВАЯ ЧАСТЬ */}

                                    <div className="min-w-0">

                                        {/* Название + статус */}

                                        <div className="
                                            flex
                                            flex-wrap
                                            items-center
                                            gap-3
                                        ">

                                            <h2 className="
                                                text-xl
                                                font-semibold
                                                tracking-tight
                                                text-white
                                            ">
                                                {booking.roomTypeName}
                                            </h2>

                                            <span
                                                className={`
                                                    inline-flex
                                                    items-center
                                                    rounded-full
                                                    border
                                                    px-3
                                                    py-1
                                                    text-xs
                                                    font-medium
                                                    ${status.color}
                                                `}
                                            >
                                                {status.text}
                                            </span>

                                        </div>


                                        {/* Номер */}

                                        <div className="
                                            mt-2
                                            flex
                                            items-center
                                            gap-2
                                            text-sm
                                            text-zinc-400
                                        ">

                                            <BedDouble
                                                size={17}
                                                className="
                                                    shrink-0
                                                    text-zinc-500
                                                "
                                            />

                                            <span>
                                                Комната №
                                                {booking.roomNumber}
                                            </span>

                                        </div>


                                        {/* Информация */}

                                        <div className="
    mt-7
    grid
    gap-6
    sm:grid-cols-[minmax(320px,1fr)_140px]
">


                                            {/* Даты */}

                                            <div className="
    flex
    items-start
    gap-3
    min-w-0
">

                                                <CalendarDays
                                                    size={21}
                                                    className="
            mt-0.5
            shrink-0
            text-blue-400
        "
                                                />

                                                <div className="min-w-0">

                                                    <p className="
            text-sm
            text-zinc-500
        ">
                                                        Даты проживания
                                                    </p>

                                                    <p className="
            mt-1
            whitespace-nowrap
            text-sm
            text-zinc-200
        ">
                                                        {formatDate(booking.checkIn)}
                                                        {" — "}
                                                        {formatDate(booking.checkOut)}
                                                    </p>

                                                    <p className="
            mt-1
            text-xs
            text-zinc-500
        ">
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

                                            </div>


                                            {/* Гости */}

                                            <div className="
    flex
    items-start
    gap-3
">

                                                <Users
                                                    size={21}
                                                    className="
            mt-0.5
            shrink-0
            text-blue-400
        "
                                                />

                                                <div>

                                                    <p className="
            text-sm
            text-zinc-500
        ">
                                                        Гостей
                                                    </p>

                                                    <p className="
            mt-1
            text-sm
            text-zinc-200
        ">
                                                        {booking.guestsCount}
                                                    </p>

                                                </div>

                                            </div>

                                        </div>

                                    </div>


                                    {/* ПРАВАЯ ЧАСТЬ */}

                                    <div className="
                                        flex
                                        shrink-0
                                        flex-col
                                        items-start
                                        gap-4
                                        border-t
                                        border-zinc-800
                                        pt-5
                                        lg:min-w-[190px]
                                        lg:items-end
                                        lg:border-l
                                        lg:border-t-0
                                        lg:pl-7
                                        lg:pt-0
                                    ">

                                        {/* Стоимость */}

                                        <div>

                                            <div className="
                                                flex
                                                items-center
                                                gap-2
                                                lg:justify-end
                                            ">

                                                <Wallet
                                                    size={18}
                                                    className="
                                                        text-green-400
                                                    "
                                                />

                                                <p className="
                                                    text-sm
                                                    text-zinc-500
                                                ">
                                                    Стоимость
                                                </p>

                                            </div>

                                            <p className="
                                                mt-1
                                                text-2xl
                                                font-semibold
                                                tracking-tight
                                                text-white
                                            ">
                                                {formatPrice(
                                                    booking.totalPrice
                                                )}
                                            </p>

                                        </div>


                                        {/* Действия */}

                                        {booking.status ===
                                            "PENDING" && (

                                                <div className="
                                                flex
                                                w-full
                                                gap-3
                                                sm:w-auto
                                            ">

                                                    <button
                                                        type="button"
                                                        onClick={() =>
                                                            setBookingToCancel(
                                                                booking
                                                            )
                                                        }
                                                        className="
                                                        flex-1
                                                        rounded-xl
                                                        border
                                                        border-red-500/30
                                                        px-5
                                                        py-2.5
                                                        text-sm
                                                        font-medium
                                                        text-red-400
                                                        transition
                                                        hover:bg-red-500/10
                                                        sm:flex-none
                                                    "
                                                    >
                                                        Отменить
                                                    </button>

                                                    <button
                                                        type="button"
                                                        onClick={() =>
                                                            setBookingToPay(
                                                                booking
                                                            )
                                                        }
                                                        className="
                                                        flex-1
                                                        rounded-xl
                                                        bg-blue-600
                                                        px-5
                                                        py-2.5
                                                        text-sm
                                                        font-medium
                                                        text-white
                                                        transition
                                                        hover:bg-blue-700
                                                        sm:flex-none
                                                    "
                                                    >
                                                        Оплатить
                                                    </button>

                                                </div>

                                            )}

                                        {booking.status ===
                                            "CONFIRMED" && (

                                                <span className="
                                                text-sm
                                                font-medium
                                                text-green-400
                                            ">
                                                ✓ Оплачено
                                            </span>

                                            )}

                                    </div>

                                </div>

                            </div>

                        );

                    })}

                </div>

            )}


            {/* Модальное окно отмены */}

            <ConfirmModal
                isOpen={bookingToCancel !== null}
                loading={cancelLoading}
                title="Отмена бронирования"
                message={
                    bookingToCancel
                        ? `Вы уверены, что хотите отменить бронирование номера №${bookingToCancel.roomNumber}?`
                        : ""
                }
                confirmText="Отменить"
                cancelText="Назад"
                loadingText="Отмена..."
                confirmButtonClassName="
                    bg-red-600
                    hover:bg-red-700
                "
                onConfirm={handleCancelBooking}
                onCancel={() => {

                    if (!cancelLoading) {
                        setBookingToCancel(null);
                    }

                }}
            />


            {/* Модальное окно оплаты */}

            <ConfirmModal
                isOpen={bookingToPay !== null}
                loading={paymentLoading}
                title="Оплата бронирования"
                message={
                    bookingToPay
                        ? `Оплатить бронирование номера №${bookingToPay.roomNumber} на сумму ${formatPrice(bookingToPay.totalPrice)}?`
                        : ""
                }
                confirmText="Оплатить"
                cancelText="Отмена"
                loadingText="Оплата..."
                confirmButtonClassName="
                    bg-blue-600
                    hover:bg-blue-700
                "
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