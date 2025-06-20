<script setup lang="ts">
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { RouterLink } from 'vue-router'

defineProps({
  room: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <Card>
    <CardHeader>
      <CardTitle>{{ room.name }}</CardTitle>
      <CardDescription>容量: {{ room.capacity }}人 | 状态: {{ room.status }}</CardDescription>
    </CardHeader>
    <CardContent>
      <div class="mb-4">
        <h4 class="font-semibold mb-2">包含设备:</h4>
        <div v-if="room.equipments.length" class="flex flex-wrap gap-2">
          <Badge v-for="eq in room.equipments" :key="eq.equipmentId" variant="secondary">
            {{ eq.deviceName }}
          </Badge>
        </div>
        <p v-else class="text-sm text-muted-foreground">无</p>
      </div>
      <Button as-child class="w-full">
        <RouterLink :to="`/reservations/new?roomId=${room.roomId}`">立即预约</RouterLink>
      </Button>
    </CardContent>
  </Card>
</template>