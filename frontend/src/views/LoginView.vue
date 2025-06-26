<script setup>
import { ref } from 'vue';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Toaster } from 'vue-sonner';
import { useAuthStore } from '@/stores/auth';
import { RouterLink } from 'vue-router';

const authStore = useAuthStore();
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
  } catch (error) {
    toast.error('登录失败', {description: error.response?.data || '请检查您的邮箱和密码'});
  }
};
</script>

<template>
  <div class="container">
    <div class="login-form">
      <Card class="w-full max-w-sm login-form" style="background: transparent; box-shadow: none;">
        <CardHeader>
          <CardTitle class="text-2xl">登录</CardTitle>
          <CardDescription>输入您的邮箱和密码以继续。</CardDescription>
        </CardHeader>
        <CardContent>
          <form @submit.prevent="handleLogin" class="grid gap-4">
            <div class="grid gap-2">
              <Label for="email">邮箱</Label>
              <Input id="email" v-model="email" type="email" placeholder="m@example.com" required class="login-form-input" />
            </div>
            <div class="grid gap-2">
              <Label for="password">密码</Label>
              <Input id="password" v-model="password" type="password" required class="login-form-input" />
            </div>
            <Button type="submit" class="w-full mt-2 login-form-button">登录</Button>
          </form>
          <div class="mt-4 text-center text-sm">
            还没有账户?
            <RouterLink to="/signup" class="underline">立即注册</RouterLink>
          </div>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<style scoped>
html, body {
  margin: 0;
  font-family: "PingFang SC", "Microsoft Yahei", sans-serif;
}

.container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: url('@/assets/background.jpeg') fixed no-repeat;
  background-size: cover;
}

.login-form {
  width: 100%;
  max-width: 340px;
  display: flex;
  flex-direction: column;
  padding: 40px 0;
  text-align: center;
  position: relative;
  z-index: 100;
  border-radius: 18px;
  overflow: hidden;
  background: transparent !important;
  box-shadow: none !important;
}

.login-form::before {
  content: "";
  width: calc(100% + 20px);
  height: calc(100% + 20px);
  position: absolute;
  top: -10px;
  left: -10px;
  background: inherit;
  box-shadow: inset 0 0 0 200px rgba(255, 255, 255, 0.08);
  filter: blur(6px);
  z-index: -1;
}

.login-form-input {
  margin: 6px 0;
  height: 36px;
  border: none;
  background-color: rgba(255, 255, 255, 0.18);
  border-radius: 4px;
  padding: 0 14px;
  color: #3d5245;
}

.login-form-input::placeholder {
  color: #3d5245;
}

.login-form-button {
  margin-top: 24px;
  background-color: rgba(76, 175, 80, 0.85) !important;
  color: #fff !important;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: 0.4s;
  border: none;
}

.login-form-button:hover {
  background-color: rgba(56, 142, 60, 0.95) !important;
}

.login-form-button:focus,
.login-form-input:focus {
  outline: 0;
}

.login-form-button::before,
.login-form-button::after {
  content: "";
  display: block;
  width: 80px;
  height: 100%;
  background: rgba(179, 255, 210, 0.5);
  opacity: 0.5;
  position: absolute;
  top: 0;
  left: 0;
  transform: skewX(-15deg);
  filter: blur(30px);
  overflow: hidden;
  transform: translateX(-100px);
}

.login-form-button::after {
  width: 40px;
  background: rgba(179, 255, 210, 0.3);
  left: 60px;
  opacity: 0;
  filter: blur(5px);
}

.login-form-button:hover::before {
  transition: 1s;
  transform: translateX(320px);
  opacity: 0.7;
}

.login-form-button:hover::after {
  transition: 1s;
  transform: translateX(320px);
  opacity: 1;
}
</style>