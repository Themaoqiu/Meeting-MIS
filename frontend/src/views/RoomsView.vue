<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getAllRooms } from '@/services/roomService'
import RoomCard from '@/components/RoomCard.vue'

const rooms = ref([])
const isLoading = ref(true)

onMounted(async () => {
  try {
    const response = await getAllRooms()
    rooms.value = response.data
  } catch (error) {
    console.error('获取会议室列表失败:', error)
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold mb-6">会议室列表</h1>
    <div v-if="isLoading" class="text-center text-muted-foreground">加载中...</div>
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <RoomCard v-for="room in rooms" :key="room.roomId" :room="room" />
    </div>
  </div>
</template>