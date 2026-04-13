import request from '@/utils/request'

export const tokenStat = {
  // 获取Token使用统计
  getStat(params) {
    return request({
      url: '/token-stat',
      method: 'get',
      params
    })
  }
}
