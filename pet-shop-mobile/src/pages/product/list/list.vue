<template>
  <view class="product-list-page">
    <!-- 类型切换 -->
    <view class="filter-bar">
      <view class="type-tabs">
        <view class="type-tab" :class="{ active: query.productType === undefined }"
              @click="selectType(undefined)">全部</view>
        <view class="type-tab" :class="{ active: query.productType === 1 }"
              @click="selectType(1)">🐾 宠物</view>
        <view class="type-tab" :class="{ active: query.productType === 2 }"
              @click="selectType(2)">🛍️ 周边</view>
      </view>
    </view>

    <!-- 搜索 + 排序 -->
    <view class="search-bar">
      <input class="search-input" v-model="query.keyword" placeholder="搜索商品..." confirm-type="search"
             @confirm="doSearch" />
      <view class="search-btn" @click="doSearch">搜索</view>
    </view>

    <!-- 排序栏 -->
    <view class="sort-bar">
      <view class="sort-item" :class="{ active: query.sortBy === 'default' }"
            @click="doSort('default')">综合</view>
      <view class="sort-item" :class="{ active: query.sortBy === 'price_asc' }"
            @click="doSort('price_asc')">价格↑</view>
      <view class="sort-item" :class="{ active: query.sortBy === 'price_desc' }"
            @click="doSort('price_desc')">价格↓</view>
      <view class="sort-item" :class="{ active: query.sortBy === 'sales' }"
            @click="doSort('sales')">销量</view>
    </view>

    <!-- 分类标签 + 筛选状态 -->
    <view class="filter-row" v-if="categories.length || hasActiveFilter">
      <scroll-view scroll-x class="category-scroll" v-if="categories.length">
        <view class="category-tags">
          <view class="category-tag" v-for="cat in categories" :key="cat.id"
                :class="{ active: query.categoryId === cat.id }"
                @click="selectCategory(cat.id)">
            {{ cat.name }}
          </view>
        </view>
      </scroll-view>
      <view class="clear-filter" v-if="hasActiveFilter" @click="clearFilters">清除筛选</view>
    </view>

    <!-- 商品网格 -->
    <!-- DEBUG: loading={{ loading }}, products.length={{ products.length }} -->
    <view v-if="loading" class="loading-wrap"><text>加载中...</text></view>
    <view class="product-grid" v-else-if="products.length > 0">
      <ProductCard v-for="item in products" :key="item.id" :item="item" />
    </view>
    <view v-else class="loading-wrap">
      <text>暂无数据 (products={{ products.length }}, loading={{ loading }})</text>
    </view>

    <!-- 加载更多 -->
    <view class="load-more" v-if="hasMore && !loading" @click="loadMore">
      <text class="load-text">{{ loadingMore ? '加载中...' : '加载更多' }}</text>
    </view>
    <view class="load-more" v-else-if="!loading && products.length > 0 && !hasMore">
      <text class="load-text">— 没有更多了 —</text>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { getProductListApi } from '@/api/product'
import { useNavStore } from '@/stores/nav'
import request from '@/utils/request'
import ProductCard from '@/components/ProductCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const navStore = useNavStore()

const loading = ref(false)
const loadingMore = ref(false)
const products = ref([])
const categories = ref([])
const page = reactive({ current: 1, size: 10, total: 0 })
const hasMore = ref(true)
const firstLoad = ref(true)

const query = reactive({
  categoryId: undefined,
  productType: undefined,
  keyword: '',
  sortBy: 'default'
})

const hasActiveFilter = computed(() =>
  query.productType !== undefined || query.categoryId !== undefined || query.keyword !== ''
)

// --- 从 NavStore 消费参数（只设置 query，不触发 fetch） ---
function applyNavFilter() {
  const f = navStore.consumeProductFilter()
  if (f.type !== null || f.categoryId !== null || f.keyword !== '') {
    query.productType = f.type !== null ? f.type : undefined
    query.categoryId = f.categoryId || undefined
    query.keyword = f.keyword
    return true
  }
  return false
}

onLoad((options) => {
  console.log('[list] onLoad fired, options:', JSON.stringify(options))
  // 先从 NavStore 读取 switchTab 传递的筛选参数（仅设置 query）
  applyNavFilter()
  // URL 参数（H5 或其他直接跳转场景）
  if (options) {
    if (options.type) query.productType = Number(options.type)
    if (options.categoryId) query.categoryId = Number(options.categoryId)
  }
  loadCategories()
  fetchData(true) // 统一在此处触发唯一一次数据加载
})

onShow(() => {
  // 每次切回 tab 时从 NavStore 消费参数并刷新
  if (!firstLoad.value) {
    if (applyNavFilter()) {
      fetchData(true)
    }
  }
  firstLoad.value = false
})

