import { useEffect, useState } from "react";

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

export default function RoomsPage() {

    const [rooms, setRooms] =
        useState<RoomType[]>([]);

    const [loading, setLoading] =
        useState(true);

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

            } finally {

                setLoading(false);
            }
        };

        fetchRooms();

    }, []);

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

    return (
        <section
            className="
                min-h-screen
                bg-zinc-950
                px-6
                py-32
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
                        Номера
                    </p>

                    <h1
                        className="
                            mt-4
                            text-5xl
                            font-bold
                            text-white
                        "
                    >
                        Выберите номер мечты
                    </h1>

                </div>

                <div
                    className="
                        mt-20
                        grid
                        gap-8
                        md:grid-cols-2
                        xl:grid-cols-3
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
    );
}