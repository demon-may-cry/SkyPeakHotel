import React from "react";
import ReactDOM from "react-dom/client";

import { RouterProvider } from "react-router-dom";

import { Toaster } from "react-hot-toast";

import "./index.css";

import { router } from "./router";

import { AuthProvider } from "./context/AuthContext";
import { BalanceProvider } from "./context/BalanceContext";

ReactDOM.createRoot(
    document.getElementById("root")!
).render(

    <React.StrictMode>

        <AuthProvider>

            <BalanceProvider>

                <RouterProvider router={router} />

                <Toaster
                    position="top-right"
                    toastOptions={{
                        duration: 3000,
                    }}
                />

            </BalanceProvider>

        </AuthProvider>

    </React.StrictMode>

);