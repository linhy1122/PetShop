<template>
  <view class="detail-page">
    <view v-if="loading" class="loading-wrap">
      <text>加载中...</text>
    </view>

    <template v-if="product">
      <!-- 图片轮播 -->
      <swiper class="image-swiper" :indicator-dots="allImages.length > 1" :autoplay="false" circular
              v-if="allImages.length">
        <swiper-item v-for="(url, i) in allImages" :key="i">
          <image :src="url" mode="aspectFill" class="detail-img"
                 @click="previewImages(i)" />
        </swiper-item>
      </swiper>
      <image v-else :src="product.mainImage || 'https://picsum.photos/seed/pd/400/300'" mode="aspectFill" class="detail-img single" />

      <!-- 商品信息 -->
      <view class="info-section">
        <view class="info-header">
          <text class="info-name">{{ product.name }}</text>
          <text class="info-type" :class="product.productType === 1 ? 'type-pet' : 'type-goods'">
            {{ product.productType === 1 ? '宠物' : '周边' }}
          </text>
        </view>
        <view class="info-price-row">
          <text class="info-price price-tag">{{ product.price }}</text>
          <text class="info-sales" v-if="product.sales">已售 {{ product.sales }}</text>
        </view>

        <!-- 宠物专属信息 -->
        <view class="info-meta" v-if="product.productType === 1">
          <text>品种：{{ product.breed || '未知' }}</text>
          <text>年龄：{{ product.age || '未知' }}</text>
          <text>性别：{{ ['未知', '公', '母'][product.gender] || '未知' }}</text>
        </view>
        <view class="info-meta" v-else>
          <text>库存：{{ product.stock }} 件</text>
        </view>
      </view>

      <!-- 数量 + 操作 -->
      <view class="action-section">
        <view class="quantity-row">
          <text>数量</text>
          <view class="qty-ctrl">
            <view class="qty-btn" @click="changeQty(-1)">-</view>
            <text class="qty-value">{{ quantity }}</text>
            <view class="qty-btn" @click="changeQty(1)">+</view>
          </view>
        </view>
        <view class="action-btns">
          <button class="cart-btn" @click="addToCart">加入购物车</button>
          <button class="buy-btn" @click="buyNow">立即购买</button>
        </view>
      </view>

      <!-- 商品详情 -->
      <view class="detail-section">
        <view class="section-title">商品详情</view>
        <rich-text v-if="product.detail" :nodes="product.detail" class="detail-content" />
        <text v-else class="detail-desc">{{ product.description || '暂无详情' }}</text>
      </view>

      <!-- 评价 -->
      <view class="reviews-section">
        <view class="section-title">商品评价 ({{ reviews.length }})</view>
        <EmptyState v-if="reviews.length === 0" description="暂无评价" />
        <view v-for="r in reviews" :key="r.id" class="review-item">
          <view class="review-header">
            <view class="review-stars">
              <text v-for="i in 5" :key="i" :style="{ color: i <= r.rating ? '#F59E0B' : '#ddd' }">★</text>
            </view>
            <text class="review-time">{{ r.createTime }}</text>
          </view>
          <text class="review-content">{{ r.content }}</text>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getProductDetailApi } from '@/api/product'
import { addToCartApi } from '@/api/cart'
import { allProducts as mockProducts, reviews as mockReviews } from '@/mock'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import EmptyState from '@/components/EmptyState.vue'

const userStore = useUserStore()
const product = ref(null)
const reviews = ref([])
const quantity = ref(1)
const loading = ref(false)
let productId = ''

const allImages = computed(() => {
  if (!product.value) return []
  const list = []
  if (product.value.mainImage) list.push(product.value.mainImage)
  try {
    const extras = JSON.parse(product.value.images || '[]')
    if (Array.isArray(extras)) {
      extras.filter(u => u).forEach(u => { if (!list.includes(u)) list.push(u) })
    }
  } catch { /* ignore */ }
  return list
})

