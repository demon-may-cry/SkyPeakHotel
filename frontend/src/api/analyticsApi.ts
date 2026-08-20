import api from "./axios";

const VISIT_KEY = "skypeak_visit_registered";

export const registerVisit = async (): Promise<void> => {

    const alreadyRegistered =
        sessionStorage.getItem(VISIT_KEY);

    if (alreadyRegistered) {
        return;
    }

    await api.post("/analytics/visit");

    sessionStorage.setItem(VISIT_KEY, "true");
};