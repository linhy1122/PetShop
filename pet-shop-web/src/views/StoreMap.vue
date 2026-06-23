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
          <div v-for="store in stores" :key="store.id" class="store-item"
               :class="{ active: selectedStore?.id === store.id }"
               @click="selectStore(store)">
            <img :src="store.image || '/vite.svg'" class="store-img" @error="e => e.target.src='/vite.svg'" />
            <div class="store-info">
              <h4>{{ store.name }}</h4>
              <p class="rating">⭐ {{ store.rating }}</p>
              <p class="address">{{ store.province }}{{ store.city }}{{ store.district }} {{ store.address }}</p>
              <p class="hours">🕐 {{ store.businessHours || '09:00-21:00' }}</p>
              <p class="phone">📞 {{ store.phone }}</p>
            </div>
          </div>
          <el-empty v-if="stores.length === 0" description="暂无店铺数据" />
        </div>

        <!-- 高德地图 -->
        <div class="map-area">
          <div id="amap-container"></div>
          <div v-if="selectedStore" class="map-actions">
            <span>{{ selectedStore.name }}</span>
            <el-button type="primary" size="small" @click="navigateTo">
              导航到此
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { getStoreListApi } from '@/api/store'
import { ElMessage } from 'element-plus'

const stores = ref([])
const selectedStore = ref(null)
const searchCity = ref('')
let map = null
let markers = []

onMounted(async () => {
  await fetchStores()
  // 等 DOM 渲染后再初始化地图
  await nextTick()
  initMap()
})

async function fetchStores() {
  try {
    const res = await getStoreListApi({ city: searchCity.value || undefined, size: 50 })
    stores.value = res.data?.records || []
  } catch (e) { ElMessage.warning('加载店铺失败') }
}

// ---- 初始化高德地图 ----
function initMap() {
  if (typeof AMap === 'undefined') {
    console.warn('高德地图 JS API 未加载，请检查 index.html 中的 script 标签')
    return
  }

  map = new AMap.Map('amap-container', {
    zoom: 12,
    center: [118.09, 24.48] // 默认：厦门
  })

  // 添加店铺标记
  addMarkers()
}

// ---- 在地图上打点 ----
function addMarkers() {
  // 清除旧标记
  markers.forEach(m => m.setMap(null))
  markers = []

  stores.value.forEach(store => {
    if (!store.longitude || !store.latitude) return

    const marker = new AMap.Marker({
      position: [store.longitude, store.latitude],
      title: store.name,
      label: {
        content: `<div style="background:#FF6B35;color:#fff;padding:2px 8px;border-radius:4px;font-size:12px;white-space:nowrap">${store.name}</div>`,
        direction: 'top'
      }
    })

    marker.on('click', () => { selectedStore.value = store })
    marker.setMap(map)
    markers.push(marker)
  })

  // 自适应显示所有店铺
  if (markers.length > 0) {
    map.setFitView(markers)
  }
}

// ---- 选中店铺，定位到地图 ----
function selectStore(store) {
  selectedStore.value = store
  if (map && store.longitude && store.latitude) {
    map.setZoomAndCenter(15, [store.longitude, store.latitude])
  }
}

// ---- 跳转高德导航 ----
function navigateTo() {
  const s = selectedStore.value
  if (!s) return
  // 高德 URI 导航协议
  window.open(
    `https://uri.amap.com/navigation?to=${s.longitude},${s.latitude},${s.name}&mode=car&callnative=1`,
    '_blank'
  )
}
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 20px; }

.map-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 20px;
}
.map-header h2 {
  font-size: 24px; font-weight: 700; color: var(--text-primary);
}

.map-content { display: flex; gap: 20px; height: 580px; }

/* ---- 左侧列表 ---- */
.store-list {
  width: 400px; flex-shrink: 0;
  overflow-y: auto; display: flex; flex-direction: column; gap: 10px;
  padding-right: 4px;
}
.store-item {
  display: flex; gap: 12px; padding: 12px;
  background: var(--bg-card); border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm); cursor: pointer;
  transition: all 0.2s; border: 2px solid transparent;
}
.store-item:hover, .store-item.active {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-md);
}
.store-img {
  width: 90px; height: 90px; object-fit: cover;
  border-radius: var(--radius-sm); flex-shrink: 0;
}
.store-info h4 { font-size: 15px; margin-bottom: 2px; }
.store-info p { font-size: 12px; color: var(--text-secondary); line-height: 1.5; }

/* ---- 右侧地图 ---- */
.map-area { flex: 1; position: relative; border-radius: var(--radius-lg); overflow: hidden; }

#amap-container {
  width: 100%; height: 100%;
}

.map-actions {
  position: absolute; bottom: 0; left: 0; right: 0;
  background: var(--bg-card); padding: 12px 20px;
  display: flex; justify-content: space-between; align-items: center;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.06);
  font-weight: 600;
}
</style>
