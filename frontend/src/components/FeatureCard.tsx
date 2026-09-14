type FeatureCardProps = {
    title: string;
    description: string;
};

export default function FeatureCard({
                                        title,
                                        description,
                                    }: FeatureCardProps) {
    return (
        <div
            className="
                rounded-3xl
                border
                border-white/10
                bg-white/5
                p-8
                backdrop-blur-md
                transition
                duration-300
                hover:-translate-y-2
                hover:bg-white/10
                hover:shadow-2xl
            "
        >

            <h3
                className="
                    text-2xl
                    font-semibold
                    text-white
                "
            >
                {title}
            </h3>

            <p
                className="
                    mt-4
                    leading-relaxed
                    text-gray-300
                "
            >
                {description}
            </p>

        </div>
    );
}