<template>
  <div class="chart-container">
    <!-- 近7日趋势折线图 -->
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>数据趋势（近7日）</span>
          <el-button size="small" @click="refreshData" :loading="loading">
            刷新
          </el-button>
        </div>
      </template>
      <div ref="chartRef" class="chart-box"></div>
    </el-card>

    <!-- 订单状态分布饼图 -->
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

// Props
const props = defineProps({
  statusDistribution: {
    type: Array,
    default: () => []
  }
})

// 订单状态分组映射
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

// 趋势图实例
let chartInstance = null
const chartRef = ref(null)
const loading = ref(false)

// 饼图实例
let pieChartInstance = null
const pieChartRef = ref(null)

// ==================== 趋势折线图 ====================

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
      tooltip: {
        trigger: 'axis'
      },
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
        {
          name: '商品',
          type: 'line',
          smooth: true,
          data: data.products,
          lineStyle: { color: '#409EFF' },
          itemStyle: { color: '#409EFF' }
        },
        {
          name: '店铺',
          type: 'line',
          smooth: true,
          data: data.stores,
          lineStyle: { color: '#67C23A' },
          itemStyle: { color: '#67C23A' }
        },
        {
          name: '订单',
          type: 'line',
          smooth: true,
          data: data.orders,
          lineStyle: { color: '#E6A23C' },
          itemStyle: { color: '#E6A23C' }
        },
        {
          name: '用户',
          type: 'line',
          smooth: true,
          data: data.users,
          lineStyle: { color: '#F56C6C' },
          itemStyle: { color: '#F56C6C' }
        }
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

  // 构建 status -> count 映射
  const countMap = {}
  rawData.forEach(item => {
    countMap[item.status] = item.count || 0
  })

  // 按分组汇总
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

  // 全为0时显示占位
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
      itemStyle: {
        borderRadius: 4,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold' }
      },
      data: pieData
    }]
  }
  pieChartInstance.setOption(option, true)
}

// 监听 statusDistribution prop 变化
watch(() => props.statusDistribution, () => {
  updatePieChart()
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
})
</script>

<style scoped>
.chart-container {
  margin-top: 20px;
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
