<template>
  <view class="store-map-page">
    <!-- 搜索栏 -->
    <view class="map-header">
      <input class="city-input" v-model="searchCity" placeholder="输入城市名搜索"
             confirm-type="search" @confirm="fetchStores" />
      <view class="search-btn" @click="fetchStores">搜索</view>
    </view>

    <!-- 地图 -->
    <map id="storeMap" class="store-map" :latitude="mapCenter.lat" :longitude="mapCenter.lng"
         :scale="mapScale" :markers="markers" :show-location="true"
         @markertap="onMarkerTap" @callouttap="onMarkerTap">
    </map>

    <!-- 选中店铺信息 -->
    <view class="selected-bar" v-if="selectedStore">
      <view class="selected-info">
        <text class="selected-name">{{ selectedStore.name }}</text>
        <text class="selected-addr">{{ selectedStore.address }}</text>
      </view>
      <view class="selected-actions">
        <button class="nav-btn" size="mini" @click="navigateTo">导航</button>
        <button class="detail-btn" size="mini" @click="goDetail">详情</button>
      </view>
    </view>

    <!-- 店铺列表 -->
    <scroll-view scroll-y class="store-list">
      <StoreCard v-for="store in stores" :key="store.id" :store="store" />
      <EmptyState v-if="stores.length === 0" description="暂无店铺数据" />
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStoreListApi } from '@/api/store'
import { stores as mockStores } from '@/mock'
import StoreCard from '@/components/StoreCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const stores = ref([])
const selectedStore = ref(null)
const searchCity = ref('')
const mapScale = ref(12)
const mapCenter = ref({ lng: 118.09, lat: 24.48 })
const markers = ref([])

onMounted(() => {
  fetchStores()
  getCurrentLocation()
})

function getCurrentLocation() {
  uni.getLocation({
    type: 'gcj02',
    success: (res) => {
      mapCenter.value = { lng: res.longitude, lat: res.latitude }
      mapScale.value = 14
    },
    fail: () => {
      // 使用默认位置
    }
  })
}

async function fetchStores() {
  try {
    const res = await getStoreListApi({ city: searchCity.value || undefined, size: 50 })
    stores.value = res.data?.records || []
    if (!stores.value.length) stores.value = mockStores
  } catch (e) {
    stores.value = mockStores
  }
  updateMarkers()
}

function updateMarkers() {
  markers.value = stores.value
    .filter(s => s.longitude && s.latitude)
    .map((s, i) => ({
      id: i,
      latitude: s.latitude,
      longitude: s.longitude,
      title: s.name,
      width: 30,
      height: 30,
      callout: { content: s.name, fontSize: 12, padding: 8, display: 'ALWAYS' }
    }))

  if (markers.value.length) {
    const mapCtx = uni.createMapContext('storeMap')
    mapCtx.includePoints({
      points: markers.value.map(m => ({ latitude: m.latitude, longitude: m.longitude })),
      padding: [50, 50, 50, 50]
    })
  }
}

function onMarkerTap(e) {
  const store = stores.value.find(s => s.latitude === e.latitude && s.longitude === e.longitude)
  if (store) {
    selectedStore.value = store
  }
}

function navigateTo() {
  if (!selectedStore.value) return
  uni.openLocation({
    latitude: selectedStore.value.latitude,
    longitude: selectedStore.value.longitude,
    name: selectedStore.value.name,
    address: selectedStore.value.address,
    scale: 16
  })
}

function goDetail() {
  if (selectedStore.value) {
    uni.navigateTo({ url: `/pages/store/detail/detail?id=${selectedStore.value.id}` })
  }
}
</script>

<style scoped>
.store-map-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100rpx);
}

.map-header {
  display: flex;
  gap: 16rpx;
  padding: 20rpx 24rpx;
  background: #fff;
}

.city-input {
  flex: 1;
  height: 68rpx;
  background: #f5f5f5;
  border-radius: 34rpx;
  padding: 0 24rpx;
  font-size: 26rpx;
}

.search-btn {
  background: #FF6B35;
  color: #fff;
  padding: 0 28rpx;
  border-radius: 34rpx;
  font-size: 26rpx;
  line-height: 68rpx;
  height: 68rpx;
}

.store-map {
  width: 100%;
  height: 400rpx;
}

.selected-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
  background: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.selected-name {
  font-size: 30rpx;
  font-weight: 600;
  display: block;
}

.selected-addr {
  font-size: 24rpx;
  color: #999;
  display: block;
  margin-top: 4rpx;
}

.selected-actions {
  display: flex;
  gap: 16rpx;
}

.nav-btn {
  background: #3B82F6;
  color: #fff;
  border: none;
  padding: 10rpx 28rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.detail-btn {
  background: #FF6B35;
  color: #fff;
  border: none;
  padding: 10rpx 28rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.store-list {
  flex: 1;
  padding: 20rpx 24rpx;
}
</style>
