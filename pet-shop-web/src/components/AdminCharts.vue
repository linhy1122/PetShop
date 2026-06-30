<template>
  <div class="chart-container">
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
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getStatisticsTrend } from '@/api/admin'

// 图表实例
let chartInstance = null
const chartRef = ref(null)
const loading = ref(false)

// 获取趋势数据
const fetchTrendData = async () => {
  const res = await getStatisticsTrend()
  return res.data
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  // 自适应
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
  // 初次加载
  updateChart()
}

// 更新图表数据
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

// 手动刷新
const refreshData = () => {
  updateChart()
}

// 定时刷新（每30秒）
let timer = null
const startPolling = () => {
  timer = setInterval(() => {
    updateChart()
  }, 30000)
}

onMounted(() => {
  nextTick(() => {
    initChart()
    startPolling()
  })
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
  window.removeEventListener('resize', () => {})
  chartInstance?.dispose()
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