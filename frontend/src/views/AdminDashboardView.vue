<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { getRoomStatusDashboard } from '@/services/adminService';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Circle } from 'lucide-vue-next';
import { toast } from 'vue-sonner';
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

interface RoomStatus {
  roomId: number;
  name: string;
  status: 'AVAILABLE' | 'OCCUPIED' | 'MAINTENANCE';
  capacity: number;
  currentBookingTheme?: string; // 可选属性
}

const rooms = ref<RoomStatus[]>([]);
const isLoading = ref(true);

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

const getStatusColor = (status) => {
  switch (status) {
    case 'AVAILABLE': return 'text-green-500';
    case 'OCCUPIED': return 'text-red-500';
    case 'MAINTENANCE': return 'text-yellow-500';
    default: return 'text-gray-400';
  }
};
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold mb-6">会议室状态看板</h1>
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
          <p v-if="room.status === 'OCCUPIED'" class="text-sm text-muted-foreground">
            主题: {{ room.currentBookingTheme }}
          </p>
          <p v-else class="text-sm text-muted-foreground">
            容量: {{ room.capacity }} 人
          </p>
        </CardContent>
      </Card>
    </div>
  </div>
</template>