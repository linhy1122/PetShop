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
        <ChartPanel title="日销售额" subtitle="最近 7 天趋势" :option="dayOption" />
        <ChartPanel title="月销售额" subtitle="最近 6 个月趋势" :option="monthOption" />
        <ChartPanel title="年销售额" subtitle="年度销售结构" :option="yearOption" />
      </section>

      <section class="chart-grid">
        <ChartPanel title="库存排行" subtitle="库存占比统计" :option="stockOption" />
        <ChartPanel title="会员分布" subtitle="会员等级结构" :option="memberOption" />
        <ChartPanel title="用户增长" subtitle="活跃用户趋势" :option="growthOption" />
      </section>
    </div>
  </AdminShell>
</template>

<script setup>
import AdminShell from '@/components/AdminShell.vue'
import ChartPanel from '@/components/ChartPanel.vue'

const summaryCards = [
  { label: '今日销售额', value: '¥12,860', note: '较昨日 +8.4%' },
  { label: '本月销售额', value: '¥86,420', note: '环比 +12.6%' },
  { label: '今年销售额', value: '¥1.08M', note: '同比 +24.2%' },
  { label: '会员用户', value: '1,248', note: '钻石会员 124 人' }
]

const dayOption = {
  tooltip: { trigger: 'axis' },
  grid: { left: 32, right: 20, top: 25, bottom: 30 },
  xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
  yAxis: { type: 'value' },
  series: [{ type: 'line', smooth: true, data: [850, 940, 1020, 1240, 1380, 1560, 1490], itemStyle: { color: '#8a6cff' } }]
}

const monthOption = {
  tooltip: { trigger: 'axis' },
  grid: { left: 32, right: 20, top: 25, bottom: 30 },
  xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
  yAxis: { type: 'value' },
  series: [{ type: 'bar', data: [86, 92, 88, 108, 124, 136], barWidth: 22, itemStyle: { borderRadius: [10, 10, 0, 0], color: '#ff8f6b' } }]
}

const yearOption = {
  tooltip: { trigger: 'item' },
  series: [{
    type: 'pie',
    radius: ['42%', '72%'],
    data: [
      { value: 38, name: '宠物食品' },
      { value: 24, name: '宠物玩具' },
      { value: 20, name: '宠物护理' },
      { value: 18, name: '其他' }
    ]
  }]
}

const stockOption = {
  tooltip: { trigger: 'axis' },
  grid: { left: 40, right: 20, top: 25, bottom: 30 },
  xAxis: { type: 'value' },
  yAxis: { type: 'category', data: ['狗粮', '猫粮', '零食', '玩具', '洗护'] },
  series: [{ type: 'bar', data: [860, 920, 640, 520, 410], itemStyle: { color: '#5bd3c7', borderRadius: [0, 12, 12, 0] } }]
}

const memberOption = {
  tooltip: { trigger: 'item' },
  series: [{
    type: 'pie',
    radius: '68%',
    data: [
      { value: 52, name: '普通会员' },
      { value: 28, name: '银卡会员' },
      { value: 15, name: '金卡会员' },
      { value: 5, name: '钻石会员' }
    ]
  }]
}

const growthOption = {
  tooltip: { trigger: 'axis' },
  grid: { left: 32, right: 20, top: 25, bottom: 30 },
  xAxis: { type: 'category', data: ['1周前', '6天前', '5天前', '4天前', '3天前', '2天前', '昨天'] },
  yAxis: { type: 'value' },
  series: [{ type: 'line', smooth: true, data: [28, 34, 41, 45, 53, 68, 78], itemStyle: { color: '#ff8f6b' } }]
}
</script>

<style scoped>
.admin-page {
  display: grid;
  gap: 20px;
}

.summary-grid,
.chart-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
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

@media (max-width: 1200px) {
  .summary-grid,
  .chart-grid {
    grid-template-columns: 1fr;
  }
}
</style>
