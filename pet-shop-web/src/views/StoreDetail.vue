<template>
  <div class="page-shell detail-page" v-loading="loading">
    <section v-if="store" class="hero-card store-hero">
      <img :src="store.image || '/vite.svg'" class="store-banner" />
      <div class="store-info">
        <div class="status-pill">Store detail</div>
        <h1>{{ store.name }}</h1>
        <p class="muted">{{ store.description || '查看门店信息、联系方式、地址和店内商品。' }}</p>
        <div class="store-metas">
          <el-tag type="success">评分 {{ store.rating || '4.8' }}</el-tag>
          <el-tag effect="plain">{{ store.businessHours || '09:00 - 21:00' }}</el-tag>
          <el-tag effect="plain">{{ store.phone }}</el-tag>
        </div>
        <p class="muted">{{ [store.province, store.city, store.district].filter(Boolean).join('') }} {{ store.address }}</p>
      </div>
    </section>

    <section class="page-section split-layout">
      <div class="panel-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">店铺信息</h2>
            <p class="section-subtitle">联系方式、营业时间、详细地址都在这里。</p>
          </div>
          <el-button type="primary" @click="$router.push('/store/map')">查看附近店铺</el-button>
        </div>
        <div class="store-detail-list">
          <div class="soft-card info-row">
            <span class="muted">联系方式</span>
            <strong>{{ store.phone || '未填写' }}</strong>
          </div>
          <div class="soft-card info-row">
            <span class="muted">营业时间</span>
            <strong>{{ store.businessHours || '09:00 - 21:00' }}</strong>
          </div>
          <div class="soft-card info-row">
            <span class="muted">店铺地址</span>
            <strong>{{ [store.province, store.city, store.district].filter(Boolean).join('') }} {{ store.address }}</strong>
          </div>
          <div class="soft-card info-row">
            <span class="muted">商店介绍</span>
            <strong>{{ store.description || '暂无介绍' }}</strong>
          </div>
        </div>
      </div>

      <div class="panel-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">店铺商品</h2>
            <p class="section-subtitle">当前店铺关联商品列表。</p>
          </div>
        </div>

        <div class="page-grid-2">
          <article v-for="item in products" :key="item.id" class="pet-card store-product" @click="$router.push(`/product/${item.id}`)">
            <img :src="item.mainImage || '/vite.svg'" class="cover-image store-product-cover" />
            <div class="store-product-body">
              <strong>{{ item.name }}</strong>
              <div class="product-foot">
                <span class="price">¥{{ item.price }}</span>
                <span class="muted">销量 {{ item.sales || 0 }}</span>
              </div>
            </div>
          </article>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getStoreDetailApi } from '@/api/store'
import { getProductListApi } from '@/api/product'

const route = useRoute()
const store = ref(null)
const products = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const storeRes = await getStoreDetailApi(route.params.id)
    store.value = storeRes.data
    const productRes = await getProductListApi({ storeId: route.params.id, size: 20 })
    products.value = productRes.data?.records || []
  } catch (error) {
    ElMessage.error('加载商店详情失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.store-hero {
  padding: 24px;
  display: grid;
  grid-template-columns: 0.95fr 1.05fr;
  gap: 24px;
}

.store-banner {
  width: 100%;
  height: 360px;
  object-fit: cover;
  border-radius: 24px;
}

.store-info h1 {
  margin: 14px 0 12px;
  font-size: 34px;
}

.store-metas {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: 18px 0;
}

.store-detail-list {
  display: grid;
  gap: 12px;
}

.info-row {
  display: grid;
  gap: 8px;
}

.store-product {
  overflow: hidden;
}

.store-product-cover {
  height: 160px;
}

.store-product-body {
  padding: 14px;
}

@media (max-width: 1024px) {
  .store-hero {
    grid-template-columns: 1fr;
  }
}
</style>
