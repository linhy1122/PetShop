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

    <!-- 搜索 -->
    <view class="search-bar">
      <input class="search-input" v-model="keyword" placeholder="搜索商品..." confirm-type="search"
             @confirm="doSearch" />
      <view class="search-btn" @click="doSearch">搜索</view>
    </view>

    <!-- 分类标签 -->
    <scroll-view scroll-x class="category-scroll" v-if="categories.length">
      <view class="category-tags">
        <view class="category-tag" v-for="cat in categories" :key="cat.id"
              :class="{ active: query.categoryId === cat.id }"
              @click="selectCategory(cat.id)">
          {{ cat.name }}
        </view>
      </view>
    </scroll-view>

    <!-- 商品网格 -->
    <view class="product-grid">
      <ProductCard v-for="item in products" :key="item.id" :item="item" />
    </view>
    <EmptyState v-if="!loading && products.length === 0" description="暂无商品" />

    <!-- 加载更多 -->
    <view class="load-more" v-if="hasMore">
      <text class="load-text" @click="loadMore">{{ loading ? '加载中...' : '加载更多' }}</text>
    </view>
    <view class="load-more" v-else-if="products.length > 0">
      <text class="load-text">— 没有更多了 —</text>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getProductListApi } from '@/api/product'
import { categories as mockCategories, allProducts as mockAllProducts } from '@/mock'
import request from '@/utils/request'
import ProductCard from '@/components/ProductCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const USE_MOCK = true // 小程序静态数据模式
const loading = ref(false)
const products = ref([])
const categories = ref([])
const keyword = ref('')
const page = reactive({ current: 1, size: 10, total: 0 })
const hasMore = ref(true)

const query = reactive({
  categoryId: undefined,
  productType: undefined,
  keyword: ''
})

// 接收 tab 切换参数
onLoad((options) => {
  if (options) {
    if (options.type) query.productType = Number(options.type)
    if (options.categoryId) query.categoryId = Number(options.categoryId)
  }
  loadCategories()
  fetchData(true)
})

async function loadCategories() {
  try {
    const res = await request.get('/category/list')
    categories.value = res.data || mockCategories
  } catch (e) { categories.value = mockCategories }
}

function filterMockProducts() {
  let list = [...mockAllProducts]
  if (query.productType) list = list.filter(p => p.productType === query.productType)
  if (query.categoryId) list = list.filter(p => p.categoryId === query.categoryId)
  if (query.keyword) list = list.filter(p => p.name.includes(query.keyword))
  return { records: list, total: list.length }
}

async function fetchData(reset = false) {
  if (loading.value) return
  if (reset) { page.current = 1; products.value = [] }
  loading.value = true
  try {
    const res = await getProductListApi({
      page: page.current,
      size: page.size,
      categoryId: query.categoryId || undefined,
      productType: query.productType,
      keyword: query.keyword || undefined
    })
    const list = res.data?.records || []
    if (reset) products.value = list
    else products.value = [...products.value, ...list]
    page.total = res.data?.total || 0
    hasMore.value = products.value.length < page.total
  } catch (e) {
    const mock = filterMockProducts()
    if (reset) products.value = mock.records
    page.total = mock.total
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

function selectType(type) {
  query.productType = type
  fetchData(true)
}

function selectCategory(id) {
  query.categoryId = query.categoryId === id ? undefined : id
  fetchData(true)
}

function doSearch() {
  query.keyword = keyword.value
  fetchData(true)
}

function loadMore() {
  if (!hasMore.value || loading.value) return
  page.current++
  fetchData(false)
}
</script>

<style scoped>
.product-list-page {
  padding: 24rpx 24rpx 40rpx;
}

.filter-bar {
  margin-bottom: 20rpx;
}

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
  margin-bottom: 20rpx;
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

.category-scroll {
  white-space: nowrap;
  margin-bottom: 20rpx;
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
