import api from "./axios";

export const getRoomTypes = async () => {

    const response =
        await api.get("/room-types");

    return response.data;
};

export const getRoomTypeBySlug = async (
    slug: string
) => {

    const response =
        await api.get(
            `/room-types/${slug}`
        );

    return response.data;
};