onLoad((options) => {
  productId = options.id
  fetchData()
})

async function fetchData() {
  loading.value = true
  try {
    product.value = (await getProductDetailApi(productId)).data
    const res = await request.get(`/review/product/${productId}`, { params: { size: 10 } })
    reviews.value = res.data?.records || []
    if (!reviews.value.length) reviews.value = mockReviews
  } catch (e) {
    product.value = mockProducts.find(p => p.id == productId) || mockProducts[0]
    reviews.value = mockReviews
  } finally {
    loading.value = false
  }
}

function previewImages(idx) {
  uni.previewImage({
    current: idx,
    urls: allImages.value
  })
}

function changeQty(delta) {
  quantity.value += delta
  if (quantity.value < 1) quantity.value = 1
  const max = product.value.productType === 1 ? 1 : product.value.stock
  if (quantity.value > max) quantity.value = max
}

async function addToCart() {
  if (!userStore.isLoggedIn()) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return
  }
  try {
    await addToCartApi(userStore.userInfo.userId, product.value.id, quantity.value)
    uni.showToast({ title: '已加入购物车', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: e.message || '添加失败', icon: 'none' })
  }
}

function buyNow() {
  addToCart()
  setTimeout(() => uni.switchTab({ url: '/pages/cart/cart' }), 500)
}
</script>

<style scoped>
.loading-wrap {
  display: flex;
  justify-content: center;
  padding: 200rpx 0;
  color: #999;
}

.image-swiper {
  width: 100%;
  height: 560rpx;
}

.detail-img {
  width: 100%;
  height: 560rpx;
  background: #f5f5f5;
}

.detail-img.single {
  display: block;
}

.info-section {
  background: #fff;
  padding: 32rpx;
  margin: 20rpx 0;
}

.info-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.info-name {
  font-size: 36rpx;
  font-weight: 700;
  flex: 1;
}

.info-type {
  font-size: 22rpx;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
}

.type-pet { background: #FFF3E0; color: #F59E0B; }
.type-goods { background: #E3F2FD; color: #3B82F6; }

.info-price-row {
  display: flex;
  align-items: baseline;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.info-price {
  font-size: 52rpx;
}

.info-sales {
  font-size: 24rpx;
  color: #999;
}

.info-meta text {
  display: block;
  font-size: 26rpx;
  color: #666;
  line-height: 2;
}

.action-section {
  background: #fff;
  padding: 32rpx;
  margin-bottom: 20rpx;
}

.quantity-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
  font-size: 28rpx;
}

.qty-ctrl {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.qty-btn {
  width: 56rpx;
  height: 56rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
}

.qty-value {
  font-size: 32rpx;
  font-weight: 600;
  min-width: 56rpx;
  text-align: center;
}

.action-btns {
  display: flex;
  gap: 20rpx;
}

.cart-btn {
  flex: 1;
  background: #fff;
  color: #FF6B35;
  border: 2rpx solid #FF6B35;
  padding: 24rpx;
  border-radius: 40rpx;
  font-size: 30rpx;
}

.buy-btn {
  flex: 1;
  background: linear-gradient(135deg, #FF6B35, #FF8F5E);
  color: #fff;
  border: none;
  padding: 24rpx;
  border-radius: 40rpx;
  font-size: 30rpx;
}

.detail-section {
  background: #fff;
  padding: 32rpx;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 700;
  margin-bottom: 20rpx;
}

.detail-content {
  font-size: 28rpx;
  line-height: 1.8;
}

.detail-desc {
  font-size: 28rpx;
  color: #666;
  line-height: 1.8;
}

.reviews-section {
  background: #fff;
  padding: 32rpx;
  margin-bottom: 40rpx;
}

.review-item {
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.review-stars {
  font-size: 28rpx;
}

.review-time {
  font-size: 24rpx;
  color: #999;
}

.review-content {
  font-size: 28rpx;
  color: #333;
  line-height: 1.6;
}
</style>
