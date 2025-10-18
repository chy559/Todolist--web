import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/Layout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'todos',
        name: 'TodoList',
        component: () => import('@/views/TodoList.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'focus',
        name: 'Focus',
        component: () => import('@/views/Focus.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'anniversaries',
        name: 'Anniversary',
        component: () => import('@/views/Anniversary.vue'),
        meta: { requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const requiresAuth = to.meta.requiresAuth

  if (requiresAuth && !token) {
    // 需要认证但未登录，跳转到登录页
    next('/login')
  } else if (!requiresAuth && token && (to.path === '/login' || to.path === '/register')) {
    // 已登录用户访问登录/注册页，跳转到首页
    next('/dashboard')
  } else {
    next()
  }
})

export default router
