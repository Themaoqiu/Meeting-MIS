<script setup lang="ts">
import Sidebar from '@/components/Sidebar.vue';
import { useAuthStore } from '@/stores/auth';
import { Button } from '@/components/ui/button';
import { LogOut } from 'lucide-vue-next';
import { toast } from 'vue-sonner';

// 获取认证状态 store
const authStore = useAuthStore();

// 定义退出登录的处理函数
const handleLogout = async () => {
  try {
    await authStore.logout();
    toast.success('您已成功退出登录');
  } catch (error) {
    toast.error('退出登录失败，请稍后重试。');
  }
};
</script>

<template>
  <div class="flex h-screen bg-secondary/30">
    <Sidebar />

    <div class="flex-1 flex flex-col overflow-hidden">
      
      <header class="flex items-center justify-between p-2 pl-6 border-b bg-background">
        <div>
          <span class="text-lg font-semibold">欢迎, {{ authStore.user?.name }}!</span>
        </div>
        
        <div class="flex items-center">
          <Button variant="ghost" @click="handleLogout">
            <LogOut class="mr-2 h-4 w-4" />
            退出登录
          </Button>
        </div>
      </header>

      <main class="flex-1 p-6 overflow-auto">
        <router-view v-slot="{ Component, route }">
          <transition
            enter-active-class="transition-opacity duration-300 ease-out"
            enter-from-class="opacity-0"
            enter-to-class="opacity-100"
            leave-active-class="transition-opacity duration-200 ease-in"
            leave-from-class="opacity-100"
            leave-to-class="opacity-0"
            mode="out-in"
          >
            <keep-alive>
              <component :is="Component" :key="route.path" />
            </keep-alive>
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<style scoped>
/* 动画效果保持不变 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>