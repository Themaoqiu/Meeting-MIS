<script setup lang="ts">
import { ref } from 'vue';
import { getStatisticsReport } from '@/services/adminService';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { toast } from 'vue-sonner';

const report = ref(null);
const isLoading = ref(false);
const selectedDate = ref({ year: new Date().getFullYear(), month: new Date().getMonth() + 1 });

const fetchReport = async () => {
  isLoading.value = true;
  report.value = null;
  try {
    const response = await getStatisticsReport(selectedDate.value.year, selectedDate.value.month);
    report.value = response.data;
  } catch (error) {
    toast.error('获取统计报告失败');
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold mb-6">使用统计</h1>
    <div class="flex items-center space-x-4 mb-6 p-4 border rounded-lg">
       <input v-model.number="selectedDate.year" type="number" class="p-2 border rounded" />
      <input v-model.number="selectedDate.month" type="number" min="1" max="12" class="p-2 border rounded" />
      <Button @click="fetchReport" :disabled="isLoading">
        {{ isLoading ? '生成中...' : '生成报告' }}
      </Button>
    </div>

    <div v-if="report">
      <div class="grid gap-6 md:grid-cols-3">
        <Card>
          <CardHeader><CardTitle>总预订次数</CardTitle></CardHeader>
          <CardContent><p class="text-2xl font-bold">{{ report.totalBookings }}</p></CardContent>
        </Card>
        <Card>
          <CardHeader><CardTitle>总预订时长 (小时)</CardTitle></CardHeader>
          <CardContent><p class="text-2xl font-bold">{{ report.totalHoursBooked.toFixed(2) }}</p></CardContent>
        </Card>
        <Card>
          <CardHeader><CardTitle>取消率</CardTitle></CardHeader>
          <CardContent><p class="text-2xl font-bold">{{ (report.cancellationRate * 100).toFixed(2) }}%</p></CardContent>
        </Card>
      </div>
    </div>
  </div>
</template>