<script setup lang="ts">
import { Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { RouterLink } from 'vue-router';

defineProps({
  room: {
    type: Object,
    required: true
  }
});

const roomStatusMap: Record<string, string> = {
  'AVAILABLE': '可用',
  'MAINTENANCE': '维修中',
  'OCCUPIED': '使用中'
};
</script>

<template>
  <Card class="flex flex-col h-full">
    
    <CardHeader>
      <CardTitle>{{ room.name }}</CardTitle>
      <CardDescription>
        容量: {{ room.capacity }}人 | 状态: 
        <span :class="{
          'text-green-600 font-semibold': room.status === 'AVAILABLE',
          'text-red-600 font-semibold': room.status === 'OCCUPIED',
          'text-yellow-600 font-semibold': room.status === 'MAINTENANCE',
        }">
          {{ roomStatusMap[room.status] || room.status }}
        </span>
      </CardDescription>
    </CardHeader>

    <CardContent class="flex-grow">
      <div class="mb-4">
        <h4 class="font-semibold mb-2 text-sm">包含设备:</h4>
        <div v-if="room.equipments && room.equipments.length" class="flex flex-wrap gap-2">
          <Badge v-for="eq in room.equipments" :key="eq.equipmentId" variant="secondary">
            {{ eq.deviceName }}
          </Badge>
        </div>
        <p v-else class="text-sm text-muted-foreground">无</p>
      </div>
    </CardContent>

    <CardFooter class="flex gap-2 pt-4 border-t">
      <Button 
        v-if="room.status === 'MAINTENANCE'" 
        disabled 
        class="w-full bg-gray-300 text-gray-500 cursor-not-allowed"
      >
        维修中，不可预约
      </Button>
      <Button v-else as-child class="w-full">
        <RouterLink :to="`/reservations/new?roomId=${room.roomId}`">立即预约</RouterLink>
      </Button>
      <slot name="actions"></slot>
    </CardFooter>

  </Card>
</template>