<script setup>
import { ref, onMounted } from 'vue';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Toaster } from 'vue-sonner';
import { useAuthStore } from '@/stores/auth';
import { RouterLink, useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();
const email = ref('');
const password = ref('');

const handleLogin = async () => {
  if (!email.value || !password.value) {
    toast.error('错误', {description: '邮箱和密码不能为空。',});
    return;
  }
  try {
    await authStore.login({ email: email.value, password: password.value });
    toast.success('登录成功', {description: '即将跳转到主面板...',});
    router.push('/dashboard');
  } catch (error) {
    toast.error('登录失败', {description: error.response?.data || '请检查您的邮箱和密码'});
  }
};

// 动态标题动画
const fullTitle = 'Meeting Room Booking System';
const animatedTitle = ref('');
const subtitleVisible = ref(false);

onMounted(() => {
  if (authStore.isLoggedIn) return
  let i = 0;
  function type() {
    if (i <= fullTitle.length) {
      animatedTitle.value = fullTitle.slice(0, i);
      i++;
      setTimeout(type, 60);
    } else {
      subtitleVisible.value = true;
    }
  }
  type();
});
</script>

<template>
  <div v-if="!authStore.isLoggedIn" class="flex items-center justify-center min-h-screen bg-secondary">
    <div class="absolute top-28 left-0 w-full flex flex-col items-center select-none z-10">
      <h1
        class="text-3xl md:text-4xl font-extrabold tracking-tight text-primary drop-shadow mb-2"
        style="letter-spacing: 0.04em; font-family: 'JetBrains Mono', 'Consolas', monospace;"
      >
        {{ animatedTitle }}<span v-if="animatedTitle.length < fullTitle.length" class="animate-pulse">|</span>
      </h1>
      <transition name="fade">
        <p
          v-if="subtitleVisible"
          class="text-base md:text-lg text-muted-foreground font-medium mb-6 tracking-wide"
        >
          Empower Your Meetings
        </p>
      </transition>
    </div>
    <Card class="w-full max-w-sm mt-32 z-20">
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

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.8s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>