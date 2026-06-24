<template>
  <div class="video-list-page">
    <div class="container">
      <h2>宠物视频</h2>
      <div class="video-grid" v-loading="loading">
        <el-empty v-if="!loading && videos.length === 0" description="暂无视频" />
        <el-card v-for="v in videos" :key="v.id" class="video-card"
                 shadow="hover" @click="$router.push(`/video/${v.id}`)">
          <div class="video-cover">
            <img :src="v.cover || '/vite.svg'" :alt="v.title" />
            <div class="play-icon">▶</div>
            <span class="duration">{{ formatDuration(v.duration) }}</span>
          </div>
          <div class="video-info">
            <h4>{{ v.title }}</h4>
            <div class="video-stats">
              <span>👁 {{ v.viewCount }}</span>
              <span>👍 {{ v.likeCount }}</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getVideoListApi } from '@/api/video'

const videos = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getVideoListApi({ size: 20, status: 1 })
    videos.value = res.data?.records || []
  } finally { loading.value = false }
})

function formatDuration(sec) {
  if (!sec) return '00:00'
  const m = Math.floor(sec / 60)
  const s = sec % 60
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 20px; }
h2 { margin-bottom: 20px; }
.video-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; }
.video-card { cursor: pointer; }
.video-cover { position: relative; height: 180px; overflow: hidden; border-radius: 8px; }
.video-cover img { width: 100%; height: 100%; object-fit: cover; }
.play-icon {
  position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);
  width: 48px; height: 48px; background: rgba(0,0,0,0.6); border-radius: 50%;
  display: flex; align-items: center; justify-content: center; color: #fff; font-size: 20px;
}
.duration {
  position: absolute; bottom: 8px; right: 8px;
  background: rgba(0,0,0,0.7); color: #fff; padding: 2px 8px; border-radius: 4px; font-size: 12px;
}
.video-info { padding: 12px 0; }
.video-info h4 { font-size: 15px; margin-bottom: 8px; }
.video-stats { display: flex; gap: 16px; color: #999; font-size: 13px; }
</style>
