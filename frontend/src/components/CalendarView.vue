<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { getReservationsByRange } from '@/services/reservationService';
import { ChevronLeft, ChevronRight } from 'lucide-vue-next';
import { Button } from '@/components/ui/button';
import { toast } from 'vue-sonner';

// 使用 withDefaults 来设置默认值，让组件更健壮
const props = withDefaults(defineProps<{
  roomId?: number,
  viewMode?: 'month' | 'day' // viewMode 变为可选
}>(), {
  viewMode: 'month' // 如果不传入 viewMode，则默认显示“月视图”
});

const currentDate = ref(new Date());
const reservations = ref<any[]>([]);
const isLoading = ref(true);
const daysOfWeek = ['日', '一', '二', '三', '四', '五', '六'];

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

const dayReservations = computed(() => {
  return reservations.value
    .filter(r => new Date(r.startTime).toDateString() === currentDate.value.toDateString())
    .sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime());
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

const fetchData = async () => {
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
        const response = await getReservationsByRange(start.toISOString(), end.toISOString(), props.roomId);
        reservations.value = response.data;
    } catch {
        toast.error("获取会议室日程失败");
    } finally {
        isLoading.value = false;
    }
}

watch([currentDate, () => props.roomId, () => props.viewMode], fetchData, { immediate: true });

const getReservationsForDay = (day: Date | null) => {
    if (!day) return [];
    return reservations.value.filter(r => new Date(r.startTime).toDateString() === day.toDateString());
}
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
               class="bg-primary/10 p-1 rounded text-[10px] leading-tight">
            <p class="font-bold truncate">{{ res.theme }}</p>
            <p v-if="!roomId" class="text-muted-foreground truncate">{{ res.roomName }}</p>
            <p>{{ new Date(res.startTime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}) }}</p>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else-if="viewMode === 'day'">
      <div v-if="dayReservations.length === 0" class="text-center text-muted-foreground py-8">本日无会议安排</div>
      <div v-else class="space-y-2">
         <div v-for="res in dayReservations" :key="res.reservationId" class="border p-3 rounded-lg bg-background/50">
            <h3 class="font-semibold">{{ res.theme }}</h3>
            <p>{{ new Date(res.startTime).toLocaleTimeString('zh-CN') }} - {{ new Date(res.endTime).toLocaleTimeString('zh-CN') }}</p>
            <p class="text-sm text-muted-foreground">会议室: {{ res.roomName }} | 预约人: {{ res.userName }}</p>
         </div>
      </div>
    </div>
  </div>
</template>