<template>
  <div class="product-list-page">
    <div class="container">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-radio-group v-model="query.productType" @change="fetchData">
          <el-radio-button :value="undefined">全部</el-radio-button>
          <el-radio-button :value="1">🐾 宠物</el-radio-button>
          <el-radio-button :value="2">🛍️ 宠物周边</el-radio-button>
        </el-radio-group>
        <el-input v-model="query.keyword" placeholder="搜索商品..." clearable
                  style="width: 240px; margin-left: 20px"
                  prefix-icon="Search" @change="fetchData" />
      </div>

      <!-- 分类标签 -->
      <div class="category-tags" v-if="categories.length">
        <el-tag v-for="cat in categories" :key="cat.id"
                :type="query.categoryId === cat.id ? 'primary' : 'info'"
                class="tag" @click="selectCategory(cat.id)">
          {{ cat.name }}
        </el-tag>
      </div>

      <!-- 商品网格 -->
      <div class="product-grid" v-loading="loading">
        <el-empty v-if="!loading && products.length === 0" description="暂无商品" />
        <el-card v-for="item in products" :key="item.id" class="product-card"
                 shadow="hover" @click="$router.push(`/product/${item.id}`)">
          <img :src="item.mainImage || '/vite.svg'" :alt="item.name" class="product-img" />
          <div class="product-info">
            <h3>{{ item.name }}
              <el-tag size="small" :type="item.productType === 1 ? 'warning' : 'info'">
                {{ item.productType === 1 ? '宠物' : '周边' }}
              </el-tag>
            </h3>
            <p class="desc">{{ item.description }}</p>
            <div class="bottom">
              <span class="price">¥{{ item.price }}</span>
              <span class="stock" v-if="item.productType === 2">
                库存: {{ item.stock }}
              </span>
            </div>
            <el-button type="primary" size="small" class="cart-btn"
                       @click.stop="handleAddToCart(item)">
              🛒 加入购物车
            </el-button>
          </div>
        </el-card>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > 0">
        <el-pagination v-model:current-page="query.page" :page-size="query.size"
                       :total="total" layout="total, prev, pager, next"
                       @current-change="fetchData" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductListApi } from '@/api/product'
import { addToCartApi } from '@/api/cart'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const loading = ref(false)
const products = ref([])
const total = ref(0)
const categories = ref([])

const query = reactive({
  page: 1, size: 12, categoryId: route.query.categoryId || undefined,
  productType: route.query.type ? Number(route.query.type) : undefined,
  keyword: ''
})

onMounted(async () => {
  await loadCategories()
  await fetchData()
})

async function loadCategories() {
  try {
    const res = await request.get('/category/list')
    categories.value = res.data || []
  } catch (e) { /* ignore */ }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getProductListApi({ ...query })
    products.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    products.value = []
  } finally {
    loading.value = false
  }
}

function selectCategory(id) {
  query.categoryId = query.categoryId === id ? undefined : id
  query.page = 1
  fetchData()
}

async function handleAddToCart(product) {
  if (!userStore.isLoggedIn()) {
    router.push({ path: '/login', query: { redirect: router.currentRoute.value.fullPath } })
    return
  }
  try {
    await addToCartApi(userStore.userInfo.userId, product.id, 1)
    cartStore.fetchCart(userStore.userInfo.userId)
    ElMessage.success('已加入购物车')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '添加失败')
  }
}
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 20px; }
.filter-bar { display: flex; align-items: center; margin-bottom: 20px; }
.category-tags { margin-bottom: 20px; }
.category-tags .tag { cursor: pointer; margin: 0 8px 8px 0; }
.product-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}
.product-card { cursor: pointer; border-radius: 12px; overflow: hidden; }
.product-img { width: 100%; height: 200px; object-fit: cover; }
.product-info { padding: 12px 0; }
.product-info h3 { font-size: 16px; margin-bottom: 6px; display: flex; align-items: center; gap: 8px; }
.desc { color: #999; font-size: 13px; margin-bottom: 10px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.bottom { display: flex; justify-content: space-between; align-items: center; }
.price { color: #f56c6c; font-size: 18px; font-weight: bold; }
.stock { color: #999; font-size: 12px; }
.cart-btn { width: 100%; margin-top: 10px; }
.pagination { display: flex; justify-content: center; margin-top: 30px; }
</style>
