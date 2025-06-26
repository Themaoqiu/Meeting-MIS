import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './index.css'

// 创建Vue应用示例
const app = createApp(App)
// 创建Pinia状态管理库实现全局状态存储
app.use(createPinia())
// 启用路由
app.use(router)
// 挂载与ID为app的dom节点上
app.mount('#app')