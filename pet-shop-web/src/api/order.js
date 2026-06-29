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
export function cancelOrderApi(orderId, reason, cancelType = 'USER') {
  return request.put(`/order/${orderId}/cancel`, null, { params: { reason, cancelType } })
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

/** 订单详情（含订单项 + 操作日志） */
export function getOrderDetailApi(orderId) {
  return request.get(`/order/${orderId}`)
}

/** 管理员订单列表（支持筛选） */
export function getAdminOrdersApi(params) {
  return request.get('/order/list', { params })
}

/** 管理员订单详情 */
export function getAdminOrderDetailApi(orderId) {
  return request.get(`/order/admin/${orderId}`)
}

/** 管理员发货 */
export function deliverOrderApi(orderId, logisticsCompany, logisticsNo) {
  return request.put(`/order/${orderId}/deliver`, null, {
    params: { logisticsCompany, logisticsNo }
  })
}

/** 审核退单 */
export function auditRefundApi(orderId, approved, auditRemark = '') {
  return request.put(`/order/${orderId}/audit-refund`, null, {
    params: { approved, auditRemark }
  })
}

/** 管理员直接退单 */
export function adminRefundApi(orderId, reason) {
  return request.put(`/order/${orderId}/admin-refund`, null, { params: { reason } })
}
