import apiClient from './api';

// 获取今日所有预约
export const getTodaysReservations = () => {
  return apiClient.get('/api/reservations/today');
};

// 获取我自己的所有预约
export const getMyReservations = () => {
  return apiClient.get('/api/reservations/my');
};

// 创建一个新的预约
export const createReservation = (data: any) => {
  return apiClient.post('/api/reservations', data);
};

// 取消一个预约
export const cancelReservation = (id: number) => {
  return apiClient.delete(`/api/reservations/${id}`);
};