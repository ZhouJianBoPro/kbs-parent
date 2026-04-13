import router from './index'
import { useUserStore } from '@/stores/user'
import { token } from '@/utils/token'

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const hasToken = token.getAccessToken()

  if (to.meta.requiresAuth === false) {
    next()
    return
  }

  if (!hasToken) {
    next('/login')
    return
  }

  if (!userStore.userInfo) {
    try {
      await userStore.getUserInfo()
    } catch (e) {
      next('/login')
      return
    }
  }

  const accountType = to.meta.accountType
  if (accountType && userStore.accountType !== accountType) {
    next('/profile')
    return
  }

  next()
})

export default router
