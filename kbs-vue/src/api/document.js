import request from '@/utils/request'

export const document = {
  upload(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/document/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  getList(params) {
    return request({
      url: '/document/list',
      method: 'post',
      data: params
    })
  },

  getDetail(id) {
    return request({
      url: `/document/${id}`,
      method: 'get'
    })
  },

  delete(id) {
    return request({
      url: `/document/${id}`,
      method: 'delete'
    })
  },

  download(id) {
    return request({
      url: `/document/download/${id}`,
      method: 'get',
      responseType: 'arraybuffer'
    })
  }
}
