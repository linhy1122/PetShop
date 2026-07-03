import request from '@/utils/request'

/** 获取控制台总览数据（商品/店铺/订单/用户总数） */
export const getStatisticsOverview = () => {
  return request.get('/admin/statistics/overview')
}

/** 获取近7日趋势数据 */
export const getStatisticsTrend = () => {
  return request.get('/admin/statistics/trend')
}

/** 获取热卖 Top 10 */
export const getTopSales = () => {
  return request.get('/admin/statistics/top-sales')
}
