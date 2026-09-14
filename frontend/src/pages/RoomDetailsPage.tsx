import { useEffect, useMemo, useState } from "react";

import { useParams } from "react-router-dom";

import toast from "react-hot-toast";

import { useNavigate } from "react-router-dom";

import { getRoomTypeBySlug }
    from "../api/roomTypesApi";

import type { RoomType }
    from "../types/roomType";

import { createBooking }
    from "../api/bookingApi";

const mockImages = [

    "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85",

    "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267",

    "https://images.unsplash.com/photo-1505693534773-35adcf2d74b8",

    "https://images.unsplash.com/photo-1496417263034-38ec4f0b665a",

    "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85",
];

export default function RoomDetailsPage() {

    const { slug } = useParams();

    const navigate = useNavigate();

    const [roomType, setRoomType] =
        useState<RoomType | null>(null);

    const [loading, setLoading] =
        useState(true);

    const [currentImage, setCurrentImage] =
        useState(0);

    const [isGalleryOpen, setIsGalleryOpen] =
        useState(false);

    const [checkIn, setCheckIn] =
        useState("");

    const [checkOut, setCheckOut] =
        useState("");

    const [guestsCount, setGuestsCount] =
        useState(1);

    const [isSubmitting, setIsSubmitting] =
        useState(false);

    useEffect(() => {

        const fetchRoomType = async () => {

            if (!slug) {
                return;
            }

            try {

                const data =
                    await getRoomTypeBySlug(slug);

                setRoomType(data);

            } catch (error) {

                console.error(
                    "Ошибка загрузки типа номера:",
                    error
                );

            } finally {

                setLoading(false);
            }
        };

        fetchRoomType();

    }, [slug]);

    useEffect(() => {

        if (isGalleryOpen) {
            return;
        }

        const interval = setInterval(() => {

            setCurrentImage((prev) =>
                prev === mockImages.length - 1
                    ? 0
                    : prev + 1
            );

        }, 5000);

        return () => clearInterval(interval);

    }, [isGalleryOpen]);

    useEffect(() => {

        if (
            checkIn &&
            checkOut &&
            checkOut <= checkIn
        ) {
            setCheckOut("");
        }

    }, [checkIn, checkOut]);

    const nights = useMemo(() => {

        if (!checkIn || !checkOut) {
            return 0;
        }

        const start = new Date(checkIn);
        const end = new Date(checkOut);

        const diff =
            end.getTime() - start.getTime();

        return Math.max(
            0,
            Math.ceil(diff / (1000 * 60 * 60 * 24))
        );

    }, [checkIn, checkOut]);

    const totalPrice =
        nights * (roomType?.basePrice ?? 0);

    if (loading) {

        return (
            <div
                className="
                    flex
                    min-h-screen
                    items-center
                    justify-center
                    bg-zinc-950
                    text-white
                "
            >
                Загрузка...
            </div>
        );
    }

    if (!roomType) {

        return (
            <div
                className="
                    flex
                    min-h-screen
                    items-center
                    justify-center
                    bg-zinc-950
                    text-white
                "
            >
                Тип номера не найден
            </div>
        );
    }

    const nextImage = () => {

        setCurrentImage((prev) =>
            prev === mockImages.length - 1
                ? 0
                : prev + 1
        );
    };

    const prevImage = () => {

        setCurrentImage((prev) =>
            prev === 0
                ? mockImages.length - 1
                : prev - 1
        );
    };

    const today = new Date()
        .toISOString()
        .split("T")[0];

    const isBookingValid =
        checkIn !== "" &&
        checkOut !== "" &&
        nights > 0;

    const getNextDay = (date: string) => {

        if (!date) {
            return today;
        }

        const nextDay = new Date(date);
        nextDay.setDate(nextDay.getDate() + 1);

        return nextDay.toISOString().split("T")[0];
    };

    const handleBooking = async () => {

        if (!roomType) {
            return;
        }

        try {

            setIsSubmitting(true);

            await createBooking({
                roomTypeSlug: roomType.slug,
                checkIn,
                checkOut,
                guestsCount
            });

            toast.success(
                "Бронирование успешно создано!"
            );

            navigate("/profile/bookings");

        } catch (error: any) {

            console.error(error);

            toast.error(
                error.response?.data?.message ??
                "Не удалось создать бронирование."
            );
        } finally {

            setIsSubmitting(false);
        }
    };

    return (
        <>
            <section
                className="
                    bg-zinc-950
                    text-white
                "
            >

                <div
                    className="
                        relative
                        h-[70vh]
                        overflow-hidden
                    "
                >

                    <button
                        onClick={() =>
                            setIsGalleryOpen(true)
                        }
                        className="
                            absolute
                            inset-0
                            z-10
                            cursor-pointer
                        "
                    />

                    <div
                        className="
                            absolute
                            inset-0
                            bg-cover
                            bg-center
                            transition-all
                            duration-700
                        "
                        style={{
                            backgroundImage:
                                `url(${mockImages[currentImage]})`,
                        }}
                    />

                    <div
                        className="
                            absolute
                            inset-0
                            bg-linear-to-b
                            from-black/50
                            via-black/30
                            to-zinc-950
                        "
                    />

                    <div
                        className="
                            absolute
                            bottom-8
                            left-1/2
                            z-20
                            flex
                            -translate-x-1/2
                            gap-3
                        "
                    >

                        {
                            mockImages.map((_, index) => (
                                <button
                                    key={index}

                                    onClick={() =>
                                        setCurrentImage(index)
                                    }

                                    className={`
                                        h-3
                                        w-3
                                        rounded-full
                                        transition

                                        ${
                                        currentImage === index
                                            ? "bg-white"
                                            : "bg-white/40"
                                    }
                                    `}
                                />
                            ))
                        }

                    </div>

                    <div
                        className="
                            relative
                            z-10
                            flex
                            h-full
                            items-end
                            px-6
                            pb-16
                        "
                    >

                        <div
                            className="
                                mx-auto
                                w-full
                                max-w-7xl
                            "
                        >

                            <p
                                className="
                                    text-sm
                                    uppercase
                                    tracking-[0.3em]
                                    text-gray-300
                                "
                            >
                                {roomType.slug}
                            </p>

                            <div
                                className="
                                    mt-4
                                    flex
                                    flex-col
                                    gap-4
                                    md:flex-row
                                    md:items-end
                                    md:justify-between
                                "
                            >

                                <h1
                                    className="
                                        text-5xl
                                        md:text-7xl
                                        font-bold
                                    "
                                >
                                    {roomType.title}
                                </h1>

                                <span
                                    className="
                                        text-2xl
                                        text-gray-300
                                    "
                                >
                                    от {roomType.basePrice} ₽
                                    / ночь
                                </span>

                            </div>

                        </div>

                    </div>

                </div>

                <div
                    className="
                        mx-auto
                        grid
                        max-w-7xl
                        gap-12
                        px-6
                        py-24
                        lg:grid-cols-[2fr_1fr]
                    "
                >

                    <div>

                        <h2
                            className="
                                text-3xl
                                font-bold
                            "
                        >
                            О номере
                        </h2>

                        <p
                            className="
                                mt-6
                                text-lg
                                leading-relaxed
                                text-gray-400
                            "
                        >
                            {roomType.description}
                        </p>

                    </div>

                    <div
                        className="
                            h-fit
                            rounded-3xl
                            border
                            border-white/10
                            bg-white/5
                            p-8
                            backdrop-blur-md
                        "
                    >

                        <p
                            className="
        text-2xl
        font-semibold
    "
                        >
                            Бронирование
                        </p>

                        <div className="mt-8 space-y-6">

                            <div>
                                <label
                                    className="
                mb-2
                block
                text-sm
                text-gray-400
            "
                                >
                                    Заезд
                                </label>

                                <input
                                    type="date"
                                    value={checkIn}
                                    min={today}
                                    onChange={(e) => setCheckIn(e.target.value)}
                                    className="
        w-full
        rounded-xl
        border
        border-white/10
        bg-zinc-900
        px-4
        py-3
        text-white
    "
                                />
                            </div>

                            <div>
                                <label
                                    className="
                mb-2
                block
                text-sm
                text-gray-400
            "
                                >
                                    Выезд
                                </label>

                                <input
                                    type="date"
                                    value={checkOut}
                                    min={getNextDay(checkIn)}
                                    onChange={(e) =>
                                        setCheckOut(e.target.value)
                                    }
                                    className="
                w-full
                rounded-xl
                border
                border-white/10
                bg-zinc-900
                px-4
                py-3
                text-white
            "
                                />
                            </div>

                            <div>
                                <label
                                    className="
                mb-2
                block
                text-sm
                text-gray-400
            "
                                >
                                    Количество гостей
                                </label>

                                <select
                                    value={guestsCount}
                                    onChange={(e) =>
                                        setGuestsCount(Number(e.target.value))
                                    }
                                    className="
                w-full
                rounded-xl
                border
                border-white/10
                bg-zinc-900
                px-4
                py-3
                text-white
            "
                                >
                                    {[1, 2, 3, 4].map((guest) => (
                                        <option
                                            key={guest}
                                            value={guest}
                                        >
                                            {guest}
                                        </option>
                                    ))}
                                </select>
                            </div>

                        </div>

                        <div
                            className="
        mt-10
        space-y-3
        border-t
        border-white/10
        pt-6
    "
                        >

                            <div
                                className="
            flex
            justify-between
            text-gray-400
        "
                            >
                                <span>Цена за ночь</span>

                                <span>
            {roomType.basePrice} ₽
        </span>
                            </div>

                            <div
                                className="
            flex
            justify-between
            text-gray-400
        "
                            >
                                <span>Количество ночей</span>

                                <span>{nights}</span>
                            </div>

                            <div
                                className="
            flex
            justify-between
            text-2xl
            font-bold
        "
                            >
                                <span>Итого</span>

                                <span>{totalPrice} ₽</span>
                            </div>

                        </div>

                        <button
                            onClick={handleBooking}
                            disabled={!isBookingValid || isSubmitting}
                            className={`
    mt-8
    w-full
    rounded-2xl
    px-6
    py-4
    text-lg
    font-semibold
    transition

    ${
                                isBookingValid
                                    ? "bg-white text-black hover:bg-gray-200"
                                    : "cursor-not-allowed bg-zinc-700 text-zinc-400"
                            }
`}
                        >
                            {
                                isSubmitting
                                    ? "Бронирование..."
                                    : "Забронировать"
                            }
                        </button>

                    </div>

                </div>

            </section>

            {
                isGalleryOpen && (
                    <div
                        className="
                            fixed
                            inset-0
                            z-100
                            flex
                            items-center
                            justify-center
                            bg-black/95
                            px-6
                        "
                    >

                        <button
                            onClick={() =>
                                setIsGalleryOpen(false)
                            }
                            className="
                                absolute
                                right-6
                                top-6
                                text-5xl
                                text-white
                            "
                        >
                            ×
                        </button>

                        <button
                            onClick={prevImage}
                            className="
                                absolute
                                left-6
                                text-5xl
                                text-white
                            "
                        >
                            ←
                        </button>

                        <img
                            src={mockImages[currentImage]}
                            alt={roomType.title}
                            className="
                                max-h-[85vh]
                                max-w-[90vw]
                                rounded-3xl
                                object-cover
                            "
                        />

                        <button
                            onClick={nextImage}
                            className="
                                absolute
                                right-6
                                text-5xl
                                text-white
                            "
                        >
                            →
                        </button>

                    </div>
                )
            }
        </>
    );
}