<template>
  <view class="product-card" @click="goDetail">
    <!-- 图片区域 + 类型角标 -->
    <view class="img-wrap">
      <image :src="imgSrc" mode="aspectFill" class="product-img"
             @error="onImgError" />
      <view class="img-badge" :class="item.productType === 1 ? 'badge-pet' : 'badge-goods'">
        {{ item.productType === 1 ? '宠物' : '周边' }}
      </view>
    </view>

    <!-- 商品信息 -->
    <view class="product-info">
      <text class="product-name">{{ item.name }}</text>
      <view class="product-meta">
        <text class="price-tag">{{ item.price }}</text>
        <text class="sales-text" v-if="item.sales">已售{{ item.sales }}</text>
      </view>
      <view class="cart-btn" hover-class="cart-btn-hover" @click.stop="handleAddToCart">
        <text class="cart-text">🛒 加入购物车</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { addToCartApi } from '@/api/cart'

const props = defineProps({
  item: { type: Object, required: true }
})

const userStore = useUserStore()
const cartStore = useCartStore()

const FALLBACK_IMG = 'https://picsum.photos/seed/placeholder/200/200'
const imgSrc = ref(uni.fixImgUrl(props.item.mainImage) || FALLBACK_IMG)

function onImgError() {
  imgSrc.value = FALLBACK_IMG
}

function goDetail() {
  uni.navigateTo({ url: `/pages/product/detail/detail?id=${props.item.id}` })
}

async function handleAddToCart() {
  if (!userStore.isLoggedIn()) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 1000)
    return
  }
  try {
    await addToCartApi(userStore.userInfo.userId, props.item.id, 1)
    cartStore.fetchCart(userStore.userInfo.userId)
    uni.showToast({ title: '已加入购物车', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: e.message || '添加失败', icon: 'none' })
  }
}
</script>

<style scoped>
.product-card {
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.06);
}

/* ---- 图片区域（padding-bottom 实现 1:1 正方形，兼容微信小程序） ---- */
.img-wrap {
  position: relative;
  width: 100%;
  padding-bottom: 100%;
  overflow: hidden;
  background: #f5f5f5;
}

.product-img {
  position: absolute;
  top: 0; left: 0;
  width: 100%;
  height: 100%;
}

.img-badge {
  position: absolute;
  top: 10rpx;
  left: 10rpx;
  font-size: 20rpx;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
}

.badge-pet { background: #FF9800; color: #fff; }
.badge-goods { background: #2196F3; color: #fff; }

/* ---- 信息区域 ---- */
.product-info {
  padding: 16rpx 16rpx 10rpx;
}

.product-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #1A1A2E;
  line-height: 1.4;
  display: block;
  margin-bottom: 10rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-meta {
  display: flex;
  align-items: baseline;
  gap: 10rpx;
}

.price-tag {
  font-size: 34rpx;
  font-weight: 800;
  color: #FF6B35;
  line-height: 1;
}

.price-tag::before {
  content: '¥';
  font-size: 22rpx;
  font-weight: 600;
  margin-right: 2rpx;
}

.sales-text {
  font-size: 22rpx;
  color: #B0B0B0;
}

/* ---- 加购按钮 ---- */
.cart-btn {
  margin-top: 12rpx;
  background: linear-gradient(135deg, #FF6B35, #FF8F5E);
  border-radius: 28rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 10rpx rgba(255, 107, 53, 0.25);
}

.cart-text {
  font-size: 24rpx;
  color: #fff;
  font-weight: 500;
}

.cart-btn-hover {
  opacity: 0.8;
}
</style>
