import request from '@/utils/request'

/** 商品列表 */
export function getProductListApi(params) {
  return request.get('/product/list', { params })
}

/** 商品详情 */
export function getProductDetailApi(id) {
  return request.get(`/product/${id}`)
}

/** 热门商品 */
export function getHotProductsApi(limit = 8) {
  return request.get('/product/hot', { params: { limit } })
}
