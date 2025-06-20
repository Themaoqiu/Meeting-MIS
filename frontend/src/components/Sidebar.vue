<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { Button } from '@/components/ui/button'
// 1. 从 lucide-vue-next 中多导入一个 CalendarPlus 图标
import { Home, Building2, CalendarDays, CalendarPlus } from 'lucide-vue-next'
</script>

<template>
  <aside class="w-64 border-r bg-background p-4 flex flex-col">
    <h2 class="text-xl font-bold mb-6 px-4">会议室管理</h2>
    <nav class="flex flex-col gap-1">
      <Button variant="ghost" class="justify-start" as-child>
        <RouterLink to="/dashboard" active-class="bg-accent text-accent-foreground">
          <Home class="mr-2 h-4 w-4" />
          总览仪表盘
        </RouterLink>
      </Button>

      <Button variant="ghost" class="justify-start" as-child>
        <RouterLink to="/reservations/new" active-class="bg-accent text-accent-foreground">
          <CalendarPlus class="mr-2 h-4 w-4" />
          新建预约
        </RouterLink>
      </Button>
      <Button variant="ghost" class="justify-start" as-child>
        <RouterLink to="/rooms" active-class="bg-accent text-accent-foreground">
          <Building2 class="mr-2 h-4 w-4" />
          会议室列表
        </RouterLink>
      </Button>
      <Button variant="ghost" class="justify-start" as-child>
        <RouterLink to="/reservations/my" active-class="bg-accent text-accent-foreground">
          <CalendarDays class="mr-2 h-4 w-4" />
          我的预约
        </RouterLink>
      </Button>
    </nav>
  </aside>
</template>