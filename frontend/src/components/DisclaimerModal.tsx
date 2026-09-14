import { useEffect, useState } from "react";

export default function DisclaimerModal() {

    const [isOpen, setIsOpen] = useState(false);

    useEffect(() => {

        setIsOpen(true);

    }, []);

    const handleClose = () => {

        setIsOpen(false);

    };

    if (!isOpen) {

        return null;

    }

    return (

        <div
            className="
                fixed
                inset-0
                z-[200]
                flex
                items-center
                justify-center
                bg-black/70
                backdrop-blur-md
                animate-fadeIn
                p-4
                sm:px-6
            "
        >

            <div
                className="
                    relative
                    w-full
                    max-w-2xl
                    max-h-[calc(100dvh-2rem)]
                    overflow-y-auto
                    overscroll-contain
                    rounded-3xl
                    border
                    border-white/10
                    bg-zinc-900/95
                    p-6
                    sm:p-10
                    text-white
                    shadow-2xl
                    backdrop-blur-xl
                    animate-scaleIn
                "
            >

                <button
                    type="button"
                    onClick={handleClose}
                    aria-label="Закрыть уведомление"
                    className="
                        absolute
                        right-4
                        top-3
                        z-10
                        flex
                        h-10
                        w-10
                        items-center
                        justify-center
                        rounded-full
                        text-3xl
                        leading-none
                        text-gray-400
                        transition
                        hover:bg-white/10
                        hover:text-white
                    "
                >
                    ×
                </button>

                <p
                    className="
                        uppercase
                        tracking-[0.3em]
                        text-gray-400
                        text-xs
                        sm:text-sm
                    "
                >
                    Важно
                </p>

                <h2
                    className="
                        mt-4
                        pr-10
                        text-2xl
                        font-bold
                        sm:text-3xl
                    "
                >
                    Информационное уведомление
                </h2>

                <div
                    className="
                        mt-6
                        space-y-5
                        text-base
                        leading-relaxed
                        text-gray-300
                        sm:text-lg
                    "
                >

                    <p>
                        Данный сайт является
                        pet-проектом и создан
                        исключительно в
                        демонстрационных
                        и образовательных целях.
                    </p>

                    <p>
                        Все данные, бронирования,
                        номера, формы,
                        изображения и действия
                        пользователей являются
                        частью учебного проекта.
                    </p>

                    <p>
                        Сайт не предоставляет
                        реальные гостиничные услуги,
                        а разработчик проекта
                        не несёт ответственности
                        за любые действия,
                        связанные с использованием
                        данного ресурса.
                    </p>

                    <p>
                        Не вводите реальные
                        платёжные данные
                        или конфиденциальную
                        информацию.
                    </p>

                </div>

                <button
                    type="button"
                    onClick={handleClose}
                    className="
                        mt-8
                        w-full
                        rounded-2xl
                        bg-white
                        px-6
                        py-3
                        text-base
                        font-semibold
                        text-black
                        transition
                        hover:bg-gray-200
                        active:scale-[0.98]
                        sm:py-4
                        sm:text-lg
                    "
                >
                    Понятно
                </button>

            </div>

        </div>

    );

}