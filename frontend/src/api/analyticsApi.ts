import api from "./axios";

const VISIT_KEY = "skypeak_visit_registered";

let visitRequestInProgress = false;

export const registerVisit = async (): Promise<void> => {

    const alreadyRegistered =
        sessionStorage.getItem(VISIT_KEY);

    if (alreadyRegistered || visitRequestInProgress) {
        return;
    }

    visitRequestInProgress = true;

    const timezone =
        Intl.DateTimeFormat()
            .resolvedOptions()
            .timeZone;

    try {

        await api.post(
            "/analytics/visit",
            {
                timezone
            }
        );

        sessionStorage.setItem(
            VISIT_KEY,
            "true"
        );

    } catch (error) {

        console.error(
            "Ошибка регистрации посещения:",
            error
        );

    } finally {

        visitRequestInProgress = false;
    }
};