import api from "./axios";
import type { User } from "../types/user";

import type {
    UpdateUserProfileRequest
} from "../types/updateUserProfileRequest";

export async function getCurrentUser(): Promise<User> {
    const response = await api.get("/users/me");
    return response.data;
}

export const updateCurrentUser = async (
    request: UpdateUserProfileRequest
) => {

    await api.put(
        "/users/me",
        request
    );
};