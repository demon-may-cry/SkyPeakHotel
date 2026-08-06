import {
    createContext,
    useContext,
    useEffect,
    useState,
} from "react";

import type { ReactNode } from "react";

import { getBalance } from "../api/balanceApi";

interface BalanceContextType {

    balance: number;

    refreshBalance: () => Promise<void>;

}

const BalanceContext =
    createContext<BalanceContextType | null>(null);

export function BalanceProvider({

                                    children,

                                }: {
    children: ReactNode;
})
{

    const [balance, setBalance] =
        useState(0);

    async function refreshBalance() {

        try {

            const response =
                await getBalance();

            setBalance(response.balance);

        } catch {

            setBalance(0);

        }

    }

    useEffect(() => {

        refreshBalance();

    }, []);

    return (

        <BalanceContext.Provider
            value={{
                balance,
                refreshBalance,
            }}
        >

            {children}

        </BalanceContext.Provider>

    );

}

export function useBalance() {

    const context =
        useContext(BalanceContext);

    if (!context) {

        throw new Error(
            "useBalance должен использоваться внутри BalanceProvider."
        );

    }

    return context;

}