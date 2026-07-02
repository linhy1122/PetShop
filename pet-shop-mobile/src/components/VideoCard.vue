<template>
  <view class="video-card" @click="goDetail">
    <view class="video-cover">
      <image :src="video.cover || 'https://picsum.photos/seed/vc/200/200'" mode="aspectFill" class="cover-img" />
      <view class="play-icon">▶</view>
      <text class="duration">{{ formatDuration(video.duration) }}</text>
    </view>
    <view class="video-info">
      <view class="video-title text-ellipsis-2">{{ video.title }}</view>
      <view class="video-stats">
        <text>👁 {{ video.viewCount || 0 }}</text>
        <text>👍 {{ video.likeCount || 0 }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  video: { type: Object, required: true }
})

function goDetail() {
  uni.navigateTo({ url: `/pages/video/player/player?id=${props.video.id}` })
}

function formatDuration(sec) {
  if (!sec) return '00:00'
  const m = Math.floor(sec / 60)
  const s = sec % 60
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}
</script>

<style scoped>
.video-card {
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
  margin-bottom: 20rpx;
}

.video-cover {
  position: relative;
  height: 340rpx;
  overflow: hidden;
}

.cover-img {
  width: 100%;
  height: 100%;
  background: #f5f5f5;
}

.play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80rpx;
  height: 80rpx;
  background: rgba(0,0,0,0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 36rpx;
}

.duration {
  position: absolute;
  bottom: 16rpx;
  right: 16rpx;
  background: rgba(0,0,0,0.7);
  color: #fff;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
}

.video-info {
  padding: 20rpx;
}

.video-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1A1A2E;
  margin-bottom: 12rpx;
  line-height: 1.4;
}

.video-stats {
  display: flex;
  gap: 32rpx;
  font-size: 24rpx;
  color: #9CA3AF;
}
</style>
