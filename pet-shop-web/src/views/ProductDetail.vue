<template>
  <div class="page-shell detail-page" v-loading="loading">
    <section v-if="product" class="hero-card detail-hero">
      <div class="detail-gallery">
        <img :src="product.mainImage || '/vite.svg'" :alt="product.name" class="main-image" />
        <div class="thumb-row">
          <img v-for="item in detailImages" :key="item" :src="item" class="thumb-image" />
        </div>
      </div>

      <div class="detail-info">
        <div class="status-pill">Product detail</div>
        <h1>{{ product.name }}</h1>
        <p class="muted detail-desc">{{ product.description || product.detail || '这里展示商品的详细介绍、评价和购买入口。' }}</p>

        <div class="detail-tags">
          <el-tag :type="product.productType === 1 ? 'warning' : 'info'">
            {{ product.productType === 1 ? '宠物' : '宠物周边' }}
          </el-tag>
          <el-tag type="success">销量 {{ product.sales || 0 }}</el-tag>
          <el-tag effect="plain">库存 {{ product.stock ?? '充足' }}</el-tag>
        </div>

        <div class="price-box">
          <div class="price">¥{{ product.price }}</div>
          <div class="muted">商品编号：{{ product.id }}</div>
        </div>

        <div class="qty-row">
          <span class="muted">购买数量</span>
          <el-input-number v-model="quantity" :min="1" :max="product.productType === 1 ? 1 : (product.stock || 99)" />
        </div>

        <div class="action-row">
          <el-button type="primary" size="large" @click="addToCart">加入购物车</el-button>
          <el-button size="large" @click="buyNow">立即购买</el-button>
          <el-button size="large" plain @click="$router.push(`/review/${product.id}`)">去评价</el-button>
        </div>

        <div v-if="store" class="store-card soft-card">
          <div class="section-heading" style="margin-bottom: 10px;">
            <div>
              <h3 class="section-title" style="font-size: 18px;">所属商店</h3>
              <p class="section-subtitle">查看该商品所属店铺信息。</p>
            </div>
            <el-button text type="primary" @click="$router.push(`/store/${store.id}`)">进入商店</el-button>
          </div>
          <div class="store-summary">
            <strong>{{ store.name }}</strong>
            <span class="muted">{{ store.city }} {{ store.address }}</span>
            <span class="muted">{{ store.phone }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="page-section split-layout">
      <div class="panel-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">商品详情</h2>
            <p class="section-subtitle">支持图文介绍、视频展示和商品说明。</p>
          </div>
        </div>
        <div class="rich-detail" v-html="product?.detail || product?.description || '暂无详细内容'"></div>

        <div v-if="product?.videoUrl" class="video-preview">
          <video :src="product.videoUrl" controls class="detail-video" />
        </div>
      </div>

      <div class="panel-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">商品评价</h2>
            <p class="section-subtitle">用户真实使用反馈。</p>
          </div>
          <el-button text type="primary" @click="$router.push(`/review/${product.id}`)">写评价</el-button>
        </div>

        <el-empty v-if="reviews.length === 0" description="暂无评价" />
        <div class="review-list">
          <article v-for="item in reviews" :key="item.id" class="soft-card review-card">
            <div class="review-head">
              <strong>{{ item.nickname || `用户 ${item.userId}` }}</strong>
              <el-rate :model-value="item.rating" disabled show-score />
            </div>
            <p class="muted">{{ item.content }}</p>
          </article>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductDetailApi } from '@/api/product'
import { addToCartApi } from '@/api/cart'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { getStoreDetailApi } from '@/api/store'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const product = ref(null)
const reviews = ref([])
const store = ref(null)
const quantity = ref(1)
const loading = ref(false)

const detailImages = computed(() => {
  const images = [product.value?.mainImage].filter(Boolean)
  return images.length ? images : ['/vite.svg']
})

onMounted(async () => {
  loading.value = true
  try {
    const response = await getProductDetailApi(route.params.id)
    product.value = response.data

    const reviewRes = await request.get(`/review/product/${route.params.id}`, { params: { page: 1, size: 10 } })
    reviews.value = reviewRes.data?.records || []
    if (response.data?.storeId) {
      const storeRes = await getStoreDetailApi(response.data.storeId)
      store.value = storeRes.data
    }
  } catch (error) {
    ElMessage.error('加载商品详情失败')
  } finally {
    loading.value = false
  }
})

async function addToCart() {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }

  try {
    await addToCartApi(userStore.userInfo.userId, product.value.id, quantity.value)
    ElMessage.success('已加入购物车')
  } catch (error) {
    ElMessage.error(error.message || '添加失败')
  }
}

function buyNow() {
  addToCart()
  router.push('/cart')
}
</script>

<style scoped>
.detail-page {
  padding-bottom: 24px;
}

.detail-hero {
  padding: 24px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.detail-gallery {
  display: grid;
  gap: 14px;
}

.main-image {
  width: 100%;
  height: 460px;
  object-fit: cover;
  border-radius: 26px;
}

.thumb-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.thumb-image {
  width: 100%;
  height: 100px;
  object-fit: cover;
  border-radius: 18px;
}

.detail-info h1 {
  margin: 14px 0 12px;
  font-size: 34px;
}

.detail-desc {
  line-height: 1.9;
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.price-box {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-top: 20px;
  padding: 18px;
  border-radius: 20px;
  background: linear-gradient(135deg, rgba(255, 143, 107, 0.1), rgba(138, 108, 255, 0.1));
}

.price {
  color: var(--pet-primary);
  font-size: 34px;
  font-weight: 800;
}

.qty-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 20px;
}

.action-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 24px;
}

.rich-detail {
  color: var(--pet-text);
  line-height: 1.9;
}

.video-preview {
  margin-top: 20px;
}

.detail-video {
  width: 100%;
  max-height: 420px;
  border-radius: 20px;
  background: #000;
}

.review-list {
  display: grid;
  gap: 12px;
}

.review-card {
  display: grid;
  gap: 10px;
}

.review-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.store-summary {
  display: grid;
  gap: 8px;
}

@media (max-width: 1024px) {
  .detail-hero {
    grid-template-columns: 1fr;
  }
}
</style>
