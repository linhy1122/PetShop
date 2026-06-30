<template>
  <view class="store-detail-page">
    <view v-if="loading" class="loading-wrap"><text>加载中...</text></view>

    <template v-if="store">
      <image :src="storeImg" mode="aspectFill" class="store-banner" @error="onImgError" />

      <view class="info-section">
        <text class="store-name">{{ store.name }}</text>
        <text class="store-rating">⭐ {{ store.rating || '5.0' }} | 🕐 {{ store.businessHours || '09:00-21:00' }}</text>
        <text class="store-addr">📍 {{ store.province }}{{ store.city }}{{ store.district }} {{ store.address }}</text>
        <text class="store-desc" v-if="store.description">{{ store.description }}</text>

        <view class="store-actions">
          <button class="action-btn" v-if="store.phone" @click="callStore">
            📞 拨打电话
          </button>
          <button class="action-btn" @click="navigateStore">
            🧭 导航到店
          </button>
        </view>
      </view>

      <!-- 本店商品 -->
      <view class="products-section">
        <text class="section-title">本店商品</text>
        <view class="product-grid" v-if="products.length > 0">
          <ProductCard v-for="p in products" :key="p.id" :item="p" />
        </view>
        <EmptyState v-else description="暂无商品" />
      </view>
    </template>

    <EmptyState v-else-if="!loading" description="店铺不存在或加载失败" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getStoreDetailApi } from '@/api/store'
import { getProductListApi } from '@/api/product'
import ProductCard from '@/components/ProductCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const store = ref(null)
const products = ref([])
const loading = ref(false)

const FALLBACK_IMG = 'https://picsum.photos/seed/store-dt/400/200'
const storeImg = uni.fixImgUrl(store.value?.image) || FALLBACK_IMG

onLoad((options) => {
  fetchData(options.id)
})

async function fetchData(id) {
  loading.value = true
  try {
    const storeData = (await getStoreDetailApi(id)).data
    store.value = storeData
    uni.preloadStoreImages(storeData)
    const res = await getProductListApi({ storeId: id, size: 20 })
    const list = res.data?.records || []
    products.value = list
    uni.preloadProductImages(list)
  } catch (e) {
    uni.showToast({ title: '加载店铺失败', icon: 'none' })
    store.value = null
    products.value = []
  } finally {
    loading.value = false
  }
}

function onImgError() { /* handled by fallback */ }

function callStore() {
  if (store.value?.phone) {
    uni.makePhoneCall({ phoneNumber: store.value.phone })
  }
}

function navigateStore() {
  uni.openLocation({
    latitude: store.value.latitude,
    longitude: store.value.longitude,
    name: store.value.name,
    address: [store.value.province, store.value.city, store.value.district, store.value.address]
      .filter(Boolean).join('')
  })
}
</script>

<style scoped>
.loading-wrap {
  display: flex;
  justify-content: center;
  padding: 200rpx 0;
  color: #999;
}

.store-banner {
  width: 100%;
  height: 400rpx;
  background: #f5f5f5;
}

.info-section {
  background: #fff;
  padding: 32rpx;
  margin-bottom: 20rpx;
}

.store-name {
  font-size: 40rpx;
  font-weight: 700;
  display: block;
  margin-bottom: 16rpx;
}

.store-rating { font-size: 26rpx; color: #F59E0B; display: block; margin-bottom: 8rpx; }

.store-addr {
  font-size: 26rpx;
  color: #666;
  display: block;
  margin-bottom: 8rpx;
  line-height: 1.6;
}

.store-desc {
  font-size: 26rpx;
  color: #666;
  display: block;
  margin-bottom: 16rpx;
  line-height: 1.6;
}

.store-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.action-btn {
  flex: 1;
  background: #f5f5f5;
  color: #333;
  border: none;
  padding: 20rpx;
  border-radius: 32rpx;
  font-size: 26rpx;
  text-align: center;
}

.products-section { padding: 0 24rpx 40rpx; }

.section-title {
  font-size: 32rpx;
  font-weight: 700;
  margin-bottom: 24rpx;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}
</style>
