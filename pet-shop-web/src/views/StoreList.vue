<template>
  <div class="page-shell store-page">
    <section class="hero-card hero-banner">
      <div class="hero-copy">
        <div class="status-pill">Store guide</div>
        <h1 class="hero-title">商店列表</h1>
        <p class="hero-desc">
          搜索店铺、筛选城市、查看热门门店。找到离你更近、更适合的宠物服务商家。
        </p>
        <div class="hero-actions">
          <el-input
            v-model="query.keyword"
            placeholder="搜索商店名称或关键词"
            size="large"
            clearable
            class="search-input"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" size="large" @click="fetchStores">搜索商店</el-button>
          <el-button size="large" @click="$router.push('/store/map')">附近商店</el-button>
        </div>
      </div>

      <div class="hero-meta">
        <div class="hero-meta-card">
          <div class="hero-meta-label">精选门店</div>
          <div class="hero-meta-value">{{ stores.length }} 家店铺在线展示</div>
        </div>
        <div class="hero-meta-card">
          <div class="hero-meta-label">城市筛选</div>
          <el-select v-model="query.city" placeholder="选择城市" size="large" style="width: 100%" clearable>
            <el-option label="全部城市" value="" />
            <el-option v-for="city in cityOptions" :key="city" :label="city" :value="city" />
          </el-select>
        </div>
        <div class="hero-meta-card">
          <div class="hero-meta-label">快速入口</div>
          <div class="chip-row">
            <el-tag v-for="tag in quickTags" :key="tag" effect="plain" round>{{ tag }}</el-tag>
          </div>
        </div>
      </div>
    </section>

    <section class="page-section">
      <div class="section-heading">
        <div>
          <h2 class="section-title">热门商店推荐</h2>
          <p class="section-subtitle">评分、地址、联系方式一眼就能看清。</p>
        </div>
        <el-radio-group v-model="sortBy" size="large">
          <el-radio-button label="rating">按评分</el-radio-button>
          <el-radio-button label="name">按名称</el-radio-button>
        </el-radio-group>
      </div>

      <div class="page-grid-3">
        <article v-for="store in sortedStores" :key="store.id" class="pet-card store-card">
          <img :src="store.image || '/vite.svg'" class="cover-image store-cover" :alt="store.name" />
          <div class="store-card-body">
            <div class="store-top">
              <h3>{{ store.name }}</h3>
              <el-tag size="small" type="success">评分 {{ store.rating || '4.8' }}</el-tag>
            </div>
            <p class="muted">{{ store.description || '宠物洗护、用品、喂养咨询、门店服务一应俱全。' }}</p>
            <div class="store-meta">
              <span><el-icon><Phone /></el-icon>{{ store.phone || '400-123-4567' }}</span>
              <span><el-icon><Location /></el-icon>{{ [store.province, store.city, store.district].filter(Boolean).join('') }} {{ store.address }}</span>
              <span><el-icon><Clock /></el-icon>{{ store.businessHours || '09:00 - 21:00' }}</span>
            </div>
            <div class="store-actions">
              <el-button type="primary" size="small" @click="$router.push(`/store/${store.id}`)">查看详情</el-button>
              <el-button size="small" @click="$router.push('/store/map')">查看地图</el-button>
            </div>
          </div>
        </article>
      </div>

      <el-empty v-if="!loading && sortedStores.length === 0" description="暂无商店数据" />
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { getStoreListApi } from '@/api/store'
import { ElMessage } from 'element-plus'

const stores = ref([])
const loading = ref(false)
const sortBy = ref('rating')
const query = reactive({ keyword: '', city: '' })

const cityOptions = ['北京', '上海', '广州', '深圳', '杭州', '成都', '重庆', '南京']
const quickTags = ['高评分', '营业中', '可配送', '支持到店']

const sortedStores = computed(() => {
  const list = stores.value.filter((store) => {
    const keyword = query.keyword.trim().toLowerCase()
    const matchesKeyword =
      !keyword ||
      [store.name, store.description, store.address, store.city]
        .filter(Boolean)
        .some((value) => String(value).toLowerCase().includes(keyword))
    const matchesCity = !query.city || store.city === query.city
    return matchesKeyword && matchesCity
  })

  return [...list].sort((left, right) => {
    if (sortBy.value === 'name') {
      return String(left.name || '').localeCompare(String(right.name || ''), 'zh-Hans-CN')
    }
    return Number(right.rating || 0) - Number(left.rating || 0)
  })
})

onMounted(fetchStores)

async function fetchStores() {
  loading.value = true
  try {
    const response = await getStoreListApi({ size: 100 })
    stores.value = response.data?.records || []
  } catch (error) {
    stores.value = []
    ElMessage.warning('加载商店列表失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.store-page {
  padding-bottom: 24px;
}

.search-input {
  flex: 1;
  min-width: 280px;
}

.store-card {
  overflow: hidden;
}

.store-cover {
  height: 210px;
}

.store-card-body {
  padding: 18px;
}

.store-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: start;
  margin-bottom: 10px;
}

.store-top h3 {
  font-size: 18px;
}

.store-meta {
  display: grid;
  gap: 10px;
  margin-top: 16px;
  color: var(--pet-text-soft);
  font-size: 13px;
  line-height: 1.6;
}

.store-meta span {
  display: flex;
  gap: 8px;
  align-items: center;
}

.store-actions {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}
</style>
