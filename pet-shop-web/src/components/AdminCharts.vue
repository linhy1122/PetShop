<template>
  <div class="chart-container">
    <!-- ==================== 月度营收每日趋势图 ==================== -->
    <div class="monthly-section-title">{{ monthLabel }}营收每日趋势</div>

    <!-- 月度GMV趋势 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <template #header>
        <div class="card-header">
          <span>{{ monthLabel }}GMV每日趋势</span>
        </div>
      </template>
      <div ref="gmvChartRef" class="chart-box"></div>
    </el-card>

    <!-- 月度实收趋势 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <template #header>
        <div class="card-header">
          <span>{{ monthLabel }}实收每日趋势</span>
        </div>
      </template>
      <div ref="revenueChartRef" class="chart-box"></div>
    </el-card>

    <!-- 月度退款趋势 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <template #header>
        <div class="card-header">
          <span>{{ monthLabel }}退款每日趋势</span>
        </div>
      </template>
      <div ref="refundChartRef" class="chart-box"></div>
    </el-card>

    <!-- 月度净收趋势 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <template #header>
        <div class="card-header">
          <span>{{ monthLabel }}净收每日趋势</span>
        </div>
      </template>
      <div ref="netRevenueChartRef" class="chart-box"></div>
    </el-card>

    <!-- ==================== 近7日趋势折线图 ==================== -->
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>基础数据趋势（近7日）</span>
          <el-button size="small" @click="refreshData" :loading="loading">
            刷新
          </el-button>
        </div>
      </template>
      <div ref="chartRef" class="chart-box"></div>
    </el-card>

    <!-- ==================== 订单状态分布饼图 ==================== -->
    <el-card shadow="hover" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>订单状态分布</span>
        </div>
      </template>
      <div ref="pieChartRef" class="chart-box"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getStatisticsTrend } from '@/api/admin'

// ==================== Props ====================
const props = defineProps({
  statusDistribution: {
    type: Array,
    default: () => []
  },
  monthlyDaily: {
    type: Object,
    default: () => ({ dates: [], gmv: [], revenue: [], refund: [], netRevenue: [] })
  },
  monthLabel: {
    type: String,
    default: '本月'
  }
})

// ==================== 对外暴露的 ref（供父组件滚动定位） ====================
const gmvChartRef = ref(null)
const revenueChartRef = ref(null)
const refundChartRef = ref(null)
const netRevenueChartRef = ref(null)

defineExpose({ gmvChartRef, revenueChartRef, refundChartRef, netRevenueChartRef })

// ==================== 订单状态分组 ====================
const STATUS_GROUPS = [
  { label: '待支付',  codes: [0] },
  { label: '待发货',  codes: [1] },
  { label: '已发货',  codes: [2] },
  { label: '待评价',  codes: [3] },
  { label: '已完成',  codes: [4] },
  { label: '已取消',  codes: [-1] },
  { label: '退款中',  codes: [-2, -3, -4] }
]

const PIE_COLORS = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#FF6B6B', '#FFA726']

// 月度趋势图颜色
const MONTHLY_COLORS = {
  gmv: '#409EFF',
  revenue: '#67C23A',
  refund: '#F56C6C',
  netRevenue: '#E6A23C'
}

// ==================== 7日趋势图实例 ====================
let chartInstance = null
const chartRef = ref(null)
const loading = ref(false)

// ==================== 饼图实例 ====================
let pieChartInstance = null
const pieChartRef = ref(null)

// ==================== 月度趋势图实例 ====================
let gmvChartInstance = null
let revenueChartInstance = null
let refundChartInstance = null
let netRevenueChartInstance = null

// ==================== 月度趋势图辅助方法 ====================

const makeMonthlyOption = (title, dates, data, color) => ({
  tooltip: {
    trigger: 'axis',
    formatter: params => `${params[0].axisValue}<br/>${params[0].marker} ${title}：¥${params[0].value.toFixed(2)}`
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '8%',
    top: '8%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: dates,
    boundaryGap: false,
    axisLabel: {
      formatter: val => {
        // 只显示日期数字
        const parts = val.split('-')
        return parts[2] + '日'
      }
    }
  },
  yAxis: {
    type: 'value',
    splitLine: { lineStyle: { color: '#eee' } },
    axisLabel: {
      formatter: val => '¥' + val
    }
  },
  series: [{
    name: title,
    type: 'line',
    smooth: true,
    data: data,
    lineStyle: { color, width: 2 },
    itemStyle: { color },
    areaStyle: {
      color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: color + '40' },
        { offset: 1, color: color + '05' }
      ])
    },
    symbol: 'circle',
    symbolSize: 6
  }]
})

const initMonthlyChart = (refVal, instanceVar, title, dataKey, color) => {
  if (!refVal.value) return
  const instance = echarts.init(refVal.value)
  window.addEventListener('resize', () => instance.resize())
  // 存到对应变量
  if (instanceVar === 'gmv') gmvChartInstance = instance
  else if (instanceVar === 'revenue') revenueChartInstance = instance
  else if (instanceVar === 'refund') refundChartInstance = instance
  else if (instanceVar === 'netRevenue') netRevenueChartInstance = instance
  updateMonthlyChart(instance, title, dataKey, color)
}

