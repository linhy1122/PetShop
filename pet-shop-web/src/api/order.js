import request from '@/utils/request'

/** 创建订单 */
export function createOrderApi(userId, addressId, remark) {
  return request.post('/order/create', { addressId, remark }, { params: { userId } })
}

/** 支付订单 */
export function payOrderApi(orderId, payMethod = 'alipay') {
  return request.put(`/order/${orderId}/pay`, null, { params: { payMethod } })
}

/** 取消订单 */
export function cancelOrderApi(orderId, reason) {
  return request.put(`/order/${orderId}/cancel`, null, { params: { reason } })
}

/** 确认收货 */
export function confirmReceiveApi(orderId) {
  return request.put(`/order/${orderId}/receive`)
}

/** 申请退单 */
export function applyRefundApi(orderId, reason) {
  return request.put(`/order/${orderId}/refund`, null, { params: { reason } })
}

/** 用户订单列表 */
export function getUserOrdersApi(userId, params) {
  return request.get(`/order/user/${userId}`, { params })
}

/** 订单详情 */
export function getOrderDetailApi(orderId) {
  return request.get(`/order/${orderId}`)
}
