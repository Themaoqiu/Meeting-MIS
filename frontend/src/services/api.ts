import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api', // 您的后端 API 地址
  withCredentials: true // 关键！允许跨域请求携带 Cookie (用于 Session 认证)
});

export default apiClient;