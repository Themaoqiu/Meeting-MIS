import apiClient from "./api";

export const getAllRooms = () => {
    return apiClient.get('/rooms');
};