import axios from "axios";

const api = axios.create({

    baseURL: "/api/v1",
});

api.interceptors.request.use(

    (config) => {

        const token =
            localStorage.getItem("token");

        if (
            token &&
            token !== "undefined" &&
            token !== "null"
        ) {

            config.headers.Authorization =
                `Bearer ${token}`;
        }

        return config;
    },

    (error) => {

        return Promise.reject(error);
    }
);

export default api;