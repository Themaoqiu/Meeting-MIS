import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import apiClient from '@/services/api';

// 定义用户数据类型
interface User {
  userId: number;
  name: string;
  email: string;
  phone: string;
  roles: string[];
}

export const useAuthStore = defineStore('auth', () => {
  const router = useRouter();
  const user = ref<User | null>(null);

  const isLoggedIn = computed(() => !!user.value);

  // 登录操作
  async function login(credentials: any) {
    await apiClient.post('/auth/login', credentials);
    // 登录成功后，获取用户状态
    await checkAuthStatus();
    router.push('/dashboard');
  }

  // 注册操作
  async function signup(userInfo: any) {
    await apiClient.post('/auth/signup', userInfo);
    // 注册成功后可以跳转到登录页
    router.push('/login');
  }

  // 登出操作
  async function logout() {
    await apiClient.post('/auth/logout');
    user.value = null;
    router.push('/login');
  }

  // 检查登录状态 (非常重要)
  async function checkAuthStatus() {
    try {
      const response = await apiClient.get<User>('/auth/status');
      user.value = response.data;
    } catch (error) {
      user.value = null;
    }
  }

  return { user, isLoggedIn, login, signup, logout, checkAuthStatus };
});