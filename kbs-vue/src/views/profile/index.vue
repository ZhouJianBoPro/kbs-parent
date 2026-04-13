<template>
  <div class="profile-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-title">
        <h2>个人中心</h2>
        <p>管理您的账户信息</p>
      </div>
    </div>

    <div class="profile-layout">
      <!-- 左侧信息卡片 -->
      <div class="profile-card">
        <div class="avatar-section">
          <div class="avatar">
            <el-icon size="48"><User /></el-icon>
          </div>
          <h3 class="username">{{ userInfo.username }}</h3>
          <el-tag :type="accountType === 'SYSTEM' ? 'danger' : 'success'" size="large">
            {{ accountType === 'SYSTEM' ? '管理员' : '普通用户' }}
          </el-tag>
        </div>
        <div class="info-list">
          <div class="info-item">
            <span class="label">昵称</span>
            <span class="value">{{ userInfo.nickname || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">手机号</span>
            <span class="value">{{ userInfo.phone || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">邮箱</span>
            <span class="value">{{ userInfo.email || '-' }}</span>
          </div>
        </div>
        <el-button type="danger" plain style="width: 100%; margin-top: 20px;" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          退出登录
        </el-button>
      </div>

      <!-- 右侧编辑区域 -->
      <div class="edit-card">
        <el-tabs v-model="activeTab" class="profile-tabs">
          <!-- 基本信息 -->
          <el-tab-pane label="基本信息" name="info">
            <el-form :model="userInfoForm" label-width="80px" class="edit-form">
              <el-form-item label="用户名">
                <el-input v-model="userInfo.username" disabled />
              </el-form-item>
              <el-form-item label="昵称">
                <el-input v-model="userInfoForm.nickname" placeholder="请输入昵称" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="userInfoForm.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userInfoForm.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdateInfo">保存修改</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 修改密码 -->
          <el-tab-pane label="修改密码" name="password">
            <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="80px" class="edit-form">
              <el-form-item label="原密码" prop="oldPassword">
                <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请确认新密码" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdatePassword">修改密码</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 注销账户 -->
          <el-tab-pane label="注销账户" name="delete">
            <div class="danger-zone">
              <el-alert
                title="注销账户后，所有数据将被永久删除，无法恢复"
                type="error"
                :closable="false"
                show-icon
              />
              <el-button type="danger" style="margin-top: 20px" @click="handleDeleteAccount">
                注销账户
              </el-button>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { user } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('info')

const userInfo = computed(() => userStore.userInfo || {})

// 用户角色类型，从userStore获取
const accountType = computed(() => userStore.accountType)

const userInfoForm = reactive({
  nickname: '',
  phone: '',
  email: ''
})

const passwordFormRef = ref()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleUpdateInfo = async () => {
  const res = await user.updateInfo(userInfoForm)
  if (res.code === 200) {
    ElMessage.success('修改成功')
    userStore.getUserInfo()
  }
}

const handleUpdatePassword = async () => {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return

  const res = await user.updatePassword(passwordForm.oldPassword, passwordForm.newPassword)
  if (res.code === 200) {
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
  }
}

const handleDeleteAccount = async () => {
  await ElMessageBox.confirm('确认注销账户？此操作不可恢复！', '警告', {
    confirmButtonText: '确定注销',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const res = await user.deleteAccount()
  if (res.code === 200) {
    ElMessage.success('账户已注销')
    userStore.logout()
  }
}

const handleLogout = async () => {
  await userStore.logout()
}

onMounted(() => {
  if (userInfo.value) {
    userInfoForm.nickname = userInfo.value.nickname || ''
    userInfoForm.phone = userInfo.value.phone || ''
    userInfoForm.email = userInfo.value.email || ''
  }
})
</script>

<style scoped>
.profile-container {
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-title h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 4px 0;
}

.page-title p {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.profile-layout {
  display: flex;
  gap: 24px;
}

.profile-card {
  width: 280px;
  background: #ffffff;
  border-radius: 16px;
  padding: 32px 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  flex-shrink: 0;
}

.avatar-section {
  text-align: center;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #f0f0ff 0%, #e8e8ff 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667eea;
  margin: 0 auto 16px;
}

.username {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 12px 0;
}

.info-list {
  padding-top: 20px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  font-size: 14px;
}

.info-item .label {
  color: #8c8c8c;
}

.info-item .value {
  color: #1a1a1a;
}

.edit-card {
  flex: 1;
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.profile-tabs {
  height: 100%;
}

:deep(.el-tabs__header) {
  margin-bottom: 24px;
}

:deep(.el-tabs__item) {
  font-size: 15px;
}

:deep(.el-tabs__item.is-active) {
  font-weight: 500;
}

.edit-form {
  max-width: 400px;
}

.danger-zone {
  padding: 20px 0;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}
</style>
