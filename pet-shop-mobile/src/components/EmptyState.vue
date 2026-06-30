<template>
  <view class="empty-state">
    <image src="https://picsum.photos/seed/empty/200/200" mode="aspectFit" class="empty-img" />
    <text class="empty-text">{{ description }}</text>
    <slot>
      <button v-if="showBtn" class="empty-btn" @click="$emit('action')">{{ btnText }}</button>
    </slot>
  </view>
</template>

<script setup>
defineProps({
  description: { type: String, default: '暂无数据' },
  showBtn: { type: Boolean, default: false },
  btnText: { type: String, default: '去看看' }
})

defineEmits(['action'])
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
}

.empty-img {
  width: 240rpx;
  height: 240rpx;
  margin-bottom: 24rpx;
  opacity: 0.6;
}

.empty-text {
  font-size: 28rpx;
  color: #9CA3AF;
  margin-bottom: 32rpx;
}

.empty-btn {
  background: #FF6B35;
  color: #fff;
  border: none;
  padding: 16rpx 48rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
}
</style>
