<template>
  <div class="video-player-page">
    <div class="container" v-loading="loading">
      <div v-if="video">
        <div class="player">
          <!-- 简易视频播放器（实际项目可替换为 video.js 等专业播放器） -->
          <video :src="video.videoUrl" controls :poster="video.cover"
                 style="width: 100%; max-height: 500px; border-radius: 12px; background: #000;">
            您的浏览器不支持视频播放
          </video>
        </div>
        <h2>{{ video.title }}</h2>
        <div class="video-meta">
          <span>👁 {{ video.viewCount }} 次播放</span>
          <el-button :icon="Like" size="small" @click="handleLike">点赞 {{ video.likeCount }}</el-button>
        </div>
        <p class="description">{{ video.description }}</p>
        <!-- 关联商品 -->
        <div v-if="linkedProduct" class="linked-product">
          <h4>视频相关商品</h4>
          <el-card class="product-link" @click="$router.push(`/product/${linkedProduct.id}`)">
            <img :src="linkedProduct.mainImage || '/vite.svg'" />
            <div>
              <h5>{{ linkedProduct.name }}</h5>
              <span class="price">¥{{ linkedProduct.price }}</span>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getVideoDetailApi, likeVideoApi } from '@/api/video'
import { getProductDetailApi } from '@/api/product'
import { ElMessage } from 'element-plus'

const route = useRoute()
const video = ref(null)
const linkedProduct = ref(null)
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    video.value = (await getVideoDetailApi(route.params.id)).data
    if (video.value?.productId) {
      try {
        linkedProduct.value = (await getProductDetailApi(video.value.productId)).data
      } catch (e) { /* ignore */ }
    }
  } finally { loading.value = false }
})

async function handleLike() {
  try {
    await likeVideoApi(video.value.id)
    video.value.likeCount++
    ElMessage.success('已点赞')
  } catch (e) { ElMessage.error(e.message) }
}
</script>

<style scoped>
.container { max-width: 900px; margin: 0 auto; padding: 20px; }
h2 { margin: 20px 0 12px; }
.video-meta { display: flex; align-items: center; gap: 20px; margin-bottom: 16px; color: #666; }
.description { color: #666; line-height: 1.8; }
.linked-product { margin-top: 30px; }
.product-link { display: flex; gap: 16px; align-items: center; cursor: pointer; margin-top: 12px; }
.product-link img { width: 80px; height: 80px; object-fit: cover; border-radius: 8px; }
.price { color: #f56c6c; font-weight: bold; }
</style>
