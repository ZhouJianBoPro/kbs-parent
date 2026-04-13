import { defineStore } from 'pinia'
import { user } from '@/api/user'
import { auth } from '@/api/auth'
import { token } from '@/utils/token'
import router from '@/router'

const USER_INFO_KEY = 'userInfo'
const ACCOUNT_TYPE_KEY = 'accountType'

// 从localStorage恢复状态
const getStoredUserInfo = () => {
  try {
    const info = localStorage.getItem(USER_INFO_KEY)
    return info ? JSON.parse(info) : null
  } catch {
    return null
  }
}

const getStoredAccountType = () => {
  return localStorage.getItem(ACCOUNT_TYPE_KEY)
}

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: getStoredUserInfo(),
    accountType: getStoredAccountType()
  }),

  getters: {
    isAdmin: state => state.accountType === 'SYSTEM',
    isNormal: state => state.accountType === 'NORMAL'
  },

  actions: {
    async getUserInfo() {
      try {
        const res = await user.getInfo()
        if (res.code === 200) {
          this.userInfo = res.data
          // 只在accountType为空时更新（避免覆盖登录时保存的值）
          if (!this.accountType && res.data.accountType) {
            this.accountType = res.data.accountType
            localStorage.setItem(ACCOUNT_TYPE_KEY, res.data.accountType)
          }
          // 持久化userInfo到localStorage
          localStorage.setItem(USER_INFO_KEY, JSON.stringify(res.data))
        }
        return res
      } catch (error) {
        this.logout()
        throw error
      }
    },

    async login(username, password) {
      const res = await auth.login(username, password)
      if (res.code === 200) {
        // 从登录响应中获取accountType并更新状态
        if (res.data.accountType) {
          this.accountType = res.data.accountType
        }
        // 登录成功后获取用户信息
        await this.getUserInfo()
      }
      return res
    },

    async logout() {
      try {
        await auth.logout()
      } catch (e) {
        // 忽略登出错误
      } finally {
        this.userInfo = null
        this.accountType = null
        // 清除localStorage
        localStorage.removeItem(USER_INFO_KEY)
        localStorage.removeItem(ACCOUNT_TYPE_KEY)
        router.push('/login')
      }
    },

    hasPermission(path) {
      if (!this.accountType) return false
      if (this.accountType === 'SYSTEM') {
        return path.startsWith('/admin')
      }
      if (this.accountType === 'NORMAL') {
        return path.startsWith('/user') || path === '/profile'
      }
      return false
    }
  }
})
