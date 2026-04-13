const ACCESS_TOKEN = 'access_token'
const REFRESH_TOKEN = 'refresh_token'
const USER_INFO_KEY = 'userInfo'
const ACCOUNT_TYPE_KEY = 'accountType'

export const token = {
  getAccessToken() {
    return localStorage.getItem(ACCESS_TOKEN)
  },

  setAccessToken(token) {
    localStorage.setItem(ACCESS_TOKEN, token)
  },

  removeAccessToken() {
    localStorage.removeItem(ACCESS_TOKEN)
  },

  getRefreshToken() {
    return localStorage.getItem(REFRESH_TOKEN)
  },

  setRefreshToken(token) {
    localStorage.setItem(REFRESH_TOKEN, token)
  },

  removeRefreshToken() {
    localStorage.removeItem(REFRESH_TOKEN)
  },

  clearAll() {
    this.removeAccessToken()
    this.removeRefreshToken()
    localStorage.removeItem(USER_INFO_KEY)
    localStorage.removeItem(ACCOUNT_TYPE_KEY)
  }
}
