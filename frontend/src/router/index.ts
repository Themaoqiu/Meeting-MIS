import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import LoginView from '../views/LoginView.vue';
import SignupView from '../views/SignupView.vue';
import DashboardView from '../views/DashboardView.vue';
import NewReservationView from '@/views/NewReservationView.vue';
import RoomsView from '@/views/RoomsView.vue';
import MyReservationsView from '@/views/MyReservationsView.vue';
import AdminDashboardView from '@/views/AdminDashboardView.vue';
import ManageRoomsView from '@/views/ManageRoomsView.vue';
import StatisticsView from '@/views/StatisticsView.vue';
import ManageUsersView from '@/views/ManageUsersView.vue';

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
    },
    {
      path: '/reservations/my',
      name: 'my-reservations',
      component: MyReservationsView,
      meta: { requiresAuth: true } 
    },
    {
      path: '/admin/dashboard',
      name: 'admin-dashboard',
      component: AdminDashboardView,
      meta: { requiresAuth: true, roles: ['ROOM_ADMIN', 'SYSTEM_ADMIN'] } 
    },
    {
      path: '/admin/manage-rooms',
      name: 'admin-manage-rooms',
      component: ManageRoomsView,
      meta: { requiresAuth: true, roles: ['ROOM_ADMIN', 'SYSTEM_ADMIN'] } 
    },
    {
      path: '/admin/statistics',
      name: 'admin-statistics',
      component: StatisticsView,
      meta: { requiresAuth: true, roles: ['ROOM_ADMIN', 'SYSTEM_ADMIN'] } 
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      component: ManageUsersView,
      meta: { requiresAuth: true, roles: ['SYSTEM_ADMIN'] } 
    },
  ]
});

// 全局前置守卫
router.beforeEach(async (to, _from, next) => { 
  const authStore = useAuthStore(); 
  
  // 在首次加载或刷新时检查认证状态
  if (!authStore.isLoggedIn) {
      await authStore.checkAuthStatus();
  }

  const requiresAuth = to.meta.requiresAuth; 
  const isLoggedIn = authStore.isLoggedIn;

  if (requiresAuth && !isLoggedIn) { 
    return next({ name: 'login' }); 
  }

  // 检查基于角色的访问权限
  const requiredRoles = to.meta.roles as string[]; 
  if (isLoggedIn && requiredRoles && requiredRoles.length > 0) { 
    const userHasRequiredRole = requiredRoles.some(role => authStore.hasRole(role));
    if (!userHasRequiredRole) {
      // 如果用户缺少所需角色，则重定向到安全的页面（例如仪表盘）
      return next({ name: 'dashboard' });
    }
  }
  
  next();
});

export default router;