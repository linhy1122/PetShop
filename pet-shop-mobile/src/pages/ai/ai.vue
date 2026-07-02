<template>
  <view class="ai-chat-page">
    <!-- 头部 -->
    <view class="chat-header">
      <view class="header-avatar">🐾</view>
      <view class="header-info">
        <text class="header-title">智能客服</text>
        <text class="header-subtitle">PetShop · 随时为您服务</text>
      </view>
    </view>

    <!-- 消息区 -->
    <scroll-view scroll-y class="chat-body" :scroll-top="scrollTop"
                 :scroll-with-animation="true" ref="msgContainer">
      <view class="chat-messages">
        <!-- 欢迎卡片 -->
        <view class="welcome-card" v-if="messages.length <= 1">
          <text class="welcome-icon">🐶</text>
          <text class="welcome-text">你好！我是 PetShop 的智能客服</text>
          <text class="welcome-desc">试试问我这些问题吧 👇</text>
        </view>

        <view v-for="(msg, idx) in messages" :key="idx"
              :class="['msg-row', msg.role === 'user' ? 'msg-user' : 'msg-ai']">
          <view class="msg-avatar" v-if="msg.role === 'ai'">
            <text>🐾</text>
          </view>
          <view class="msg-bubble" :class="msg.role === 'user' ? 'bubble-user' : 'bubble-ai'">
            <text>{{ msg.content }}</text>
          </view>
          <view class="msg-avatar" v-if="msg.role === 'user'">
            <text>😊</text>
          </view>
        </view>

        <!-- 打字动画 -->
        <view v-if="loading" class="msg-row msg-ai">
          <view class="msg-avatar"><text>🐾</text></view>
          <view class="msg-bubble bubble-ai typing-bubble">
            <view class="dot"></view>
            <view class="dot"></view>
            <view class="dot"></view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部输入区 -->
    <view class="chat-footer">
      <scroll-view scroll-x class="quick-row">
        <view class="quick-chip" v-for="q in quickQuestions" :key="q"
              @click="sendQuick(q)">{{ q }}</view>
      </scroll-view>
      <view class="input-row">
        <input class="chat-input" v-model="input" placeholder="输入您的问题..."
               confirm-type="send" @confirm="sendMessage" :disabled="loading" />
        <view class="send-btn" :class="{ disabled: loading || !input.trim() }"
              @click="sendMessage">
          <text class="send-icon">➤</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { aiChatApi } from '@/api/ai'

const messages = ref([
  { role: 'ai', content: '你好！我是 PetShop 智能客服小助手 🐾\n我可以帮你查找在售宠物、推荐周边好物、店铺信息、宠物养护建议等，有什么可以帮你的吗？' }
])
const input = ref('')
const loading = ref(false)
const scrollTop = ref(0)

const quickQuestions = [
  '🐕 有哪些狗狗？',
  '🛒 热门推荐',
  '📍 店铺在哪？',
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

async function scrollToBottom() {
  await nextTick()
  scrollTop.value = scrollTop.value + 99999
}
</script>

<style scoped>
.ai-chat-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 88rpx);
  background: #faf9f7;
}

/* ---- 头部 ---- */
.chat-header {
  background: linear-gradient(135deg, #FF6B35, #FF8F5E);
  padding: 28rpx 32rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.header-avatar {
  width: 80rpx; height: 80rpx;
  background: rgba(255,255,255,0.25);
  border-radius: 20rpx;
  display: flex; align-items: center; justify-content: center;
  font-size: 44rpx;
}
.header-info { display: flex; flex-direction: column; }
.header-title { font-size: 36rpx; font-weight: 700; color: #fff; }
.header-subtitle { font-size: 24rpx; color: rgba(255,255,255,0.85); margin-top: 4rpx; }

/* ---- 消息区 ---- */
.chat-body { flex: 1; overflow-y: auto; }
.chat-messages { padding: 24rpx; display: flex; flex-direction: column; gap: 4rpx; }

/* 欢迎卡片 */
.welcome-card {
  text-align: center;
  padding: 80rpx 40rpx 40rpx;
  display: flex; flex-direction: column; align-items: center;
}
.welcome-icon { font-size: 100rpx; margin-bottom: 20rpx; }
.welcome-text { font-size: 32rpx; font-weight: 600; color: #333; margin-bottom: 8rpx; }
.welcome-desc { font-size: 26rpx; color: #999; }

/* 消息行 */
.msg-row { display: flex; align-items: flex-end; gap: 12rpx; margin-bottom: 24rpx; }
.msg-user { justify-content: flex-end; }
.msg-ai { justify-content: flex-start; }

/* 头像 */
.msg-avatar {
  width: 64rpx; height: 64rpx;
  border-radius: 16rpx;
  background: #fff;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.06);
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.msg-avatar text { font-size: 36rpx; }

/* 气泡 */
.msg-bubble {
  max-width: 70%;
  padding: 20rpx 28rpx;
  border-radius: 28rpx;
  font-size: 28rpx;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-all;
}
.bubble-user {
  background: linear-gradient(135deg, #FF6B35, #FF8F5E);
  color: #fff;
  border-bottom-right-radius: 8rpx;
}
.bubble-ai {
  background: #fff;
  color: #333;
  border-bottom-left-radius: 8rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}

/* 打字动画 */
.typing-bubble {
  display: flex; align-items: center; gap: 8rpx;
  padding: 28rpx 32rpx;
}
.dot {
  width: 14rpx; height: 14rpx;
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
  border-top: 1rpx solid #f0ede8;
  padding: 20rpx 24rpx 24rpx;
}

/* 快捷问题 */
.quick-row {
  display: flex; gap: 12rpx;
  margin-bottom: 18rpx;
  white-space: nowrap;
  padding-bottom: 4rpx;
}
.quick-chip {
  display: inline-block;
  font-size: 24rpx;
  padding: 12rpx 24rpx;
  background: #FFF5ED;
  color: #FF6B35;
  border-radius: 28rpx;
  border: 1rpx solid #FFE8D6;
  white-space: nowrap;
  flex-shrink: 0;
}
.quick-chip:active {
  background: #FF6B35;
  color: #fff;
}

/* 输入行 */
.input-row {
  display: flex; gap: 16rpx; align-items: center;
}
.chat-input {
  flex: 1;
  height: 76rpx;
  background: #faf9f7;
  border: 1.5rpx solid #ece6df;
  border-radius: 38rpx;
  padding: 0 28rpx;
  font-size: 28rpx;
}
.send-btn {
  width: 76rpx; height: 76rpx;
  background: linear-gradient(135deg, #FF6B35, #FF8F5E);
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.send-btn.disabled { opacity: 0.4; }
.send-icon { color: #fff; font-size: 32rpx; }
</style>
