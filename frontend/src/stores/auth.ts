import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import apiClient from '@/services/api';
import router from '@/router';

// 用户数据类型
interface User {
  userId: number;
  name: string;
  email: string;
  phone: string;
  roles: Set<string>;
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as User | null,
    isLoggedIn: false,
  }),
  getters: {
    // 提供一个getter，方便在组件中检查用户是否拥有特定角色
    hasRole: (state) => (role: string): boolean => {
      return state.user?.roles.has(role) ?? false;
    },
  },
  actions: {
    // 登录时，后端会返回包含角色信息的用户对象
    async login(credentials: any) {
      const response = await apiClient.post('/auth/login', credentials);
      const userData = response.data;
      this.user = {
        ...userData,
        roles: new Set(userData.roles), // 将后端返回的角色数组转换为Set
      };
      this.isLoggedIn = true;
      router.push('/dashboard');
    },
    async  signup(userInfo: any) {
      await apiClient.post('/auth/signup', userInfo);
      // 注册成功后可以跳转到登录页
      router.push('/login');
    },
    async logout() {
      await apiClient.post('/auth/logout');
      this.user = null;
      this.isLoggedIn = false;
      router.push('/login');
    },
    async checkAuthStatus() {
        try {
            const response = await apiClient.get('/auth/status');
            const userData = response.data;
            this.user = {
                ...userData,
                roles: new Set(userData.roles),
            };
            this.isLoggedIn = true;
        } catch (error) {
            this.user = null;
            this.isLoggedIn = false;
        }
    }
  },
});