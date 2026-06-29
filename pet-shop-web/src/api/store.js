import request from '@/utils/request'

/** 店铺列表 */
export function getStoreListApi(params) {
  return request.get('/store/list', { params })
}

/** 附近店铺 */
export function getNearbyStoresApi(lng, lat, radius) {
  return request.get('/store/nearby', { params: { lng, lat, radius } })
}

/** 店铺详情 */
export function getStoreDetailApi(id) {
  return request.get(`/store/${id}`)
}

/** 管理端店铺分页列表 */
export function getAdminStoreListApi(params) {
  return request.get('/store/admin/list', { params })
}

/** 管理端新增店铺 */
export function createStoreApi(data) {
  return request.post('/store/admin', data)
}

/** 管理端编辑店铺 */
export function updateStoreApi(id, data) {
  return request.put(`/store/admin/${id}`, data)
}

/** 管理端删除店铺 */
export function deleteStoreApi(id) {
  return request.delete(`/store/admin/${id}`)
}

/** 管理端修改店铺营业状态 */
export function updateStoreStatusApi(id, status) {
  return request.put(`/store/admin/${id}/status`, null, { params: { status } })
}
