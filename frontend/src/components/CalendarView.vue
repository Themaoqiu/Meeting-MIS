<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { getReservationsByRange } from '@/services/reservationService';
import { ChevronLeft, ChevronRight } from 'lucide-vue-next';
import { Button } from '@/components/ui/button';

const props = defineProps<{
  initialDate?: Date,
  viewMode: 'month' | 'day'
}>();

const currentDate = ref(props.initialDate || new Date());
const reservations = ref([]);
const isLoading = ref(false);

const daysOfWeek = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];

// 获取当前月份的所有日期
const monthDays = computed(() => {
  const year = currentDate.value.getFullYear();
  const month = currentDate.value.getMonth();
  const firstDayOfMonth = new Date(year, month, 1);
  const lastDayOfMonth = new Date(year, month + 1, 0);
  
  const days = [];
  // 补齐月初的空白
  for (let i = 0; i < firstDayOfMonth.getDay(); i++) {
    days.push(null);
  }
  // 添加本月所有日期
  for (let i = 1; i <= lastDayOfMonth.getDate(); i++) {
    days.push(new Date(year, month, i));
  }
  return days;
});

const dayReservations = computed(() => {
  if (props.viewMode !== 'day') return [];
  return reservations.value.filter(r => new Date(r.startTime).toDateString() === currentDate.value.toDateString());
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

// 获取数据
const fetchData = async () => {
    isLoading.value = true;
    const year = currentDate.value.getFullYear();
    const month = currentDate.value.getMonth();
    let start, end;
    if (props.viewMode === 'month') {
        start = new Date(year, month, 1);
        end = new Date(year, month + 1, 0, 23, 59, 59);
    } else { // day view
        start = new Date(currentDate.value.setHours(0, 0, 0, 0));
        end = new Date(currentDate.value.setHours(23, 59, 59, 999));
    }

    try {
        const response = await getReservationsByRange(start.toISOString(), end.toISOString());
        reservations.value = response.data;
    } catch {
        toast.error("获取预约数据失败");
    } finally {
        isLoading.value = false;
    }
}

// 监听日期变化并重新获取数据
watch(currentDate, fetchData, { immediate: true });

onMounted(fetchData);

const getReservationsForDay = (day) => {
    if (!day) return [];
    return reservations.value.filter(r => new Date(r.startTime).toDateString() === day.toDateString());
}
</script>

<template>
  <div class="p-4 border rounded-lg bg-card">
    <div class="flex items-center justify-between mb-4">
      <Button variant="outline" @click="changeDate(-1)"><ChevronLeft class="h-4 w-4"/></Button>
      <h2 class="text-xl font-bold">
        {{ currentDate.getFullYear() }}年 {{ currentDate.getMonth() + 1 }}月
        <span v-if="viewMode === 'day'">{{ currentDate.getDate() }}日</span>
      </h2>
      <Button variant="outline" @click="changeDate(1)"><ChevronRight class="h-4 w-4"/></Button>
    </div>

    <div v-if="viewMode === 'month'" class="grid grid-cols-7 gap-1">
      <div v-for="day in daysOfWeek" :key="day" class="font-bold text-center p-2 text-muted-foreground">{{ day }}</div>
      <div v-for="(day, index) in monthDays" :key="index"
           class="border rounded-md min-h-[120px] p-2 bg-background/50 flex flex-col"
           :class="{'opacity-50': !day}">
        <span v-if="day" class="font-semibold">{{ day.getDate() }}</span>
        <div v-if="day" class="mt-1 space-y-1 text-xs overflow-y-auto">
          <div v-for="res in getReservationsForDay(day)" :key="res.reservationId"
               class="bg-secondary p-1 rounded">
            <p class="font-bold truncate">{{ res.theme }}</p>
            <p class="text-muted-foreground">{{ res.roomName }}</p>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="viewMode === 'day'">
      <div v-if="isLoading" class="text-center">加载中...</div>
      <div v-else-if="dayReservations.length === 0" class="text-center text-muted-foreground py-8">本日无会议安排</div>
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