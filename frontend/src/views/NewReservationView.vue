<script setup lang="ts">
import { onMounted, ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from 'vue-sonner';
import { createReservation } from '@/services/reservationService';
import { getAllRooms } from '@/services/roomService';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Checkbox } from '@/components/ui/checkbox'; 

const router = useRouter();

const rooms = ref<any[]>([]);
const reservation = ref({
  roomId: '',
  theme: '',
  personNum: 1,
  startTime: '',
  endTime: '',
  equipmentIds: [] as number[],
});

// Get available equipment for the selected room
const availableEquipment = computed(() => {
  if (!reservation.value.roomId) return [];
  const selectedRoom = rooms.value.find(r => r.roomId === Number(reservation.value.roomId));
  return selectedRoom ? selectedRoom.equipments.filter(e => e.status === 'AVAILABLE') : [];
});

onMounted(async () => {
  try {
    const response = await getAllRooms();
    rooms.value = response.data;
  } catch (error) {
    toast.error('获取会议室列表失败');
  }
});

const handleSubmit = async () => {
  try {
    await createReservation({
      ...reservation.value,
      roomId: reservation.value.roomId,
      personNum: reservation.value.personNum,
      startTime: reservation.value.startTime,
      endTime: reservation.value.endTime,
    });
    toast.success('预约成功！');
    router.push('/reservations/my');
  } catch (error: any) {
    toast.error('预约失败', { description: error.response?.data || '请检查您的输入信息。' });
  }
};
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

        <div v-if="availableEquipment.length > 0" class="space-y-2">
            <Label>所需设备</Label>
            <div class="flex flex-wrap gap-4">
                <div v-for="equip in availableEquipment" :key="equip.equipmentId" class="flex items-center gap-2">
                    <Checkbox :id="`equip-${equip.equipmentId}`" :value="equip.equipmentId" v-model="reservation.equipmentIds" />
                    <Label :for="`equip-${equip.equipmentId}`">{{ equip.deviceName }}</Label>
                </div>
            </div>
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