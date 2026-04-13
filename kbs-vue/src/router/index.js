import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/profile',
    children: [
      {
        path: 'admin/document',
        name: 'AdminDocument',
        component: () => import('@/views/admin/document/index.vue'),
        meta: { title: '文档管理', requiresAuth: true, accountType: 'SYSTEM' }
      },
      {
        path: 'admin/user',
        name: 'AdminUser',
        component: () => import('@/views/admin/user/index.vue'),
        meta: { title: '用户管理', requiresAuth: true, accountType: 'SYSTEM' }
      },
      {
        path: 'admin/token-stat',
        name: 'AdminTokenStat',
        component: () => import('@/views/admin/token-stat/index.vue'),
        meta: { title: 'Token统计', requiresAuth: true, accountType: 'SYSTEM' }
      },
      {
        path: 'user/chat',
        name: 'UserChat',
        component: () => import('@/views/user/chat/index.vue'),
        meta: { title: 'AI对话', requiresAuth: true, accountType: 'NORMAL' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
