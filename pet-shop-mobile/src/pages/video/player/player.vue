<template>
  <view class="video-player-page">
    <view v-if="loading" class="loading-wrap"><text>加载中...</text></view>

    <template v-if="video">
      <!-- 视频播放器 -->
      <video :src="video.videoUrl" :poster="video.cover" controls
             style="width: 100%; height: 450rpx; background: #000;"
             object-fit="contain" />

      <view class="video-info">
        <text class="video-title">{{ video.title }}</text>
        <view class="video-meta">
          <text>👁 {{ video.viewCount || 0 }} 次播放</text>
          <text class="like-btn" @click="handleLike">👍 {{ video.likeCount || 0 }}</text>
        </view>
        <text class="video-desc" v-if="video.description">{{ video.description }}</text>
      </view>

      <!-- 关联商品 -->
      <view class="linked-section" v-if="linkedProduct">
        <text class="section-title">视频相关商品</text>
        <view class="linked-card" @click="goProduct">
          <image :src="linkedProduct.mainImage || 'https://picsum.photos/seed/vp/100/100'" mode="aspectFill" class="linked-img" />
          <view class="linked-info">
            <text class="linked-name">{{ linkedProduct.name }}</text>
            <text class="price-tag">{{ linkedProduct.price }}</text>
          </view>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getVideoDetailApi, likeVideoApi } from '@/api/video'
import { getProductDetailApi } from '@/api/product'
import { videos as mockVideos, allProducts as mockProducts } from '@/mock'

const video = ref(null)
const linkedProduct = ref(null)
const loading = ref(false)

onLoad((options) => {
  fetchData(options.id)
})

async function fetchData(id) {
  loading.value = true
  try {
    video.value = (await getVideoDetailApi(id)).data
    if (video.value?.productId) {
      try { linkedProduct.value = (await getProductDetailApi(video.value.productId)).data } catch { /* ignore */ }
    }
  } catch (e) {
    video.value = mockVideos.find(v => v.id == id) || mockVideos[0]
    if (video.value) linkedProduct.value = mockProducts[0]
  } finally { loading.value = false }
}

async function handleLike() {
  try {
    await likeVideoApi(video.value.id)
    video.value.likeCount++
    uni.showToast({ title: '已点赞', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: e.message, icon: 'none' })
  }
}

function goProduct() {
  if (linkedProduct.value) {
    uni.navigateTo({ url: `/pages/product/detail/detail?id=${linkedProduct.value.id}` })
  }
}
</script>

<style scoped>
.loading-wrap {
  display: flex;
  justify-content: center;
  padding: 200rpx 0;
  color: #999;
}

.video-info {
  padding: 32rpx;
  background: #fff;
  margin-bottom: 20rpx;
}

.video-title {
  font-size: 36rpx;
  font-weight: 700;
  display: block;
  margin-bottom: 20rpx;
}

.video-meta {
  display: flex;
  gap: 32rpx;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 20rpx;
}

.like-btn {
  color: #3B82F6;
}

.video-desc {
  font-size: 28rpx;
  color: #666;
  line-height: 1.8;
}

.linked-section {
  padding: 32rpx;
  background: #fff;
}

.section-title {
  font-size: 30rpx;
  font-weight: 700;
  margin-bottom: 20rpx;
}

.linked-card {
  display: flex;
  gap: 20rpx;
  align-items: center;
  background: #f9f9f9;
  padding: 20rpx;
  border-radius: 16rpx;
}

.linked-img {
  width: 140rpx;
  height: 140rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
}

.linked-name {
  font-size: 28rpx;
  font-weight: 600;
  display: block;
  margin-bottom: 12rpx;
}
</style>
