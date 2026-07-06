<template>
  <div>
    <h3>管理控制台</h3>

    <!-- 营收统计卡片（全部以月为计量单位） -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card shadow="hover" class="revenue-card" @click="scrollTo('gmv')">
          <el-statistic title="本月GMV" :value="stats.monthGmv" prefix="¥" :precision="2" />
          <div class="card-tip">点击查看每日趋势</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="revenue-card" @click="scrollTo('revenue')">
          <el-statistic title="本月实收" :value="stats.monthRevenue" prefix="¥" :precision="2" />
          <div class="card-tip">点击查看每日趋势</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="revenue-card" @click="scrollTo('refund')">
          <el-statistic title="本月退款" :value="stats.monthRefund" prefix="¥" :precision="2" />
          <div class="card-tip">点击查看每日趋势</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="revenue-card" @click="scrollTo('netRevenue')">
          <el-statistic title="本月净收" :value="stats.monthNetRevenue" prefix="¥" :precision="2" />
          <div class="card-tip">点击查看每日趋势</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 基础统计卡片 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-statistic title="商品总数" :value="stats.products" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="店铺总数" :value="stats.stores" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="订单总数" :value="stats.orders" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="用户总数" :value="stats.users" />
      </el-col>
    </el-row>

    <!-- 图表组件（含月度每日趋势 + 近7日趋势 + 订单状态分布） -->
    <AdminCharts
      ref="chartsRef"
      :status-distribution="statusDistribution"
      :monthly-daily="monthlyDaily"
    />

  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import AdminCharts from '@/components/AdminCharts.vue'
import { getStatisticsOverview, getMonthlyDaily } from '@/api/admin'

const stats = reactive({
  products: 0, stores: 0, orders: 0, users: 0,
  monthGmv: 0, monthRevenue: 0, monthRefund: 0, monthNetRevenue: 0
})

const statusDistribution = ref([])
const monthlyDaily = ref({ dates: [], gmv: [], revenue: [], refund: [], netRevenue: [] })
const chartsRef = ref(null)

// 从后端API加载统计数据
const loadStats = async () => {
  try {
    const res = await getStatisticsOverview()
    if (res.data) {
      stats.products = res.data.products || 0
      stats.stores = res.data.stores || 0
      stats.orders = res.data.orders || 0
      stats.users = res.data.users || 0
      stats.monthGmv = res.data.monthGmv || 0
      stats.monthRevenue = res.data.monthRevenue || 0
      stats.monthRefund = res.data.monthRefund || 0
      stats.monthNetRevenue = res.data.monthNetRevenue || 0
      statusDistribution.value = res.data.statusDistribution || []
    }
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

// 加载当月每日趋势数据
const loadMonthlyDaily = async () => {
  try {
    const res = await getMonthlyDaily()
    if (res.data) {
      monthlyDaily.value = res.data
    }
  } catch (error) {
    console.error('获取月度趋势数据失败', error)
  }
}

// 点击卡片滚动到对应图表
const scrollTo = (key) => {
  const map = {
    gmv: chartsRef.value?.gmvChartRef,
    revenue: chartsRef.value?.revenueChartRef,
    refund: chartsRef.value?.refundChartRef,
    netRevenue: chartsRef.value?.netRevenueChartRef
  }
  const el = map[key]
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

onMounted(() => {
  loadStats()
  loadMonthlyDaily()
})
</script>

<style scoped>
.revenue-card {
  cursor: pointer;
  transition: all 0.3s ease;
}
.revenue-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}
.card-tip {
  text-align: right;
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}
</style>