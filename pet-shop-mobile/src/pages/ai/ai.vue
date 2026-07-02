<template>
  <view class="ai-chat-page">
    <!-- 消息列表 -->
    <scroll-view scroll-y class="chat-messages" :scroll-top="scrollTop" :scroll-with-animation="true" ref="msgContainer">
      <view v-for="(msg, idx) in messages" :key="idx"
            :class="['message', msg.role === 'user' ? 'user-msg' : 'ai-msg']">
        <view class="msg-content">{{ msg.content }}</view>
      </view>
      <view v-if="loading" class="message ai-msg">
        <view class="msg-content typing">思考中...</view>
      </view>
    </scroll-view>

    <!-- 输入区 -->
    <view class="chat-input-bar">
      <input class="chat-input" v-model="input" placeholder="输入您的问题..."
             confirm-type="send" @confirm="sendMessage" :disabled="loading" />
      <button class="send-btn" @click="sendMessage" :disabled="loading || !input.trim()">发送</button>
    </view>

    <!-- 快捷问题 -->
    <view class="quick-questions">
      <view class="quick-tag" v-for="q in quickQuestions" :key="q" @click="sendQuick(q)">{{ q }}</view>
    </view>
  </view>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { aiChatApi } from '@/api/ai'

const messages = ref([
  { role: 'ai', content: '你好！我是宠物商店的智能客服小助手 🐾\n我可以帮你解答宠物选购、养护、喂养等各种问题，有什么可以帮你的吗？' }
])
const input = ref('')
const loading = ref(false)
const scrollTop = ref(0)

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

function sendQuick(q) {
  input.value = q
  sendMessage()
}

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
}

.chat-messages {
  flex: 1;
  padding: 24rpx;
  overflow-y: auto;
}

.message {
  margin-bottom: 24rpx;
  display: flex;
}

.user-msg {
  justify-content: flex-end;
}

.user-msg .msg-content {
  background: #FF6B35;
  color: #fff;
}

.ai-msg .msg-content {
  background: #f0f2f5;
  color: #333;
}

.msg-content {
  padding: 16rpx 24rpx;
  border-radius: 16rpx;
  max-width: 75%;
  white-space: pre-wrap;
  font-size: 28rpx;
  line-height: 1.6;
  word-break: break-all;
}

.typing {
  color: #999;
  font-style: italic;
}

.chat-input-bar {
  display: flex;
  gap: 16rpx;
  padding: 20rpx 24rpx;
  background: #fff;
  border-top: 1rpx solid #f0f0f0;
}

.chat-input {
  flex: 1;
  height: 72rpx;
  background: #f5f5f5;
  border-radius: 36rpx;
  padding: 0 28rpx;
  font-size: 28rpx;
}

.send-btn {
  background: #FF6B35;
  color: #fff;
  border: none;
  padding: 0 32rpx;
  border-radius: 36rpx;
  font-size: 28rpx;
  line-height: 72rpx;
  height: 72rpx;
}

.send-btn[disabled] {
  background: #ccc;
}

.quick-questions {
  padding: 16rpx 24rpx 24rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  background: #fff;
}

.quick-tag {
  padding: 10rpx 24rpx;
  background: #FFF5ED;
  color: #FF6B35;
  border-radius: 28rpx;
  font-size: 24rpx;
}
</style>
