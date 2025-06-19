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
const email = ref('');
const password = ref('');

const handleLogin = async () => {
  if (!email.value || !password.value) {
    // 2. API 调用方式改变，更简洁
    toast.error('错误', {
      description: '邮箱和密码不能为空。',
    });
    return;
  }
  try {
    await authStore.login({ email: email.value, password: password.value });
    // 使用 toast.success 来显示成功状态
    toast.success('登录成功', {
      description: '即将跳转到主面板...',
    });
  } catch (error) {
    // 使用 toast.error 来显示失败状态
    toast.error('登录失败', {
      description: error.response?.data || '请检查您的邮箱和密码。',
    });
  }
};
</script>

<template>
  <div class="flex items-center justify-center min-h-screen bg-secondary">
    <Card class="w-full max-w-sm">
      <CardHeader>
        <CardTitle class="text-2xl">登录</CardTitle>
        <CardDescription>输入您的邮箱和密码以继续。</CardDescription>
      </CardHeader>
      <CardContent>
        <form @submit.prevent="handleLogin" class="grid gap-4">
          <div class="grid gap-2">
            <Label for="email">邮箱</Label>
            <Input id="email" v-model="email" type="email" placeholder="m@example.com" required />
          </div>
          <div class="grid gap-2">
            <Label for="password">密码</Label>
            <Input id="password" v-model="password" type="password" required />
          </div>
          <Button type="submit" class="w-full mt-2">登录</Button>
        </form>
        <div class="mt-4 text-center text-sm">
          还没有账户?
          <RouterLink to="/signup" class="underline">立即注册</RouterLink>
        </div>
      </CardContent>
    </Card>
  </div>
</template>