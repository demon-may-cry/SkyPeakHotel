import { useState } from "react";

import type { User }
    from "../types/user";

import {
    updateCurrentUser
} from "../api/userApi";

const months = [
    "Январь",
    "Февраль",
    "Март",
    "Апрель",
    "Май",
    "Июнь",
    "Июль",
    "Август",
    "Сентябрь",
    "Октябрь",
    "Ноябрь",
    "Декабрь",
];

const currentYear = new Date().getFullYear();

const years = Array.from(
    { length: 120 },
    (_, i) => currentYear - i
);

interface Props {

    user: User;

    onClose: () => void;

    onUpdated: () => void;
}

export default function ProfileEditModal({

                                             user,

                                             onClose,

                                             onUpdated,

                                         }: Props) {

    const [firstName, setFirstName] =
        useState(user.firstName);

    const [lastName, setLastName] =
        useState(user.lastName);

    const [middleName, setMiddleName] =
        useState(user.middleName ?? "");

    const [phoneNumber, setPhoneNumber] =
        useState(user.phoneNumber);

    const existingBirthDate = user.birthDate
        ? new Date(user.birthDate)
        : null;

    const [birthDay, setBirthDay] =
        useState(
            existingBirthDate
                ? String(
                    existingBirthDate.getDate()
                )
                : ""
        );

    const [birthMonth, setBirthMonth] =
        useState(
            existingBirthDate
                ? String(
                    existingBirthDate.getMonth() + 1
                )
                : ""
        );

    const [birthYear, setBirthYear] =
        useState(
            existingBirthDate
                ? String(
                    existingBirthDate.getFullYear()
                )
                : ""
        );

    const birthDate =

        birthDay &&
        birthMonth &&
        birthYear

            ? `${birthYear}-${birthMonth.padStart(2, "0")}-${birthDay.padStart(2, "0")}`

            : null;

    const handleSubmit = async (
        e: React.FormEvent
    ) => {

        e.preventDefault();

        await updateCurrentUser({

            firstName,

            lastName,

            middleName,

            phoneNumber,

            birthDate,
        });

        onUpdated();

        onClose();
    };

    return (
        <div
            className="
                fixed
                inset-0
                z-[300]
                flex
                items-center
                justify-center
                bg-black/70
                backdrop-blur-md
                px-6
            "
        >

            <form
                onSubmit={handleSubmit}
                className="
                    w-full
                    max-w-2xl
                    rounded-3xl
                    border
                    border-white/10
                    bg-zinc-900
                    p-8
                "
            >

                <h2
                    className="
                        text-3xl
                        font-bold
                        text-white
                    "
                >
                    Редактирование профиля
                </h2>

                <div
                    className="
                        mt-8
                        grid
                        gap-5
                    "
                >

                    <input
                        value={firstName}
                        onChange={(e) =>
                            setFirstName(
                                e.target.value
                            )
                        }
                        placeholder="Имя"
                        className="rounded-xl bg-zinc-800 p-4 text-white"
                    />

                    <input
                        value={lastName}
                        onChange={(e) =>
                            setLastName(
                                e.target.value
                            )
                        }
                        placeholder="Фамилия"
                        className="rounded-xl bg-zinc-800 p-4 text-white"
                    />

                    <input
                        value={middleName}
                        onChange={(e) =>
                            setMiddleName(
                                e.target.value
                            )
                        }
                        placeholder="Отчество"
                        className="rounded-xl bg-zinc-800 p-4 text-white"
                    />

                    <input
                        value={phoneNumber}
                        onChange={(e) =>
                            setPhoneNumber(
                                e.target.value
                            )
                        }
                        placeholder="+79999999999"
                        className="rounded-xl bg-zinc-800 p-4 text-white"
                    />

                    <label
                        className="
        text-sm
        text-gray-400
    "
                    >
                        Дата рождения
                    </label>

                    <div
                        className="
        grid
        grid-cols-3
        gap-4
    "
                    >

                        <select
                            value={birthDay}
                            onChange={(e) =>
                                setBirthDay(
                                    e.target.value
                                )
                            }
                            className="
            rounded-2xl
            border
            border-white/10
            bg-black/30
            px-4
            py-4
            text-white
        "
                        >
                            <option value="" disabled>
                                День
                            </option>

                            {
                                Array.from(
                                    { length: 31 },
                                    (_, i) => i + 1
                                ).map(day => (

                                    <option
                                        key={day}
                                        value={day}
                                    >
                                        {day}
                                    </option>

                                ))
                            }
                        </select>

                        <select
                            value={birthMonth}
                            onChange={(e) =>
                                setBirthMonth(
                                    e.target.value
                                )
                            }
                            className="
            rounded-2xl
            border
            border-white/10
            bg-black/30
            px-4
            py-4
            text-white
        "
                        >
                            <option value="" disabled>
                                Месяц
                            </option>

                            {
                                months.map(
                                    (month, index) => (

                                        <option
                                            key={month}
                                            value={index + 1}
                                        >
                                            {month}
                                        </option>

                                    )
                                )
                            }
                        </select>

                        <select
                            value={birthYear}
                            onChange={(e) =>
                                setBirthYear(
                                    e.target.value
                                )
                            }
                            className="
            rounded-2xl
            border
            border-white/10
            bg-black/30
            px-4
            py-4
            text-white
        "
                        >
                            <option value="" disabled>
                                Год
                            </option>

                            {
                                years.map(year => (

                                    <option
                                        key={year}
                                        value={year}
                                    >
                                        {year}
                                    </option>

                                ))
                            }
                        </select>

                    </div>

                </div>

                <div
                    className="
                        mt-8
                        flex
                        gap-4
                    "
                >

                    <button
                        type="submit"
                        className="
                            flex-1
                            rounded-xl
                            bg-white
                            px-6
                            py-4
                            font-semibold
                            text-black
                        "
                    >
                        Сохранить
                    </button>

                    <button
                        type="button"
                        onClick={onClose}
                        className="
                            flex-1
                            rounded-xl
                            border
                            border-white/10
                            px-6
                            py-4
                            text-white
                        "
                    >
                        Отмена
                    </button>

                </div>

            </form>

        </div>
    );
}