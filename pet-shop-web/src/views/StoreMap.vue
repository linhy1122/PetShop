<template>
  <div class="page-shell store-map-page">
    <section class="hero-card map-hero">
      <div>
        <div class="status-pill">Nearby stores</div>
        <h1 class="hero-title" style="margin-top: 12px;">附近商店</h1>
        <p class="hero-desc">输入城市快速查看附近门店，地图区域保留给后续高德地图接入。</p>
      </div>
      <div class="map-search">
        <el-input v-model="searchCity" placeholder="输入城市名称" size="large" clearable @change="fetchStores">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" size="large" @click="fetchStores">搜索</el-button>
      </div>
    </section>

    <section class="page-section split-layout">
      <div class="panel-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">门店列表</h2>
            <p class="section-subtitle">选中门店后可快速查看地址与导航入口。</p>
          </div>
        </div>

        <div class="list-compact">
          <article
            v-for="store in stores"
            :key="store.id"
            class="soft-card list-item"
            :class="{ active: selectedStore?.id === store.id }"
            @click="selectedStore = store"
          >
            <img :src="store.image || '/vite.svg'" class="list-thumb" />
            <div class="list-content">
              <div class="list-top">
                <strong>{{ store.name }}</strong>
                <el-tag size="small" type="success">{{ store.rating || '4.8' }}</el-tag>
              </div>
              <p class="muted">{{ store.city }} {{ store.address }}</p>
              <p class="muted">{{ store.phone }}</p>
            </div>
          </article>
        </div>

        <el-empty v-if="stores.length === 0" description="暂无门店数据" />
      </div>

      <div class="panel-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">地图预览</h2>
            <p class="section-subtitle">这里可替换为高德地图 JS API。</p>
          </div>
        </div>

        <div class="map-placeholder">
          <template v-if="selectedStore">
            <h3>{{ selectedStore.name }}</h3>
            <p class="muted">{{ selectedStore.city }} {{ selectedStore.address }}</p>
            <p class="muted">{{ selectedStore.phone }}</p>
            <el-button type="primary" @click="navigateTo(selectedStore)">打开导航</el-button>
          </template>
          <template v-else>
            <p class="muted">请选择左侧门店查看地图信息。</p>
          </template>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getStoreListApi } from '@/api/store'
import { ElMessage } from 'element-plus'

const stores = ref([])
const selectedStore = ref(null)
const searchCity = ref('')

onMounted(fetchStores)

async function fetchStores() {
  try {
    const response = await getStoreListApi({ city: searchCity.value || undefined, size: 50 })
    stores.value = response.data?.records || []
    selectedStore.value = stores.value[0] || null
  } catch (error) {
    stores.value = []
    selectedStore.value = null
    ElMessage.warning('加载附近商店失败')
  }
}

function navigateTo(store) {
  window.open(
    `https://uri.amap.com/navigation?to=${store.longitude},${store.latitude},${store.name}&mode=car&callnative=1`,
    '_blank'
  )
}
</script>

<style scoped>
.map-hero {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: end;
}

.map-search {
  display: flex;
  gap: 12px;
  align-items: center;
  min-width: 420px;
}

.list-item.active {
  outline: 2px solid rgba(138, 108, 255, 0.28);
}

.map-placeholder {
  min-height: 480px;
  border-radius: 24px;
  display: grid;
  place-items: center;
  text-align: center;
  gap: 12px;
  background:
    radial-gradient(circle at top, rgba(138, 108, 255, 0.12), transparent 28%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.88), rgba(246, 249, 255, 0.95));
}

@media (max-width: 1024px) {
  .map-hero {
    flex-direction: column;
    align-items: start;
  }

  .map-search {
    min-width: 0;
    width: 100%;
    flex-direction: column;
  }
}
</style>
