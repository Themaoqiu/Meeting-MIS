<script setup>
import { ref } from 'vue';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { toast } from 'vue-sonner';
import { useAuthStore } from '@/stores/auth';
import { RouterLink } from 'vue-router';

const authStore = useAuthStore();

const username = ref('');
const email = ref('');
const password = ref('');
const phone = ref('');

const handleSignup = async () => {
  try {
    await authStore.signup({
      username: username.value,
      email: email.value,
      password: password.value,
      phone: phone.value,
    });
    toast.success({ title: '注册成功', description: '请使用您的新账户登录。' });
  } catch (error) {
    toast.error('注册失败', {description: error.response?.data || '请检查您输入的信息'});
  }
};
</script>

<template>
  <div class="flex items-center justify-center min-h-screen bg-secondary">
    <Card class="w-full max-w-sm">
      <CardHeader>
        <CardTitle class="text-2xl">注册</CardTitle>
        <CardDescription>创建一个新账户。</CardDescription>
      </CardHeader>
      <CardContent>
        <form @submit.prevent="handleSignup" class="grid gap-4">
          <div class="grid gap-2">
            <Label for="username">用户名</Label>
            <Input id="username" v-model="username" required />
          </div>
          <div class="grid gap-2">
            <Label for="email">邮箱</Label>
            <Input id="email" v-model="email" type="email" required />
          </div>
          <div class="grid gap-2">
            <Label for="password">密码</Label>
            <Input id="password" v-model="password" type="password" required />
          </div>
          <div class="grid gap-2">
            <Label for="phone">电话</Label>
            <Input id="phone" v-model="phone" />
          </div>
          <Button type="submit" class="w-full mt-2">创建账户</Button>
        </form>
        <div class="mt-4 text-center text-sm">
          已有账户?
          <RouterLink to="/login" class="underline">前往登录</RouterLink>
        </div>
      </CardContent>
    </Card>
  </div>
</template>