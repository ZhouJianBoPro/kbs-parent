import axios from 'axios'
import { token } from './token'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 如果 token 已失效，将请求加入队列等待
    if (isTokenInvalid) {
      return new Promise(resolve => {
        requests.push(() => resolve(request(config)))
      })
    }

    const accessToken = token.getAccessToken()
    if (accessToken) {
      config.headers['Authorization'] = `Bearer ${accessToken}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
let isRefreshing = false
let requests = []
let isTokenInvalid = false  // token 是否已失效（401/402）

/**
 * 清除所有登录信息并跳转到登录页
 */
const clearAndRedirect = () => {
  token.clearAll()
  // 清除 Pinia store 中的状态
  const userStore = useUserStore()
  userStore.$reset()
  window.location.href = '/login'
}

/**
 * 刷新token并重试请求
 */
const refreshTokenAndRetry = async (originalRequest) => {
  if (isRefreshing) {
    return new Promise(resolve => {
      requests.push(() => resolve(request(originalRequest)))
    })
  }

  originalRequest._retry = true
  isRefreshing = true

  const refreshTokenValue = token.getRefreshToken()
  if (!refreshTokenValue) {
    clearAndRedirect()
    return Promise.reject(new Error('refreshToken不存在'))
  }

  try {
    const res = await axios.get('/api/auth/refreshToken', {
      params: { refreshToken: refreshTokenValue }
    })

    if (res.data.code === 200) {
      token.setAccessToken(res.data.data.accessToken)
      token.setRefreshToken(res.data.data.refreshToken)

      // 刷新成功，重置 token 失效标志
      isTokenInvalid = false

      requests.forEach(cb => cb())
      requests = []
      return request(originalRequest)
    } else {
      clearAndRedirect()
      return Promise.reject(new Error(res.data.message || '刷新token失败'))
    }
  } catch (e) {
    clearAndRedirect()
    return Promise.reject(e)
  } finally {
    isRefreshing = false
  }
}

request.interceptors.response.use(
  response => {
    // blob/arraybuffer类型响应直接返回response对象
    if (response.config.responseType === 'blob' || response.config.responseType === 'arraybuffer') {
      return response
    }
    const res = response.data

    // 业务处理失败 - 500需要toast提示
    if (res.code === 500) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }

    // 401未认证，直接跳转登录页
    if (res.code === 401) {
      isTokenInvalid = true  // 标记 token 失效
      ElMessage.error(res.message || '未授权，请重新登录')
      clearAndRedirect()
      return Promise.reject(new Error(res.message))
    }

    // 402 accessToken过期，刷新token并重试
    if (res.code === 402 && response.config && !response.config._retry) {
      isTokenInvalid = true  // 标记 token 失效，阻止其他请求
      return refreshTokenAndRetry(response.config)
    }

    // 其他业务错误
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  async error => {
    // blob/arraybuffer类型响应直接返回response对象
    if (error.config?.responseType === 'blob' || error.config?.responseType === 'arraybuffer') {
      return error.response
    }

    const originalRequest = error.config
    // 从error中提取响应数据（可能是HTTP错误或业务错误）
    const responseData = error.response?.data || error.data

    // 业务返回码处理
    if (responseData?.code) {
      // 未认证，跳转到登录页
      if (responseData.code === 401) {
        isTokenInvalid = true  // 标记 token 失效
        ElMessage.error(responseData.message || '未授权，请重新登录')
        clearAndRedirect()
        return Promise.reject(error)
      }

      // accessToken过期，尝试刷新token
      if (responseData.code === 402 && originalRequest && !originalRequest._retry) {
        isTokenInvalid = true  // 标记 token 失效，阻止其他请求
        return refreshTokenAndRetry(originalRequest)
      }

      // 业务失败，toast提示
      if (responseData.code === 500) {
        ElMessage.error(responseData.message || '请求失败')
        return Promise.reject(error)
      }

      // 其他业务错误，toast提示
      ElMessage.error(responseData.message || '请求失败')
      return Promise.reject(error)
    }

    // 处理HTTP层面的401（后端未正确返回Result格式的情况）
    if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
      isTokenInvalid = true  // 标记 token 失效，阻止其他请求
      return refreshTokenAndRetry(originalRequest)
    }

    // 处理其他HTTP错误
    if (error.response) {
      const errorMsg = responseData?.message || `请求失败 (${error.response.status})`
      ElMessage.error(errorMsg)
    } else if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('网络错误')
    }

    return Promise.reject(error)
  }
)

export default request
