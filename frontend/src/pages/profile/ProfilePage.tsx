import {useEffect, useState} from "react";

import { getCurrentUser } from "../../api/userApi";

import ProfileEditModal from "../../components/ProfileEditModal";

import type {User} from "../../types/user";

export default function ProfilePage() {

    const [user, setUser] =
        useState<User | null>(null);

    const [loading, setLoading] =
        useState(true);

    const [editOpen, setEditOpen] =
        useState(false);

    const [success, setSuccess] =
        useState(false);

    useEffect(() => {

        loadProfile();

    }, []);

    const loadProfile = async () => {

        try {

            const data =
                await getCurrentUser();

            setUser(data);

        } catch (error) {

            console.error(
                "Ошибка загрузки профиля",
                error
            );

        } finally {

            setLoading(false);
        }
    };

    if (loading) {

        return (
            <div
                className="
    flex
    items-center
    justify-center
    py-20
    text-white
"
            >
                Загрузка профиля...
            </div>
        );
    }

    if (!user) {

        return (
            <div
                className="
    flex
    items-center
    justify-center
    py-20
    text-white
"
            >
                Не удалось загрузить профиль
            </div>
        );
    }

    return (
        <>
                    <div className="mt-4">

                        <h1
                            className="
            text-5xl
            font-bold
            text-white
        "
                        >
                            Здравствуйте,
                            {" "}
                            {user.firstName}
                            👋
                        </h1>

                        <p
                            className="
            mt-4
            text-lg
            text-gray-400
        "
                        >
                            Добро пожаловать в личный кабинет SkyPeak Hotel
                        </p>

                    </div>

                    <div
                        className="
                            mt-12
                            rounded-3xl
                            border
                            border-white/10
                            bg-white/5
                            p-10
                            backdrop-blur-md
                        "
                    >
                        {
                            success && (
                                <div
                                    className="
                mb-6
                rounded-2xl
                border
                border-green-500/30
                bg-green-500/10
                p-4
                text-green-400
            "
                                >
                                    Профиль успешно обновлён
                                </div>
                            )
                        }

                        <div
                            className="
        flex
        flex-col
        items-center
        gap-6

        md:flex-row
    "
                        >

                            {
                                user.avatarUrl ? (

                                    <img
                                        src={user.avatarUrl}
                                        alt="Аватар"
                                        className="
                h-24
                w-24
                rounded-full
                object-cover
            "
                                    />

                                ) : (

                                    <div
                                        className="
                flex
                h-24
                w-24
                items-center
                justify-center
                rounded-full
                bg-white/10
                text-3xl
                font-bold
                text-white
            "
                                    >
                                        {user.firstName.charAt(0)}
                                    </div>

                                )
                            }

                            <div>

                                <h2
                                    className="
                                        text-3xl
                                        font-semibold
                                        text-white
                                    "
                                >
                                    {[
                                        user.firstName,
                                        user.middleName,
                                        user.lastName
                                    ]
                                        .filter(Boolean)
                                        .join(" ")}
                                </h2>

                                <p
                                    className="
        mt-2
        text-sm
        uppercase
        tracking-widest
        text-gray-500
    "
                                >
                                    {
                                        getRoleLabel(
                                            user.role
                                        )
                                    }
                                </p>

                            </div>

                        </div>


                        <div
                            className="
        mt-10
        grid
        gap-4

        md:grid-cols-3
    "
                        >

                            <StatCard
                                title="Статус"
                                value={
                                    getStatusLabel(
                                        user.status
                                    )
                                }
                            />

                            <StatCard
                                title="Роль"
                                value={
                                    getRoleLabel(
                                        user.role
                                    )
                                }
                            />

                            <StatCard
                                title="Дата регистрации"
                                value={
                                    new Date(
                                        user.createdAt
                                    ).toLocaleDateString(
                                        "ru-RU"
                                    )
                                }
                            />

                        </div>

                        <div
                            className="
        mt-10
        grid
        gap-6
        md:grid-cols-2
    "
                        >

                            <InfoBlock
                                label="Email"
                                value={user.email}
                            />

                            <InfoBlock
                                label="Телефон"
                                value={user.phoneNumber}
                            />

                            <InfoBlock
                                label="Дата рождения"
                                value={
                                    user.birthDate
                                        ? new Date(
                                            user.birthDate
                                        ).toLocaleDateString(
                                            "ru-RU"
                                        )
                                        : "Не указана"
                                }
                            />

                            <InfoBlock
                                label="Последний вход"
                                value={
                                    user.lastLoginAt
                                        ? new Date(
                                            user.lastLoginAt
                                        ).toLocaleString("ru-RU")
                                        : "Нет данных"
                                }
                            />

                        </div>

                        <button
                            onClick={() =>
                                setEditOpen(true)
                            }
                            className="
                                mt-10
                                rounded-2xl
                                bg-white
                                px-8
                                py-4
                                font-semibold
                                text-black
                                transition
                                hover:bg-gray-200
                            "
                        >
                            Редактировать профиль
                        </button>

                    </div>

            {
                editOpen && (
                    <ProfileEditModal

                        user={user}

                        onClose={() =>
                            setEditOpen(false)
                        }

                        onUpdated={async () => {

                            const updated =
                                await getCurrentUser();

                            setUser(updated);

                            setSuccess(true);

                            setTimeout(
                                () => setSuccess(false),
                                3000
                            );
                        }}
                    />
                )
            }
        </>
    );
}

function getRoleLabel(
    role: string
) {

    switch (role) {

        case "ADMIN":
            return "Администратор";

        case "MANAGER":
            return "Менеджер";

        case "USER":
            return "Пользователь";

        default:
            return role;
    }
}

function getStatusLabel(
    status: string
) {

    switch (status) {

        case "ACTIVE":
            return "🟢 Активен";

        case "BLOCKED":
            return "🔴 Заблокирован";

        default:
            return status;
    }
}

function StatCard({
                      title,
                      value,
                  }: {
    title: string;
    value: string;
}) {

    return (
        <div
            className="
                rounded-2xl
                border
                border-white/10
                bg-white/5
                p-5
            "
        >

            <p
                className="
                    text-sm
                    text-gray-400
                "
            >
                {title}
            </p>

            <p
                className="
                    mt-2
                    text-lg
                    font-semibold
                    text-white
                "
            >
                {value}
            </p>

        </div>
    );
}

function InfoBlock({
                       label,
                       value,
                   }: {
    label: string;
    value: string;
}) {

    return (
        <div
            className="
                rounded-2xl
                border
                border-white/10
                bg-black/20
                p-5
            "
        >

            <p
                className="
                    text-sm
                    text-gray-400
                "
            >
                {label}
            </p>

            <p
                className="
                    mt-2
                    text-lg
                    text-white
                "
            >
                {value}
            </p>

        </div>
    );
}