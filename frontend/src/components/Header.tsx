import { Link } from "react-router-dom";

import { useState } from "react";

import {
    useAuth
} from "../context/AuthContext";

function getGreeting(

    firstName: string,

    role: string | null

) {

    switch (role) {

        case "ADMIN":
            return `Здравствуйте, ${firstName} 👑`;

        case "MANAGER":
            return `Здравствуйте, ${firstName} ⭐`;

        default:
            return `Здравствуйте, ${firstName}`;
    }
}

export default function Header() {

    const [isOpen, setIsOpen] = useState(false);

    const auth = useAuth();

    return (
        <header
            className="
                fixed
                top-0
                left-0
                w-full
                z-50
                px-4
                pt-4
            "
        >

            <div
                className="
                    mx-auto
                    max-w-7xl
                    rounded-2xl
                    border
                    border-white/10
                    bg-white/5
                    backdrop-blur-md
                    shadow-xl
                "
            >

                <div
                    className="
                        flex
                        items-center
                        justify-between
                        px-6
                        py-4
                    "
                >

                    <Link
                        to="/"
                        className="
                            text-2xl
                            md:text-3xl
                            font-bold
                            tracking-wide
                            text-white
                        "
                    >
                        SkyPeak
                    </Link>

                    <nav
                        className="
        hidden
        md:flex
        items-center
        gap-8
        text-white
    "
                    >

                        <Link
                            to="/"
                            className="
            transition
            hover:text-gray-300
        "
                        >
                            Главная
                        </Link>

                        <Link
                            to="/rooms"
                            className="
            transition
            hover:text-gray-300
        "
                        >
                            Номера
                        </Link>

                        <Link
                            to="/about"
                            className="
            transition
            hover:text-gray-300
        "
                        >
                            О нас
                        </Link>

                        <Link
                            to="/contacts"
                            className="
            transition
            hover:text-gray-300
        "
                        >
                            Контакты
                        </Link>

                        {
                            auth.isAuthenticated ? (
                                <>
                                    <Link
                                        to="/profile"
                                        className="
    rounded-full
    border
    border-white/10
    px-4
    py-2
    transition
    hover:bg-white/10
"
                                    >
                                        {
                                            auth.firstName
                                                ? getGreeting(
                                                    auth.firstName,
                                                    auth.role
                                                )
                                                : "Профиль"
                                        }
                                    </Link>

                                    <button
                                        onClick={auth.logout}
                                        className="
                        rounded-full
                        bg-white
                        px-5
                        py-2
                        font-medium
                        text-black
                        transition
                        hover:bg-gray-200
                    "
                                    >
                                        Выйти
                                    </button>
                                </>
                            ) : (
                                <>
                                    <Link
                                        to="/login"
                                        className="
                        transition
                        hover:text-gray-300
                    "
                                    >
                                        Войти
                                    </Link>

                                    <Link
                                        to="/register"
                                        className="
                        rounded-full
                        bg-white
                        px-5
                        py-2
                        font-medium
                        text-black
                        transition
                        hover:bg-gray-200
                    "
                                    >
                                        Регистрация
                                    </Link>
                                </>
                            )
                        }

                    </nav>

                    <button
                        onClick={() => setIsOpen(!isOpen)}
                        className="
                            flex
                            flex-col
                            gap-1
                            md:hidden
                        "
                    >

                        <span className="block h-0.5 w-6 bg-white"></span>
                        <span className="block h-0.5 w-6 bg-white"></span>
                        <span className="block h-0.5 w-6 bg-white"></span>

                    </button>

                </div>

                {
                    isOpen && (
                        <div
                            className="
                                border-t
                                border-white/10
                                px-6
                                py-4
                                md:hidden
                            "
                        >

                            <nav
                                className="
                                    flex
                                    flex-col
                                    gap-4
                                    text-white
                                "
                            >

                                <Link to="/">
                                    Главная
                                </Link>

                                <Link to="/rooms">
                                    Номера
                                </Link>

                                <Link to="/about">
                                    О нас
                                </Link>

                                <Link to="/contacts">
                                    Контакты
                                </Link>

                                {
                                    auth.isAuthenticated ? (
                                        <>
                                            <Link
                                                to="/profile"
                                            >
                                                {
                                                    auth.firstName
                                                        ? getGreeting(
                                                            auth.firstName,
                                                            auth.role
                                                        )
                                                        : "Профиль"
                                                }
                                            </Link>

                                            <button
                                                onClick={auth.logout}
                                                className="
                    text-left
                "
                                            >
                                                Выйти
                                            </button>
                                        </>
                                    ) : (
                                        <>
                                            <Link to="/login">
                                                Войти
                                            </Link>

                                            <Link to="/register">
                                                Регистрация
                                            </Link>
                                        </>
                                    )
                                }

                            </nav>

                        </div>
                    )
                }

            </div>

        </header>
    );
}