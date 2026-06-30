<template>
  <div>
    <h3>管理控制台</h3>
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
    <AdminCharts />
       
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import AdminCharts from '@/components/AdminCharts.vue'
import { getStatisticsOverview } from '@/api/admin'

const stats = reactive({ products: 0, stores: 0, orders: 0, users: 0 })

// 从后端API加载统计数据
const loadStats = async () => {
  try {
    const res = await getStatisticsOverview()
    if (res.data) {
      stats.products = res.data.products || 0
      stats.stores = res.data.stores || 0
      stats.orders = res.data.orders || 0
      stats.users = res.data.users || 0
    }
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>
