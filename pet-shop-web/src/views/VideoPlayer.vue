<template>
  <div class="page-shell video-player-page" v-loading="loading">
    <section v-if="video" class="hero-card player-hero">
      <div class="player-main">
        <video :src="video.videoUrl" controls :poster="video.cover" class="player-video">
          你的浏览器不支持视频播放
        </video>
      </div>

      <div class="player-side">
        <div class="status-pill">Video detail</div>
        <h1>{{ video.title }}</h1>
        <p class="muted">{{ video.description || '视频播放、商品推荐和跳转购买入口。' }}</p>
        <div class="video-meta">
          <span>播放 {{ video.viewCount || 0 }}</span>
          <span>点赞 {{ video.likeCount || 0 }}</span>
        </div>
        <div class="action-row">
          <el-button :icon="ThumbUp" @click="handleLike">点赞</el-button>
          <el-button type="primary" @click="$router.push('/product/list')">去逛商品</el-button>
        </div>
      </div>
    </section>

    <section v-if="linkedProduct" class="page-section panel-card">
      <div class="section-heading">
        <div>
          <h2 class="section-title">推荐商品</h2>
          <p class="section-subtitle">视频内容关联的商品可直接跳转详情页。</p>
        </div>
      </div>

      <article class="linked-product" @click="$router.push(`/product/${linkedProduct.id}`)">
        <img :src="linkedProduct.mainImage || '/vite.svg'" class="linked-thumb" />
        <div>
          <strong>{{ linkedProduct.name }}</strong>
          <p class="muted">{{ linkedProduct.description }}</p>
          <div class="price">¥{{ linkedProduct.price }}</div>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getVideoDetailApi, likeVideoApi } from '@/api/video'
import { getProductDetailApi } from '@/api/product'

const route = useRoute()
const loading = ref(false)
const video = ref(null)
const linkedProduct = ref(null)

onMounted(async () => {
  loading.value = true
  try {
    const response = await getVideoDetailApi(route.params.id)
    video.value = response.data
    if (response.data?.productId) {
      const productRes = await getProductDetailApi(response.data.productId)
      linkedProduct.value = productRes.data
    }
  } catch (error) {
    ElMessage.error('加载视频失败')
  } finally {
    loading.value = false
  }
})

async function handleLike() {
  try {
    await likeVideoApi(video.value.id)
    video.value.likeCount = Number(video.value.likeCount || 0) + 1
    ElMessage.success('已点赞')
  } catch (error) {
    ElMessage.error(error.message || '点赞失败')
  }
}
</script>

<style scoped>
.player-hero {
  padding: 24px;
  display: grid;
  grid-template-columns: 1.25fr 0.75fr;
  gap: 24px;
}

.player-video {
  width: 100%;
  max-height: 560px;
  border-radius: 24px;
  background: #000;
}

.player-side {
  display: grid;
  align-content: start;
  gap: 14px;
}

.player-side h1 {
  font-size: 30px;
}

.video-meta {
  display: flex;
  gap: 14px;
  color: var(--pet-text-soft);
}

.linked-product {
  display: grid;
  grid-template-columns: 120px 1fr;
  gap: 16px;
  align-items: center;
  cursor: pointer;
}

.linked-thumb {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 20px;
}

.price {
  margin-top: 10px;
  color: var(--pet-primary);
  font-size: 22px;
  font-weight: 800;
}

@media (max-width: 1024px) {
  .player-hero {
    grid-template-columns: 1fr;
  }
}
</style>
