<template>
  <view class="home-page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input class="search-input" v-model="searchKeyword" placeholder="搜索宠物、周边..."
             confirm-type="search" @confirm="doSearch" />
      <view class="search-btn" @click="doSearch">🔍</view>
    </view>

    <!-- 用户问候 -->
    <view class="greeting">
      <text class="greeting-text" v-if="userStore.isLoggedIn()">
        Hi，{{ userStore.userInfo.nickname || userStore.userInfo.username }}
      </text>
      <text class="greeting-text" v-else>
        Hi，欢迎光临 <text class="login-link" @click="goLogin">登录</text>
      </text>
    </view>

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
      <view v-if="loading" class="loading-wrap"><text>加载中...</text></view>
      <view class="product-grid" v-else-if="hotProducts.length > 0">
        <ProductCard v-for="item in hotProducts" :key="item.id" :item="item" />
      </view>
      <EmptyState v-else description="暂无热门商品" />
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHotProductsApi } from '@/api/product'
import { useUserStore } from '@/stores/user'
import { useNavStore } from '@/stores/nav'
import ProductCard from '@/components/ProductCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const userStore = useUserStore()
const navStore = useNavStore()

const hotProducts = ref([])
const loading = ref(false)
const searchKeyword = ref('')

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
    const res = await getHotProductsApi(8)
    const products = res.data || []
    hotProducts.value = products
    uni.preloadProductImages(products)
  } catch (e) {
    uni.showToast({ title: '加载热门商品失败', icon: 'none' })
    hotProducts.value = []
  } finally {
    loading.value = false
  }
})

function doSearch() {
  const kw = searchKeyword.value.trim()
  if (!kw) return
  navStore.setProductFilter({ keyword: kw })
  uni.switchTab({ url: '/pages/product/list/list' })
}

function goProductList(type) {
  navStore.setProductFilter({ type })
  uni.switchTab({ url: '/pages/product/list/list' })
}

function goCategory(categoryId) {
  navStore.setProductFilter({ categoryId })
  uni.switchTab({ url: '/pages/product/list/list' })
}

function goLogin() {
  uni.navigateTo({ url: '/pages/login/login' })
}
</script>

<style scoped>
.home-page {
  padding-bottom: 40rpx;
}

.search-bar {
  display: flex;
  gap: 16rpx;
  padding: 20rpx 32rpx;
  background: #fff;
}

.search-input {
  flex: 1;
  height: 72rpx;
  background: #f5f5f5;
  border-radius: 36rpx;
  padding: 0 28rpx;
  font-size: 28rpx;
}

.search-btn {
  width: 72rpx;
  height: 72rpx;
  background: #f5f5f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
}

.greeting {
  padding: 16rpx 32rpx 8rpx;
  background: #fff;
}

.greeting-text {
  font-size: 28rpx;
  color: #6B7280;
}

.login-link {
  color: #FF6B35;
  font-weight: 600;
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

.loading-wrap {
  display: flex;
  justify-content: center;
  padding: 120rpx 0;
  color: #9CA3AF;
  font-size: 28rpx;
}
</style>
