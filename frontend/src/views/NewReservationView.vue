<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { toast } from 'vue-sonner'
import { createReservation } from '@/services/reservationService'
import { getAllRooms } from '@/services/roomService'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

const router = useRouter()
const route = useRoute()

const rooms = ref([])
const reservation = ref({
  roomId: '',
  theme: '',
  personNum: 1,
  startTime: '',
  endTime: ''
})

onMounted(async () => {
  // 获取所有会议室以填充下拉列表
  try {
    const response = await getAllRooms()
    rooms.value = response.data
    // 如果URL参数中带了roomId，则自动选中
    if (route.query.roomId) {
      reservation.value.roomId = route.query.roomId
    }
  } catch (error) {
    toast.error('获取会议室列表失败')
    console.error('获取会议室列表失败')
  }
})

const handleSubmit = async () => {
  try {
    await createReservation({
      ...reservation.value,
      // 将字符串转换为数字和正确的日期时间格式
      roomId: Number(reservation.value.roomId),
      personNum: Number(reservation.value.personNum),
      startTime: new Date(reservation.value.startTime).toISOString(),
      endTime: new Date(reservation.value.endTime).toISOString()
    })
    toast.success('预约成功！')
    router.push('/reservations/my')
  } catch (error: any) {
    toast.error('预约失败', {
      description: error.response?.data || '请检查您的输入信息。'
    })
  }
}
</script>

<template>
  <Card class="max-w-2xl mx-auto">
    <CardHeader>
      <CardTitle>新建会议预约</CardTitle>
    </CardHeader>
    <CardContent>
      <form @submit.prevent="handleSubmit" class="space-y-6">
        <div class="space-y-2">
          <Label for="room">选择会议室</Label>
          <Select v-model="reservation.roomId">
            <SelectTrigger>
              <SelectValue placeholder="请选择一个会议室" />
            </SelectTrigger>
            <SelectContent>
              <SelectGroup>
                <SelectItem v-for="room in rooms" :key="room.roomId" :value="String(room.roomId)">
                  {{ room.name }} (容量: {{ room.capacity }}人)
                </SelectItem>
              </SelectGroup>
            </SelectContent>
          </Select>
        </div>
        <div class="space-y-2">
          <Label for="theme">会议主题</Label>
          <Input id="theme" v-model="reservation.theme" required />
        </div>
        <div class="space-y-2">
          <Label for="personNum">参会人数</Label>
          <Input id="personNum" v-model="reservation.personNum" type="number" min="1" required />
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="startTime">开始时间</Label>
            <Input id="startTime" v-model="reservation.startTime" type="datetime-local" required />
          </div>
          <div class="space-y-2">
            <Label for="endTime">结束时间</Label>
            <Input id="endTime" v-model="reservation.endTime" type="datetime-local" required />
          </div>
        </div>
        <Button type="submit" class="w-full">提交预约</Button>
      </form>
    </CardContent>
  </Card>
</template>