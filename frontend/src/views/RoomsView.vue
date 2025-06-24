<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getAllRooms } from '@/services/roomService'
import RoomCard from '@/components/RoomCard.vue'
import { toast } from 'vue-sonner'
import { useAuthStore } from '@/stores/auth'


const authStore = useAuthStore()
const rooms = ref([])
interface Room{
  roomId: string,
  name: string
}
const rooms = ref<Room[]>([])
const isLoading = ref(true)
const selectedRoomForCalendar = ref<any>(null);


onMounted(async () => {
  await authStore.checkAuthStatus()
  if (!authStore.isLoggedIn) {
    isLoading.value = false
    return
  }
  try {
    const response = await getAllRooms()
    rooms.value = response.data
  } catch (error) {
    toast.error('获取会议室列表失败', { description: error instanceof Error ? error.message : '未知错误' })
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold mb-6">会议室列表</h1>
    <div v-if="isLoading">加载中...</div>
    <div v-else class="grid gap-6 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
      <RoomCard v-for="room in rooms" :key="room.roomId" :room="room">
        <template #actions>
            <Dialog>
                <DialogTrigger as-child>
                    <Button variant="outline" size="sm">查看日程</Button>
                </DialogTrigger>
                <DialogContent class="max-w-4xl">
                    <DialogHeader>
                        <DialogTitle>会议室日程: {{ room.name }}</DialogTitle>
                    </DialogHeader>
                    <CalendarView :room-id="room.roomId" />
                </DialogContent>
            </Dialog>
        </template>
      </RoomCard>
    </div>
  </div>
</template>