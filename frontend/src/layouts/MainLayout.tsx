import { Outlet } from "react-router-dom";
import DisclaimerModal from "../components/DisclaimerModal";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { registerVisit } from "../api/analyticsApi";

export default function MainLayout() {

    useEffect(() => {
        registerVisit()
            .catch((error) => {
                console.error(
                    "Ошибка регистрации посещения:",
                    error
                );
            });
    }, []);

    return (
        <div className="min-h-screen bg-zinc-950">

            <DisclaimerModal />

            <Header />

            <main>
                <Outlet />
            </main>

            <Footer />
        </div>
    );
}