<template>
  <div class="home">
    <!-- 首页Banner -->
    <div class="hero-wrapper">
      <el-carousel height="500px" :interval="4000" arrow="always">
        <el-carousel-item v-for="(img, idx) in carouselImages" :key="idx">
          <div class="carousel-slide" :style="{ backgroundImage: `url(${img})` }"></div>
        </el-carousel-item>
      </el-carousel>
      <div class="hero-content">
        <h1>找到你最好的宠物伙伴</h1>
        <p>优质宠物 · 专业服务 · 周边好物 · 一站式宠物购物体验</p>
        <el-button type="primary" size="large" @click="$router.push('/product/list?type=1')">
          浏览宠物
        </el-button>
        <el-button size="large" @click="$router.push('/product/list?type=2')">
          宠物周边
        </el-button>
      </div>
    </div>

    <!-- 分类导航 -->
    <section class="categories section">
      <h2 class="section-title">商品分类</h2>
      <div class="category-grid">
        <div class="category-card" v-for="cat in petCategories" :key="cat.id"
             @click="$router.push(`/product/list?categoryId=${cat.id}`)">
          <span class="category-icon">{{ cat.icon }}</span>
          <span class="category-name">{{ cat.name }}</span>
        </div>
      </div>
    </section>

    <!-- 热门商品 -->
    <section class="hot-products section">
      <h2 class="section-title">热门推荐</h2>
      <div class="product-grid">
        <el-card v-for="item in hotProducts" :key="item.id" class="product-card"
                 shadow="hover" @click="$router.push(`/product/${item.id}`)">
          <img :src="item.mainImage || '/vite.svg'" :alt="item.name" class="product-img" />
          <div class="product-info">
            <h3>{{ item.name }}</h3>
            <p class="product-desc">{{ item.description }}</p>
            <div class="product-bottom">
              <span class="price">¥{{ item.price }}</span>
              <span class="sales">已售 {{ item.sales }}</span>
            </div>
          </div>
        </el-card>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHotProductsApi } from '@/api/product'
import { ElMessage } from 'element-plus'

const hotProducts = ref([])
const carouselImages = ref([
  'https://picsum.photos/seed/pet1/1200/500',
  'https://picsum.photos/seed/pet2/1200/500',
  'https://picsum.photos/seed/pet3/1200/500',
])

const petCategories = ref([
  { id: 1, icon: '🐕', name: '狗狗' },
  { id: 2, icon: '🐱', name: '猫咪' },
  { id: 3, icon: '🐹', name: '小宠' },
  { id: 4, icon: '🐟', name: '水族' },
  { id: 5, icon: '🐦', name: '鸟类' },
  { id: 6, icon: '🦴', name: '狗粮' },
  { id: 7, icon: '🐟', name: '猫粮' },
  { id: 8, icon: '🧸', name: '玩具' },
])

onMounted(async () => {
  try {
    hotProducts.value = (await getHotProductsApi(8)).data || []
  } catch (e) {
    ElMessage.warning('加载热门商品失败')
  }
})
</script>

<style scoped>
.hero-wrapper {
  position: relative;
}
.carousel-slide {
  height: 100%;
  background-size: cover;
  background-position: center;
}
.hero-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #fff;
}
.hero-content h1 { font-size: 36px; margin-bottom: 12px; }
.hero-content p { font-size: 18px; opacity: 0.9; margin-bottom: 24px; }

.section { max-width: 1200px; margin: 0 auto; padding: 40px 20px; }
.section-title { font-size: 24px; margin-bottom: 20px; text-align: center; }

.category-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
  gap: 16px;
}
.category-card {
  display: flex; flex-direction: column; align-items: center; gap: 8px;
  padding: 20px; background: #fff; border-radius: 12px;
  cursor: pointer; transition: all 0.3s;
}
.category-card:hover { transform: translateY(-4px); box-shadow: 0 8px 25px rgba(0,0,0,0.1); }
.category-icon { font-size: 36px; }
.category-name { font-size: 14px; color: #666; }

.product-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}
.product-card { cursor: pointer; border-radius: 12px; overflow: hidden; }
.product-img { width: 100%; height: 200px; object-fit: cover; }
.product-info { padding: 12px 0; }
.product-info h3 { font-size: 16px; margin-bottom: 6px; }
.product-desc { color: #999; font-size: 13px; margin-bottom: 10px;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-bottom { display: flex; justify-content: space-between; align-items: center; }
.price { color: #f56c6c; font-size: 18px; font-weight: bold; }
.sales { color: #999; font-size: 12px; }
</style>
