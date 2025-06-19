import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './index.css'

const app = createApp(App)

// 必须先 use(pinia)，再 use(router)
// 这样路由守卫在执行时才能访问到已经实例化的Pinia store
app.use(createPinia())
app.use(router)

app.mount('#app')