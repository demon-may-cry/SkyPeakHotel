import {
    Mail,
    Send,
    Code2,
} from "lucide-react";

const contacts = [
    {
        title: "Email",
        description: "Свяжитесь со мной по электронной почте.",
        value: "demonmaycry@mail.ru",
        href: "mailto:demonmaycry@mail.ru",
        icon: Mail,
    },
    {
        title: "Telegram",
        description: "Напишите мне в Telegram.",
        value: "@DmitryEltsov",
        href: "https://t.me/DmitryEltsov",
        icon: Send,
    },
    {
        title: "GitHub",
        description: "Исходный код и другие проекты.",
        value: "GitHub",
        href: "https://github.com/demon-may-cry",
        icon: Code2,
    },
];

export default function ContactsPage() {

    return (

        <section className="
            min-h-screen
            bg-zinc-950
            px-6
            pb-20
            pt-32
            text-white
        ">

            <div className="
                mx-auto
                max-w-5xl
            ">

                {/* Заголовок */}

                <div className="max-w-2xl">

                    <p className="
                        text-xs
                        uppercase
                        tracking-[0.35em]
                        text-zinc-500
                    ">
                        SkyPeak Hotel
                    </p>

                    <h1 className="
                        mt-4
                        text-5xl
                        font-semibold
                        tracking-tight
                        sm:text-6xl
                    ">
                        Контакты
                    </h1>

                    <p className="
                        mt-5
                        text-lg
                        leading-relaxed
                        text-zinc-400
                    ">
                        Если у вас есть вопросы по проекту,
                        предложения или вы хотите связаться
                        с разработчиком — используйте любой
                        удобный способ связи.
                    </p>

                </div>


                {/* Контакты */}

                <div className="
                    mt-14
                    grid
                    gap-5
                    md:grid-cols-3
                ">

                    {contacts.map((contact) => {

                        const Icon = contact.icon;

                        return (

                            <a
                                key={contact.title}
                                href={contact.href}
                                target={
                                    contact.title === "Email"
                                        ? undefined
                                        : "_blank"
                                }
                                rel={
                                    contact.title === "Email"
                                        ? undefined
                                        : "noopener noreferrer"
                                }
                                className="
                                    group
    flex
    h-full
    flex-col
    rounded-3xl
    border
    border-white/10
    bg-white/5
    p-7
    transition-all
    duration-300
    hover:-translate-y-1
    hover:border-white/20
    hover:bg-white/10
                                "
                            >

                                <div className="
                                    flex
                                    h-12
                                    w-12
                                    items-center
                                    justify-center
                                    rounded-2xl
                                    bg-blue-600/10
                                    text-blue-400
                                    transition
                                    group-hover:bg-blue-600/20
                                ">

                                    <Icon size={24} />

                                </div>

                                <h2 className="
                                    mt-6
                                    text-xl
                                    font-semibold
                                    text-white
                                ">
                                    {contact.title}
                                </h2>

                                <p className="
                                    mt-2
                                    text-sm
                                    leading-relaxed
                                    text-zinc-400
                                ">
                                    {contact.description}
                                </p>

                                <p className="
                                    mt-auto
        pt-5
        break-all
        text-sm
        font-medium
        text-blue-400
        transition-colors
        group-hover:text-blue-300
                                ">
                                    {contact.value}
                                </p>

                            </a>

                        );

                    })}

                </div>


                {/* Дополнительная информация */}

                <div className="
                    mt-8
                    rounded-3xl
                    border
                    border-zinc-800
                    bg-zinc-900/40
                    p-8
                ">

                    <h2 className="
                        text-2xl
                        font-semibold
                        text-white
                    ">
                        О проекте
                    </h2>

                    <p className="
                        mt-4
                        max-w-3xl
                        leading-relaxed
                        text-zinc-400
                    ">
                        SkyPeak Hotel — учебный pet-проект,
                        созданный для демонстрации навыков
                        разработки frontend и backend-приложений.
                    </p>

                </div>

            </div>

        </section>

    );

}