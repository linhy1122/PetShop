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
import VideoCard from '@/components/VideoCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const videos = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getVideoListApi({ size: 20, status: 1 })
    const list = res.data?.records || []
    videos.value = list
    if (list.length) {
      list.forEach(v => {
        if (v.cover) v.cover = uni.fixImgUrl(v.cover)
        if (v.videoUrl) v.videoUrl = uni.fixImgUrl(v.videoUrl)
      })
    }
  } catch (e) {
    videos.value = []
    uni.showToast({ title: '加载视频失败', icon: 'none' })
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
