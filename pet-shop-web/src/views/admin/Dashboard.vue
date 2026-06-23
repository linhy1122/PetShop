<template>
  <AdminShell>
    <div class="admin-page">
      <section class="summary-grid">
        <div v-for="item in summaryCards" :key="item.label" class="summary-card">
          <div class="summary-label">{{ item.label }}</div>
          <div class="summary-value">{{ item.value }}</div>
          <div class="summary-note">{{ item.note }}</div>
        </div>
      </section>

      <section class="chart-grid">
        <ChartPanel title="销售趋势图" subtitle="最近 7 天销售额走势" :option="salesOption" />
        <ChartPanel title="商品销量排行" subtitle="TOP 商品销量对比" :option="productOption" />
        <ChartPanel title="用户增长趋势" subtitle="最近 7 天新增用户" :option="userOption" />
      </section>

      <section class="admin-table-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">运营概览</h2>
            <p class="section-subtitle">一眼看懂当前平台核心数据。</p>
          </div>
          <el-button type="primary" @click="$router.push('/admin/stats')">查看统计详情</el-button>
        </div>

        <div class="page-grid-3">
          <div class="soft-card metric-card">
            <div class="muted">活跃用户</div>
            <strong>1,248</strong>
          </div>
          <div class="soft-card metric-card">
            <div class="muted">在售商品</div>
            <strong>368</strong>
          </div>
          <div class="soft-card metric-card">
            <div class="muted">本月销售额</div>
            <strong>¥86,420</strong>
          </div>
        </div>
      </section>
    </div>
  </AdminShell>
</template>

<script setup>
import AdminShell from '@/components/AdminShell.vue'
import ChartPanel from '@/components/ChartPanel.vue'

const summaryCards = [
  { label: '用户总数', value: '1,248', note: '较上周 +12%' },
  { label: '商店总数', value: '86', note: '新增 4 家商店' },
  { label: '商品总数', value: '368', note: '热销品类增长' },
  { label: '订单总数', value: '5,402', note: '昨日新增 98 单' }
]

const salesOption = {
  tooltip: { trigger: 'axis' },
  grid: { left: 30, right: 20, top: 35, bottom: 30 },
  xAxis: {
    type: 'category',
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  },
  yAxis: { type: 'value' },
  series: [
    {
      type: 'line',
      smooth: true,
      data: [3200, 4100, 3950, 4680, 5200, 6100, 5880],
      areaStyle: { opacity: 0.15 },
      lineStyle: { width: 4, color: '#8a6cff' },
      itemStyle: { color: '#ff8f6b' }
    }
  ]
}

const productOption = {
  tooltip: { trigger: 'axis' },
  grid: { left: 40, right: 20, top: 25, bottom: 30 },
  xAxis: {
    type: 'value'
  },
  yAxis: {
    type: 'category',
    data: ['猫粮', '狗粮', '零食', '玩具', '洗护']
  },
  series: [
    {
      type: 'bar',
      data: [1260, 1180, 980, 760, 520],
      barWidth: 18,
      itemStyle: {
        borderRadius: [0, 12, 12, 0],
        color: '#ff8f6b'
      }
    }
  ]
}

const userOption = {
  tooltip: { trigger: 'axis' },
  grid: { left: 30, right: 20, top: 30, bottom: 30 },
  xAxis: {
    type: 'category',
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  },
  yAxis: { type: 'value' },
  series: [
    {
      type: 'line',
      smooth: true,
      data: [34, 42, 51, 48, 60, 72, 78],
      lineStyle: { width: 4, color: '#5bd3c7' },
      itemStyle: { color: '#5bd3c7' }
    }
  ]
}
</script>

<style scoped>
.admin-page {
  display: grid;
  gap: 20px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.summary-card {
  padding: 20px;
  border-radius: 24px;
  background: linear-gradient(135deg, rgba(255, 143, 107, 0.13), rgba(138, 108, 255, 0.12));
  border: 1px solid rgba(138, 108, 255, 0.1);
}

.summary-label {
  color: #7b748d;
  font-size: 13px;
}

.summary-value {
  margin-top: 10px;
  font-size: 30px;
  font-weight: 800;
}

.summary-note {
  margin-top: 10px;
  color: #7b748d;
  font-size: 13px;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.admin-table-card {
  padding: 20px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(94, 77, 133, 0.1);
  box-shadow: 0 14px 32px rgba(88, 74, 126, 0.08);
}

.metric-card {
  display: grid;
  gap: 8px;
}

.metric-card strong {
  font-size: 26px;
}

@media (max-width: 1200px) {
  .summary-grid,
  .chart-grid {
    grid-template-columns: 1fr;
  }
}
</style>
