import {useEffect, useState} from "react";
import {Wallet} from "lucide-react";
import { Link } from "react-router-dom";
import DepositModal from "../../components/profile/DepositModal";

import {
    deposit,
    getTransactions,
} from "../../api/balanceApi";

import {
    formatMoney,
    formatTransactionDate,
    getTransactionTitle,
} from "../../utils/transactionUtils";

import toast from "react-hot-toast";

import { useBalance } from "../../context/BalanceContext";
import type {Transaction} from "../../types/transaction";

export default function BalancePage() {

    const {balance, refreshBalance} = useBalance();
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [loading, setLoading] = useState(true);
    const [depositOpen, setDepositOpen] = useState(false);
    const [depositLoading, setDepositLoading] = useState(false);

    useEffect(() => {
        loadBalanceData();
    }, []);

    async function handleDeposit(amount: number) {

        try {

            setDepositLoading(true);

            await deposit(amount);

            toast.success("Баланс успешно пополнен.");

            setDepositOpen(false);

            await Promise.all([
                loadBalanceData(),
                refreshBalance(),
            ]);

        } catch (error: any) {

            toast.error(
                error.response?.data?.message ??
                "Не удалось пополнить баланс."
            );

        } finally {

            setDepositLoading(false);

        }

    }

    async function loadBalanceData() {

        try {

            const transactionResponse = await getTransactions(0, 5);

            setTransactions(transactionResponse.content);

            await refreshBalance();

        } finally {

            setLoading(false);

        }

    }

    if (loading) {

        return (

            <div className="py-20 text-center text-zinc-400">
                Загрузка...
            </div>

        );

    }

    return (

        <>

            <div className="mb-10">

                <p className="text-xs uppercase tracking-[0.4em] text-zinc-500">
                    Личный кабинет
                </p>

                <h1 className="mt-3 text-5xl font-bold text-white">
                    Баланс
                </h1>

                <p className="mt-3 text-zinc-400">
                    Управляйте средствами и просматривайте историю операций.
                </p>

            </div>

            <div
                className="
                    rounded-3xl
                    border
                    border-zinc-800
                    bg-gradient-to-r
                    from-zinc-900
                    to-zinc-800
                    p-8
                "
            >

                <div className="flex items-start justify-between">

                    <div>

                        <p className="text-zinc-400">
                            Доступный баланс
                        </p>

                        <h2 className="mt-4 text-5xl font-bold text-white">

                            {balance.toLocaleString("ru-RU")} ₽

                        </h2>

                        <p className="mt-4 text-sm text-zinc-500">
                            Средства можно использовать для оплаты бронирований.
                        </p>

                        <button
                            onClick={() => setDepositOpen(true)}
                            className="
        mt-8
        rounded-xl
        bg-blue-600
        px-6
        py-3
        font-medium
        text-white
        transition-all
        hover:bg-blue-700
    "
                        >
                            Пополнить баланс
                        </button>

                    </div>

                    <div
                        className="
                            rounded-2xl
                            bg-blue-600/20
                            p-4
                        "
                    >

                        <Wallet
                            size={42}
                            className="text-blue-400"
                        />

                    </div>

                </div>

            </div>

            <div className="mt-12 flex items-center justify-between">

                <h2 className="text-2xl font-bold text-white">
                    История операций
                </h2>

                {transactions.length > 0 && (
                    <span className="text-sm text-zinc-500">
        Последние {transactions.length} операций
    </span>
                )}

            </div>

            {transactions.length === 0 ? (

                <div
                    className="
                        mt-6
                        rounded-3xl
                        border
                        border-dashed
                        border-zinc-700
                        p-16
                        text-center
                    "
                >

                    <div className="text-6xl">
                        💳
                    </div>

                    <h3 className="mt-6 text-2xl font-semibold text-white">
                        Пока нет операций
                    </h3>

                    <p className="mt-3 text-zinc-400">
                        Пополните баланс или забронируйте номер,
                        чтобы увидеть историю операций.
                    </p>

                </div>

            ) : (

                <div className="mt-6 space-y-4">

                    {transactions.map((transaction) => (

                        <div
                            key={transaction.id}
                            className="
        flex
        items-center
        justify-between
        rounded-2xl
        border
        border-zinc-800
        bg-zinc-900/50
        p-6
    "
                        >

                            <div>

                                <p className="font-semibold text-white">
                                    {getTransactionTitle(
                                        transaction.type,
                                        transaction.description
                                    )}
                                </p>

                                <p className="mt-2 text-sm text-zinc-500">
                                    {formatTransactionDate(
                                        transaction.createdAt
                                    )}
                                </p>

                            </div>

                            <div
                                className={`text-2xl font-bold ${
                                    transaction.type === "DEPOSIT"
                                        ? "text-green-400"
                                        : "text-red-400"
                                }`}
                            >

                                {transaction.type === "DEPOSIT"
                                    ? "+"
                                    : "-"}

                                {formatMoney(transaction.amount)}
                            </div>

                        </div>

                    ))}

                    <div className="mt-8 flex justify-center">

                        <Link
                            to="/profile/transactions"
                            className="
            rounded-xl
            border
            border-zinc-700
            px-6
            py-3
            text-zinc-300
            transition
            hover:border-blue-500
            hover:text-white
        "
                        >
                            Показать всю историю →
                        </Link>

                    </div>

                </div>

            )}

            <DepositModal
                open={depositOpen}
                loading={depositLoading}
                onClose={() => setDepositOpen(false)}
                onDeposit={handleDeposit}
            />
        </>

    );

}