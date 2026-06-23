import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Home', component: () => import('@/views/Home.vue'), meta: { title: '首页' } },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { title: '用户登录' } },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue'), meta: { title: '用户注册' } },
  { path: '/admin/login', name: 'AdminLogin', component: () => import('@/views/AdminLogin.vue'), meta: { title: '管理员登录' } },
  { path: '/product/list', name: 'ProductList', component: () => import('@/views/ProductList.vue'), meta: { title: '商品商城' } },
  { path: '/product/:id', name: 'ProductDetail', component: () => import('@/views/ProductDetail.vue'), meta: { title: '商品详情' } },
  { path: '/review/:productId', name: 'ReviewList', component: () => import('@/views/ReviewList.vue'), meta: { title: '商品评价', requireAuth: true } },
  { path: '/store/list', name: 'StoreList', component: () => import('@/views/StoreList.vue'), meta: { title: '商店列表' } },
  { path: '/store/map', name: 'StoreMap', component: () => import('@/views/StoreMap.vue'), meta: { title: '附近商店' } },
  { path: '/store/:id', name: 'StoreDetail', component: () => import('@/views/StoreDetail.vue'), meta: { title: '商店详情' } },
  { path: '/cart', name: 'Cart', component: () => import('@/views/Cart.vue'), meta: { title: '购物车', requireAuth: true } },
  { path: '/order/list', name: 'OrderList', component: () => import('@/views/OrderList.vue'), meta: { title: '我的订单', requireAuth: true } },
  { path: '/order/:id', name: 'OrderDetail', component: () => import('@/views/OrderDetail.vue'), meta: { title: '订单详情', requireAuth: true } },
  { path: '/video/list', name: 'VideoList', component: () => import('@/views/VideoList.vue'), meta: { title: '视频推荐' } },
  { path: '/video/:id', name: 'VideoPlayer', component: () => import('@/views/VideoPlayer.vue'), meta: { title: '视频详情' } },
  { path: '/user/center', name: 'UserCenter', component: () => import('@/views/UserCenter.vue'), meta: { title: '个人中心', requireAuth: true } },
  { path: '/ai/chat', name: 'AiChat', component: () => import('@/views/AiChat.vue'), meta: { title: 'AI 智能助手' } },
  { path: '/admin/dashboard', name: 'AdminDashboard', component: () => import('@/views/admin/Dashboard.vue'), meta: { title: 'Dashboard', requireAuth: true, requireAdmin: true } },
  { path: '/admin/stats', name: 'AdminStats', component: () => import('@/views/admin/DataStats.vue'), meta: { title: '数据统计', requireAuth: true, requireAdmin: true } },
  { path: '/admin/product', name: 'AdminProduct', component: () => import('@/views/admin/ProductManage.vue'), meta: { title: '商品管理', requireAuth: true, requireAdmin: true } },
  { path: '/admin/store', name: 'AdminStore', component: () => import('@/views/admin/StoreManage.vue'), meta: { title: '商店管理', requireAuth: true, requireAdmin: true } },
  { path: '/admin/order', name: 'AdminOrder', component: () => import('@/views/admin/OrderManage.vue'), meta: { title: '订单管理', requireAuth: true, requireAdmin: true } },
  { path: '/admin/user', name: 'AdminUser', component: () => import('@/views/admin/UserManage.vue'), meta: { title: '用户管理', requireAuth: true, requireAdmin: true } },
  { path: '/admin/video', name: 'AdminVideo', component: () => import('@/views/admin/VideoManage.vue'), meta: { title: '视频管理', requireAuth: true, requireAdmin: true } },
  { path: '/admin/ai', name: 'AdminAI', component: () => import('@/views/admin/AIManage.vue'), meta: { title: 'AI 客服', requireAuth: true, requireAdmin: true } },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - PetShop 宠物商城` : 'PetShop 宠物商城'
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')

  if (to.meta.requireAdmin) {
    if (!token || userInfo?.role !== 'admin') {
      next('/admin/login')
      return
    }
  } else if (to.meta.requireAuth && !token) {
    next('/login')
    return
  }

  next()
})

export default router
