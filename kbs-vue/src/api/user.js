import request from '@/utils/request'

export const user = {
  register(data) {
    return request({
      url: '/user/register',
      method: 'post',
      data
    })
  },

  getInfo() {
    return request({
      url: '/user/info',
      method: 'get'
    })
  },

  updatePassword(oldPassword, newPassword) {
    return request({
      url: '/user/password',
      method: 'put',
      data: { oldPassword, newPassword }
    })
  },

  updateInfo(data) {
    return request({
      url: '/user/info',
      method: 'put',
      data
    })
  },

  deleteAccount() {
    return request({
      url: '/user/account',
      method: 'delete'
    })
  },

  // ====== 用户管理 ======
  // 分页查询用户列表
  getPage(params) {
    return request({
      url: '/user/page',
      method: 'get',
      params
    })
  },

  // 更新用户状态
  updateStatus(id, status) {
    return request({
      url: `/user/status/${id}`,
      method: 'put',
      params: { status }
    })
  }
}
