<template>
  <div class="page-shell home-page">
    <section class="hero-card hero-banner">
      <div class="hero-copy">
        <div class="status-pill">PetShop · Smart Pet Mall</div>
        <h1 class="hero-title">让宠物购物更温暖、更清晰、更有层次</h1>
        <p class="hero-desc">
          浏览商品、找到合适店铺、观看宠物视频、咨询 AI 助手，所有入口都在这里。我们把首页做成一张“宠物生活总览卡片”。
        </p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="$router.push('/product/list')">去逛商品</el-button>
          <el-button size="large" @click="$router.push('/store/list')">查看商店</el-button>
          <el-button size="large" @click="$router.push('/ai/chat')">打开 AI 助手</el-button>
        </div>
      </div>

      <div class="hero-meta">
        <el-carousel height="280px" indicator-position="outside" class="promo-carousel">
          <el-carousel-item v-for="slide in slides" :key="slide.title">
            <div class="promo-card" :style="{ background: slide.background }">
              <div class="promo-tag">{{ slide.tag }}</div>
              <h3>{{ slide.title }}</h3>
              <p>{{ slide.desc }}</p>
              <el-button round @click="$router.push(slide.link)">{{ slide.cta }}</el-button>
            </div>
          </el-carousel-item>
        </el-carousel>
        <div class="hero-meta-card">
          <div class="hero-meta-label">今日推荐</div>
          <div class="hero-meta-value">商品、商店、视频、AI 四个入口一键直达</div>
        </div>
      </div>
    </section>

    <section class="page-section">
      <div class="section-heading">
        <div>
          <h2 class="section-title">快捷入口</h2>
          <p class="section-subtitle">面向普通用户最常用的 6 个功能模块。</p>
        </div>
      </div>

      <div class="page-grid-3">
        <article v-for="item in quickEntries" :key="item.title" class="soft-card quick-card" @click="$router.push(item.link)">
          <div class="quick-icon">{{ item.icon }}</div>
          <h3>{{ item.title }}</h3>
          <p class="muted">{{ item.desc }}</p>
        </article>
      </div>
    </section>

    <section class="page-section">
      <div class="section-heading">
        <div>
          <h2 class="section-title">热门商品</h2>
          <p class="section-subtitle">销量优先展示，让用户更快找到人气好物。</p>
        </div>
        <el-button text type="primary" @click="$router.push('/product/list')">查看全部</el-button>
      </div>

      <div class="page-grid-4">
        <article v-for="product in hotProducts" :key="product.id" class="pet-card product-card" @click="$router.push(`/product/${product.id}`)">
          <img :src="product.mainImage || '/vite.svg'" :alt="product.name" class="cover-image product-cover" />
          <div class="product-body">
            <div class="product-head">
              <h3>{{ product.name }}</h3>
              <el-tag size="small" type="warning">销量 {{ product.sales || 0 }}</el-tag>
            </div>
            <p class="muted">{{ product.description || '精选宠物好物，支持快速下单。' }}</p>
            <div class="product-foot">
              <span class="price">¥{{ product.price }}</span>
              <span class="muted">{{ product.stock ? `库存 ${product.stock}` : '热销中' }}</span>
            </div>
          </div>
        </article>
      </div>
    </section>

    <section class="page-section split-layout">
      <div class="panel-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">热门商店</h2>
            <p class="section-subtitle">高评分店铺优先展示。</p>
          </div>
        </div>
        <div class="list-compact">
          <article v-for="store in hotStores" :key="store.id" class="soft-card list-item" @click="$router.push(`/store/${store.id}`)">
            <img :src="store.image || '/vite.svg'" class="list-thumb" />
            <div class="list-content">
              <div class="list-top">
                <strong>{{ store.name }}</strong>
                <el-tag size="small" type="success">{{ store.rating || '4.8' }}</el-tag>
              </div>
              <p class="muted">{{ store.city }} {{ store.address }}</p>
            </div>
          </article>
        </div>
      </div>

      <div class="panel-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">视频推荐</h2>
            <p class="section-subtitle">边看边买，提升种草转化。</p>
          </div>
        </div>
        <div class="list-compact">
          <article v-for="video in hotVideos" :key="video.id" class="soft-card list-item" @click="$router.push(`/video/${video.id}`)">
            <img :src="video.cover || '/vite.svg'" class="list-thumb" />
            <div class="list-content">
              <div class="list-top">
                <strong>{{ video.title }}</strong>
                <span class="muted">{{ video.viewCount || 0 }} 播放</span>
              </div>
              <p class="muted">{{ video.description || '宠物日常、喂养技巧、用品种草。' }}</p>
            </div>
          </article>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getHotProductsApi } from '@/api/product'
