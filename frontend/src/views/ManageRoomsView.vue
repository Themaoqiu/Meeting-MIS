<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { getAllRooms } from '@/services/roomService';
import { createRoom, updateRoom, deleteRoom } from '@/services/adminService';
import { toast } from 'vue-sonner';
import { Button } from '@/components/ui/button';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import {Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogClose, DialogTrigger} from '@/components/ui/dialog';
import {AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger} from '@/components/ui/alert-dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { PlusCircle, Trash2 } from 'lucide-vue-next';
import { useAuthStore } from '@/stores/auth'

const rooms = ref<any[]>([]);
const isLoading = ref(true);
const isDialogOpen = ref(false);
const isEditing = ref(false);
const currentRoom = ref<any>({});
const roomToDelete = ref<any>(null);

const equipmentTypes = ['PROJECTOR', 'WHITEBOARD', 'VIDEO_CONFERENCE', 'MICROPHONE'];
const equipmentStatuses = ['AVAILABLE', 'FAULTY', 'MAINTENANCE'];
const roomStatuses = ['AVAILABLE', 'MAINTENANCE'];
const authStore = useAuthStore()

const equipmentTypeMap: Record<string, string> = {
  'PROJECTOR': '投影仪',
  'WHITEBOARD': '白板',
  'VIDEO_CONFERENCE': '视频会议系统',
  'MICROPHONE': '麦克风'
};
const equipmentStatusMap: Record<string, string> = {
  'AVAILABLE': '可用',
  'FAULTY': '故障',
  'MAINTENANCE': '维修中'
};
const roomStatusMap: Record<string, string> = {
  'AVAILABLE': '可用',
  'MAINTENANCE': '维修中'
};


