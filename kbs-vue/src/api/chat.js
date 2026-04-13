import request from '@/utils/request'

export const chat = {
  createConversation() {
    return request({
      url: '/chat/createNewConversation',
      method: 'get'
    })
  },

  getConversationList(params) {
    return request({
      url: '/chat/getConversationList',
      method: 'post',
      data: params
    })
  },

  getConversationDetail(id) {
    return request({
      url: '/chat/getConversationDetail',
      method: 'get',
      params: { id }
    })
  },

  deleteConversation(id) {
    return request({
      url: '/chat/deleteConversation',
      method: 'get',
      params: { id }
    })
  }
}
