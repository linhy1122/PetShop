<template>
  <header class="topbar">
    <div class="topbar-inner">
      <router-link to="/" class="brand">
        <div class="brand-icon">
          <el-icon><ShoppingBag /></el-icon>
        </div>
        <div>
          <div class="brand-title">PetShop</div>
          <div class="brand-subtitle">宠物商城 · 温暖陪伴</div>
        </div>
      </router-link>

      <nav class="nav-links">
        <router-link v-for="item in navItems" :key="item.to" :to="item.to" class="nav-link">
          {{ item.label }}
        </router-link>
      </nav>

      <div class="nav-actions">
        <router-link to="/ai/chat" class="ai-entry">
          <el-icon><ChatDotRound /></el-icon>
          AI 助手
        </router-link>

        <template v-if="userStore.isLoggedIn()">
          <router-link to="/cart" class="icon-btn">
            <el-badge :value="cartStore.cartCount" :hidden="cartStore.cartCount === 0">
              <el-icon :size="20"><ShoppingCart /></el-icon>
            </el-badge>
          </router-link>

          <el-dropdown>
            <span class="user-chip">
              {{ userStore.userInfo?.nickname || '用户' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/user/center')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/order/list')">我的订单</el-dropdown-item>
                <el-dropdown-item v-if="userStore.isAdmin()" @click="$router.push('/admin/dashboard')" divided>
                  管理后台
                </el-dropdown-item>
                <el-dropdown-item @click="handleLogout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <template v-else>
          <el-button round @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" round @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const userStore = useUserStore()
const cartStore = useCartStore()

const navItems = [
  { label: '首页', to: '/' },
  { label: '商品商城', to: '/product/list' },
  { label: '商店列表', to: '/store/list' },
  { label: '视频推荐', to: '/video/list' },
]

onMounted(() => {
  if (userStore.isLoggedIn() && userStore.userInfo?.userId) {
    cartStore.fetchCart(userStore.userInfo.userId)
  }
})

function handleLogout() {
  userStore.logout()
  cartStore.cartCount = 0
  location.reload()
}
</script>

<style scoped>
.topbar {
  position: fixed;
  inset: 0 0 auto;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.76);
  border-bottom: 1px solid rgba(94, 77, 133, 0.08);
  backdrop-filter: blur(18px);
}

.topbar-inner {
  width: min(1240px, calc(100vw - 32px));
  height: 88px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 180px;
}

.brand-icon {
  width: 44px;
  height: 44px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  color: #fff;
  background: linear-gradient(135deg, #ff8f6b, #8a6cff);
  box-shadow: 0 10px 20px rgba(138, 108, 255, 0.22);
}

.brand-title {
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 0.3px;
}

.brand-subtitle {
  margin-top: 2px;
  color: #7b748d;
  font-size: 12px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.nav-link {
  padding: 10px 14px;
  border-radius: 999px;
  color: #4a4461;
  font-weight: 600;
  transition: all 0.2s ease;
}

.nav-link.router-link-active {
  background: rgba(255, 143, 107, 0.12);
  color: #c95a3d;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-entry,
.icon-btn,
.user-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  border-radius: 999px;
  background: rgba(138, 108, 255, 0.1);
  color: #5c4a91;
}

.ai-entry {
  background: linear-gradient(135deg, rgba(255, 143, 107, 0.16), rgba(138, 108, 255, 0.14));
}

.icon-btn {
  padding: 10px 12px;
  cursor: pointer;
}

.user-chip {
  cursor: pointer;
  background: rgba(255, 143, 107, 0.11);
}

@media (max-width: 1100px) {
  .topbar-inner {
    height: auto;
    flex-wrap: wrap;
    padding: 16px 0;
  }

  .nav-links {
    order: 3;
    width: 100%;
    justify-content: flex-start;
    overflow-x: auto;
  }
}
</style>
