<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Home, Calendar, UserCheck } from 'lucide-vue-next'
import apiClient from '@/services/api'
import { toast } from 'vue-sonner';
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const stats = ref({
  totalRooms: 0,
  todaysMeetings: 0,
  myUpcomingMeetings: 0,
});

const isLoading = ref(true);

onMounted(async () => {
  await authStore.checkAuthStatus()
  if (!authStore.isLoggedIn) {
    isLoading.value = false
    return
  }
  try {
    const response = await apiClient.get('/dashboard/stats');
    stats.value = response.data;
  } catch (error: any) {
    toast.error('获取仪表盘数据失败', {
      description: error.response?.data || 'Network Error: 无法连接到服务器。',
    });
  } finally {
    isLoading.value = false;
  }
});
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold mb-6">总览仪表盘</h1>
    <div v-if="isLoading" class="text-center">加载中...</div>
    <div v-else class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">
            会议室总数
          </CardTitle>
          <Home class="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ stats.totalRooms }}</div>
          <p class="text-xs text-muted-foreground">
            系统中所有可用的会议室种类
          </p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">
            今日会议总数
          </CardTitle>
          <Calendar class="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ stats.todaysMeetings }}</div>
          <p class="text-xs text-muted-foreground">
            今天所有已确认的会议安排
          </p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">
            我未来的预约
          </CardTitle>
          <UserCheck class="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ stats.myUpcomingMeetings }}</div>
          <p class="text-xs text-muted-foreground">
            您所有尚未结束的会议预约
          </p>
        </CardContent>
      </Card>
    </div>

    <div class="mt-8">
      </div>
  </div>
</template>