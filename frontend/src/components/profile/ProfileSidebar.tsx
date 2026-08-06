import { NavLink } from "react-router-dom";
import {
    User,
    CalendarDays,
    Wallet,
    Settings,
} from "lucide-react";

export default function ProfileSidebar() {

    const menu = [
        {
            to: "/profile",
            title: "Профиль",
            icon: User,
            end: true,
        },
        {
            to: "/profile/bookings",
            title: "Бронирования",
            icon: CalendarDays,
        },
        {
            to: "/profile/balance",
            title: "Баланс",
            icon: Wallet,
        },
        {
            to: "/profile/settings",
            title: "Настройки",
            icon: Settings,
        },
    ];

    return (

        <aside
            className="
                rounded-3xl
                border
                border-zinc-800
                bg-zinc-900/60
                backdrop-blur-xl
                p-6
                h-fit
            "
        >

            <h2 className="text-2xl font-bold text-white mb-8">
                Личный кабинет
            </h2>

            <nav className="space-y-2">

                {menu.map(item => {

                    const Icon = item.icon;

                    return (

                        <NavLink
                            key={item.to}
                            to={item.to}
                            end={item.end}
                            className={({ isActive }) =>
                                `
                                flex
                                items-center
                                gap-3
                                rounded-xl
                                px-4
                                py-3
                                transition

                                ${
                                    isActive
                                        ? "bg-blue-600 text-white"
                                        : "text-zinc-400 hover:bg-zinc-800 hover:text-white"
                                }
                                `
                            }
                        >

                            <Icon size={20} />

                            {item.title}

                        </NavLink>

                    );

                })}

            </nav>

        </aside>

    );

}