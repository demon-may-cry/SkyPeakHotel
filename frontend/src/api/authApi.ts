import api from "./axios";

export interface LoginRequest {

    email: string;

    password: string;
}

export interface RegisterRequest {

    email: string;

    password: string;

    firstName: string;

    lastName: string;

    phoneNumber: string;
}

export interface LoginResponse {

    accessToken: string;

    tokenType: string;

    email: string;

    firstName: string;

    role: string;
}

export const login = async (
    request: LoginRequest
): Promise<LoginResponse> => {

    const response =
        await api.post<LoginResponse>(
            "/auth/login",
            request
        );

    return response.data;
};

export const register = async (
    request: RegisterRequest
) => {

    const response =
        await api.post(
            "/auth/register",
            request
        );

    return response.data;
};