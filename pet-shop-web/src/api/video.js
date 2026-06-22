import request from '@/utils/request'

/** 视频列表 */
export function getVideoListApi(params) {
  return request.get('/video/list', { params })
}

/** 视频详情 */
export function getVideoDetailApi(id) {
  return request.get(`/video/${id}`)
}

/** 点赞 */
export function likeVideoApi(id) {
  return request.put(`/video/${id}/like`)
}
