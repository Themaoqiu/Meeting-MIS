<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getTodaysReservations } from '@/services/reservationService'
import { Button } from '@/components/ui/button'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { RouterLink } from 'vue-router'

const todaysReservations = ref([])

onMounted(async () => {
  try {
    const response = await getTodaysReservations()
    todaysReservations.value = response.data
  } catch (error) {
    console.error('获取今日会议失败:', error)
  }
})

// 格式化时间
const formatTime = (datetime: string) => new Date(datetime).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
</script>

<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <h1 class="text-2xl font-bold">仪表盘</h1>
      <Button as-child>
        <RouterLink to="/reservations/new">快速预约会议</RouterLink>
      </Button>
    </div>

    <div>
      <h2 class="text-xl font-semibold mb-4">今日会议</h2>
      <div class="border rounded-md">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>主题</TableHead>
              <TableHead>会议室</TableHead>
              <TableHead>时间</TableHead>
              <TableHead>预约人</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-if="todaysReservations.length === 0">
              <TableCell colspan="4" class="text-center text-muted-foreground">今日暂无会议</TableCell>
            </TableRow>
            <TableRow v-for="reservation in todaysReservations" :key="reservation.reservationId">
              <TableCell>{{ reservation.theme }}</TableCell>
              <TableCell>{{ reservation.roomName }}</TableCell>
              <TableCell>{{ formatTime(reservation.startTime) }} - {{ formatTime(reservation.endTime) }}</TableCell>
              <TableCell>{{ reservation.userName }}</TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </div>
    </div>
  </div>
</template>