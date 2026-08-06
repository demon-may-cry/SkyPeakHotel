import { useEffect, useState } from "react";

export default function DisclaimerModal() {

    const [isOpen, setIsOpen] =
        useState(false);

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
                px-6
            "
        >

            <div
                className="
                    relative
                    w-full
                    max-w-2xl
                    rounded-3xl
                    border
                    border-white/10
                    bg-white/10
                    p-10
                    text-white
                    shadow-2xl
                    backdrop-blur-xl
                    animate-scaleIn
                "
            >

                <button
                    onClick={handleClose}
                    className="
                        absolute
                        right-5
                        top-5
                        text-3xl
                        text-gray-300
                        transition
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
                        text-sm
                    "
                >
                    Важно
                </p>

                <h2
                    className="
                        mt-4
                        text-3xl
                        font-bold
                    "
                >
                    Информационное уведомление
                </h2>

                <div
                    className="
                        mt-6
                        space-y-5
                        leading-relaxed
                        text-gray-300
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
                    onClick={handleClose}
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
                    Понятно
                </button>

            </div>

        </div>
    );
}