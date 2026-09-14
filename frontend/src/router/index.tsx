import { createBrowserRouter } from "react-router-dom";
import MainLayout from "../layouts/MainLayout";
import HomePage from "../pages/HomePage";
import RoomsPage from "../pages/RoomsPage";
import RoomDetailsPage from "../pages/RoomDetailsPage";
import LoginPage from "../pages/LoginPage";
import RegisterPage from "../pages/RegisterPage";
import ProtectedRoute from "../components/ProtectedRoute";
import ProfilePage from "../pages/profile/ProfilePage";
import BookingsPage from "../pages/profile/BookingsPage";
import BalancePage from "../pages/profile/BalancePage";
import TransactionsPage from "../pages/profile/TransactionsPage";
import ProfileLayout from "../layouts/ProfileLayout.tsx";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <MainLayout />,
        children: [
            {
                index: true,
                element: <HomePage />,
            },

            {
                path: "rooms",
                element: <RoomsPage />,
            },

            {
                path: "rooms/:slug",
                element: <RoomDetailsPage />,
            },

            {
                path: "login",
                element: <LoginPage />,
            },

            {
                path: "register",
                element: <RegisterPage />,
            },

            {
                path: "profile",
                element: <ProtectedRoute />,
                children: [
                    {
                        element: <ProfileLayout />,
                        children: [
                            {
                                index: true,
                                element: <ProfilePage />,
                            },
                            {
                                path: "bookings",
                                element: <BookingsPage />,
                            },
                            {
                                path: "balance",
                                element: <BalancePage />,
                            },
                            {
                                path: "transactions",
                                element: <TransactionsPage />,
                            },
                            /*{
                                path: "settings",
                                element: <SettingsPage />,
                            },*/
                        ],
                    },
                ],
            },
        ],
    },
]);