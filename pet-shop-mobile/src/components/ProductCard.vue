<template>
  <view class="product-card" @click="goDetail">
    <image :src="imgSrc" mode="aspectFill" class="product-img"
           @error="onImgError" />
    <view class="product-info">
      <view class="product-name text-ellipsis-2">
        {{ item.name }}
        <text class="type-tag" :class="item.productType === 1 ? 'tag-pet' : 'tag-goods'">
          {{ item.productType === 1 ? '宠物' : '周边' }}
        </text>
      </view>
      <view class="product-desc text-ellipsis" v-if="item.description">{{ item.description }}</view>
      <view class="product-bottom">
        <text class="price-tag">{{ item.price }}</text>
        <view class="bottom-right">
          <text class="stock" v-if="item.productType === 2">库存 {{ item.stock }}</text>
          <text class="sales" v-if="item.sales !== undefined">已售 {{ item.sales }}</text>
        </view>
      </view>
      <button class="cart-btn" hover-class="cart-btn-hover" @click.stop="handleAddToCart">
        🛒 加入购物车
      </button>
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
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
  margin-bottom: 20rpx;
}

.product-img {
  width: 100%;
  height: 340rpx;
  background: #f5f5f5;
}

.product-info {
  padding: 20rpx;
}

.product-name {
  font-size: 30rpx;
  font-weight: 600;
  color: #1A1A2E;
  margin-bottom: 8rpx;
  line-height: 1.4;
}

.type-tag {
  font-size: 20rpx;
  padding: 2rpx 10rpx;
  border-radius: 6rpx;
  vertical-align: middle;
  margin-left: 8rpx;
}

.tag-pet { background: #FFF3E0; color: #F59E0B; }
.tag-goods { background: #E3F2FD; color: #3B82F6; }

.product-desc {
  font-size: 24rpx;
  color: #9CA3AF;
  margin-bottom: 12rpx;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.bottom-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4rpx;
}

.sales {
  font-size: 22rpx;
  color: #9CA3AF;
}

.stock {
  font-size: 20rpx;
  color: #9CA3AF;
}

.cart-btn {
  width: 100%;
  background: linear-gradient(135deg, #FF6B35, #FF8F5E);
  color: #fff;
  border: none;
  padding: 16rpx 0;
  border-radius: 32rpx;
  font-size: 26rpx;
  font-weight: 600;
  text-align: center;
}

.cart-btn-hover {
  opacity: 0.85;
}
</style>
