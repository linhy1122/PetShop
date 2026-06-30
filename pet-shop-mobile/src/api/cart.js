import request from '@/utils/request'

/** 获取购物车 */
export function getCartApi(userId) {
  return request.get('/cart/list', { params: { userId } })
}

/** 加入购物车 */
export function addToCartApi(userId, productId, quantity = 1) {
  return request.post('/cart/add', null, { params: { userId, productId, quantity } })
}

/** 修改数量 */
export function updateCartQuantityApi(cartId, quantity) {
  return request.put(`/cart/${cartId}/quantity`, null, { params: { quantity } })
}

/** 选中/取消 */
export function updateCartCheckApi(cartId, checked) {
  return request.put(`/cart/${cartId}/check`, null, { params: { checked } })
}

/** 删除购物车项 */
export function removeCartItemApi(cartId) {
  return request.delete(`/cart/${cartId}`)
}
