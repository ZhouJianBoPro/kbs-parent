import request from '@/utils/request'
import { token } from '@/utils/token'

export const auth = {
  login(username, password) {
    return request({
      url: '/auth/login',
      method: 'post',
      data: { username, password }
    }).then(res => {
      if (res.code === 200) {
        token.setAccessToken(res.data.accessToken)
        token.setRefreshToken(res.data.refreshToken)
        // 保存accountType到localStorage
        if (res.data.accountType) {
          localStorage.setItem('accountType', res.data.accountType)
        }
      }
      return res
    })
  },

  logout() {
    return request({
      url: '/auth/logout',
      method: 'post'
    }).then(res => {
      token.clearAll()
      localStorage.removeItem('accountType')
      return res
    })
  },

  refreshToken(refreshToken) {
    return request({
      url: '/auth/refreshToken',
      method: 'get',
      params: { refreshToken }
    })
  }
}
