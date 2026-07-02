<template>
  <view class="store-map-page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input class="search-input" v-model="searchCity" placeholder="搜索城市..."
             confirm-type="search" @confirm="doCitySearch" />
      <view class="search-btn" @click="doCitySearch">搜索</view>
      <view class="locate-btn" @click="locateMe">📍</view>
    </view>

    <!-- 地图 -->
    <map class="store-map" :latitude="centerLat" :longitude="centerLng" :markers="markers"
         :scale="14" @markertap="onMarkerTap" show-location />

    <!-- 选中店铺信息条 -->
    <view class="selected-bar" v-if="selectedStore">
      <view class="selected-info">
        <text class="selected-name text-ellipsis">{{ selectedStore.name }}</text>
        <text class="selected-distance" v-if="selectedStore._distance !== undefined">
          距您 {{ selectedStore._distance }}km
        </text>
      </view>
      <view class="selected-actions">
        <view class="selected-action" @click="navigateToStore">🧭 导航</view>
        <view class="selected-action primary" @click="goStoreDetail">详情 ›</view>
      </view>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-wrap"><text>查找店铺中...</text></view>

    <!-- 店铺列表 -->
    <view class="store-list" v-else>
      <view class="list-header">
        <text>附近商店 ({{ stores.length }})</text>
        <text class="list-sort" v-if="myLat !== null">按距离排序</text>
      </view>
      <StoreCard v-for="s in stores" :key="s.id" :store="s" :distance="s._distance" />
      <EmptyState v-if="stores.length === 0" description="附近暂无商店" />
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStoreListApi, getNearbyStoresApi } from '@/api/store'
import StoreCard from '@/components/StoreCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const stores = ref([])
const markers = ref([])
const selectedStore = ref(null)
const searchCity = ref('')
const loading = ref(false)
const centerLat = ref(24.4798)
const centerLng = ref(118.0894)
const myLat = ref(null)
const myLng = ref(null)

function calcDistance(lat1, lng1, lat2, lng2) {
  const R = 6371
  const dLat = (lat2 - lat1) * Math.PI / 180
  const dLng = (lng2 - lng1) * Math.PI / 180
  const a = Math.sin(dLat / 2) ** 2 +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
    Math.sin(dLng / 2) ** 2
  return parseFloat((R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))).toFixed(1))
}

function sortByDistance(list) {
  if (myLat.value === null || myLng.value === null) return list
  list.forEach(s => {
    s._distance = calcDistance(myLat.value, myLng.value, s.latitude, s.longitude)
  })
  list.sort((a, b) => a._distance - b._distance)
  return list
}

function updateMarkers(list) {
  markers.value = list
    .filter(s => s.longitude && s.latitude)
    .map(s => ({
      id: s.id,
      latitude: s.latitude,
      longitude: s.longitude,
      title: s.name,
      width: 30,
      height: 30,
      callout: { content: s.name, fontSize: 12, padding: 4, display: 'BYCLICK' }
    }))
}

function getCurrentLocation() {
  return new Promise((resolve) => {
    uni.getLocation({
      type: 'gcj02',
      success: (res) => {
        myLat.value = res.latitude
        myLng.value = res.longitude
        centerLat.value = res.latitude
        centerLng.value = res.longitude
        resolve(true)
      },
      fail: () => {
        uni.showToast({ title: '无法获取位置，使用默认位置', icon: 'none' })
        resolve(false)
      }
    })
  })
}

async function doCitySearch() {
  loading.value = true
  selectedStore.value = null
  try {
    const city = searchCity.value.trim()
    const res = await getStoreListApi({ city: city || undefined, size: 50 })
    let list = res.data?.records || []
    list = sortByDistance(list)
    if (list.length > 0) {
      centerLat.value = list[0].latitude
      centerLng.value = list[0].longitude
    }
    stores.value = list
    updateMarkers(list)
  } catch (e) {
    uni.showToast({ title: '搜索店铺失败', icon: 'none' })
    stores.value = []
  } finally {
    loading.value = false
  }
}

async function locateMe() {
  loading.value = true
  try {
    await getCurrentLocation()
    if (myLat.value && myLng.value) {
      const res = await getNearbyStoresApi(myLng.value, myLat.value, 50)
      let list = res.data || []
      list = sortByDistance(list)
      stores.value = list
      updateMarkers(list)
    }
  } catch (e) {
    uni.showToast({ title: '加载附近商店失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  loading.value = true
  await getCurrentLocation()
  try {
    if (myLat.value && myLng.value) {
      const res = await getNearbyStoresApi(myLng.value, myLat.value, 50)
      let list = res.data || []
      list = sortByDistance(list)
      stores.value = list
      updateMarkers(list)
    } else {
      const res = await getStoreListApi({ size: 50 })
      stores.value = res.data?.records || []
      updateMarkers(stores.value)
    }
  } catch (e) {
    try {
      const res = await getStoreListApi({ size: 50 })
      stores.value = res.data?.records || []
      updateMarkers(stores.value)
    } catch (e2) {
      uni.showToast({ title: '加载店铺失败', icon: 'none' })
    }
  } finally {
    loading.value = false
  }
})

function onMarkerTap(e) {
  const store = stores.value.find(s => s.id === e.detail.markerId)
  if (store) selectedStore.value = store
}

function navigateToStore() {
  if (!selectedStore.value) return
  uni.openLocation({
    latitude: selectedStore.value.latitude,
    longitude: selectedStore.value.longitude,
    name: selectedStore.value.name,
    address: selectedStore.value.address
  })
}

function goStoreDetail() {
  if (selectedStore.value) {
    uni.navigateTo({ url: `/pages/store/detail/detail?id=${selectedStore.value.id}` })
  }
}
</script>

<style scoped>
.store-map-page { display: flex; flex-direction: column; height: calc(100vh - 100rpx); }

.search-bar {
  display: flex;
  gap: 16rpx;
  padding: 20rpx 24rpx;
  background: #fff;
}

.search-input {
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

.locate-btn {
  width: 68rpx;
  height: 68rpx;
  background: #f5f5f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
}

.store-map { width: 100%; height: 500rpx; }

.selected-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 24rpx;
  background: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.selected-info { flex: 1; overflow: hidden; }

.selected-name { font-size: 30rpx; font-weight: 600; display: block; }

.selected-distance { font-size: 24rpx; color: #FF6B35; }

.selected-actions { display: flex; gap: 16rpx; flex-shrink: 0; }

.selected-action {
  padding: 12rpx 24rpx;
  background: #f5f5f5;
  border-radius: 24rpx;
  font-size: 24rpx;
}

.selected-action.primary { background: #FF6B35; color: #fff; }

.list-header {
  display: flex;
  justify-content: space-between;
  padding: 24rpx 24rpx 8rpx;
  font-size: 28rpx;
  font-weight: 600;
}

.list-sort { font-size: 24rpx; color: #FF6B35; font-weight: 400; }

.store-list { flex: 1; padding: 0 24rpx 40rpx; overflow-y: auto; }

.loading-wrap { display: flex; justify-content: center; padding: 120rpx 0; color: #9CA3AF; font-size: 28rpx; }
</style>
