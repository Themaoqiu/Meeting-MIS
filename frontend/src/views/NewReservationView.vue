<script setup lang="ts">
import { onMounted, ref, computed, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { toast } from 'vue-sonner';
import { createReservation } from '@/services/reservationService';
import { getAllRooms } from '@/services/roomService';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Checkbox } from '@/components/ui/checkbox';
import CalendarView from '@/components/CalendarView.vue';
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const router = useRouter();
const route = useRoute();
const allRooms = ref<any[]>([]);
const reservation = ref({
  roomId: '',
  theme: '',
  personNum: 1,
  startTime: '',
  endTime: '',
  equipmentIds: [] as number[],
});

// 计算属性，用于获取当前选中会议室的可用设备
const availableEquipment = computed(() => {
  if (!reservation.value.roomId) return [];
  const selectedRoom = allRooms.value.find(r => r.roomId === Number(reservation.value.roomId));
  return selectedRoom ? selectedRoom.equipments.filter(e => e.status === 'AVAILABLE') : [];
});

// 监听会议室选择的变化，清空之前勾选的设备
watch(() => reservation.value.roomId, () => {
    reservation.value.equipmentIds = [];
});

onMounted(async () => {
  if (!authStore.isLoggedIn) return
  try {
    const response = await getAllRooms();
    allRooms.value = response.data;
    
    // 如果URL中有roomId参数，自动选择该会议室
    if (route.query.roomId) {
      reservation.value.roomId = String(route.query.roomId);
    }
  } catch (error) {
    toast.error('获取会议室列表失败');
  }
});

// 监听路由参数变化
watch(() => route.query.roomId, (newRoomId) => {
  if (newRoomId && allRooms.value.length > 0) {
    reservation.value.roomId = String(newRoomId);
  }
});

const handleSubmit = async () => {
  try {
    await createReservation({
      ...reservation.value,
      roomId: Number(reservation.value.roomId),
      personNum: Number(reservation.value.personNum),
      startTime: new Date(reservation.value.startTime).toISOString(),
      endTime: new Date(reservation.value.endTime).toISOString(),
    });
    toast.success('预约成功！');
    router.push('/reservations/my');
    window.location.reload();
  } catch (error: any) {
    toast.error('预约失败', { description: error.response?.data?.message || '请检查您的输入信息。' });
  }
};
</script>

<template>
  <Card class="w-full mx-auto">
    <CardContent>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-4 items-start">
        <!-- 左侧：标题+表单 -->
        <div>
          <CardHeader class="p-0 mb-6">
            <CardTitle>新建会议预约</CardTitle>
            <CardDescription>请填写必要的会议信息</CardDescription>
          </CardHeader>
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <div class="space-y-2">
            <Label for="room">会议室</Label>
            <Select v-model="reservation.roomId">
              <SelectTrigger><SelectValue placeholder="请选择一个会议室" /></SelectTrigger>
              <SelectContent>
                  <SelectItem 
                    v-for="room in allRooms" 
                    :key="room.roomId" 
                    :value="String(room.roomId)"
                    :disabled="room.status === 'MAINTENANCE'"
                    :class="{ 'opacity-50 cursor-not-allowed': room.status === 'MAINTENANCE' }"
                  >
                  {{ room.name }} (容量: {{ room.capacity }}人)
                    <span v-if="room.status === 'MAINTENANCE'" class="text-red-500 ml-2">- 维修中</span>
                </SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div class="space-y-2">
            <Label for="theme">会议主题</Label>
            <Input id="theme" v-model="reservation.theme" required />
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-2">
              <Label for="personNum">参会人数</Label>
              <Input id="personNum" v-model="reservation.personNum" type="number" min="1" required />
            </div>
             <div v-if="availableEquipment.length > 0" class="space-y-2">
                <Label>设备</Label>
                <div class="flex flex-wrap gap-x-4 gap-y-2 pt-2">
                    <div v-for="equip in availableEquipment" :key="equip.equipmentId" class="flex items-center gap-2">
                    <Checkbox 
                      :id="`equip-${equip.equipmentId}`" 
                      @update:checked="(checked) => {
                            if(checked) reservation.equipmentIds.push(equip.equipmentId);
                            else reservation.equipmentIds = reservation.equipmentIds.filter(id => id !== equip.equipmentId);
                      }" 
                    />
                        <Label :for="`equip-${equip.equipmentId}`" class="font-normal">{{ equip.deviceName }}</Label>
                    </div>
                </div>
            </div>
          </div>

          <div class="space-y-2">
            <Label>会议时间</Label>
            <div class="grid grid-cols-2 gap-4">
                <div>
                    <Label for="startTime" class="text-sm text-muted-foreground">开始时间</Label>
                    <Input id="startTime" v-model="reservation.startTime" type="datetime-local" required />
                </div>
                <div>
                    <Label for="endTime" class="text-sm text-muted-foreground">结束时间</Label>
                    <Input id="endTime" v-model="reservation.endTime" type="datetime-local" required />
                </div>
            </div>
          </div>

          <Button type="submit" class="w-full !mt-8">提交预约</Button>
        </form>
        </div>

  <div class="space-y-2 w-full">
    <Label>会议室日程参考</Label>
      <div v-if="!reservation.roomId" class="border rounded-lg h-full flex items-center justify-center text-muted-foreground bg-muted/30 min-h-[400px]">
          请先在左侧选择一个会议室
        </div>
          <CalendarView 
            v-else 
            :key="reservation.roomId" 
            :room-id="Number(reservation.roomId)"
            view-mode="month" 
            class="w-full"
          />
        </div>
      </div>
    </CardContent>
  </Card>
</template>