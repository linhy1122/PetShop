<template>
  <div class="product-detail-page" v-loading="loading">
    <div class="container" v-if="product">
      <div class="detail-top">
        <div class="gallery">
          <el-image :src="currentImage" :preview-src-list="allImages"
                    fit="cover" class="main-image" />
          <div class="thumbnail-strip" v-if="allImages.length > 1">
            <div v-for="(url, i) in allImages" :key="i"
                 class="thumbnail" :class="{ active: url === currentImage }"
                 @click="currentImage = url">
              <img :src="url" alt="" />
            </div>
          </div>
        </div>
        <div class="info">
          <h1>{{ product.name }}</h1>
          <el-tag :type="product.productType === 1 ? 'warning' : 'info'">
            {{ product.productType === 1 ? '宠物（唯一）' : '宠物周边' }}
          </el-tag>
          <div class="price-section">
            <span class="price">¥{{ product.price }}</span>
            <span class="sales">已售 {{ product.sales }}</span>
          </div>
          <div class="meta" v-if="product.productType === 1">
            <p>品种：{{ product.breed || '未知' }}</p>
            <p>年龄：{{ product.age || '未知' }}</p>
            <p>性别：{{ ['未知', '公', '母'][product.gender] || '未知' }}</p>
          </div>
          <div class="meta" v-else>
            <p>库存：{{ product.stock }} 件</p>
          </div>
          <div class="actions">
            <el-input-number v-model="quantity" :min="1"
                             :max="product.productType === 1 ? 1 : product.stock" />
            <el-button type="primary" size="large" @click="addToCart">加入购物车</el-button>
            <el-button type="warning" size="large" @click="buyNow">立即购买</el-button>
          </div>
        </div>
      </div>

      <!-- 商品详情 -->
      <div class="detail-content">
        <h3>商品详情</h3>
        <div v-html="product.detail || product.description"></div>
      </div>

      <!-- 评价列表 -->
      <div class="reviews">
        <h3>商品评价</h3>
        <el-empty v-if="reviews.length === 0" description="暂无评价" />
        <div v-for="r in reviews" :key="r.id" class="review-item">
          <div class="review-header">
            <el-rate :model-value="r.rating" disabled show-score />
            <span class="review-time">{{ r.createTime }}</span>
          </div>
          <p class="review-content">{{ r.content }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetailApi } from '@/api/product'
import { addToCartApi } from '@/api/cart'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const product = ref(null)
const reviews = ref([])
const quantity = ref(1)
const loading = ref(false)

// 图集：合并主图 + 额外图片
const allImages = computed(() => {
  if (!product.value) return []
  const list = []
  if (product.value.mainImage) list.push(product.value.mainImage)
  try {
    const extras = JSON.parse(product.value.images || '[]')
    if (Array.isArray(extras)) {
      extras.filter(u => u).forEach(u => { if (!list.includes(u)) list.push(u) })
    }
  } catch { /* ignore */ }
  return list
})
const currentImage = ref('')
watch(() => product.value, (p) => {
  if (p) currentImage.value = p.mainImage || ''
})

onMounted(async () => {
  const id = route.params.id
  loading.value = true
  try {
    product.value = (await getProductDetailApi(id)).data
    const res = await request.get(`/review/product/${id}`, { params: { size: 10 } })
    reviews.value = res.data?.records || []
  } catch (e) {
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
})

async function addToCart() {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }
  try {
    await addToCartApi(userStore.userInfo.userId, product.value.id, quantity.value)
    ElMessage.success('已加入购物车')
  } catch (e) {
    ElMessage.error(e.message || '添加失败')
  }
}

function buyNow() {
  addToCart()
  router.push('/cart')
}
</script>

<style scoped>
.container { max-width: 1100px; margin: 0 auto; padding: 20px; }
.detail-top { display: flex; gap: 40px; margin-bottom: 40px; }
.gallery { flex-shrink: 0; }
.main-image { width: 450px; height: 350px; border-radius: 12px; overflow: hidden; }
.main-image :deep(img) { object-fit: cover; }
.thumbnail-strip {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}
.thumbnail {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color .2s, transform .2s, box-shadow .2s;
  flex-shrink: 0;
}
.thumbnail:hover {
  transform: scale(1.08);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.12);
}
.thumbnail.active {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}
.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.info { flex: 1; }
.info h1 { font-size: 24px; margin-bottom: 12px; }
.price-section { margin: 20px 0; }
.price { font-size: 28px; color: #f56c6c; font-weight: bold; margin-right: 16px; }
.sales { color: #999; font-size: 14px; }
.meta { margin: 16px 0; }
.meta p { line-height: 2; color: #666; }
.actions { display: flex; gap: 12px; align-items: center; margin-top: 24px; }
.detail-content { margin: 40px 0; padding: 20px; background: #fff; border-radius: 12px; }
.detail-content h3 { margin-bottom: 16px; }
.reviews { padding: 20px; background: #fff; border-radius: 12px; }
.review-item { padding: 16px 0; border-bottom: 1px solid #f0f0f0; }
.review-header { display: flex; justify-content: space-between; align-items: center; }
.review-time { color: #999; font-size: 13px; }
.review-content { margin-top: 8px; color: #333; }
</style>
