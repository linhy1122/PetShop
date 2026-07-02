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
        <div class="reviews-header">
          <h3>商品评价</h3>
          <el-button type="primary" size="small" @click="openReviewDialog">填写评价</el-button>
        </div>
        <el-empty v-if="reviews.length === 0" description="暂无评价" />
        <div v-for="r in reviews" :key="r.id" class="review-item">
          <div class="review-user-row">
            <el-avatar :src="r.avatar" :size="40" class="review-avatar">
              <span class="avatar-placeholder">{{ (r.username || '匿')[0] }}</span>
            </el-avatar>
            <span class="review-username">{{ r.username || '匿名用户' }}</span>
          </div>
          <div class="review-meta">
            <el-rate :model-value="r.rating" disabled show-score />
            <span class="review-time">{{ formatDate(r.createTime) }}</span>
            <el-button
              v-if="canDelete(r)"
              type="danger"
              size="small"
              text
              @click="handleDeleteReview(r)"
            >删除</el-button>
          </div>
          <p class="review-content">{{ r.content }}</p>
          <div class="review-images" v-if="getReviewImages(r).length > 0">
            <el-image
              v-for="(url, i) in getReviewImages(r)"
              :key="i"
              :src="url"
              :preview-src-list="getReviewImages(r)"
              fit="cover"
              class="review-image"
            />
          </div>
        </div>
      </div>

      <!-- 填写评价弹窗 -->
      <el-dialog v-model="showReviewDialog" title="填写评价" width="550px" :close-on-click-modal="false">
        <div class="review-form">
          <div class="review-label">评分</div>
          <el-rate v-model="reviewForm.rating" show-score style="margin-bottom: 16px;" />

          <div class="review-label">评价内容</div>
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="请分享您的购物体验..."
            maxlength="500"
            show-word-limit
          />

          <div class="review-label" style="margin-top: 16px;">上传图片（最多9张）</div>
          <el-upload
            v-model:file-list="reviewForm.fileList"
            :action="uploadUrl"
            :headers="uploadHeaders"
            list-type="picture-card"
            :limit="9"
            :on-success="onUploadSuccess"
            :on-remove="onUploadRemove"
            :before-upload="beforeUpload"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </div>
        <template #footer>
          <el-button @click="showReviewDialog = false">取消</el-button>
          <el-button type="primary" :loading="reviewSubmitting" @click="handleSubmitReview">提交评价</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetailApi } from '@/api/product'
import { addToCartApi } from '@/api/cart'
import { submitReviewApi, deleteReviewApi } from '@/api/review'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const product = ref(null)
const reviews = ref([])
const quantity = ref(1)
const loading = ref(false)

// 评价弹窗
const showReviewDialog = ref(false)
const reviewSubmitting = ref(false)
const reviewForm = reactive({
  rating: 5,
  content: '',
  fileList: [],
  uploadedImages: []
})

const uploadUrl = '/api/file/upload'
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token') || ''}`
}

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

function getReviewImages(review) {
  try {
    const imgs = JSON.parse(review.images || '[]')
    return Array.isArray(imgs) ? imgs.filter(u => u) : []
  } catch {
    return []
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
}

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

// 评价相关
function openReviewDialog() {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }
  reviewForm.rating = 5
  reviewForm.content = ''
  reviewForm.fileList = []
  reviewForm.uploadedImages = []
  showReviewDialog.value = true
}

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

function onUploadSuccess(response) {
  if (response.code === 200 && response.data) {
    reviewForm.uploadedImages.push(response.data)
  } else {
    ElMessage.error('图片上传失败')
  }
}

function onUploadRemove(file) {
  const url = file.response?.data || file.url
  const idx = reviewForm.uploadedImages.indexOf(url)
  if (idx > -1) reviewForm.uploadedImages.splice(idx, 1)
}

// 判断当前用户能否删除该评价（自己的评价或管理员）
function canDelete(review) {
  if (!userStore.isLoggedIn()) return false
  if (userStore.isAdmin()) return true
  return review.userId === userStore.userInfo?.userId
}

async function handleDeleteReview(review) {
  try {
    await ElMessageBox.confirm('确定要删除这条评价吗？', '删除确认', { type: 'warning' })
    await deleteReviewApi(review.id, userStore.userInfo.userId, userStore.isAdmin() ? 'admin' : 'user')
    ElMessage.success('评价已删除')
    // 刷新评价列表
    const res = await request.get(`/review/product/${product.value.id}`, { params: { size: 10 } })
    reviews.value = res.data?.records || []
  } catch (e) { /* 取消或失败 */ }
}

async function handleSubmitReview() {
  if (!reviewForm.content.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  reviewSubmitting.value = true
  try {
    await submitReviewApi({
      productId: product.value.id,
      orderId: 0,
      userId: userStore.userInfo.userId,
      rating: reviewForm.rating,
      content: reviewForm.content,
      images: JSON.stringify(reviewForm.uploadedImages)
    })
    ElMessage.success('评价提交成功')
    showReviewDialog.value = false
    // 刷新评价列表
    const res = await request.get(`/review/product/${product.value.id}`, { params: { size: 10 } })
    reviews.value = res.data?.records || []
  } catch (e) {
    ElMessage.error(e.message || '评价提交失败')
  } finally {
    reviewSubmitting.value = false
  }
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
.reviews-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.reviews-header h3 { margin: 0; }
.review-item { padding: 16px 0; border-bottom: 1px solid #f0f0f0; }
.review-user-row { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.review-avatar { flex-shrink: 0; }
.avatar-placeholder { font-size: 14px; color: #fff; }
.review-username { font-weight: 500; color: #333; font-size: 14px; }
.review-meta { display: flex; align-items: center; gap: 16px; margin-bottom: 8px; }
.review-time { color: #999; font-size: 13px; }
.review-content { margin-top: 8px; color: #333; }
.review-images { display: flex; gap: 8px; flex-wrap: wrap; margin-top: 10px; }
.review-image { width: 80px; height: 80px; border-radius: 6px; overflow: hidden; }
.review-image :deep(img) { object-fit: cover; }
.review-label { font-weight: 500; margin-bottom: 8px; color: #333; }
</style>
