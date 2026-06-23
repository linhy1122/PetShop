<template>
  <div class="page-shell ai-page">
    <section class="hero-card ai-hero">
      <div>
        <div class="status-pill">AI assistant</div>
        <h1 class="hero-title" style="margin-top: 12px;">宠物智能助手</h1>
        <p class="hero-desc">支持宠物知识问答、商品推荐和售后咨询。让买前、买中、买后都更有陪伴感。</p>
      </div>
      <div class="ai-tags chip-row">
        <el-tag v-for="item in quickQuestions" :key="item" effect="plain" round>{{ item }}</el-tag>
      </div>
    </section>

    <section class="page-section panel-card ai-panel">
      <div class="chat-header">
        <div>
          <h2 class="section-title">AI 聊天窗口</h2>
          <p class="section-subtitle">可以直接问宠物护理、商品推荐和售后问题。</p>
        </div>
      </div>

      <div class="chat-box" ref="msgContainer">
        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message-row', msg.role === 'user' ? 'user-row' : 'ai-row']"
        >
          <div class="message-bubble">{{ msg.content }}</div>
        </div>
        <div v-if="loading" class="message-row ai-row">
          <div class="message-bubble typing">思考中...</div>
        </div>
      </div>

      <div class="chat-input">
        <el-input
          v-model="input"
          placeholder="例如：如何挑选猫粮？"
          size="large"
          @keyup.enter="sendMessage"
        >
          <template #append>
            <el-button :icon="Promotion" :loading="loading" @click="sendMessage">发送</el-button>
          </template>
        </el-input>
      </div>
    </section>
  </div>
</template>

<script setup>
import { nextTick, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { aiChatApi } from '@/api/ai'

const messages = ref([
  { role: 'ai', content: '你好，我是 PetShop 智能助手。你可以问我宠物知识、商品推荐和售后问题。' }
])
const input = ref('')
const loading = ref(false)
const msgContainer = ref(null)
const quickQuestions = ['如何挑选猫粮？', '狗狗驱虫怎么做？', '推荐宠物玩具', '售后怎么申请退款？']

async function sendMessage() {
  const content = input.value.trim()
  if (!content || loading.value) return
  messages.value.push({ role: 'user', content })
  input.value = ''
  await scrollToBottom()

  loading.value = true
  try {
    const response = await aiChatApi(content)
    messages.value.push({ role: 'ai', content: response.data?.reply || 'AI 暂时没有回复，请稍后再试。' })
  } catch (error) {
    messages.value.push({ role: 'ai', content: 'AI 服务暂时不可用，请稍后再试。' })
    ElMessage.warning('AI 服务暂时不可用')
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

async function scrollToBottom() {
  await nextTick()
  if (msgContainer.value) {
    msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  }
}
</script>

<style scoped>
.ai-page {
  padding-bottom: 24px;
}

.ai-hero {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: end;
}

.ai-panel {
  padding: 20px;
}

.chat-box {
  height: 480px;
  overflow-y: auto;
  display: grid;
  gap: 12px;
  padding: 10px 0 18px;
}

.message-row {
  display: flex;
}

.user-row {
  justify-content: end;
}

.message-bubble {
  max-width: min(78%, 680px);
  padding: 12px 16px;
  border-radius: 18px;
  line-height: 1.8;
  white-space: pre-wrap;
}

.user-row .message-bubble {
  background: linear-gradient(135deg, #ff8f6b, #8a6cff);
  color: #fff;
  border-bottom-right-radius: 6px;
}

.ai-row .message-bubble {
  background: rgba(138, 108, 255, 0.08);
  color: var(--pet-text);
  border-bottom-left-radius: 6px;
}

.typing {
  color: var(--pet-text-soft);
}

.chat-input {
  margin-top: 8px;
}

@media (max-width: 1024px) {
  .ai-hero {
    flex-direction: column;
    align-items: start;
  }
}
</style>
