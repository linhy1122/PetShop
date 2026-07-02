<template>
  <div class="ai-chat-page">
    <div class="container">
      <!-- 聊天卡片 -->
      <div class="chat-card">
        <!-- 头部 -->
        <div class="chat-header">
          <div class="header-avatar">🐾</div>
          <div class="header-info">
            <div class="header-title">智能客服</div>
            <div class="header-subtitle">PetShop · 随时为您服务</div>
          </div>
        </div>

        <!-- 消息区 -->
        <div class="chat-body" ref="msgContainer">
          <div class="chat-messages">
            <!-- 欢迎卡片 -->
            <div class="welcome-card" v-if="messages.length <= 1">
              <div class="welcome-icon">(∠・ω < )⌒★</div>
              <div class="welcome-text">你好！我是 PetShop 的智能客服</div>
              <div class="welcome-desc">试试问我这些问题吧 👇</div>
            </div>

            <div v-for="(msg, idx) in messages" :key="idx"
                 :class="['msg-row', msg.role === 'user' ? 'msg-user' : 'msg-ai']">
              <!-- AI 头像 -->
              <div class="msg-avatar" v-if="msg.role === 'ai'">🐾</div>
              <div class="msg-bubble" :class="msg.role === 'user' ? 'bubble-user' : 'bubble-ai'">
                <template v-for="(seg, si) in parseContent(msg.content)" :key="si">
                  <a v-if="seg.link" class="product-link"
                     :href="`/product/detail?id=${seg.id}`"
                     @click.prevent="goProduct(seg.id)">{{ seg.text }}</a>
                  <span v-else>{{ seg.text }}</span>
                </template>
              </div>
              <!-- 用户头像 -->
              <div class="msg-avatar" v-if="msg.role === 'user'">😊</div>
            </div>

            <!-- 打字动画 -->
            <div v-if="loading" class="msg-row msg-ai">
              <div class="msg-avatar">🐾</div>
              <div class="msg-bubble bubble-ai typing-bubble">
                <span class="dot"></span>
                <span class="dot"></span>
                <span class="dot"></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区 -->
        <div class="chat-footer">
          <div class="quick-row">
            <span v-for="q in quickQuestions" :key="q"
                  class="quick-chip" @click="sendQuick(q)">{{ q }}</span>
          </div>
          <div class="input-row">
            <input class="chat-input" v-model="input"
                   placeholder="输入您的问题..."
                   @keyup.enter="sendMessage" :disabled="loading" />
            <button class="send-btn" @click="sendMessage" :disabled="loading || !input.trim()">
              <span class="send-icon">➤</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { aiChatApi } from '@/api/ai'

const messages = ref([
  { role: 'ai', content: '你好！我是 PetShop 智能客服小助手 🐾\n我可以帮你查找在售宠物、推荐周边好物、店铺信息、宠物养护建议等，有什么可以帮你的吗？' }
])
const input = ref('')
const loading = ref(false)
const msgContainer = ref(null)

const quickQuestions = [
  '🐕 有哪些在售的狗狗？',
  '🛒 热门商品推荐',
  '📍 店铺在哪里？',
  '🐱 猫咪怎么选？'
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

function parseContent(content) {
  const parts = []
  const regex = /\[([^\]]*)\]\(product:(\d+)\)/g
  let last = 0
  let m
  while ((m = regex.exec(content)) !== null) {
    if (m.index > last) parts.push({ text: content.slice(last, m.index) })
    parts.push({ text: m[1], link: true, id: m[2] })
    last = regex.lastIndex
  }
  if (last < content.length) parts.push({ text: content.slice(last) })
  return parts.length ? parts : [{ text: content }]
}

function goProduct(id) {
  window.open(`/product/detail?id=${id}`, '_blank')
}

async function scrollToBottom() {
  await nextTick()
  if (msgContainer.value) {
    msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  }
}
</script>

<style scoped>
.container { max-width: 720px; margin: 0 auto; padding: 20px; }

/* ---- 卡片 ---- */
.chat-card {
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 40px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
  height: calc(100vh - 40px);
  max-height: 750px;
}

