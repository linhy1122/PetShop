<template>
  <div class="store-detail-page">
    <div class="container" v-loading="loading">
      <el-card v-if="store">
        <img :src="store.image || '/vite.svg'" class="store-banner" />
        <h1>{{ store.name }}</h1>
        <p>⭐ {{ store.rating }} | {{ store.businessHours || '09:00-21:00' }}</p>
        <p>📞 {{ store.phone }}</p>
        <p>📍 {{ store.province }}{{ store.city }}{{ store.district }} {{ store.address }}</p>
        <p>{{ store.description }}</p>
      </el-card>
      <!-- 该店铺的商品列表 -->
      <h3 style="margin: 30px 0 16px">本店商品</h3>
      <div class="product-grid">
        <el-card v-for="p in products" :key="p.id" class="product-card"
                 shadow="hover" @click="$router.push(`/product/${p.id}`)">
          <img :src="p.mainImage || '/vite.svg'" :alt="p.name" class="product-img" />
          <div class="product-info">
            <h4>{{ p.name }}</h4>
            <span class="price">¥{{ p.price }}</span>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getStoreDetailApi } from '@/api/store'
import { getProductListApi } from '@/api/product'

const route = useRoute()
const store = ref(null)
const products = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    store.value = (await getStoreDetailApi(route.params.id)).data
    const res = await getProductListApi({ storeId: route.params.id, size: 20 })
    products.value = res.data?.records || []
  } finally { loading.value = false }
})
</script>

<style scoped>
.container { max-width: 1100px; margin: 0 auto; padding: 20px; }
.store-banner { width: 100%; height: 250px; object-fit: cover; border-radius: 12px; margin-bottom: 20px; }
h1 { margin-bottom: 12px; }
p { line-height: 2; color: #666; }
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; }
.product-card { cursor: pointer; }
.product-img { width: 100%; height: 150px; object-fit: cover; border-radius: 8px; }
.product-info { padding: 8px 0; display: flex; justify-content: space-between; align-items: center; }
.product-info h4 { font-size: 14px; }
.price { color: #f56c6c; font-weight: bold; }
</style>
