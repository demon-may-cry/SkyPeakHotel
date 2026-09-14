import { Link } from "react-router-dom";

type RoomCardProps = {

    slug: string;

    title: string;

    description: string;

    price: string;

    images: string[];
};

export default function RoomCard({

                                     slug,

                                     title,

                                     description,

                                     price,

                                     images,

                                 }: RoomCardProps) {

    return (
        <div
            className="
                overflow-hidden
                rounded-3xl
                border
                border-white/10
                bg-white/5
                backdrop-blur-md
                transition
                duration-300
                hover:-translate-y-2
                hover:shadow-2xl
            "
        >

            <div
                className="
                    h-72
                    bg-cover
                    bg-center
                "
                style={{
                    backgroundImage:
                        `url(${images[0]})`,
                }}
            />

            <div className="p-6">

                <p
                    className="
                        text-sm
                        uppercase
                        tracking-[0.3em]
                        text-gray-400
                    "
                >
                    {slug}
                </p>

                <div
                    className="
                        mt-3
                        flex
                        items-start
                        justify-between
                        gap-4
                    "
                >

                    <h3
                        className="
                            text-2xl
                            font-semibold
                            text-white
                        "
                    >
                        {title}
                    </h3>

                    <span
                        className="
                            whitespace-nowrap
                            text-lg
                            font-medium
                            text-gray-300
                        "
                    >
                        {price}
                    </span>

                </div>

                <p
                    className="
                        mt-4
                        leading-relaxed
                        text-gray-400
                    "
                >
                    {description}
                </p>

                <Link
                    to={`/rooms/${slug}`}
                    className="
                        mt-6
                        block
                        w-full
                        rounded-2xl
                        bg-white
                        px-6
                        py-4
                        text-center
                        font-semibold
                        text-black
                        transition
                        hover:bg-gray-200
                    "
                >
                    Подробнее
                </Link>

            </div>

        </div>
    );
}