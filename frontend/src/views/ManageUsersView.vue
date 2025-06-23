<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { getAllUsers, updateUserRole, deleteUser } from '@/services/adminService';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Button } from '@/components/ui/button';
import { toast } from 'vue-sonner';
import { Trash2 } from 'lucide-vue-next';

const users = ref([]);
const isLoading = ref(true);
const roles = ['USER', 'ROOM_ADMIN', 'SYSTEM_ADMIN'];

const fetchUsers = async () => {
  try {
    const response = await getAllUsers();
    // 假设每个用户只有一个角色，简化处理
    users.value = response.data.map((u: { roles: any[]; }) => ({...u, roles: u.roles[0] }));
  } catch (error) {
    toast.error('获取用户列表失败');
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchUsers);

const handleRoleChange = async (userId: number, newRole: string) => {
  try {
    console.log(`准备为用户ID ${userId} 更新角色为:`, newRole);
    console.log('收到的 newRole 的类型是:', typeof newRole);
    console.log(`[调试信息] 用户ID: ${userId}, 准备更新的角色值:`, newRole);
    await updateUserRole(userId, newRole);
    toast.success('用户角色更新成功！');
    await fetchUsers(); // 刷新列表
  } catch (error) {
    toast.error('更新角色失败');
  }
};

const handleDeleteUser = async (userId: number) => {
    if(confirm('确定要删除该用户吗？此操作不可逆！')) {
        try {
            await deleteUser(userId);
            toast.success('用户已删除');
            await fetchUsers(); // 刷新列表
        } catch(error) {
            toast.error('删除用户失败');
        }
    }
};
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold mb-6">用户管理</h1>
    <div v-if="isLoading" class="text-center">加载中...</div>
    <div v-else class="border rounded-lg">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>用户名</TableHead>
            <TableHead>邮箱</TableHead>
            <TableHead>电话</TableHead>
            <TableHead>角色</TableHead>
            <TableHead>操作</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <TableRow v-for="user in users" :key="user.userId">
            <TableCell>{{ user.name }}</TableCell>
            <TableCell>{{ user.email }}</TableCell>
            <TableCell>{{ user.phone || 'N/A' }}</TableCell>
            <TableCell>
              <Select :model-value="user.roles" @update:model-value="(newRole) => handleRoleChange(user.userId, newRole)">
                <SelectTrigger>
                  <SelectValue placeholder="选择角色" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem v-for="role in roles" :key="role" :value="role">
                    {{ role }}
                  </SelectItem>
                </SelectContent>
              </Select>
            </TableCell>
            <TableCell>
                <Button variant="ghost" size="icon" @click="handleDeleteUser(user.userId)">
                    <Trash2 class="h-4 w-4 text-red-500"/>
                </Button>
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>
  </div>
</template>