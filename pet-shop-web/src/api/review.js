import request from '@/utils/request'

/** 提交评价 */
export function submitReviewApi(data) {
  return request.post('/review/submit', data)
}

/** 获取商品评价列表 */
export function getProductReviewsApi(productId, params) {
  return request.get(`/review/product/${productId}`, { params })
}

/** 获取用户在订单下的评价 */
export function getOrderReviewsApi(orderId, userId) {
  return request.get(`/review/order/${orderId}`, { params: { userId } })
}

/** 修改评价（批量更新订单下所有评价） */
export function updateReviewApi(orderId, data) {
  return request.put(`/review/order/${orderId}`, data)
}

/** 修改单条评价 */
export function updateOneReviewApi(reviewId, data) {
  return request.put(`/review/${reviewId}`, data)
}

/** 删除评价 */
export function deleteReviewApi(reviewId, userId, role = 'user') {
  return request.delete(`/review/${reviewId}`, { params: { userId, role } })
}