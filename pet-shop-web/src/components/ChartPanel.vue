<template>
  <div class="chart-panel">
    <div class="chart-head">
      <div>
        <div class="chart-title">{{ title }}</div>
        <div v-if="subtitle" class="chart-subtitle">{{ subtitle }}</div>
      </div>
      <slot name="extra" />
    </div>
    <div ref="chartEl" class="chart-body" :style="{ height: `${height}px` }" />
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, default: '' },
  option: { type: Object, required: true },
  height: { type: Number, default: 320 }
})

const chartEl = ref(null)
let chartInstance = null

function renderChart() {
  if (!chartEl.value) return
  if (!chartInstance) {
    chartInstance = echarts.init(chartEl.value)
  }
  chartInstance.setOption(props.option, true)
}

function handleResize() {
  chartInstance?.resize()
}

onMounted(async () => {
  await nextTick()
  renderChart()
  window.addEventListener('resize', handleResize)
})

watch(
  () => props.option,
  async () => {
    await nextTick()
    renderChart()
  },
  { deep: true }
)

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
  chartInstance = null
})
</script>

<style scoped>
.chart-panel {
  padding: 20px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(94, 77, 133, 0.1);
  box-shadow: 0 14px 32px rgba(88, 74, 126, 0.08);
}

.chart-head {
  display: flex;
  justify-content: space-between;
  align-items: start;
  gap: 12px;
  margin-bottom: 18px;
}

.chart-title {
  font-size: 18px;
  font-weight: 700;
}

.chart-subtitle {
  margin-top: 6px;
  color: #7b748d;
  font-size: 13px;
}

.chart-body {
  width: 100%;
}
</style>
