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
