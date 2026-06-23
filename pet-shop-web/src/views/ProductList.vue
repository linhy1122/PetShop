<template>
  <div class="page-shell product-page">
    <section class="hero-card hero-banner">
      <div class="hero-copy">
        <div class="status-pill">Product mall</div>
        <h1 class="hero-title">商品商城</h1>
        <p class="hero-desc">支持搜索、分类筛选和销量/价格/上新排序，快速找到你想买的宠物好物。</p>
        <div class="hero-actions">
          <el-input v-model="query.keyword" placeholder="搜索商品名称" size="large" clearable class="search-input">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-button type="primary" size="large" @click="fetchProducts">搜索商品</el-button>
          <el-button size="large" @click="$router.push('/store/list')">按商店看</el-button>
        </div>
      </div>

      <div class="hero-meta">
        <div class="hero-meta-card">
          <div class="hero-meta-label">排序方式</div>
          <el-select v-model="sortBy" size="large" style="width: 100%">
            <el-option label="销量优先" value="sales" />
            <el-option label="价格优先" value="price" />
            <el-option label="最新上架" value="latest" />
          </el-select>
        </div>
        <div class="hero-meta-card">
          <div class="hero-meta-label">商品类型</div>
          <el-radio-group v-model="query.productType">
            <el-radio-button :value="undefined">全部</el-radio-button>
            <el-radio-button :value="1">宠物</el-radio-button>
            <el-radio-button :value="2">宠物周边</el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </section>

    <section class="page-section">
      <div class="section-heading">
        <div>
          <h2 class="section-title">分类筛选</h2>
          <p class="section-subtitle">点一下标签即可快速切换商品分类。</p>
        </div>
      </div>

      <div class="chip-row">
        <el-tag
          v-for="category in categories"
          :key="category.id"
          effect="plain"
          round
          :type="query.categoryId === category.id ? 'primary' : 'info'"
          class="category-chip"
          @click="toggleCategory(category.id)"
        >
          {{ category.name }}
        </el-tag>
      </div>
    </section>

    <section class="page-section">
      <div class="section-heading">
        <div>
          <h2 class="section-title">商品列表</h2>
          <p class="section-subtitle">当前页商品按选择条件实时排序。</p>
        </div>
        <span class="muted">共 {{ total }} 件商品</span>
      </div>

      <div class="page-grid-4" v-loading="loading">
        <article v-for="item in sortedProducts" :key="item.id" class="pet-card product-card" @click="$router.push(`/product/${item.id}`)">
          <img :src="item.mainImage || '/vite.svg'" :alt="item.name" class="cover-image product-cover" />
          <div class="product-body">
            <div class="product-head">
              <h3>{{ item.name }}</h3>
              <el-tag size="small" :type="item.productType === 1 ? 'warning' : 'info'">
                {{ item.productType === 1 ? '宠物' : '周边' }}
              </el-tag>
            </div>
            <p class="muted">{{ item.description || '精选推荐商品，支持加入购物车与立即购买。' }}</p>
            <div class="product-foot">
              <span class="price">¥{{ item.price }}</span>
              <span class="muted">销量 {{ item.sales || 0 }}</span>
            </div>
          </div>
        </article>
      </div>

      <el-empty v-if="!loading && sortedProducts.length === 0" description="暂无商品" />

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.page"
          :page-size="query.size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchProducts"
        />
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { getProductListApi } from '@/api/product'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const products = ref([])
const categories = ref([])
const total = ref(0)
const loading = ref(false)
const sortBy = ref('sales')

const query = reactive({
  page: 1,
  size: 12,
  categoryId: undefined,
  productType: undefined,
  keyword: ''
})

const sortedProducts = computed(() => {
  const list = [...products.value]
  if (sortBy.value === 'price') {
    return list.sort((left, right) => Number(left.price || 0) - Number(right.price || 0))
  }
  if (sortBy.value === 'latest') {
    return list.sort((left, right) => Number(right.id || 0) - Number(left.id || 0))
  }
  return list.sort((left, right) => Number(right.sales || 0) - Number(left.sales || 0))
})

watch([() => query.productType, () => query.categoryId], () => {
  query.page = 1
  fetchProducts()
})

onMounted(async () => {
  await loadCategories()
  await fetchProducts()
})

async function loadCategories() {
  try {
    const response = await request.get('/category/list')
    categories.value = response.data || [
      { id: 1, name: '狗狗用品' },
      { id: 2, name: '猫咪用品' },
      { id: 3, name: '喂养零食' },
      { id: 4, name: '宠物玩具' }
    ]
  } catch (error) {
    categories.value = [
      { id: 1, name: '狗狗用品' },
      { id: 2, name: '猫咪用品' },
      { id: 3, name: '喂养零食' },
      { id: 4, name: '宠物玩具' }
    ]
  }
}

async function fetchProducts() {
  loading.value = true
  try {
    const response = await getProductListApi({ ...query })
    const pageData = response.data || {}
    products.value = pageData.records || []
    total.value = pageData.total || 0
  } catch (error) {
    products.value = []
    total.value = 0
    ElMessage.warning('加载商品失败')
  } finally {
    loading.value = false
  }
}

function toggleCategory(id) {
  query.categoryId = query.categoryId === id ? undefined : id
}
</script>

<style scoped>
.product-page {
  padding-bottom: 24px;
}

.search-input {
  flex: 1;
  min-width: 280px;
}

.category-chip {
  cursor: pointer;
}

.product-card {
  overflow: hidden;
  cursor: pointer;
}

.product-cover {
  height: 220px;
}

.product-body {
  padding: 16px;
}

.product-head,
.product-foot {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: start;
}

.product-head h3 {
  font-size: 16px;
  line-height: 1.4;
}

.product-foot {
  margin-top: 12px;
  align-items: center;
}

.price {
  color: var(--pet-primary);
  font-size: 18px;
  font-weight: 800;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
