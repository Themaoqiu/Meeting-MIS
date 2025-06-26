<script setup lang="ts">
import { Toaster, toast } from 'vue-sonner'
import 'vue-sonner/style.css'
import { useAuthStore } from '@/stores/auth'
import MainLayout from '@/layouts/MainLayout.vue'

const authStore = useAuthStore()
</script>

<template>
  <MainLayout v-if="authStore.isLoggedIn" />
  <RouterView v-else />
  
  <Toaster richColors position="top-right"   />
</template>