/* ---- 头部 ---- */
.chat-header {
  background: linear-gradient(135deg, #FF6B35 0%, #FF8F5E 50%, #FFB088 100%);
  padding: 20px 24px;
  display: flex;
  align-items: center;
  gap: 14px;
}
.header-avatar {
  width: 48px; height: 48px;
  background: rgba(255,255,255,0.2);
  border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  font-size: 26px;
}
.header-info { color: #fff; }
.header-title { font-size: 18px; font-weight: 700; }
.header-subtitle { font-size: 12px; opacity: 0.85; margin-top: 2px; }

/* ---- 消息区 ---- */
.chat-body { flex: 1; overflow-y: auto; background: #faf9f7; }
.chat-messages { padding: 20px; display: flex; flex-direction: column; gap: 4px; }

/* 欢迎卡片 */
.welcome-card {
  text-align: center;
  padding: 40px 20px 20px;
}
.welcome-icon { margin-bottom: 07px;font-size: 21px;  }
.welcome-text { font-size: 16px; font-weight: 600; color: #333; margin-bottom: 6px; }
.welcome-desc { font-size: 13px; color: #999; }

/* 消息行 */
.msg-row { display: flex; align-items: flex-end; gap: 10px; margin-bottom: 16px; }
.msg-user { justify-content: flex-end; }
.msg-ai { justify-content: flex-start; }

/* 头像 */
.msg-avatar {
  width: 36px; height: 36px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

/* 气泡 */
.msg-bubble {
  max-width: 70%;
  padding: 12px 18px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}
.bubble-user {
  background: linear-gradient(135deg, #FF6B35, #FF8F5E);
  color: #fff;
  border-bottom-right-radius: 6px;
}
.bubble-ai {
  background: #fff;
  color: #333;
  border-bottom-left-radius: 6px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}

/* 打字动画 */
.typing-bubble {
  display: flex; align-items: center; gap: 5px;
  padding: 16px 20px;
}
.dot {
  width: 7px; height: 7px;
  border-radius: 50%;
  background: #ccc;
  animation: bounce 1.4s infinite ease-in-out both;
}
.dot:nth-child(1) { animation-delay: -0.32s; }
.dot:nth-child(2) { animation-delay: -0.16s; }
@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

/* ---- 底部 ---- */
.chat-footer {
  background: #fff;
  border-top: 1px solid #f0ede8;
  padding: 16px 20px;
}

/* 快捷问题 */
.quick-row {
  display: flex; flex-wrap: wrap; gap: 8px;
  margin-bottom: 14px;
}
.quick-chip {
  font-size: 13px;
  padding: 7px 16px;
  background: #FFF5ED;
  color: #FF6B35;
  border-radius: 20px;
  cursor: pointer;
  border: 1px solid #FFE8D6;
  transition: all 0.2s;
  user-select: none;
}
.quick-chip:hover {
  background: #FF6B35;
  color: #fff;
  border-color: #FF6B35;
}

/* 输入行 */
.input-row { display: flex; gap: 10px; align-items: center; }
.chat-input {
  flex: 1;
  height: 44px;
  border: 1.5px solid #ece6df;
  border-radius: 22px;
  padding: 0 20px;
  font-size: 14px;
  outline: none;
  background: #faf9f7;
  transition: border 0.2s;
}
.chat-input:focus { border-color: #FF6B35; background: #fff; }
.send-btn {
  width: 44px; height: 44px;
  border: none;
  background: linear-gradient(135deg, #FF6B35, #FF8F5E);
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  transition: transform 0.15s, opacity 0.15s;
  flex-shrink: 0;
}
.send-btn:hover { transform: scale(1.05); }
.send-btn:disabled { opacity: 0.4; cursor: not-allowed; transform: none; }
.send-icon { color: #fff; font-size: 16px; }

/* 商品链接 */
.product-link {
  color: #FF6B35;
  text-decoration: underline;
  cursor: pointer;
  font-weight: 500;
}
.product-link:hover { color: #e55a2b; }
</style>
