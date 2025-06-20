import { createRouter, createWebHistory } from 'vue-router';
import LoginView from '../views/LoginView.vue';
import SignupView from '../views/SignupView.vue';
import DashboardView from '../views/DashboardView.vue';
import NewReservationView from '@/views/NewReservationView.vue';
import RoomsView from '@/views/RoomsView.vue';
import MyReservationsView from '@/views/MyReservationsView.vue';
import { useAuthStore } from '@/stores/auth';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/dashboard' },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/signup', name: 'signup', component: SignupView },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
      meta: { requiresAuth: true }
    },
    {
      path: '/rooms',
      name: 'rooms',
      component: RoomsView,
      meta: { requiresAuth: true }
    },
    {
      path: '/reservations/new',
      name: 'new-reservation',
      component: NewReservationView,
      meta: { requiresAuth: true }
    },
    {
      path: '/reservations/my',
      name: 'my-reservations',
      component: MyReservationsView,
      meta: { requiresAuth: true }
    }
  ]
});

// 全局前置守卫
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  
  // 首次进入或刷新页面时，检查登录状态
  // if (authStore.user === null) {
  //     await authStore.checkAuthStatus();
  // }

  const requiresAuth = to.meta.requiresAuth;
  const isLoggedIn = authStore.isLoggedIn;

  if (requiresAuth && !isLoggedIn) {
    // 如果页面需要登录但用户未登录，跳转到登录页
    next({ name: 'login' });
  } else {
    // 否则，正常放行
    next();
  }
});

export default router;