import { useState } from "react";

import {
    login
} from "../api/authApi";

import {
    useAuth
} from "../context/AuthContext";

export default function LoginPage() {

    const auth =
        useAuth();

    const [email, setEmail] =
        useState("");

    const [password, setPassword] =
        useState("");

    const [error, setError] =
        useState("");

    const handleSubmit = async (
        e: React.FormEvent
    ) => {

        e.preventDefault();

        try {

            const response =
                await login({
                    email,
                    password,
                });

            auth.login(

                response.accessToken,

                response.email,

                response.firstName,

                response.role
            );

            window.location.href = "/";

        } catch {

            setError(
                "Неверный email или пароль"
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
                    Вход
                </h1>

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
                    Войти
                </button>

            </form>

        </section>
    );
}