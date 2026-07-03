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

/** 个性化推荐（协同过滤） */
export function getRecommendApi(userId, limit = 8) {
  return request.get('/product/recommend', { params: { userId, limit } })
}

/** 新增商品 */
export function createProductApi(data) {
  return request.post('/product', data)
}

/** 编辑商品 */
export function updateProductApi(id, data) {
  return request.put(`/product/${id}`, data)
}

/** 删除商品 */
export function deleteProductApi(id) {
  return request.delete(`/product/${id}`)
}

/** 上下架 */
export function toggleProductStatusApi(productId, status) {
  return request.put('/product/status', null, { params: { productId, status } })
}
