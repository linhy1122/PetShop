<template>
  <AdminShell>
    <div class="admin-page">
      <section class="page-grid-2">
        <div class="admin-table-card">
          <div class="section-heading">
            <div>
              <h2 class="section-title">问答记录</h2>
              <p class="section-subtitle">查看用户问题和 AI 回复。</p>
            </div>
          </div>

          <el-table :data="records" border>
            <el-table-column prop="question" label="用户提问" min-width="220" />
            <el-table-column prop="answer" label="AI 回复" min-width="260" />
            <el-table-column prop="time" label="时间" width="170" />
          </el-table>
        </div>

        <div class="admin-table-card">
          <div class="section-heading">
            <div>
              <h2 class="section-title">AI 配置</h2>
              <p class="section-subtitle">预留大模型参数和 Prompt 管理入口。</p>
            </div>
          </div>

          <el-form :model="config" label-position="top">
            <el-form-item label="模型名称">
              <el-input v-model="config.model" />
            </el-form-item>
            <el-form-item label="Temperature">
              <el-slider v-model="config.temperature" :max="1" :step="0.1" />
            </el-form-item>
            <el-form-item label="System Prompt">
              <el-input v-model="config.prompt" type="textarea" :rows="8" />
            </el-form-item>
            <el-button type="primary">保存配置</el-button>
          </el-form>
        </div>
      </section>
    </div>
  </AdminShell>
</template>

<script setup>
import AdminShell from '@/components/AdminShell.vue'

const records = [
  { question: '如何给幼猫选粮？', answer: '建议选择高蛋白、易消化、适合幼猫阶段的粮食。', time: '2025-06-21 10:20' },
  { question: '狗狗拉稀怎么办？', answer: '先观察饮食和精神状态，必要时尽快就医。', time: '2025-06-21 11:05' },
  { question: '售后怎么申请退款？', answer: '可在订单详情中提交退款申请。', time: '2025-06-21 12:40' }
]

const config = {
  model: 'gpt-4.1-mini',
  temperature: 0.5,
  prompt: '你是 PetShop 的宠物智能客服，回答要准确、友好、简洁。'
}
</script>

<style scoped>
.admin-page {
  display: grid;
  gap: 20px;
}

.admin-table-card {
  padding: 20px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(94, 77, 133, 0.1);
  box-shadow: 0 14px 32px rgba(88, 74, 126, 0.08);
}
</style>
