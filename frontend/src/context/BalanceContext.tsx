import {
    createContext,
    useContext,
    useEffect,
    useState,
} from "react";

import type { ReactNode } from "react";

import { getBalance } from "../api/balanceApi";
import { useAuth } from "./AuthContext";

interface BalanceContextType {

    balance: number;

    refreshBalance: () => Promise<void>;

    clearBalance: () => void;

}

const BalanceContext =
    createContext<BalanceContextType | null>(null);

export function BalanceProvider({
                                    children,
                                }: {
    children: ReactNode;
}) {

    const { isAuthenticated } = useAuth();

    const [balance, setBalance] =
        useState(0);

    async function refreshBalance() {

        if (!isAuthenticated) {

            setBalance(0);

            return;
        }

        try {

            const response =
                await getBalance();

            setBalance(response.balance);

        } catch {

            setBalance(0);

        }

    }

    function clearBalance() {

        setBalance(0);

    }

    useEffect(() => {

        if (isAuthenticated) {

            refreshBalance();

        } else {

            clearBalance();

        }

    }, [isAuthenticated]);

    return (

        <BalanceContext.Provider
            value={{
                balance,
                refreshBalance,
                clearBalance,
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