<template>
  <div class="layout-container">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left">
        <h1 class="logo">知识库</h1>
      </div>
      <nav class="nav">
        <router-link
          v-if="userStore.isAdmin"
          to="/admin/document"
          class="nav-item"
          :class="{ active: activePath === '/admin/document' }"
        >
          <el-icon><Document /></el-icon>
          文档管理
        </router-link>
        <router-link
          v-if="userStore.isAdmin"
          to="/admin/user"
          class="nav-item"
          :class="{ active: activePath === '/admin/user' }"
        >
          <el-icon><UserFilled /></el-icon>
          用户管理
        </router-link>
        <router-link
          v-if="userStore.isAdmin"
          to="/admin/token-stat"
          class="nav-item"
          :class="{ active: activePath === '/admin/token-stat' }"
        >
          <el-icon><DataLine /></el-icon>
          Token统计
        </router-link>
        <router-link
          v-if="userStore.isNormal"
          to="/user/chat"
          class="nav-item"
          :class="{ active: activePath === '/user/chat' }"
        >
          <el-icon><ChatDotRound /></el-icon>
          AI 对话
        </router-link>
        <router-link
          to="/profile"
          class="nav-item"
          :class="{ active: activePath === '/profile' }"
        >
          <el-icon><User /></el-icon>
          个人中心
        </router-link>
      </nav>
      <div class="header-right">
        <span class="username">{{ userStore.userInfo?.username }}</span>
        <el-button text @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          退出
        </el-button>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Document, ChatDotRound, User, SwitchButton, UserFilled, DataLine } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activePath = computed(() => route.path)

// 页面加载时获取用户信息
onMounted(() => {
  // 如果没有用户信息或accountType，需要获取
  if (!userStore.userInfo || !userStore.accountType) {
    userStore.getUserInfo()
  }
})

const handleLogout = async () => {
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  background: #f8f9fa;
}

.header {
  height: 64px;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  font-size: 22px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
  letter-spacing: 1px;
}

.nav {
  display: flex;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  font-size: 14px;
  color: #595959;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.2s;
}

.nav-item:hover {
  background: #f5f5f5;
  color: #1a1a1a;
}

.nav-item.active {
  background: #f0f0ff;
  color: #667eea;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.username {
  font-size: 14px;
  color: #595959;
}

.main-content {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}
</style>
