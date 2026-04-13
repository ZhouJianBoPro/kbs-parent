<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-header">
        <h1 class="logo">知识库</h1>
        <p class="subtitle">智能文档管理与 AI 对话</p>
      </div>

      <div class="login-box">
        <h2 class="title">欢迎回来</h2>
        <p class="hint">请登录您的账号</p>

        <el-form ref="loginFormRef" :model="loginForm" :rules="rules" label-width="0">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              style="width: 100%"
              :loading="loading"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="footer">
          <span>还没有账号？</span>
          <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)
const loginForm = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await userStore.login(loginForm.value.username, loginForm.value.password)
    if (res.code === 200) {
      ElMessage.success('登录成功')
      router.push(userStore.isAdmin ? '/admin/document' : '/user/chat')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  padding: 20px;
}

.login-wrapper {
  width: 100%;
  max-width: 420px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  font-size: 36px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px 0;
  letter-spacing: 2px;
}

.subtitle {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.login-box {
  background: #ffffff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px 0;
  text-align: center;
}

.hint {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0 0 32px 0;
  text-align: center;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 12px;
  box-shadow: 0 0 0 1px #e5e5e5 inset;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c0c0 inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #667eea inset;
}

:deep(.el-button--primary) {
  border-radius: 10px;
  background: #667eea;
  border-color: #667eea;
  font-weight: 500;
  letter-spacing: 2px;
}

:deep(.el-button--primary:hover) {
  background: #5a6fd6;
  border-color: #5a6fd6;
}

.footer {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 24px;
  font-size: 14px;
  color: #8c8c8c;
}
</style>
