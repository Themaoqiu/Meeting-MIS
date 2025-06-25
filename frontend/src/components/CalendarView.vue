<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { getReservationsByRange } from '@/services/reservationService';
import { ChevronLeft, ChevronRight } from 'lucide-vue-next';
import { Button } from '@/components/ui/button';
import { toast } from 'vue-sonner';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const props = withDefaults(defineProps<{
  roomId?: number,
  viewMode?: 'month' | 'day' 
  onlyMine?: boolean
}>(), {
  viewMode: 'month' // 如果不传入 viewMode，则默认显示月视图
});

const currentDate = ref(new Date());
const reservations = ref<any[]>([]);
const isLoading = ref(true);
const daysOfWeek = ['日', '一', '二', '三', '四', '五', '六'];
const router = useRouter();

const monthDays = computed(() => {
  const year = currentDate.value.getFullYear();
  const month = currentDate.value.getMonth();
  const firstDayOfMonth = new Date(year, month, 1);
  const lastDayOfMonth = new Date(year, month + 1, 0);
  const days: (Date | null)[] = [];
  for (let i = 0; i < firstDayOfMonth.getDay(); i++) {
    days.push(null);
  }
  for (let i = 1; i <= lastDayOfMonth.getDate(); i++) {
    days.push(new Date(year, month, i));
  }
  return days;
});

const getReservationsForDay = (day: Date | null) => {
  if (!day) return [];
  return reservations.value
    .filter(r => {
      const d1 = new Date(r.startTime);
      return d1.getFullYear() === day.getFullYear() &&
             d1.getMonth() === day.getMonth() &&
             d1.getDate() === day.getDate();
    })
    .sort((a, b) => {
      // 未取消的排前面，已取消的排后面
      if (a.status !== 'CANCELED' && b.status === 'CANCELED') return -1;
      if (a.status === 'CANCELED' && b.status !== 'CANCELED') return 1;
      // 其他按开始时间排序
      return new Date(a.startTime).getTime() - new Date(b.startTime).getTime();
    });
}

const dayReservations = computed(() => {
  return reservations.value
    .filter(r => {
      const d1 = new Date(r.startTime);
      const d2 = currentDate.value;
      return d1.getFullYear() === d2.getFullYear() &&
             d1.getMonth() === d2.getMonth() &&
             d1.getDate() === d2.getDate();
    })
    .sort((a, b) => {
      // 未取消的排前面，已取消的排后面
      if (a.status !== 'CANCELED' && b.status === 'CANCELED') return -1;
      if (a.status === 'CANCELED' && b.status !== 'CANCELED') return 1;
      // 未完成的排前面，已完成的排后面
      if (!isCompleted(a) && isCompleted(b)) return -1;
      if (isCompleted(a) && !isCompleted(b)) return 1;
      // 其他按开始时间排序
      return new Date(a.startTime).getTime() - new Date(b.startTime).getTime();
    });
});

const changeDate = (amount: number) => {
  const newDate = new Date(currentDate.value);
  if (props.viewMode === 'month') {
    newDate.setMonth(newDate.getMonth() + amount);
  } else {
    newDate.setDate(newDate.getDate() + amount);
  }
  currentDate.value = newDate;
};

function formatLocalDateTime(date: Date) {
  return date.getFullYear() + '-' +
    String(date.getMonth() + 1).padStart(2, '0') + '-' +
    String(date.getDate()).padStart(2, '0') + 'T' +
    String(date.getHours()).padStart(2, '0') + ':' +
    String(date.getMinutes()).padStart(2, '0') + ':' +
    String(date.getSeconds()).padStart(2, '0');
}

const fetchData = async () => {
    if (!authStore.isLoggedIn) return
    isLoading.value = true;
    reservations.value = [];
    let start, end;
    if (props.viewMode === 'month') {
        const year = currentDate.value.getFullYear();
        const month = currentDate.value.getMonth();
        start = new Date(year, month, 1);
        end = new Date(year, month + 1, 0, 23, 59, 59);
    } else {
        start = new Date(currentDate.value);
        start.setHours(0,0,0,0);
        end = new Date(currentDate.value);
        end.setHours(23,59,59,999);
    }
    try {
      let response;
        if (props.onlyMine) {
          // 获取当前用户的预约，前端过滤时间范围
          response = await import('@/services/reservationService').then(m => m.getMyReservations());
          reservations.value = response.data.filter((r: any) => {
            const st = new Date(r.startTime).getTime();
            const et = new Date(r.endTime).getTime();
            return st <= end.getTime() && et >= start.getTime();
          });
        } else {
          response = await getReservationsByRange(formatLocalDateTime(start), formatLocalDateTime(end), props.roomId);
        reservations.value = response.data;
        }
    } catch {
        toast.error("获取会议室日程失败");
    } finally {
        isLoading.value = false;
    }
}

