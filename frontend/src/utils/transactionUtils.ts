export function getTransactionTitle(
    type: string,
    description: string
): string {

    if (type === "DEPOSIT") {
        return "💳 Пополнение баланса";
    }

    if (
        description.toLowerCase().includes("бронир")
    ) {
        return "🏨 Оплата бронирования";
    }

    if (
        description.toLowerCase().includes("возврат")
    ) {
        return "↩ Возврат средств";
    }

    return description;

}

export function formatTransactionDate(
    value: string
): string {

    return new Date(value).toLocaleString(
        "ru-RU",
        {
            day: "2-digit",
            month: "long",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        }
    );

}

export function formatMoney(
    amount: number
): string {

    return amount.toLocaleString(
        "ru-RU"
    ) + " ₽";

}