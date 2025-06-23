import apiClient from './api';

// 会议室管理员
// 获取所有会议室的可视化状态看板
export const getRoomStatusDashboard = () => {
  return apiClient.get('/admin/rooms/status');
};

// 获取指定年月的用量统计报告
export const getStatisticsReport = (year: number, month: number) => {
  return apiClient.get(`/admin/statistics/report?year=${year}&month=${month}`);
};

// 创建一个新的会议室
export const createRoom = (roomData: any) => {
  return apiClient.post('/admin/rooms', roomData);
};

// 更新一个已存在的会议室
export const updateRoom = (roomId: number, roomData: any) => {
  return apiClient.put(`/admin/rooms/${roomId}`, roomData);
};

export const deleteRoom = (roomId: number) => {
  return apiClient.delete(`/admin/rooms/${roomId}`);
}

// 系统管理员
// 获取系统中的所有用户列表
export const getAllUsers = () => {
    return apiClient.get('/admin/users');
};

// 更新用户的角色
export const updateUserRole = (userId: number, role: string) => {
    return apiClient.put(`/admin/users/${userId}/role`, JSON.stringify(role),{
      headers: {
        'Content-Type': 'application/json' // 明确告知后端这是JSON数据
      }
    });
};

// 删除一个用户
export const deleteUser = (userId: number) => {
    return apiClient.delete(`/admin/users/${userId}`);
};