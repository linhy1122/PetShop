<template>
  <div class="store-map-page">
    <div class="container">
      <div class="map-header">
        <h2>附近宠物商店</h2>
        <el-input v-model="searchCity" placeholder="输入城市名搜索" style="width: 200px"
                  prefix-icon="Search" @change="fetchStores" />
      </div>
      <div class="map-content">
        <!-- 店铺列表 -->
        <div class="store-list">
          <el-card v-for="store in stores" :key="store.id" class="store-item"
                   shadow="hover" @click="selectedStore = store">
            <img :src="store.image || '/vite.svg'" class="store-img" />
            <div class="store-info">
              <h4>{{ store.name }}</h4>
              <p class="rating">⭐ {{ store.rating }}</p>
              <p class="address">{{ store.province }}{{ store.city }}{{ store.district }} {{ store.address }}</p>
              <p class="hours">🕐 {{ store.businessHours || '09:00-21:00' }}</p>
              <p class="phone">📞 {{ store.phone }}</p>
            </div>
          </el-card>
          <el-empty v-if="stores.length === 0" description="暂无店铺数据" />
        </div>

        <!-- 地图区域（占位） -->
        <div class="map-area">
          <div class="map-placeholder">
            <div v-if="selectedStore" class="selected-info">
              <h4>已选择：{{ selectedStore.name }}</h4>
              <p>{{ selectedStore.address }}</p>
              <p>经纬度：{{ selectedStore.longitude }}, {{ selectedStore.latitude }}</p>
              <el-button type="primary" size="small" @click="navigateTo(selectedStore)">
                导航到此
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStoreListApi } from '@/api/store'
import { ElMessage } from 'element-plus'

const stores = ref([])
const selectedStore = ref(null)
const searchCity = ref('')

onMounted(() => { fetchStores() })

async function fetchStores() {
  try {
    const res = await getStoreListApi({ city: searchCity.value || undefined, size: 50 })
    stores.value = res.data?.records || []
  } catch (e) { ElMessage.warning('加载店铺失败') }
}

function navigateTo(store) {
  // TODO: 调用高德地图导航 API
  window.open(`https://uri.amap.com/navigation?to=${store.longitude},${store.latitude},${store.name}&mode=car&callnative=1`, '_blank')
}
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 20px; }
.map-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.map-content { display: flex; gap: 20px; }
.store-list { flex: 1; max-height: 600px; overflow-y: auto; display: flex; flex-direction: column; gap: 12px; }
.store-item { cursor: pointer; display: flex; gap: 14px; }
.store-img { width: 100px; height: 100px; object-fit: cover; border-radius: 8px; }
.store-info h4 { font-size: 16px; margin-bottom: 4px; }
.store-info p { font-size: 13px; color: #666; line-height: 1.6; }
.store-item:hover { border-color: #409eff; }
.map-area { flex: 1.5; min-height: 500px; }
.map-placeholder {
  height: 100%; min-height: 500px; background: #f0f2f5; border-radius: 12px;
  display: flex; flex-direction: column; align-items: center; justify-content: center; color: #999;
}
.map-placeholder p { font-size: 18px; }
.hint { font-size: 13px !important; margin-top: 8px; }
.selected-info { margin-top: 20px; text-align: center; }
.selected-info h4 { color: #333; margin-bottom: 8px; }
</style>