const fetchRooms = async () => {
  if (!authStore.isLoggedIn) return
  isLoading.value = true;
  try {
    const response = await getAllRooms();
    rooms.value = response.data;
  } catch (error) {
    toast.error('获取会议室列表失败');
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchRooms);

const openCreateDialog = () => {
  isEditing.value = false;
  currentRoom.value = { name: '', capacity: 10, status: 'AVAILABLE', equipments: [] };
  isDialogOpen.value = true;
};

const openEditDialog = (room) => {
  isEditing.value = true;
  currentRoom.value = JSON.parse(JSON.stringify(room));
  isDialogOpen.value = true;
};

const handleSave = async () => {
  try {
    const payload = { ...currentRoom.value, capacity: Number(currentRoom.value.capacity) };
    if (isEditing.value) {
      await updateRoom(currentRoom.value.roomId, payload);
      toast.success('会议室更新成功！');
    } else {
      await createRoom(payload);
      toast.success('会议室创建成功！');
    }
    isDialogOpen.value = false;
    window.location.reload();
  } catch (error: any) {
    toast.error('操作失败', { description: error.response?.data });
  }
};

const confirmDelete = (room) => {
  roomToDelete.value = room;
};

const handleDelete = async () => {
  if (!roomToDelete.value) return;
  try {
    await deleteRoom(roomToDelete.value.roomId);
    toast.success(`会议室 "${roomToDelete.value.name}" 已删除`);
    fetchRooms();
  } catch (error: any) {
    toast.error('删除失败', { description: error.response?.data });
  } finally {
    roomToDelete.value = null;
  }
};

const addEquipment = () => {
  if (!currentRoom.value.equipments) {
      currentRoom.value.equipments = [];
  }
  currentRoom.value.equipments.push({ deviceName: '', type: 'PROJECTOR', status: 'AVAILABLE' });
};

const removeEquipment = (index: number) => {
  currentRoom.value.equipments.splice(index, 1);
};

</script>

<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold">会议室设置</h1>
       <Dialog v-model:open="isDialogOpen">
        <DialogTrigger as-child>
          <Button @click="openCreateDialog"><PlusCircle class="mr-2 h-4 w-4" />新增会议室</Button>
        </DialogTrigger>
        <DialogContent class="sm:max-w-[600px]">
          <DialogHeader><DialogTitle>{{ isEditing ? '编辑会议室' : '新增会议室' }}</DialogTitle></DialogHeader>
          <div class="space-y-4 py-4 max-h-[70vh] overflow-y-auto pr-4">
            
            <div class="space-y-2">
              <Label>名称</Label>
              <Input v-model="currentRoom.name" />
            </div>

            <div class="space-y-2">
              <Label>容量</Label>
              <Input v-model="currentRoom.capacity" type="number" />
            </div>
            
            <div class="space-y-2">
              <Label>状态</Label>
              <Select v-model="currentRoom.status">
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  <SelectItem v-for="s in roomStatuses" :key="s" :value="s">
                    {{ roomStatusMap[s] }}
                  </SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div class="space-y-4 pt-4 border-t">
              <h4 class="font-bold">设备管理</h4>
              <div v-for="(equip, index) in currentRoom.equipments" :key="index" class="flex gap-2 items-center">
                <Input v-model="equip.deviceName" placeholder="设备名" class="flex-1" />
                <Select v-model="equip.type">
                  <SelectTrigger class="w-[180px]"><SelectValue/></SelectTrigger>
                  <SelectContent>
                    <SelectItem v-for="t in equipmentTypes" :key="t" :value="t">
                      {{ equipmentTypeMap[t] }}
                    </SelectItem>
                  </SelectContent>
                </Select>
                <Select v-model="equip.status">
                    <SelectTrigger class="w-[120px]"><SelectValue/></SelectTrigger>
                    <SelectContent>
                      <SelectItem v-for="s in equipmentStatuses" :key="s" :value="s">
                        {{ equipmentStatusMap[s] }}
                      </SelectItem>
                    </SelectContent>
                </Select>
                <Button variant="ghost" size="icon" @click="removeEquipment(index)"><Trash2 class="h-4 w-4 text-red-500" /></Button>
              </div>
              <Button variant="outline" size="sm" @click="addEquipment" class="mt-2">添加设备</Button>
            </div>
          </div>
          <DialogFooter>
            <DialogClose as-child><Button variant="outline">取消</Button></DialogClose>
            <Button @click="handleSave">保存</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>

    <div class="border rounded-lg">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead class="pl-6">名称</TableHead>
            <TableHead>容量</TableHead>
            <TableHead>状态</TableHead>
            <TableHead>设备数量</TableHead>
            <TableHead class="text-right pr-16">操作</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <TableRow v-if="isLoading"><TableCell :colspan="5" class="text-center">加载中...</TableCell></TableRow>
          <TableRow v-else v-for="room in rooms" :key="room.roomId">
            <TableCell class="font-medium pl-6">{{ room.name }}</TableCell>
            <TableCell>{{ room.capacity }}</TableCell>
            <TableCell>{{ roomStatusMap[room.status] || room.status }}</TableCell>
            <TableCell>{{ room.equipments.length }}</TableCell>
            <TableCell class="text-right pr-6 space-x-2">
              <Button variant="outline" size="sm" @click="openEditDialog(room)">编辑</Button>
              <AlertDialog>
                <AlertDialogTrigger as-child>
                  <Button variant="destructive" size="sm" @click="confirmDelete(room)">删除</Button>
                </AlertDialogTrigger>
                <AlertDialogContent v-if="roomToDelete">
                  <AlertDialogHeader>
                    <AlertDialogTitle>确认删除会议室？</AlertDialogTitle>
                    <AlertDialogDescription>
                      此操作不可撤销。您确定要永久删除会议室 "<b>{{ roomToDelete.name }}</b>" 吗？
                    </AlertDialogDescription>
                  </AlertDialogHeader>
                  <AlertDialogFooter>
                    <AlertDialogCancel @click="roomToDelete = null">取消</AlertDialogCancel>
                    <AlertDialogAction @click="handleDelete">确认删除</AlertDialogAction>
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