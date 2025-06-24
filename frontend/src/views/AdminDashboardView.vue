<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { getRoomStatusDashboard } from '@/services/adminService';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Circle } from 'lucide-vue-next';
import { toast } from 'vue-sonner';
import CalendarView from '@/components/CalendarView.vue'; 
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

interface RoomStatus {
  roomId: number;
  name: string;
  status: 'AVAILABLE' | 'OCCUPIED' | 'MAINTENANCE';
  capacity: number;
  currentBookingTheme?: string;
}

const rooms = ref<RoomStatus[]>([]);
const isLoading = ref(true);
const viewMode = ref<'rooms' | 'month' | 'day'>('rooms');

onMounted(async () => {
  if (!authStore.isLoggedIn) return
  try {
    const response = await getRoomStatusDashboard();
    rooms.value = response.data;
  } catch (error) {
    toast.error('获取会议室状态失败');
  } finally {
    isLoading.value = false;
  }
});
const getStatusColor = (status: 'AVAILABLE' | 'OCCUPIED' | 'MAINTENANCE') => {
  const statusColors = {
    'AVAILABLE': 'text-green-500',
    'OCCUPIED': 'text-red-500',
    'MAINTENANCE': 'text-yellow-500'
  };
  return statusColors[status] || 'text-gray-400';
};
</script>

<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold">会议室状态看板</h1>
      <Select v-model="viewMode">
        <SelectTrigger class="w-[180px]">
          <SelectValue placeholder="切换视图" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="rooms">会议室视图</SelectItem>
          <SelectItem value="month">月视图</SelectItem>
          <SelectItem value="day">日视图</SelectItem>
        </SelectContent>
      </Select>
    </div>

    <div v-if="viewMode === 'rooms'">
      <div v-if="isLoading" class="text-center">加载中...</div>
      <div v-else class="grid gap-6 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        <Card v-for="room in rooms" :key="room.roomId">
          <CardHeader>
            <CardTitle class="flex items-center justify-between">
              <span>{{ room.name }}</span>
              <Circle :class="getStatusColor(room.status)" fill="currentColor" class="h-4 w-4" />
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p class="font-semibold">{{ room.status }}</p>
            <p v-if="room.status === 'OCCUPIED'" class="text-sm text-muted-foreground">主题: {{ room.currentBookingTheme }}</p>
            <p v-else class="text-sm text-muted-foreground">容量: {{ room.capacity }} 人</p>
          </CardContent>
        </Card>
      </div>
    </div>

    <CalendarView v-else :key="viewMode" :view-mode="viewMode" />
  </div>
</template>