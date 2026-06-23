<template>
  <div class="page-shell review-page">
    <section class="hero-card review-hero" v-loading="loading">
      <div class="review-summary">
        <div class="status-pill">Review center</div>
        <h1>{{ product?.name || '商品评价' }}</h1>
        <p class="muted">{{ product?.description || '给喜欢的商品打分、写评价、晒图片。' }}</p>
      </div>
      <div class="review-stats">
        <div class="hero-meta-card">
          <div class="hero-meta-label">平均评分</div>
          <div class="hero-meta-value">{{ averageRating }}</div>
        </div>
        <div class="hero-meta-card">
          <div class="hero-meta-label">评价数量</div>
          <div class="hero-meta-value">{{ totalReviews }}</div>
        </div>
        <div class="hero-meta-card">
          <el-button type="primary" @click="$router.push(`/product/${productId}`)">返回商品详情</el-button>
        </div>
      </div>
    </section>

    <section class="page-section split-layout">
      <div class="panel-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">提交评价</h2>
            <p class="section-subtitle">星级评分、文字评价和图片上传都已准备好。</p>
          </div>
        </div>

        <el-form label-position="top" :model="form" class="review-form">
          <el-form-item label="评分">
            <el-rate v-model="form.rating" :texts="['很差', '较差', '一般', '满意', '超赞']" show-text />
          </el-form-item>

          <el-form-item label="评价内容">
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="6"
              placeholder="分享你的真实使用体验，帮助更多铲屎官选择合适商品。"
            />
          </el-form-item>

          <el-form-item label="评价图片">
            <el-upload
              v-model:file-list="fileList"
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :limit="6"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>

          <el-button type="primary" size="large" :loading="submitting" @click="submitReview">
            提交评价
          </el-button>
        </el-form>
      </div>

      <div class="panel-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">历史评价</h2>
            <p class="section-subtitle">当前商品的最新评论。</p>
          </div>
        </div>

        <el-empty v-if="reviews.length === 0" description="暂无评价" />
        <div class="review-list">
          <article v-for="item in reviews" :key="item.id" class="review-item soft-card">
            <div class="review-item-head">
              <div>
                <strong>{{ item.nickname || `用户 ${item.userId}` }}</strong>
                <div class="muted">{{ item.createTime }}</div>
              </div>
              <el-rate :model-value="item.rating" disabled show-score />
            </div>
            <p class="review-content">{{ item.content }}</p>
            <p v-if="item.reply" class="review-reply">商家回复：{{ item.reply }}</p>
          </article>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const productId = route.params.productId

const loading = ref(false)
const submitting = ref(false)
const product = ref(null)
const reviews = ref([])
const fileList = ref([])

const form = reactive({
  rating: 5,
  content: ''
})

const totalReviews = computed(() => reviews.value.length)
const averageRating = computed(() => {
  if (reviews.value.length === 0) return '5.0'
  const sum = reviews.value.reduce((acc, item) => acc + Number(item.rating || 0), 0)
  return (sum / reviews.value.length).toFixed(1)
})

onMounted(fetchData)

async function fetchData() {
  loading.value = true
  try {
    const [productRes, reviewRes] = await Promise.all([
      request.get(`/product/${productId}`),
      request.get(`/review/product/${productId}`, { params: { page: 1, size: 20 } })
    ])
    product.value = productRes.data
    reviews.value = reviewRes.data?.records || []
  } catch (error) {
    ElMessage.error('加载评价页失败')
  } finally {
    loading.value = false
  }
}

async function submitReview() {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }

  if (!form.content.trim()) {
    ElMessage.warning('请先填写评价内容')
    return
  }

  submitting.value = true
  try {
    await request.post('/review/submit', {
      userId: userStore.userInfo.userId,
      productId: Number(productId),
      rating: form.rating,
      content: form.content,
      images: JSON.stringify(fileList.value.map((item) => item.name || item.url || 'image'))
    })
    ElMessage.success('评价提交成功')
    form.content = ''
    fileList.value = []
    await fetchData()
  } catch (error) {
    ElMessage.error(error.message || '评价提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.review-page {
  padding-bottom: 24px;
}

.review-hero {
  padding: 28px;
  display: flex;
  justify-content: space-between;
  gap: 20px;
}

.review-summary h1 {
  margin: 14px 0 10px;
  font-size: 34px;
}

.review-stats {
  display: grid;
  gap: 12px;
  min-width: 260px;
}

.review-list {
  display: grid;
  gap: 12px;
}

.review-item-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: start;
  margin-bottom: 12px;
}

.review-content {
  line-height: 1.8;
  color: var(--pet-text);
}

.review-reply {
  margin-top: 12px;
  color: #8a6cff;
  background: rgba(138, 108, 255, 0.08);
  padding: 10px 12px;
  border-radius: 14px;
}

@media (max-width: 1024px) {
  .review-hero {
    flex-direction: column;
  }
}
</style>
