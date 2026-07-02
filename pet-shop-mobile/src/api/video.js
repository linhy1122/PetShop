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

/** 新增视频 */
export function createVideoApi(data) {
  return request.post('/video', data)
}

/** 编辑视频 */
export function updateVideoApi(id, data) {
  return request.put(`/video/${id}`, data)
}

/** 删除视频 */
export function deleteVideoApi(id) {
  return request.delete(`/video/${id}`)
}
