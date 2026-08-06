import { useEffect, useState } from "react";

import {
    getTransactions,
} from "../../api/balanceApi";

import type { Transaction } from "../../types/transaction";
import type { Page } from "../../types/page";

import {
    formatMoney,
    formatTransactionDate,
    getTransactionTitle,
} from "../../utils/transactionUtils";

export default function TransactionsPage() {

    const [page, setPage] =
        useState<Page<Transaction> | null>(null);

    const [loading, setLoading] =
        useState(true);

    useEffect(() => {

        load();

    }, []);

    async function load(pageNumber = 0) {

        try {

            const result =
                await getTransactions(pageNumber, 20);

            setPage(result);

        } finally {

            setLoading(false);

        }

    }

    if (loading) {

        return (
            <div className="py-20 text-center">
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
                    История операций
                </h1>

                <p className="mt-3 text-zinc-400">
                    Все операции по вашему счёту.
                </p>

            </div>

            <div className="space-y-4">

                {page?.content.map(transaction => (

                    <div
                        key={transaction.id}
                        className="
                            flex
                            items-center
                            justify-between
                            rounded-2xl
                            border
                            border-zinc-800
                            bg-zinc-900/40
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

                            {formatMoney(
                                transaction.amount
                            )}

                        </div>

                    </div>

                ))}

            </div>

        </>

    );

}