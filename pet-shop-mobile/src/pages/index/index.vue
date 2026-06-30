<template>
  <view class="home-page">
    <!-- Banner 轮播 -->
    <swiper class="banner-swiper" :indicator-dots="true" :autoplay="true" :interval="4000" :duration="500" circular>
      <swiper-item v-for="(img, idx) in carouselImages" :key="idx">
        <image :src="img" mode="aspectFill" class="banner-img" />
      </swiper-item>
    </swiper>

    <!-- 欢迎文字 -->
    <view class="hero-text">
      <text class="hero-title">找到你最好的宠物伙伴</text>
      <text class="hero-desc">优质宠物 · 专业服务 · 周边好物 · 一站式宠物购物体验</text>
      <view class="hero-btns">
        <button class="btn-primary" @click="goProductList(1)">浏览宠物</button>
        <button class="btn-outline" @click="goProductList(2)">宠物周边</button>
      </view>
    </view>

    <!-- 分类导航 -->
    <view class="section">
      <view class="section-title">商品分类</view>
      <view class="category-grid">
        <view class="category-card" v-for="cat in petCategories" :key="cat.id"
              @click="goCategory(cat.id)">
          <text class="category-icon">{{ cat.icon }}</text>
          <text class="category-name">{{ cat.name }}</text>
        </view>
      </view>
    </view>

    <!-- 热门商品 -->
    <view class="section">
      <view class="section-title">热门推荐</view>
      <view class="product-grid">
        <ProductCard v-for="item in hotProducts" :key="item.id" :item="item" />
      </view>
      <view v-if="!loading && hotProducts.length === 0" class="empty-hint">
        <text>暂无热门商品</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHotProductsApi } from '@/api/product'
import ProductCard from '@/components/ProductCard.vue'

const hotProducts = ref([])
const loading = ref(false)

const carouselImages = [
  'https://picsum.photos/seed/pet1/750/400',
  'https://picsum.photos/seed/pet2/750/400',
  'https://picsum.photos/seed/pet3/750/400'
]

const petCategories = [
  { id: 1, icon: '🐕', name: '狗狗' },
  { id: 2, icon: '🐱', name: '猫咪' },
  { id: 3, icon: '🐹', name: '小宠' },
  { id: 4, icon: '🐟', name: '水族' },
  { id: 5, icon: '🐦', name: '鸟类' },
  { id: 6, icon: '🦴', name: '狗粮' },
  { id: 7, icon: '🐟', name: '猫粮' },
  { id: 8, icon: '🧸', name: '玩具' }
]

onMounted(async () => {
  loading.value = true
  try {
    hotProducts.value = (await getHotProductsApi(8)).data || []
  } catch (e) {
    hotProducts.value = []
  } finally {
    loading.value = false
  }
})

function goProductList(type) {
  uni.switchTab({ url: `/pages/product/list/list?type=${type}` })
}

function goCategory(categoryId) {
  uni.switchTab({ url: `/pages/product/list/list?categoryId=${categoryId}` })
}
</script>

<style scoped>
.home-page {
  padding-bottom: 40rpx;
}

.banner-swiper {
  width: 100%;
  height: 400rpx;
}

.banner-img {
  width: 100%;
  height: 100%;
}

.hero-text {
  text-align: center;
  padding: 40rpx 32rpx;
  background: linear-gradient(135deg, #FFF5ED 0%, #FFFFFF 100%);
}

.hero-title {
  font-size: 40rpx;
  font-weight: 800;
  color: #1A1A2E;
  display: block;
  margin-bottom: 12rpx;
}

.hero-desc {
  font-size: 28rpx;
  color: #6B7280;
  display: block;
  margin-bottom: 32rpx;
}

.hero-btns {
  display: flex;
  gap: 24rpx;
  justify-content: center;
}

.btn-primary {
  background: linear-gradient(135deg, #FF6B35, #FF8F5E);
  color: #fff;
  border: none;
  padding: 20rpx 48rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 600;
  box-shadow: 0 8rpx 28rpx rgba(255,107,53,0.3);
}

.btn-outline {
  background: #fff;
  color: #FF6B35;
  border: 2rpx solid #FF6B35;
  padding: 20rpx 48rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 600;
}

.section {
  padding: 40rpx 32rpx 0;
}

.section-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #1A1A2E;
  text-align: center;
  margin-bottom: 32rpx;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20rpx;
}

.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  padding: 28rpx 16rpx;
  background: #fff;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}

.category-icon {
  font-size: 56rpx;
}

.category-name {
  font-size: 24rpx;
  color: #6B7280;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.empty-hint {
  text-align: center;
  padding: 80rpx 0;
  color: #9CA3AF;
  font-size: 28rpx;
}
</style>
