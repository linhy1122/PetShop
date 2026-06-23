import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/oauth/callback',
    name: 'OAuthCallback',
    component: () => import('@/views/OAuthCallback.vue'),
    meta: { title: '登录中...' }
  },
  {
    path: '/product/list',
    name: 'ProductList',
    component: () => import('@/views/ProductList.vue'),
    meta: { title: '商品列表' }
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('@/views/ProductDetail.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/store/map',
    name: 'StoreMap',
    component: () => import('@/views/StoreMap.vue'),
    meta: { title: '附近商店' }
  },
  {
    path: '/store/:id',
    name: 'StoreDetail',
    component: () => import('@/views/StoreDetail.vue'),
    meta: { title: '店铺详情' }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/Cart.vue'),
    meta: { title: '购物车', requireAuth: true }
  },
  {
    path: '/order/list',
    name: 'OrderList',
    component: () => import('@/views/OrderList.vue'),
    meta: { title: '我的订单', requireAuth: true }
  },
  {
    path: '/order/:id',
    name: 'OrderDetail',
    component: () => import('@/views/OrderDetail.vue'),
    meta: { title: '订单详情', requireAuth: true }
  },
  {
    path: '/video/list',
    name: 'VideoList',
    component: () => import('@/views/VideoList.vue'),
    meta: { title: '宠物视频' }
  },
  {
    path: '/video/:id',
    name: 'VideoPlayer',
    component: () => import('@/views/VideoPlayer.vue'),
    meta: { title: '视频播放' }
  },
  {
    path: '/user/center',
    name: 'UserCenter',
    component: () => import('@/views/UserCenter.vue'),
    meta: { title: '个人中心', requireAuth: true }
  },
  {
    path: '/ai/chat',
    name: 'AiChat',
    component: () => import('@/views/AiChat.vue'),
    meta: { title: '智能客服' }
  },
  // 管理员路由
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('@/views/admin/Dashboard.vue'),
    meta: { title: '管理后台', requireAuth: true, requireAdmin: true }
  },
  {
    path: '/admin/product',
    name: 'AdminProduct',
    component: () => import('@/views/admin/ProductManage.vue'),
    meta: { title: '商品管理', requireAuth: true, requireAdmin: true }
  },
  {
    path: '/admin/store',
    name: 'AdminStore',
    component: () => import('@/views/admin/StoreManage.vue'),
    meta: { title: '店铺管理', requireAuth: true, requireAdmin: true }
  },
  {
    path: '/admin/order',
    name: 'AdminOrder',
    component: () => import('@/views/admin/OrderManage.vue'),
    meta: { title: '订单管理', requireAuth: true, requireAdmin: true }
  },
  {
    path: '/admin/user',
    name: 'AdminUser',
    component: () => import('@/views/admin/UserManage.vue'),
    meta: { title: '用户管理', requireAuth: true, requireAdmin: true }
  },
  {
    path: '/admin/video',
    name: 'AdminVideo',
    component: () => import('@/views/admin/VideoManage.vue'),
    meta: { title: '视频管理', requireAuth: true, requireAdmin: true }
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 宠物商店` : '宠物商店'
  const token = localStorage.getItem('token')
  if (to.meta.requireAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
