<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import CalendarView from '@/components/CalendarView.vue';
import { ArrowLeft } from 'lucide-vue-next'
const route = useRoute();
const viewMode = ref<'month' | 'day'>(route.query.view === 'day' ? 'day' : 'month');
watch(() => route.query.view, (val) => {
  if (val === 'day' || val === 'month') {
    viewMode.value = val;
  }
});
</script>

<template>
  <div class="px-4">
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold">会议日程</h1>
      <div class="flex items-center gap-2">
        <button @click="$router.push('/dashboard')" class="p-2 rounded-full hover:bg-gray-200 transition">
          <ArrowLeft class="w-5 h-5" />
        </button>
        <button
          class="px-3 py-1.5 rounded-2xl text-sm border"
          :class="viewMode === 'month' ? 'bg-primary text-white' : 'bg-white'"
          @click="viewMode = 'month'"
        >
          月视图
        </button>
        <button
          class="px-3 py-1.5 rounded-2xl text-sm border"
          :class="viewMode === 'day' ? 'bg-primary text-white' : 'bg-white'"
          @click="viewMode = 'day'"
        >
          日视图
        </button>
      </div>
    </div>
    <CalendarView :viewMode="viewMode" :onlyMine="true" />
  </div>
</template> 
