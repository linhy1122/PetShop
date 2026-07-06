import request from '@/utils/request'

/** 获取控制台总览数据（商品/店铺/订单/用户总数 + 指定月份营收） */
export const getStatisticsOverview = (yearMonth) => {
  return request.get('/admin/statistics/overview', { params: { yearMonth } })
}

/** 获取近7日趋势数据 */
export const getStatisticsTrend = () => {
  return request.get('/admin/statistics/trend')
}

/** 获取指定月份每日营收趋势数据 */
export const getMonthlyDaily = (yearMonth) => {
  return request.get('/admin/statistics/monthly-daily', { params: { yearMonth } })
}

/** 获取热卖 Top 10 */
export const getTopSales = () => {
  return request.get('/admin/statistics/top-sales')
}
