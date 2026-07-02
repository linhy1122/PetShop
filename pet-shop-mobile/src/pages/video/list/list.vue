<template>
  <view class="video-list-page">
    <view class="video-grid" v-if="!loading">
      <VideoCard v-for="v in videos" :key="v.id" :video="v" />
      <EmptyState v-if="videos.length === 0" description="暂无视频" />
    </view>
    <view v-else class="loading-wrap"><text>加载中...</text></view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getVideoListApi } from '@/api/video'
import { videos as mockVideos } from '@/mock'
import VideoCard from '@/components/VideoCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const videos = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getVideoListApi({ size: 20, status: 1 })
    videos.value = res.data?.records || []
    if (!videos.value.length) videos.value = mockVideos
  } catch (e) {
    videos.value = mockVideos
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.video-list-page {
  padding: 24rpx;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.loading-wrap {
  display: flex;
  justify-content: center;
  padding: 200rpx 0;
  color: #999;
}
</style>