async function loadCategories() {
  try {
    const res = await request.get('/category/list')
    categories.value = res.data || []
  } catch (e) {
    categories.value = []
  }
}

async function fetchData(reset = false) {
  if (loading.value && reset) return
  if (reset) { page.current = 1; products.value = [] }
  loading.value = reset
  loadingMore.value = !reset
  try {
    const res = await getProductListApi({
      page: page.current,
      size: page.size,
      categoryId: query.categoryId || undefined,
      productType: query.productType,
      keyword: query.keyword || undefined,
      sortBy: query.sortBy !== 'default' ? query.sortBy : undefined
    })
    console.log('[fetchData] API response:', JSON.stringify({ code: res.code, hasData: !!res.data, recordsCount: res.data?.records?.length }))
    const list = res.data?.records || []
    console.log('[fetchData] list length:', list.length, 'reset:', reset)
    // 先渲染数据，图片预加载放到后台（避免 wx.downloadFile 超时阻塞页面）
    products.value = reset ? list : [...products.value, ...list]
    console.log('[fetchData] products.value length:', products.value.length)
    uni.preloadProductImages(list)
    page.total = res.data?.total || 0
    hasMore.value = products.value.length < page.total
  } catch (e) {
    console.error('[fetchData] error:', e)
    uni.showToast({ title: '加载商品失败', icon: 'none' })
    if (reset) products.value = []
    hasMore.value = false
  } finally {
    loading.value = false
    loadingMore.value = false
    console.log('[fetchData] done — loading:', loading.value, 'products:', products.value.length)
  }
}

function selectType(type) {
  query.productType = type
  query.categoryId = undefined
  fetchData(true)
}

function selectCategory(id) {
  query.categoryId = query.categoryId === id ? undefined : id
  fetchData(true)
}

function doSearch() {
  fetchData(true)
}

function doSort(sortBy) {
  query.sortBy = sortBy
  fetchData(true)
}

function clearFilters() {
  query.productType = undefined
  query.categoryId = undefined
  query.keyword = ''
  fetchData(true)
}

function loadMore() {
  if (!hasMore.value || loadingMore.value) return
  page.current++
  fetchData(false)
}
</script>

<style scoped>
.product-list-page {
  padding: 24rpx 24rpx 40rpx;
}

.filter-bar { margin-bottom: 20rpx; }

.type-tabs {
  display: flex;
  background: #f0f0f0;
  border-radius: 40rpx;
  padding: 6rpx;
}

.type-tab {
  flex: 1;
  text-align: center;
  padding: 16rpx 0;
  font-size: 28rpx;
  color: #666;
  border-radius: 40rpx;
  transition: all 0.3s;
}

.type-tab.active {
  background: #FF6B35;
  color: #fff;
  font-weight: 600;
}

.search-bar {
  display: flex;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.search-input {
  flex: 1;
  height: 72rpx;
  background: #f5f5f5;
  border-radius: 36rpx;
  padding: 0 28rpx;
  font-size: 28rpx;
}

.search-btn {
  background: #FF6B35;
  color: #fff;
  padding: 0 32rpx;
  border-radius: 36rpx;
  font-size: 28rpx;
  line-height: 72rpx;
  height: 72rpx;
  white-space: nowrap;
}

.sort-bar {
  display: flex;
  gap: 8rpx;
  margin-bottom: 16rpx;
  background: #fff;
  border-radius: 12rpx;
  padding: 12rpx 20rpx;
}

.sort-item {
  flex: 1;
  text-align: center;
  font-size: 24rpx;
  color: #9CA3AF;
  padding: 8rpx 0;
  border-radius: 8rpx;
}

.sort-item.active {
  color: #FF6B35;
  font-weight: 600;
  background: #FFF3E0;
}

.filter-row {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
  gap: 16rpx;
}

.category-scroll {
  flex: 1;
  white-space: nowrap;
}

.category-tags {
  display: inline-flex;
  gap: 16rpx;
}

.category-tag {
  display: inline-block;
  padding: 10rpx 24rpx;
  background: #f0f0f0;
  border-radius: 28rpx;
  font-size: 24rpx;
  color: #666;
}

.category-tag.active {
  background: #FF6B35;
  color: #fff;
}

.clear-filter {
  flex-shrink: 0;
  font-size: 24rpx;
  color: #FF6B35;
  padding: 8rpx 16rpx;
}

.loading-wrap {
  display: flex;
  justify-content: center;
  padding: 200rpx 0;
  color: #9CA3AF;
  font-size: 28rpx;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.load-more {
  text-align: center;
  padding: 40rpx 0;
}

.load-text {
  color: #9CA3AF;
  font-size: 26rpx;
}
</style>
