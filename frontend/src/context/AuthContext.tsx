import {
    createContext,
    useContext,
    useState,
} from "react";

import type {
    ReactNode
} from "react";

interface AuthContextType {

    token: string | null;

    email: string | null;

    firstName: string | null;

    role: string | null;

    login: (
        token: string,
        email: string,
        firstName: string,
        role: string
    ) => void;

    logout: () => void;

    isAuthenticated: boolean;
}

const AuthContext =
    createContext<AuthContextType | null>(null);

export function AuthProvider({
                                 children,
                             }: {
    children: ReactNode;
}) {

    const [token, setToken] =
        useState<string | null>(
            localStorage.getItem("token")
        );

    const handleLogin = (

        jwt: string,

        emailValue: string,

        firstNameValue: string,

        roleValue: string

    ) => {

        localStorage.setItem(
            "token",
            jwt
        );

        localStorage.setItem(
            "email",
            emailValue
        );

        localStorage.setItem(
            "firstName",
            firstNameValue
        );

        localStorage.setItem(
            "role",
            roleValue
        );

        setToken(jwt);

        setEmail(emailValue);

        setFirstName(
            firstNameValue
        );

        setRole(roleValue);
    };

    const handleLogout = () => {

        localStorage.removeItem("token");

        localStorage.removeItem("email");

        localStorage.removeItem("firstName");

        localStorage.removeItem("role");

        setToken(null);

        setEmail(null);

        setFirstName(null);

        setRole(null);

        window.location.href = "/";
    };

    const [email, setEmail] =
        useState<string | null>(
            localStorage.getItem("email")
        );

    const [firstName, setFirstName] =
        useState<string | null>(
            localStorage.getItem("firstName")
        );

    const [role, setRole] =
        useState<string | null>(
            localStorage.getItem("role")
        );

    return (
        <AuthContext.Provider
            value={{

                token,

                email,

                firstName,

                role,

                login: handleLogin,

                logout: handleLogout,

                isAuthenticated: !!token,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {

    const context =
        useContext(AuthContext);

    if (!context) {

        throw new Error(
            "useAuth must be used within AuthProvider"
        );
    }

    return context;
}