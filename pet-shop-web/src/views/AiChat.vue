<template>
  <div class="ai-chat-page">
    <div class="container">
      <div class="chat-card">
        <div class="chat-header">
          <el-icon :size="24"><ChatDotRound /></el-icon>
          <span>宠物商店 AI 智能客服</span>
        </div>

        <div class="chat-messages" ref="msgContainer">
          <div v-for="(msg, idx) in messages" :key="idx"
               :class="['message', msg.role === 'user' ? 'user-msg' : 'ai-msg']">
            <div class="msg-content">{{ msg.content }}</div>
          </div>
          <div v-if="loading" class="message ai-msg">
            <div class="msg-content typing">思考中...</div>
          </div>
        </div>

        <div class="chat-input">
          <el-input v-model="input" placeholder="输入您的问题，比如：如何挑选健康的猫咪？"
                    @keyup.enter="sendMessage" :disabled="loading">
            <template #append>
              <el-button :icon="Position" @click="sendMessage" :loading="loading" />
            </template>
          </el-input>
        </div>

        <div class="quick-questions">
          <el-tag v-for="q in quickQuestions" :key="q" class="quick-tag"
                  @click="sendQuick(q)">{{ q }}</el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { aiChatApi } from '@/api/ai'
import { ElMessage } from 'element-plus'

const messages = ref([
  { role: 'ai', content: '你好！我是宠物商店的智能客服小助手 🐾\n我可以帮你解答宠物选购、养护、喂养等各种问题，有什么可以帮你的吗？' }
])
const input = ref('')
const loading = ref(false)
const msgContainer = ref(null)

const quickQuestions = [
  '如何挑选健康的狗狗？',
  '猫咪日常需要打哪些疫苗？',
  '有什么推荐的宠物玩具？',
  '如何训练幼犬大小便？'
]

async function sendMessage() {
  const text = input.value.trim()
  if (!text || loading.value) return
  input.value = ''
  messages.value.push({ role: 'user', content: text })
  await scrollToBottom()
  loading.value = true
  try {
    const res = await aiChatApi(text)
    messages.value.push({ role: 'ai', content: res.data?.reply || '抱歉，AI暂时无法回复' })
  } catch (e) {
    messages.value.push({ role: 'ai', content: '抱歉，AI服务暂时不可用，请稍后再试！' })
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

function sendQuick(q) { input.value = q; sendMessage() }

async function scrollToBottom() {
  await nextTick()
  if (msgContainer.value) {
    msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  }
}
</script>

<style scoped>
.container { max-width: 700px; margin: 0 auto; padding: 20px; }
.chat-card { background: #fff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.08); }
.chat-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff; padding: 16px 20px; display: flex; align-items: center; gap: 10px; font-size: 16px;
}
.chat-messages { height: 400px; overflow-y: auto; padding: 20px; }
.message { margin-bottom: 16px; display: flex; }
.user-msg { justify-content: flex-end; }
.user-msg .msg-content { background: #409eff; color: #fff; }
.ai-msg .msg-content { background: #f0f2f5; color: #333; }
.msg-content { padding: 10px 16px; border-radius: 12px; max-width: 80%; white-space: pre-wrap; font-size: 14px; line-height: 1.6; }
.typing { color: #999; font-style: italic; }
.chat-input { padding: 0 20px 12px; }
.quick-questions { padding: 0 20px 16px; display: flex; flex-wrap: wrap; gap: 8px; }
.quick-tag { cursor: pointer; }
.quick-tag:hover { background: #409eff; color: #fff; }
</style>
