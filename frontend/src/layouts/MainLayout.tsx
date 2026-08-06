import { Outlet } from "react-router-dom";
import DisclaimerModal from "../components/DisclaimerModal";
import Header from "../components/Header";
import Footer from "../components/Footer";

export default function MainLayout() {
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