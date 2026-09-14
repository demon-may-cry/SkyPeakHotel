import type { ChangeEvent } from "react";
import { useEffect, useState } from "react";
import { createPortal } from "react-dom";
import { Wallet } from "lucide-react";

interface DepositModalProps {

    open: boolean;

    loading: boolean;

    onClose: () => void;

    onDeposit: (amount: number) => Promise<void>;

}

export default function DepositModal({

                                         open,
                                         loading,
                                         onClose,
                                         onDeposit,

                                     }: DepositModalProps) {

    const [amount, setAmount] = useState("");


    useEffect(() => {

        if (!open) {
            return;
        }

        const originalOverflow =
            document.body.style.overflow;

        document.body.style.overflow = "hidden";

        return () => {

            document.body.style.overflow =
                originalOverflow;

        };

    }, [open]);


    useEffect(() => {

        if (!open || loading) {
            return;
        }

        const handleKeyDown = (
            event: KeyboardEvent
        ) => {

            if (event.key === "Escape") {
                onClose();
            }

        };

        window.addEventListener(
            "keydown",
            handleKeyDown
        );

        return () => {

            window.removeEventListener(
                "keydown",
                handleKeyDown
            );

        };

    }, [open, loading, onClose]);


    if (!open) {
        return null;
    }


    async function handleSubmit() {

        const value = Number(amount);

        if (
            value <= 0 ||
            Number.isNaN(value)
        ) {
            return;
        }

        await onDeposit(value);

        setAmount("");

    }


    return createPortal(

        <div
            className="
                fixed
                inset-0
                z-[9999]
                flex
                items-center
                justify-center
                overflow-y-auto
                bg-black/70
                px-4
                py-6
            "
            onClick={() =>
                !loading && onClose()
            }
        >

            <div
                onClick={(event) =>
                    event.stopPropagation()
                }
                className="
                    my-auto
                    w-full
                    max-w-md
                    rounded-3xl
                    border
                    border-zinc-700
                    bg-zinc-900
                    p-6
                    shadow-2xl
                    sm:p-8
                "
            >

                {/* Заголовок */}

                <div className="
                    flex
                    items-center
                    gap-3
                ">

                    <div className="
                        rounded-xl
                        bg-blue-600/20
                        p-3
                    ">

                        <Wallet
                            size={26}
                            className="text-blue-400"
                        />

                    </div>

                    <div>

                        <h2 className="
                            text-2xl
                            font-bold
                            text-white
                        ">
                            Пополнение баланса
                        </h2>

                        <p className="
                            mt-1
                            text-zinc-400
                        ">
                            Укажите сумму пополнения.
                        </p>

                    </div>

                </div>


                {/* Сумма */}

                <input
                    type="number"
                    min={1}
                    value={amount}
                    onChange={(
                        e: ChangeEvent<HTMLInputElement>
                    ) =>
                        setAmount(e.target.value)
                    }
                    className="
                        mt-6
                        w-full
                        rounded-xl
                        border
                        border-zinc-700
                        bg-zinc-800
                        px-4
                        py-3
                        text-white
                        outline-none
                        transition
                        focus:border-blue-500
                        focus:ring-1
                        focus:ring-blue-500/30
                    "
                    placeholder="10000 ₽"
                    disabled={loading}
                />


                {/* Кнопки */}

                <div className="
                    mt-8
                    flex
                    flex-col-reverse
                    gap-3
                    sm:flex-row
                    sm:justify-end
                    sm:gap-4
                ">

                    <button
                        type="button"
                        onClick={onClose}
                        disabled={loading}
                        className="
                            w-full
                            rounded-xl
                            border
                            border-red-500/40
                            bg-red-500/10
                            px-6
                            py-3
                            font-medium
                            text-red-400
                            transition-all
                            duration-200
                            hover:border-red-500
                            hover:bg-red-500/20
                            hover:text-red-300
                            disabled:cursor-not-allowed
                            disabled:opacity-50
                            sm:min-w-[140px]
                            sm:w-auto
                        "
                    >
                        Отмена
                    </button>


                    <button
                        type="button"
                        disabled={
                            loading ||
                            Number(amount) <= 0
                        }
                        onClick={handleSubmit}
                        className="
                            w-full
                            rounded-xl
                            bg-blue-600
                            px-6
                            py-3
                            font-medium
                            text-white
                            shadow-lg
                            shadow-blue-600/20
                            transition-all
                            duration-200
                            hover:bg-blue-500
                            hover:shadow-blue-500/40
                            disabled:cursor-not-allowed
                            disabled:bg-zinc-700
                            disabled:text-zinc-400
                            disabled:shadow-none
                            sm:min-w-[160px]
                            sm:w-auto
                        "
                    >
                        {loading
                            ? "Пополнение..."
                            : "Пополнить"}
                    </button>

                </div>

            </div>

        </div>,

        document.body

    );

}