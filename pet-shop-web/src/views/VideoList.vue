<template>
  <div class="page-shell video-page">
    <section class="hero-card video-hero">
      <div>
        <div class="status-pill">Video inspiration</div>
        <h1 class="hero-title" style="margin-top: 12px;">宠物视频</h1>
        <p class="hero-desc">搜索视频、浏览热点内容、跳转商品详情，打造“看视频—买商品”闭环。</p>
      </div>
      <div class="video-search">
        <el-input v-model="keyword" placeholder="搜索视频标题" size="large" clearable @change="fetchVideos">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" size="large" @click="fetchVideos">搜索</el-button>
      </div>
    </section>

    <section class="page-section">
      <div class="page-grid-3" v-loading="loading">
        <article v-for="video in videos" :key="video.id" class="pet-card video-card" @click="$router.push(`/video/${video.id}`)">
          <div class="video-cover">
            <img :src="video.cover || '/vite.svg'" :alt="video.title" />
            <div class="play-icon">▶</div>
          </div>
          <div class="video-body">
            <div class="video-top">
              <h3>{{ video.title }}</h3>
              <span class="muted">{{ video.viewCount || 0 }} 播放</span>
            </div>
            <p class="muted">{{ video.description || '宠物日常、护理技巧和商品种草。' }}</p>
          </div>
        </article>
      </div>

      <el-empty v-if="!loading && videos.length === 0" description="暂无视频" />
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getVideoListApi } from '@/api/video'
import { ElMessage } from 'element-plus'

const videos = ref([])
const loading = ref(false)
const keyword = ref('')

onMounted(fetchVideos)

async function fetchVideos() {
  loading.value = true
  try {
    const response = await getVideoListApi({ size: 20 })
    const list = response.data?.records || []
    const key = keyword.value.trim().toLowerCase()
    videos.value = key
      ? list.filter((item) => String(item.title || '').toLowerCase().includes(key))
      : list
  } catch (error) {
    videos.value = []
    ElMessage.warning('加载视频失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.video-hero {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: end;
}

.video-search {
  display: flex;
  gap: 12px;
  min-width: 420px;
}

.video-card {
  overflow: hidden;
  cursor: pointer;
}

.video-cover {
  position: relative;
  height: 210px;
}

.video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.play-icon {
  position: absolute;
  inset: 50% auto auto 50%;
  transform: translate(-50%, -50%);
  width: 58px;
  height: 58px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 18px;
}

.video-body {
  padding: 16px;
}

.video-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: start;
  margin-bottom: 10px;
}

@media (max-width: 1024px) {
  .video-hero {
    flex-direction: column;
    align-items: start;
  }

  .video-search {
    width: 100%;
    min-width: 0;
    flex-direction: column;
  }
}
</style>
