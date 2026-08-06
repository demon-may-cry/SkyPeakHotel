export type UserRole = "USER" | "MANAGER" | "ADMIN";

export type UserStatus = "ACTIVE" | "BLOCKED";

export interface User {

    id: string;

    email: string;

    phoneNumber: string;

    status: UserStatus;

    role: UserRole;

    firstName: string;

    lastName: string;

    middleName: string | null;

    birthDate: string | null;

    avatarUrl: string | null;

    createdAt: string;

    lastLoginAt: string | null;

}