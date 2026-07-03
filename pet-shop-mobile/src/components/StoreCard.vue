<template>
  <view class="store-card" @click="$emit('select')">
    <image :src="storeImg" mode="aspectFill" class="store-img"
           @error="onImgError" />
    <view class="store-info">
      <view class="store-name">{{ store.name }}</view>
      <view class="store-rating">
        ⭐ {{ store.rating || '5.0' }}
        <text class="store-distance" v-if="distance"> · {{ distance }}km</text>
      </view>
      <view class="store-address text-ellipsis">
        📍 {{ store.province }}{{ store.city }}{{ store.district }} {{ store.address }}
      </view>
      <view class="store-hours">🕐 {{ store.businessHours || '09:00-21:00' }}</view>
      <view class="store-phone" v-if="store.phone">📞 {{ store.phone }}</view>
      <view class="store-action" @click.stop="goDetail">
        <text class="action-text">进店看看 ›</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  store: { type: Object, required: true },
  distance: { type: Number, default: null }
})

defineEmits(['select'])

const FALLBACK_IMG = 'https://picsum.photos/seed/store-fb/200/200'
const storeImg = ref(uni.fixImgUrl(props.store.image) || FALLBACK_IMG)

function onImgError() {
  storeImg.value = FALLBACK_IMG
}

function goDetail() {
  uni.navigateTo({ url: `/pages/store/detail/detail?id=${props.store.id}` })
}
</script>

<style scoped>
.store-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
  display: flex;
  gap: 20rpx;
}

.store-img {
  width: 180rpx;
  height: 180rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
  background: #f5f5f5;
}

.store-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  overflow: hidden;
}

.store-name {
  font-size: 32rpx;
  font-weight: 700;
  color: #1A1A2E;
}

.store-rating {
  font-size: 26rpx;
  color: #F59E0B;
}

.store-distance {
  font-size: 24rpx;
  color: #FF6B35;
  font-weight: 600;
}

.store-address, .store-hours, .store-phone {
  font-size: 24rpx;
  color: #6B7280;
}

.store-action {
  margin-top: 12rpx;
  align-self: flex-start;
  background: linear-gradient(135deg, #FF6B35, #FF8F5E);
  padding: 12rpx 28rpx;
  border-radius: 24rpx;
}

.action-text {
  font-size: 24rpx;
  color: #fff;
  font-weight: 500;
}
</style>
