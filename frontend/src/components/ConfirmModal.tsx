import { useEffect } from "react";
import type { ReactNode } from "react";

interface ConfirmModalProps {
    isOpen: boolean;
    title: string;
    message: ReactNode;

    loading?: boolean;

    confirmText?: string;
    cancelText?: string;
    loadingText?: string;

    confirmButtonClassName?: string;

    onConfirm: () => void;
    onCancel: () => void;
}

export default function ConfirmModal({
                                         isOpen,
                                         title,
                                         message,

                                         loading = false,

                                         confirmText = "Подтвердить",
                                         cancelText = "Отмена",
                                         loadingText = "Выполняется...",

                                         confirmButtonClassName = "bg-red-600 hover:bg-red-700",

                                         onConfirm,
                                         onCancel,
                                     }: ConfirmModalProps) {

    useEffect(() => {
        if (!isOpen || loading) {
            return;
        }

        const handleKeyDown = (event: KeyboardEvent) => {
            if (event.key === "Escape") {
                onCancel();
            }
        };

        window.addEventListener("keydown", handleKeyDown);

        return () => {
            window.removeEventListener("keydown", handleKeyDown);
        };
    }, [isOpen, loading, onCancel]);

    useEffect(() => {
        if (!isOpen) {
            return;
        }

        const originalOverflow = document.body.style.overflow;

        document.body.style.overflow = "hidden";

        return () => {
            document.body.style.overflow = originalOverflow;
        };
    }, [isOpen]);

    if (!isOpen) {
        return null;
    }

    return (
        <div
            className="
                fixed inset-0 z-50
                flex items-center justify-center
                bg-black/60
                backdrop-blur-sm
                px-4
            "
            onClick={() => !loading && onCancel()}
        >
            <div
                onClick={(event) => event.stopPropagation()}
                className="
                    w-full
                    max-w-md
                    rounded-2xl
                    border
                    border-zinc-800
                    bg-zinc-900
                    p-6
                    shadow-2xl
                "
            >
                <h2 className="text-2xl font-bold text-white">
                    {title}
                </h2>

                <div className="mt-4 text-zinc-300">
                    {message}
                </div>

                <div className="mt-8 flex justify-end gap-3">

                    <button
                        onClick={onCancel}
                        disabled={loading}
                        className="
                            rounded-lg
                            border
                            border-zinc-700
                            px-5
                            py-2
                            text-white
                            hover:bg-zinc-800
                            transition-colors
                            disabled:cursor-not-allowed
                            disabled:opacity-50
                        "
                    >
                        {cancelText}
                    </button>

                    <button
                        onClick={onConfirm}
                        disabled={loading}
                        className={`
                            rounded-lg
                            px-5
                            py-2
                            text-white
                            transition-colors
                            disabled:cursor-not-allowed
                            disabled:opacity-50
                            ${confirmButtonClassName}
                        `}
                    >
                        {loading
                            ? loadingText
                            : confirmText}
                    </button>

                </div>

            </div>

        </div>
    );
}