const updateMonthlyChart = (instance, title, dataKey, color) => {
  if (!instance) return
  const { dates } = props.monthlyDaily
  const data = props.monthlyDaily[dataKey] || []
  // 将 BigDecimal 字符串转为数字
  const numericData = data.map(v => Number(v) || 0)
  instance.setOption(makeMonthlyOption(title, dates, numericData, color), true)
}

const updateAllMonthlyCharts = () => {
  updateMonthlyChart(gmvChartInstance, 'GMV', 'gmv', MONTHLY_COLORS.gmv)
  updateMonthlyChart(revenueChartInstance, '实收', 'revenue', MONTHLY_COLORS.revenue)
  updateMonthlyChart(refundChartInstance, '退款', 'refund', MONTHLY_COLORS.refund)
  updateMonthlyChart(netRevenueChartInstance, '净收', 'netRevenue', MONTHLY_COLORS.netRevenue)
}

// ==================== 7日趋势折线图 ====================

const fetchTrendData = async () => {
  const res = await getStatisticsTrend()
  return res.data
}

const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
  updateChart()
}

const updateChart = async () => {
  try {
    loading.value = true
    const data = await fetchTrendData()
    const option = {
      tooltip: { trigger: 'axis' },
      legend: {
        data: ['商品', '店铺', '订单', '用户'],
        bottom: 0
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '18%',
        top: '10%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: data.dates,
        boundaryGap: false
      },
      yAxis: {
        type: 'value',
        splitLine: { lineStyle: { color: '#eee' } }
      },
      series: [
        { name: '商品', type: 'line', smooth: true, data: data.products, lineStyle: { color: '#409EFF' }, itemStyle: { color: '#409EFF' } },
        { name: '店铺', type: 'line', smooth: true, data: data.stores, lineStyle: { color: '#67C23A' }, itemStyle: { color: '#67C23A' } },
        { name: '订单', type: 'line', smooth: true, data: data.orders, lineStyle: { color: '#E6A23C' }, itemStyle: { color: '#E6A23C' } },
        { name: '用户', type: 'line', smooth: true, data: data.users, lineStyle: { color: '#F56C6C' }, itemStyle: { color: '#F56C6C' } }
      ]
    }
    chartInstance.setOption(option, true)
  } catch (error) {
    ElMessage.error('获取图表数据失败')
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  updateChart()
}

// ==================== 订单状态饼图 ====================

const initPieChart = () => {
  if (!pieChartRef.value) return
  pieChartInstance = echarts.init(pieChartRef.value)
  window.addEventListener('resize', () => {
    pieChartInstance?.resize()
  })
  updatePieChart()
}

const updatePieChart = () => {
  if (!pieChartInstance) return

  const rawData = props.statusDistribution || []
  const countMap = {}
  rawData.forEach(item => {
    countMap[item.status] = item.count || 0
  })

  const pieData = STATUS_GROUPS.map((group, index) => {
    let total = 0
    group.codes.forEach(code => {
      total += (Number(countMap[code]) || 0)
    })
    return {
      value: total,
      name: group.label,
      itemStyle: { color: PIE_COLORS[index] }
    }
  }).filter(item => item.value > 0)

  if (pieData.length === 0) {
    pieData.push({ value: 1, name: '暂无数据', itemStyle: { color: '#DCDFE6' } })
  }

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      top: 'center'
    },
    series: [{
      name: '订单状态',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['55%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data: pieData
    }]
  }
  pieChartInstance.setOption(option, true)
}

// ==================== Watch ====================

watch(() => props.statusDistribution, () => {
  updatePieChart()
}, { deep: true })

watch(() => props.monthlyDaily, () => {
  updateAllMonthlyCharts()
}, { deep: true })

// ==================== 生命周期 ====================

let timer = null
const startPolling = () => {
  timer = setInterval(() => {
    updateChart()
  }, 30000)
}

onMounted(() => {
  nextTick(() => {
    // 初始化月度趋势图
    initMonthlyChart(gmvChartRef, 'gmv', 'GMV', 'gmv', MONTHLY_COLORS.gmv)
    initMonthlyChart(revenueChartRef, 'revenue', '实收', 'revenue', MONTHLY_COLORS.revenue)
    initMonthlyChart(refundChartRef, 'refund', '退款', 'refund', MONTHLY_COLORS.refund)
    initMonthlyChart(netRevenueChartRef, 'netRevenue', '净收', 'netRevenue', MONTHLY_COLORS.netRevenue)
    // 初始化7日趋势图和饼图
    initChart()
    initPieChart()
    startPolling()
  })
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
  window.removeEventListener('resize', () => {})
  chartInstance?.dispose()
  pieChartInstance?.dispose()
  gmvChartInstance?.dispose()
  revenueChartInstance?.dispose()
  refundChartInstance?.dispose()
  netRevenueChartInstance?.dispose()
})
</script>

<style scoped>
.chart-container {
  margin-top: 20px;
}
.monthly-section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
  padding-left: 4px;
  border-left: 3px solid #409EFF;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.chart-box {
  width: 100%;
  height: 350px;
}
</style>
