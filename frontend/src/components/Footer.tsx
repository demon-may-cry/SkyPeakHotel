import { Mail, Send, Code2 } from "lucide-react";

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
                        flex-wrap
                        gap-5
                        text-gray-400
                    "
                >
                    <a
                        href="mailto:demonmaycry@mail.ru"
                        className="
                            flex
                            items-center
                            gap-2
                            transition-colors
                            hover:text-white
                        "
                    >
                        <Mail size={17} />
                        <span>Email</span>
                    </a>

                    <a
                        href="https://t.me/DmitryEltsov"
                        target="_blank"
                        rel="noopener noreferrer"
                        className="
                            flex
                            items-center
                            gap-2
                            transition-colors
                            hover:text-white
                        "
                    >
                        <Send size={17} />
                        <span>Telegram</span>
                    </a>

                    <a
                        href="https://github.com/demon-may-cry"
                        target="_blank"
                        rel="noopener noreferrer"
                        className="
                            flex
                            items-center
                            gap-2
                            transition-colors
                            hover:text-white
                        "
                    >
                        <Code2 size={17} />
                        <span>GitHub</span>
                    </a>
                </div>
            </div>
        </footer>
    );
}