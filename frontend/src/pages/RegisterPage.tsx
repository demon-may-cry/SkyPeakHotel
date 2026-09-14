import { useState } from "react";

import { useNavigate }
    from "react-router-dom";

import {
    register
} from "../api/authApi";

export default function RegisterPage() {

    const navigate =
        useNavigate();

    const [email, setEmail] =
        useState("");

    const [password, setPassword] =
        useState("");

    const [firstName, setFirstName] =
        useState("");

    const [lastName, setLastName] =
        useState("");

    const [phoneNumber, setPhoneNumber] =
        useState("");

    const [success, setSuccess] =
        useState("");

    const [error, setError] =
        useState("");

    const handleSubmit = async (
        e: React.FormEvent
    ) => {

        e.preventDefault();

        try {

            await register({
                email,
                password,
                firstName,
                lastName,
                phoneNumber,
            });

            setSuccess(
                "Регистрация успешна"
            );

            setTimeout(() => {

                navigate("/login");

            }, 1500);

        } catch {

            setError(
                "Ошибка регистрации"
            );
        }
    };

    return (
        <section
            className="
                flex
                min-h-screen
                items-center
                justify-center
                bg-zinc-950
                px-6
                pt-18
            "
        >

            <form
                onSubmit={handleSubmit}
                className="
                    w-full
                    max-w-md
                    rounded-3xl
                    border
                    border-white/10
                    bg-white/5
                    p-10
                    backdrop-blur-md
                "
            >

                <h1
                    className="
                        text-4xl
                        font-bold
                        text-white
                    "
                >
                    Регистрация
                </h1>

                {
                    success && (
                        <p
                            className="
                                mt-4
                                text-green-400
                            "
                        >
                            {success}
                        </p>
                    )
                }

                {
                    error && (
                        <p
                            className="
                                mt-4
                                text-red-400
                            "
                        >
                            {error}
                        </p>
                    )
                }

                <div className="mt-6">

                    <label
                        className="
            text-sm
            text-gray-400
        "
                    >
                        Имя
                    </label>

                    <input
                        type="text"

                        value={firstName}

                        onChange={(e) =>
                            setFirstName(e.target.value)
                        }

                        required

                        className="
            mt-2
            w-full
            rounded-2xl
            border
            border-white/10
            bg-black/30
            px-4
            py-4
            text-white
        "
                    />

                </div>

                <div className="mt-6">

                    <label
                        className="
            text-sm
            text-gray-400
        "
                    >
                        Фамилия
                    </label>

                    <input
                        type="text"

                        value={lastName}

                        onChange={(e) =>
                            setLastName(e.target.value)
                        }

                        required

                        className="
            mt-2
            w-full
            rounded-2xl
            border
            border-white/10
            bg-black/30
            px-4
            py-4
            text-white
        "
                    />

                </div>

                <div className="mt-6">

                    <label
                        className="
            text-sm
            text-gray-400
        "
                    >
                        Телефон
                    </label>

                    <input
                        type="text"

                        value={phoneNumber}
                        placeholder="+79991234567"

                        onChange={(e) =>
                            setPhoneNumber(e.target.value)
                        }

                        required

                        className="
            mt-2
            w-full
            rounded-2xl
            border
            border-white/10
            bg-black/30
            px-4
            py-4
            text-white
        "
                    />

                </div>

                <div className="mt-8">

                    <label
                        className="
                            text-sm
                            text-gray-400
                        "
                    >
                        Email
                    </label>

                    <input
                        type="email"

                        value={email}

                        onChange={(e) =>
                            setEmail(e.target.value)
                        }

                        required

                        className="
                            mt-2
                            w-full
                            rounded-2xl
                            border
                            border-white/10
                            bg-black/30
                            px-4
                            py-4
                            text-white
                            outline-none
                        "
                    />

                </div>

                <div className="mt-6">

                    <label
                        className="
                            text-sm
                            text-gray-400
                        "
                    >
                        Пароль
                    </label>

                    <input
                        type="password"

                        value={password}

                        onChange={(e) =>
                            setPassword(e.target.value)
                        }

                        required

                        className="
                            mt-2
                            w-full
                            rounded-2xl
                            border
                            border-white/10
                            bg-black/30
                            px-4
                            py-4
                            text-white
                            outline-none
                        "
                    />

                </div>

                <button
                    type="submit"
                    className="
                        mt-8
                        w-full
                        rounded-2xl
                        bg-white
                        px-6
                        py-4
                        text-lg
                        font-semibold
                        text-black
                        transition
                        hover:bg-gray-200
                    "
                >
                    Зарегистрироваться
                </button>

            </form>

        </section>
    );
}