<script setup lang="ts">
import { onMounted, ref, computed, watch } from 'vue'
import { toast } from 'vue-sonner'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Bell, ArrowLeft, Clock, MapPin, Calendar, Mail, Inbox, Trash2 } from 'lucide-vue-next'
import apiClient from '@/services/api'
import { useAuthStore } from '@/stores/auth'
const authStore = useAuthStore()

interface Notice {
  id: number
  content: string
  createdTime: string
  sentTime: string
  sendStatus: string
  reservationTheme?: string
  reservationRoomName?: string
  reservationStartTime?: string
  reservationEndTime?: string
}

const notices = ref<Notice[]>([])
const isLoading = ref(true)

const fetchNotices = async () => {
  if (!authStore.isLoggedIn) return
  try {
    const response = await apiClient.get('/notices/my')
    notices.value = response.data
  } catch (error) {
    toast.error('获取通知列表失败')
  } finally {
    isLoading.value = false
  }
}

const formatDateTime = (datetime: string) => {
  return new Date(datetime).toLocaleString('zh-CN')
}

const formatTime = (datetime: string) => {
  return new Date(datetime).toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatDate = (datetime: string) => {
  return new Date(datetime).toLocaleDateString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    weekday: 'long'
  })
}

const getStatusText = (status: string) => {
  const statusMap = {
    'PENDING': '待发送',
    'SENT': '已发送',
    'FAILED': '发送失败'
  }
  return statusMap[status as keyof typeof statusMap] || status
}

const getStatusColor = (status: string) => {
  const colorMap = {
    'PENDING': 'bg-yellow-100 text-yellow-800',
    'SENT': 'bg-green-100 text-green-800',
    'FAILED': 'bg-red-100 text-red-800'
  }
  return colorMap[status as keyof typeof colorMap] || 'bg-gray-100 text-gray-800'
}

const isReminderNotice = (content: string | undefined) => {
  return !!content && (content.includes('会议提醒') || content.includes('即将开始'))
}

const isCancelNotice = (content: string | undefined) => {
  return !!content && content.includes('取消')
}

const filteredNotices = computed(() =>
  notices.value.filter(n => {
    // 显示会议提醒通知（有主题的）
    if (isReminderNotice(n.content) && n.reservationTheme) {
      return true
    }
    // 显示取消通知（有主题的）
    if (isCancelNotice(n.content) && n.reservationTheme) {
      return true
    }
    return false
  })
)

// 未读消息统计
const lastReadId = ref(Number(localStorage.getItem('lastReadNoticeId') || 0));
const unreadCount = computed(() => {
  if (!filteredNotices.value.length) return 0;
  return filteredNotices.value.filter(n => n.id > lastReadId.value).length;
});

// 进入通知页面时，标记为已读
watch(filteredNotices, (list) => {
  if (list.length) {
    const maxId = Math.max(...list.map(n => n.id));
    localStorage.setItem('lastReadNoticeId', String(maxId));
    lastReadId.value = maxId;
  }
}, { immediate: true });

const handleClearNotices = async () => {
  try {
    await apiClient.delete('/notices/my')
    await fetchNotices()
    toast.success('通知已清空')
  } catch {
    toast.error('清空通知失败')
  }
}

const markAllRead = () => {
  if (filteredNotices.value.length) {
    const maxId = Math.max(...filteredNotices.value.map(n => n.id))
    localStorage.setItem('lastReadNoticeId', String(maxId))
    lastReadId.value = maxId
    toast.success('已全部标为已读')
  }
}

onMounted(fetchNotices)
</script>

