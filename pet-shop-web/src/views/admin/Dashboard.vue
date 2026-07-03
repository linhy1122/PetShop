<template>
  <div>
    <h3>管理控制台</h3>

    <!-- 营收统计卡片 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="今日GMV" :value="stats.todayGmv" prefix="¥" :precision="2" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="今日实收" :value="stats.todayRevenue" prefix="¥" :precision="2" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="本月实收" :value="stats.monthRevenue" prefix="¥" :precision="2" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="本月退款" :value="stats.monthRefund" prefix="¥" :precision="2" />
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

    <!-- 图表组件 -->
    <AdminCharts :status-distribution="statusDistribution" />

  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import AdminCharts from '@/components/AdminCharts.vue'
import { getStatisticsOverview } from '@/api/admin'

const stats = reactive({
  products: 0, stores: 0, orders: 0, users: 0,
  todayGmv: 0, todayRevenue: 0, monthRevenue: 0, monthRefund: 0
})

const statusDistribution = ref([])

// 从后端API加载统计数据
const loadStats = async () => {
  try {
    const res = await getStatisticsOverview()
    if (res.data) {
      stats.products = res.data.products || 0
      stats.stores = res.data.stores || 0
      stats.orders = res.data.orders || 0
      stats.users = res.data.users || 0
      stats.todayGmv = res.data.todayGmv || 0
      stats.todayRevenue = res.data.todayRevenue || 0
      stats.monthRevenue = res.data.monthRevenue || 0
      stats.monthRefund = res.data.monthRefund || 0
      statusDistribution.value = res.data.statusDistribution || []
    }
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>
