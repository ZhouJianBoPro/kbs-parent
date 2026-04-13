<template>
  <div class="register-container">
    <div class="register-wrapper">
      <div class="register-header">
        <h1 class="logo">知识库</h1>
        <p class="subtitle">智能文档管理与 AI 对话</p>
      </div>

      <div class="register-box">
        <h2 class="title">创建账号</h2>
        <p class="hint">开始使用智能知识库系统</p>

        <el-form ref="registerFormRef" :model="registerForm" :rules="rules" label-width="0">
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="用户名 (4-20字符)"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="密码 (6-20字符)"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item prop="nickname">
            <el-input
              v-model="registerForm.nickname"
              placeholder="昵称 (可选)"
              size="large"
              :prefix-icon="UserFilled"
            />
          </el-form-item>
          <el-form-item prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="手机号 (可选)"
              size="large"
              :prefix-icon="Phone"
            />
          </el-form-item>
          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="邮箱 (可选)"
              size="large"
              :prefix-icon="Message"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              style="width: 100%"
              :loading="loading"
              @click="handleRegister"
            >
              注 册
            </el-button>
          </el-form-item>
        </el-form>

        <div class="footer">
          <span>已有账号？</span>
          <el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { user } from '@/api/user'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled, Phone, Message } from '@element-plus/icons-vue'

const router = useRouter()

const registerFormRef = ref()
const loading = ref(false)
const registerForm = ref({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  email: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度为4-20字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20字符', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await user.register(registerForm.value)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  padding: 20px;
}

.register-wrapper {
  width: 100%;
  max-width: 420px;
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
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

.register-box {
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