<template>
  <div class="px-6">
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center gap-3">
        <h1 class="text-2xl font-bold">我的通知</h1>
        <Badge v-if="unreadCount > 0" class="bg-red-500 text-white">
          {{ unreadCount }} 条未读
        </Badge>
      </div>
      <button @click="handleClearNotices" class="p-2 rounded-full hover:bg-gray-200 transition" title="清空通知">
        <Trash2 class="w-5 h-5" />
      </button>
    </div>

    <div v-if="isLoading" class="text-center py-10">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
      <p class="mt-2 text-muted-foreground">加载中...</p>
    </div>

    <div v-else-if="notices.length === 0 || filteredNotices.length === 0" class="text-center py-10 pt-24">
      <Inbox class="h-20 w-20 text-muted-foreground mx-auto mb-6" />
      <p class="text-muted-foreground text-xl">空空如也~</p>
    </div>

    <div v-else class="space-y-4">
      <Card v-for="notice in filteredNotices" :key="notice.id || notice._id || notice.createdTime">
        <template v-if="notice">
          <CardHeader class="pb-3">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-2">
                <Bell v-if="isReminderNotice(notice.content)" class="h-5 w-5 text-orange-500" />
                <Bell v-else-if="isCancelNotice(notice.content)" class="h-5 w-5 text-red-500" />
                <CardTitle class="text-lg">
                  <span v-if="isReminderNotice(notice.content) && notice.reservationTheme" class="text-orange-600 font-bold">
                    【{{ notice.reservationTheme }}】会议即将开始！
                  </span>
                  <span v-else-if="isCancelNotice(notice.content) && notice.reservationTheme" class="text-red-600 font-bold">
                    【{{ notice.reservationTheme }}】会议已取消预约
                  </span>
                  <span v-else class="text-gray-400">无关联会议</span>
                </CardTitle>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            <div class="space-y-3">
              <!-- 会议提醒通知 -->
              <div v-if="isReminderNotice(notice.content) && notice.reservationTheme" class="bg-orange-50 border-l-4 border-orange-400 p-4 rounded-r-lg">
                <p class="text-orange-800 font-medium mb-2">请注意时间安排！</p>
                <div class="space-y-2 text-sm">
                  <div class="flex items-center gap-2">
                    <MapPin class="h-4 w-4 text-orange-600" />
                    <span class="text-orange-700">会议室：{{ notice.reservationRoomName }}</span>
                  </div>
                  <div class="flex items-center gap-2">
                    <Clock class="h-4 w-4 text-orange-600" />
                    <span class="text-orange-700">时间：{{ notice.reservationStartTime ? formatTime(notice.reservationStartTime) : '' }} - {{ notice.reservationEndTime ? formatTime(notice.reservationEndTime) : '' }}</span>
                  </div>
                  <div class="flex items-center gap-2">
                    <Calendar class="h-4 w-4 text-orange-600" />
                    <span class="text-orange-700">日期：{{ notice.reservationStartTime ? formatDate(notice.reservationStartTime) : '' }}</span>
                  </div>
                </div>
                <div class="text-sm text-muted-foreground pt-2 border-t mt-2">
                  <p>创建时间：{{ formatDateTime(notice.createdTime) }}</p>
                  <p v-if="notice.sentTime">发送时间：{{ formatDateTime(notice.sentTime) }}</p>
                </div>
              </div>
              <!-- 会议取消通知 -->
              <div v-else-if="isCancelNotice(notice.content) && notice.reservationTheme" class="bg-red-50 border-l-4 border-red-400 p-2 rounded-r-lg">
                <p class="text-red-800 font-medium mb-1">会议已取消预约</p>
                <div class="space-y-1 text-sm">
                  <div class="flex items-center gap-2">
                    <MapPin class="h-4 w-4 text-red-600" />
                    <span class="text-red-700">会议室：{{ notice.reservationRoomName }}</span>
                  </div>
                  <div class="flex items-center gap-2">
                    <Clock class="h-4 w-4 text-red-600" />
                    <span class="text-red-700">时间：{{ notice.reservationStartTime ? formatTime(notice.reservationStartTime) : '' }} - {{ notice.reservationEndTime ? formatTime(notice.reservationEndTime) : '' }}</span>
                  </div>
                  <div class="flex items-center gap-2">
                    <Calendar class="h-4 w-4 text-red-600" />
                    <span class="text-red-700">日期：{{ notice.reservationStartTime ? formatDate(notice.reservationStartTime) : '' }}</span>
                  </div>
                </div>
                <!-- 取消通知不显示时间信息 -->
              </div>
              <!-- 无关联会议 -->
              <div v-else class="bg-gray-50 border-l-4 border-gray-300 p-4 rounded-r-lg">
                <p class="text-gray-500">无关联会议</p>
              </div>
            </div>
          </CardContent>
        </template>
      </Card>
    </div>
  </div>
</template>
 