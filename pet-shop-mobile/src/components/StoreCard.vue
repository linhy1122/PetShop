<template>
  <view class="store-card" @click="goDetail">
    <image :src="store.image || 'https://picsum.photos/seed/sc/200/200'" mode="aspectFill" class="store-img" />
    <view class="store-info">
      <view class="store-name">{{ store.name }}</view>
      <view class="store-rating">⭐ {{ store.rating || '5.0' }}</view>
      <view class="store-address text-ellipsis">
        📍 {{ store.province }}{{ store.city }}{{ store.district }} {{ store.address }}
      </view>
      <view class="store-hours">🕐 {{ store.businessHours || '09:00-21:00' }}</view>
      <view class="store-phone" v-if="store.phone">📞 {{ store.phone }}</view>
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  store: { type: Object, required: true }
})

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

.store-address, .store-hours, .store-phone {
  font-size: 24rpx;
  color: #6B7280;
}
</style>