import { getStoreListApi } from '@/api/store'
import { getVideoListApi } from '@/api/video'

const hotProducts = ref([])
const hotStores = ref([])
const hotVideos = ref([])

const slides = [
  {
    tag: '今日上新',
    title: '热门商品推荐',
    desc: '让高销量商品更容易被发现。',
    link: '/product/list',
    cta: '去看看',
    background: 'linear-gradient(135deg, #ffcfbf 0%, #fff5f0 100%)'
  },
  {
    tag: '商店探索',
    title: '附近优质商店',
    desc: '评分、地址、电话、营业时间一目了然。',
    link: '/store/list',
    cta: '找商店',
    background: 'linear-gradient(135deg, #d8d0ff 0%, #f7f4ff 100%)'
  },
  {
    tag: '智能陪伴',
    title: 'AI 宠物助手',
    desc: '宠物知识、商品推荐、售后咨询一起搞定。',
    link: '/ai/chat',
    cta: '开始聊天',
    background: 'linear-gradient(135deg, #d1f4ef 0%, #f4fffd 100%)'
  }
]

const quickEntries = [
  { icon: '🐶', title: '商品商城', desc: '查看热门、分类和搜索结果。', link: '/product/list' },
  { icon: '🏪', title: '商店列表', desc: '按城市和关键词筛选商店。', link: '/store/list' },
  { icon: '🛒', title: '购物车', desc: '管理购物车和结算流程。', link: '/cart' },
  { icon: '📦', title: '我的订单', desc: '查看订单状态和物流。', link: '/order/list' },
  { icon: '🎬', title: '视频列表', desc: '浏览宠物视频与商品推荐。', link: '/video/list' },
  { icon: '🤖', title: 'AI 助手', desc: '宠物知识问答与售后咨询。', link: '/ai/chat' },
]

onMounted(async () => {
  try {
    const [productRes, storeRes, videoRes] = await Promise.all([
      getHotProductsApi(8),
      getStoreListApi({ size: 6 }),
      getVideoListApi({ size: 6 })
    ])
    hotProducts.value = productRes.data || []
    hotStores.value = storeRes.data?.records || []
    hotVideos.value = videoRes.data?.records || []
  } catch (error) {
    ElMessage.warning('首页推荐加载失败，已展示基础布局')
  }
})
</script>

<style scoped>
.home-page {
  padding-bottom: 24px;
}

.promo-carousel {
  border-radius: 24px;
  overflow: hidden;
}

.promo-card {
  height: 100%;
  padding: 26px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 12px;
}

.promo-card h3 {
  font-size: 24px;
}

.promo-card p {
  color: var(--pet-text-soft);
  line-height: 1.8;
}

.promo-tag,
.quick-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: fit-content;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.78);
  font-weight: 700;
}

.quick-card {
  cursor: pointer;
}

.quick-card h3 {
  margin: 12px 0 8px;
}

.product-card {
  overflow: hidden;
  cursor: pointer;
}

.product-cover {
  height: 210px;
}

.product-body {
  padding: 16px;
}

.product-head,
.product-foot,
.list-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: start;
}

.product-head h3 {
  font-size: 16px;
  line-height: 1.4;
}

.product-foot {
  margin-top: 12px;
  align-items: center;
}

.price {
  color: var(--pet-primary);
  font-size: 18px;
  font-weight: 800;
}

.list-item {
  display: grid;
  grid-template-columns: 78px 1fr;
  gap: 14px;
  cursor: pointer;
}

.list-thumb {
  width: 78px;
  height: 78px;
  object-fit: cover;
  border-radius: 18px;
}

.list-content {
  display: grid;
  gap: 8px;
}
</style>
