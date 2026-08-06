export default function Footer() {
    return (
        <footer
            className="
                border-t
                border-white/10
                bg-black
                px-6
                py-10
            "
        >

            <div
                className="
                    mx-auto
                    flex
                    max-w-7xl
                    flex-col
                    gap-6
                    md:flex-row
                    md:items-center
                    md:justify-between
                "
            >

                <div>

                    <h3
                        className="
                            text-2xl
                            font-bold
                            text-white
                        "
                    >
                        SkyPeak Hotel
                    </h3>

                    <p className="mt-2 text-gray-400">
                        Роскошный отдых в сердце гор
                    </p>

                </div>

                <div
                    className="
                        flex
                        gap-6
                        text-gray-400
                    "
                >

                    <span>Email</span>
                    <span>Telegram</span>
                    <span>GitHub</span>

                </div>

            </div>

        </footer>
    );
}