watch([currentDate, () => props.roomId, () => props.viewMode], fetchData, { immediate: true });

// 检查预约是否已完成（已过当前时间）
const isCompleted = (reservation: any) => {
  const now = new Date();
  return new Date(reservation.endTime) < now;
};
</script>

<template>
  <div class="p-4 border rounded-lg bg-card">
    <div class="flex items-center justify-between mb-4">
      <Button variant="outline" size="icon" @click="changeDate(-1)"><ChevronLeft class="h-4 w-4"/></Button>
      <h2 class="text-xl font-bold text-center">
        {{ currentDate.getFullYear() }}年 {{ currentDate.getMonth() + 1 }}月
        <span v-if="viewMode === 'day'">{{ currentDate.getDate() }}日</span>
      </h2>
      <Button variant="outline" size="icon" @click="changeDate(1)"><ChevronRight class="h-4 w-4"/></Button>
    </div>
    
    <div v-if="isLoading" class="text-center py-10 text-muted-foreground">日程加载中...</div>
    
    <div v-else-if="viewMode === 'month'" class="grid grid-cols-7 gap-1">
      <div v-for="day in daysOfWeek" :key="day" class="font-bold text-center p-2 text-sm text-muted-foreground">{{ day }}</div>
      <div v-for="(day, index) in monthDays" :key="index"
           class="border rounded-md min-h-[100px] p-2 flex flex-col"
           :class="[!day ? 'bg-muted/30' : 'bg-background', day && new Date(day).toDateString() === new Date().toDateString() ? 'border-primary' : '']">
        <span v-if="day" class="font-semibold text-sm">{{ day.getDate() }}</span>
        <div v-if="day" class="mt-1 space-y-1 text-xs overflow-y-auto">
          <div v-for="res in getReservationsForDay(day)" :key="res.reservationId"
              :class="[
                'p-1 rounded text-[10px] leading-tight',
                res.status === 'CANCELED' ? 'bg-gray-200 text-gray-500' :
                isCompleted(res) ? 'bg-orange-100 text-orange-600' : 'bg-primary/10'
              ]">
            <p class="font-bold truncate">
              {{ res.theme }}
              <span v-if="res.status === 'CANCELED' && onlyMine">（已取消）</span>
              <span v-else-if="res.status === 'CANCELED'">（已取消）</span>
              <span v-else-if="isCompleted(res)">（√）</span>
            </p>
            <p v-if="!roomId" class="text-muted-foreground truncate">{{ res.roomName }}</p>
            <p>{{ new Date(res.startTime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}) }} - {{ new Date(res.endTime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}) }}</p>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else-if="viewMode === 'day'">
      <div v-if="dayReservations.length === 0" class="text-center text-muted-foreground py-8">本日无会议安排</div>
      <div v-else class="space-y-2">
        <div v-for="res in dayReservations" :key="res.reservationId"
          :class="[
            'border p-3 rounded-lg',
            res.status === 'CANCELED' ? 'bg-gray-200 text-gray-500' :
            isCompleted(res) ? 'bg-orange-100 text-orange-600' : 'bg-primary/10 text-primary'
          ]">
            <h3 class="font-semibold">
              {{ res.theme }}
              <span v-if="res.status === 'CANCELED'">（已取消）</span>
              <span v-else-if="isCompleted(res)">（√）</span>
            </h3>
            <p>{{ new Date(res.startTime).toLocaleTimeString('zh-CN') }} - {{ new Date(res.endTime).toLocaleTimeString('zh-CN') }}</p>
            <p class="text-sm text-muted-foreground">会议室: {{ res.roomName }} | 预约人: {{ res.userName }}</p>
         </div>
      </div>
    </div>
  </div>
</template>