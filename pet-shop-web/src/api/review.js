import request from '@/utils/request'

/** 提交评价 */
export function submitReviewApi(data) {
  return request.post('/review/submit', data)
}

/** 获取商品评价列表 */
export function getProductReviewsApi(productId, params) {
  return request.get(`/review/product/${productId}`, { params })
}

/** 删除评价 */
export function deleteReviewApi(reviewId, userId, role = 'user') {
  return request.delete(`/review/${reviewId}`, { params: { userId, role } })
}