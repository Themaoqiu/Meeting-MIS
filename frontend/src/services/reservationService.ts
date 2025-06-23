import apiClient from './api';

// 获取今日所有预约
export const getTodaysReservations = () => {
  return apiClient.get('/reservations/today');
};

// 获取我自己的所有预约
export const getMyReservations = () => {
  return apiClient.get('/reservations/my');
};

// 创建新预约
export const createReservation = (data: any) => {
  return apiClient.post('/reservations', data);
};

// 取消预约
export const cancelReservation = (id: number) => {
  return apiClient.delete(`/reservations/${id}`);
};

// 根据时段获取预约列表
export const getReservationsByRange = (start: String, end: String, roomId?: number) => {
  return  apiClient.get('/reservations/range', {
    params: {start, end, roomId},
  });
};