<script setup lang="ts">
import { Toaster } from '@/components/ui/sonner'
import { Button } from '@/components/ui/button'
import { useAuthStore } from '@/stores/auth'
import { RouterLink } from 'vue-router'

const authStore = useAuthStore()
</script>

<template>
  <header v-if="authStore.isLoggedIn" class="p-4 border-b bg-background">
    <nav class="container mx-auto flex justify-between items-center">
      <div class="flex items-center gap-4">
        <RouterLink to="/dashboard" class="font-bold">仪表盘</RouterLink>
        <RouterLink to="/rooms" class="text-muted-foreground hover:text-foreground">会议室列表</RouterLink>
        <RouterLink to="/reservations/my" class="text-muted-foreground hover:text-foreground">我的预约</RouterLink>
      </div>
      <div class="flex items-center gap-4">
        <span class="text-sm text-muted-foreground">欢迎, {{ authStore.user?.name }}</span>
        <Button @click="authStore.logout()" variant="outline">退出登录</Button>
      </div>
    </nav>
  </header>

  <main class="container mx-auto p-4 md:p-8">
    <RouterView />
  </main>
  <Toaster position="top-right" rich-colors />
</template>