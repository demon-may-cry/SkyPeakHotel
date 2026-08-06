import { useEffect, useState } from "react";

import { Link } from "react-router-dom";

import FeatureCard from "../components/FeatureCard";
import RoomCard from "../components/RoomCard";

import { getRoomTypes }
    from "../api/roomTypesApi";

import type { RoomType }
    from "../types/roomType";

const mockImages = [

    "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85",

    "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267",

    "https://images.unsplash.com/photo-1505693534773-35adcf2d74b8",
];

export default function HomePage() {

    const [rooms, setRooms] =
        useState<RoomType[]>([]);

    useEffect(() => {

        const fetchRooms = async () => {

            try {

                const data =
                    await getRoomTypes();

                setRooms(data);

            } catch (error) {

                console.error(
                    "Ошибка загрузки типов номеров:",
                    error
                );
            }
        };

        fetchRooms();

    }, []);

    return (
        <>
            <section
                className="
                    relative
                    min-h-screen
                    flex
                    items-center
                    justify-center
                    overflow-hidden
                    bg-black
                "
            >

                <div
                    className="
                        absolute
                        inset-0
                        bg-[url('https://images.unsplash.com/photo-1506744038136-46273834b3fb')]
                        bg-cover
                        bg-center
                        opacity-40
                    "
                />

                <div
                    className="
                        absolute
                        inset-0
                        bg-gradient-to-b
                        from-black/60
                        via-black/40
                        to-zinc-950
                    "
                />

                <div
                    className="
                        relative
                        z-10
                        text-center
                        px-6
                        max-w-4xl
                    "
                >

                    <p
                        className="
                            text-gray-300
                            uppercase
                            tracking-[0.4em]
                            mb-6
                            text-sm
                        "
                    >
                        SKYPEAK HOTEL
                    </p>

                    <h1
                        className="
                            text-5xl
                            md:text-7xl
                            font-bold
                            text-white
                            leading-tight
                        "
                    >
                        Роскошный отдых
                        <br />
                        в сердце гор
                    </h1>

                    <p
                        className="
                            mt-6
                            text-lg
                            md:text-2xl
                            text-gray-300
                        "
                    >
                        Уютные номера, панорамные виды
                        и незабываемая атмосфера
                    </p>

                    <div
                        className="
                            mt-10
                            flex
                            flex-col
                            sm:flex-row
                            gap-4
                            justify-center
                        "
                    >

                        <button
                            className="
                                rounded-full
                                bg-white
                                px-8
                                py-4
                                text-lg
                                font-semibold
                                text-black
                                transition
                                hover:scale-105
                                hover:bg-gray-200
                            "
                        >
                            Забронировать
                        </button>

                        <Link
                            to="/rooms"
                            className="
                                rounded-full
                                border
                                border-white/20
                                bg-white/5
                                px-8
                                py-4
                                text-lg
                                text-white
                                backdrop-blur-sm
                                transition
                                hover:scale-105
                                hover:bg-white/10
                            "
                        >
                            Смотреть номера
                        </Link>

                    </div>

                </div>

            </section>

            <section
                className="
                    bg-zinc-950
                    px-6
                    py-24
                "
            >

                <div
                    className="
                        mx-auto
                        max-w-7xl
                    "
                >

                    <div className="text-center">

                        <p
                            className="
                                uppercase
                                tracking-[0.3em]
                                text-gray-400
                                text-sm
                            "
                        >
                            Наши преимущества
                        </p>

                        <h2
                            className="
                                mt-4
                                text-4xl
                                md:text-5xl
                                font-bold
                                text-white
                            "
                        >
                            Комфорт премиального уровня
                        </h2>

                    </div>

                    <div
                        className="
                            mt-16
                            grid
                            gap-8
                            md:grid-cols-2
                            xl:grid-cols-4
                        "
                    >

                        <FeatureCard
                            title="Панорамный вид"
                            description="Наслаждайтесь захватывающими видами на горные вершины прямо из вашего номера."
                        />

                        <FeatureCard
                            title="SPA & Wellness"
                            description="Расслабьтесь в современном SPA-комплексе с бассейном и саунами."
                        />

                        <FeatureCard
                            title="Авторская кухня"
                            description="Ресторан SkyPeak предлагает блюда европейской и локальной кухни."
                        />

                        <FeatureCard
                            title="Горнолыжный отдых"
                            description="Быстрый доступ к лыжным трассам и зимним развлечениям."
                        />

                    </div>

                </div>

            </section>

            <section
                className="
                    bg-zinc-950
                    px-6
                    pb-24
                "
            >

                <div
                    className="
                        mx-auto
                        max-w-7xl
                    "
                >

                    <div
                        className="
                            flex
                            flex-col
                            gap-6
                            md:flex-row
                            md:items-end
                            md:justify-between
                        "
                    >

                        <div>

                            <p
                                className="
                                    uppercase
                                    tracking-[0.3em]
                                    text-gray-400
                                    text-sm
                                "
                            >
                                Номера
                            </p>

                            <h2
                                className="
                                    mt-4
                                    text-4xl
                                    md:text-5xl
                                    font-bold
                                    text-white
                                "
                            >
                                Выберите идеальный номер
                            </h2>

                        </div>

                        <Link
                            to="/rooms"
                            className="
                                rounded-full
                                border
                                border-white/10
                                bg-white/5
                                px-6
                                py-3
                                text-white
                                backdrop-blur-sm
                                transition
                                hover:bg-white/10
                            "
                        >
                            Смотреть все
                        </Link>

                    </div>

                    <div
                        className="
                            mt-16
                            grid
                            gap-8
                            lg:grid-cols-3
                        "
                    >

                        {
                            rooms.map((room) => (

                                <RoomCard
                                    key={room.id}

                                    slug={room.slug}

                                    title={room.title}

                                    description={
                                        room.description
                                    }

                                    price={
                                        `от ${room.basePrice} ₽`
                                    }

                                    images={mockImages}
                                />
                            ))
                        }

                    </div>

                </div>

            </section>
        </>
    );
}