<script setup lang="ts">
import { ref } from 'vue';
// 直接使用原有的 service 方法
import { getStatisticsReport } from '@/services/adminService'; 
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { toast } from 'vue-sonner';

const report = ref<any>(null);
const isLoading = ref(false);
const selectedDate = ref({ year: new Date().getFullYear(), month: new Date().getMonth() + 1 });

const fetchReport = async () => {
  isLoading.value = true;
  report.value = null;
  try {
    // 调用未改变的 service 方法
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

    <div v-if="isLoading" class="text-center">加载中...</div>

    <div v-if="report" class="space-y-8">
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

      <div class="grid gap-8 md:grid-cols-2">
        <Card>
          <CardHeader><CardTitle>各会议室使用情况</CardTitle></CardHeader>
          <CardContent>
            <Table>
              <TableHeader><TableRow><TableHead>会议室</TableHead><TableHead>预订次数</TableHead><TableHead>总时长 (小时)</TableHead></TableRow></TableHeader>
              <TableBody>
                <TableRow v-for="item in report.usageByRoom" :key="item.roomName">
                  <TableCell>{{ item.roomName }}</TableCell>
                  <TableCell>{{ item.bookingCount }}</TableCell>
                  <TableCell>{{ item.totalHours.toFixed(2) }}</TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </CardContent>
        </Card>
        <Card>
          <CardHeader><CardTitle>用户活跃度排名</CardTitle></CardHeader>
          <CardContent>
            <Table>
              <TableHeader><TableRow><TableHead>用户</TableHead><TableHead>预订次数</TableHead></TableRow></TableHeader>
              <TableBody>
                <TableRow v-for="item in report.userActivity" :key="item.userName">
                  <TableCell>{{ item.userName }}</TableCell>
                  <TableCell>{{ item.bookingCount }}</TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </div>
    </div>
  </div>
</template>