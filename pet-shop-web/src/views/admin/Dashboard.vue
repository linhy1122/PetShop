<template>
  <div>
    <div class="dashboard-header">
      <h3>管理控制台</h3>
      <div class="month-switcher">
        <el-date-picker
          v-model="selectedMonth"
          type="month"
          placeholder="选择月份"
          format="YYYY年MM月"
          value-format="YYYY-MM"
          :disabled-date="disabledDate"
          @change="onMonthChange"
        />
        <el-button v-if="!isCurrentMonth" type="primary" size="small" @click="goToCurrentMonth">
          回到本月
        </el-button>
      </div>
    </div>

    <!-- 营收统计卡片（全部以月为计量单位） -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card shadow="hover" class="revenue-card" @click="scrollTo('gmv')">
          <el-statistic :title="monthLabel + 'GMV'" :value="stats.monthGmv" prefix="¥" :precision="2" />
          <div class="card-tip">点击查看每日趋势</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="revenue-card" @click="scrollTo('revenue')">
          <el-statistic :title="monthLabel + '实收'" :value="stats.monthRevenue" prefix="¥" :precision="2" />
          <div class="card-tip">点击查看每日趋势</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="revenue-card" @click="scrollTo('refund')">
          <el-statistic :title="monthLabel + '退款'" :value="stats.monthRefund" prefix="¥" :precision="2" />
          <div class="card-tip">点击查看每日趋势</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="revenue-card" @click="scrollTo('netRevenue')">
          <el-statistic :title="monthLabel + '净收'" :value="stats.monthNetRevenue" prefix="¥" :precision="2" />
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
      :month-label="monthLabel"
    />

  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import AdminCharts from '@/components/AdminCharts.vue'
import { getStatisticsOverview, getMonthlyDaily } from '@/api/admin'

// ==================== 月份选择 ====================
const now = new Date()
const thisMonthStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
const selectedMonth = ref(thisMonthStr)

const monthLabel = computed(() => {
  const [y, m] = selectedMonth.value.split('-')
  return `${y}年${parseInt(m)}月`
})

const isCurrentMonth = computed(() => selectedMonth.value === thisMonthStr)

/** 禁止选择未来月份 */
function disabledDate(date) {
  return date.getTime() > Date.now()
}

function onMonthChange() {
  loadStats()
  loadMonthlyDaily()
}

function goToCurrentMonth() {
  selectedMonth.value = thisMonthStr
  loadStats()
  loadMonthlyDaily()
}

// ==================== 统计数据 ====================
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
    const res = await getStatisticsOverview(selectedMonth.value)
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
    const res = await getMonthlyDaily(selectedMonth.value)
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
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}
.dashboard-header h3 {
  margin: 0;
}
.month-switcher {
  display: flex;
  align-items: center;
  gap: 10px;
}
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