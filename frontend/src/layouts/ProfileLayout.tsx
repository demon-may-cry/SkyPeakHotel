import { Outlet } from "react-router-dom";
import ProfileSidebar from "../components/profile/ProfileSidebar";

export default function ProfileLayout() {

    return (
        <div className="max-w-7xl mx-auto px-6 pt-32 pb-16">

            <div className="grid lg:grid-cols-[280px_1fr] gap-8">

                <ProfileSidebar />

                <main
                    className="
                        rounded-3xl
                        border
                        border-zinc-800
                        bg-zinc-900/60
                        backdrop-blur-xl
                        p-8
                        min-h-[700px]
                    "
                >
                    <Outlet />
                </main>

            </div>

        </div>
    );
}