<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { getMyReservations, cancelReservation } from '@/services/reservationService'
import { toast } from 'vue-sonner'
import { Button } from '@/components/ui/button'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger } from '@/components/ui/alert-dialog'
import { useAuthStore } from '@/stores/auth'
const authStore = useAuthStore()

export interface Reservation {
  reservationId: number
  theme: string
  roomName: string
  startTime: string
  endTime: string
  status: 'CONFIRMED' | 'CANCELLED' | 'PENDING' | 'COMPLETED'
}

const myReservations = ref<Reservation[]>([])

const fetchReservations = async () => {
  if (!authStore.isLoggedIn) return
  try {
    const response = await getMyReservations()
    myReservations.value = response.data
  } catch (error) {
    toast.error('获取预约列表失败')
  }
}

onMounted(fetchReservations)

const handleCancel = async (id: number) => {
  try {
    await cancelReservation(id)
    toast.success('预约已取消')
    window.location.reload()
  } catch (error: any) {
    toast.error('取消失败', {description: error.response?.data})
  }
}

// 格式化日期和时间
const formatDateTime = (datetime: string) => new Date(datetime).toLocaleString('zh-CN')
const sortedReservations = computed(() => {
  return [...myReservations.value].sort((a, b) => {
    // 未取消的排前面，已取消的排后面
    if (a.status !== 'CANCELED' && b.status === 'CANCELED') return -1;
    if (a.status === 'CANCELED' && b.status !== 'CANCELED') return 1;
    // 其他按开始时间排序
    return new Date(a.startTime).getTime() - new Date(b.startTime).getTime();
  });
});

// 判断会议是否已开始
const canCancel = (r: Reservation) => {
  return r.status === 'CONFIRMED' && new Date() < new Date(r.startTime);
};
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold mb-6">我的预约</h1>
    <div class="border rounded-md">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead class="text-left pl-6">主题</TableHead>
            <TableHead>会议室</TableHead>
            <TableHead>开始时间</TableHead>
            <TableHead>结束时间</TableHead>
            <TableHead>状态</TableHead>
            <TableHead class="text-center">操作</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <TableRow v-if="myReservations.length === 0">
            <TableCell colspan="6" class="text-center text-muted-foreground">您还没有任何预约</TableCell>
          </TableRow>
          <TableRow v-for="r in sortedReservations" :key="r.reservationId">
            <TableCell class="pl-6">{{ r.theme }}</TableCell>
            <TableCell>{{ r.roomName }}</TableCell>
            <TableCell>{{ formatDateTime(r.startTime) }}</TableCell>
            <TableCell>{{ formatDateTime(r.endTime) }}</TableCell>
            <TableCell>{{ r.status }}</TableCell>
            <TableCell class="text-center">
              <AlertDialog v-if="canCancel(r)">
                <AlertDialogTrigger as-child>
                  <Button variant="destructive" size="sm">取消预约</Button>
                </AlertDialogTrigger>
                <AlertDialogContent>
                  <AlertDialogHeader>
                    <AlertDialogTitle>确认取消吗？</AlertDialogTitle>
                    <AlertDialogDescription>
                      此操作无法撤销。您确定要取消关于【{{ r.theme }}】的会议预约吗？
                    </AlertDialogDescription>
                  </AlertDialogHeader>
                  <AlertDialogFooter>
                    <AlertDialogCancel>返回</AlertDialogCancel>
                    <AlertDialogAction @click="handleCancel(r.reservationId)">确认取消</AlertDialogAction>
                  </AlertDialogFooter>
                </AlertDialogContent>
              </AlertDialog>
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>
  </div>
</template>