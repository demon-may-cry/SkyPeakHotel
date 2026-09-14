import api from "./axios";

import type { Balance } from "../types/balance";
import type { Transaction } from "../types/transaction";
import type { Page } from "../types/page";

export async function getBalance(): Promise<Balance> {

    const response = await api.get("/balance");

    return response.data;

}

export async function getTransactions(
    page = 0,
    size = 5
): Promise<Page<Transaction>> {

    const response = await api.get("/balance/transactions", {
        params: {
            page,
            size,
        },
    });

    return response.data;

}

export async function deposit(amount: number): Promise<void> {

    await api.post("/balance/deposit", {
        amount,
    });

}