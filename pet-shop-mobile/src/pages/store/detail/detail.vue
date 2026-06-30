<template>
  <view class="store-detail-page">
    <view v-if="loading" class="loading-wrap"><text>加载中...</text></view>

    <template v-if="store">
      <image :src="store.image || 'https://picsum.photos/seed/sd/400/200'" mode="aspectFill" class="store-banner" />

      <view class="info-section">
        <text class="store-name">{{ store.name }}</text>
        <text class="store-rating">⭐ {{ store.rating || '5.0' }} | 🕐 {{ store.businessHours || '09:00-21:00' }}</text>
        <text class="store-phone">📞 {{ store.phone || '暂无' }}</text>
        <text class="store-addr">📍 {{ store.province }}{{ store.city }}{{ store.district }} {{ store.address }}</text>
        <text class="store-desc" v-if="store.description">{{ store.description }}</text>
      </view>

      <!-- 本店商品 -->
      <view class="products-section">
        <text class="section-title">本店商品</text>
        <view class="product-grid">
          <ProductCard v-for="p in products" :key="p.id" :item="p" />
        </view>
        <EmptyState v-if="products.length === 0" description="暂无商品" />
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getStoreDetailApi } from '@/api/store'
import { getProductListApi } from '@/api/product'
import { stores as mockStores, allProducts as mockProducts } from '@/mock'
import ProductCard from '@/components/ProductCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const store = ref(null)
const products = ref([])
const loading = ref(false)

onLoad((options) => {
  fetchData(options.id)
})

async function fetchData(id) {
  loading.value = true
  try {
    store.value = (await getStoreDetailApi(id)).data
    const res = await getProductListApi({ storeId: id, size: 20 })
    products.value = res.data?.records || []
  } catch (e) {
    store.value = mockStores.find(s => s.id == id) || mockStores[0]
    products.value = mockProducts.filter(p => p.productType === 2).slice(0, 4)
  } finally { loading.value = false }
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

.store-rating, .store-phone, .store-addr, .store-desc {
  display: block;
  font-size: 26rpx;
  color: #666;
  line-height: 2;
}

.products-section {
  padding: 0 24rpx 40rpx;
}

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
