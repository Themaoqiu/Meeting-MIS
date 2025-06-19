<script setup>
import { useAuthStore } from '@/stores/auth';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';

const authStore = useAuthStore();
</script>

<template>
  <div class="p-4 md:p-8">
    <Card v-if="authStore.isLoggedIn">
      <CardHeader>
        <div class="flex justify-between items-center">
          <CardTitle>欢迎回来, {{ authStore.user.name }}!</CardTitle>
          <Button @click="authStore.logout()" variant="destructive">
            安全退出
          </Button>
        </div>
        <CardDescription>
          这是您的会议室预定系统主面板。
        </CardDescription>
      </CardHeader>
      <CardContent>
        <p><strong>用户ID:</strong> {{ authStore.user.userId }}</p>
        <p><strong>邮箱:</strong> {{ authStore.user.email }}</p>
        <p><strong>电话:</strong> {{ authStore.user.phone }}</p>
        <p><strong>角色:</strong> {{ authStore.user.roles.join(', ') }}</p>
      </CardContent>
    </Card>
    <div v-else>
      <p>正在加载用户信息...</p>
    </div>
  </div>
</template>