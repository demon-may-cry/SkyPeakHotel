import api from "./axios";

const VISIT_KEY = "skypeak_visit_registered";

export const registerVisit = async (): Promise<void> => {

    const alreadyRegistered =
        sessionStorage.getItem(VISIT_KEY);

    if (alreadyRegistered) {
        return;
    }

    const timezone =
        Intl.DateTimeFormat()
            .resolvedOptions()
            .timeZone;

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
};