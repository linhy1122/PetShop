<template>
  <el-menu :default-active="activeIndex" class="navbar" mode="horizontal" router  :ellipsis="false">
    <div class="nav-left">
      <router-link to="/" class="logo">
        <el-icon><ShoppingBag /></el-icon>
        <span>PetShop 宠物商店</span>
      </router-link>
    </div>

    <div class="nav-center">
      <el-menu-item index="/">首页</el-menu-item>
      <el-menu-item index="/product/list">宠物商城</el-menu-item>
      <el-menu-item index="/store/map">附近商店</el-menu-item>
      <el-menu-item index="/video/list">宠物视频</el-menu-item>
      <el-menu-item index="/ai/chat">
        <el-icon><ChatDotRound /></el-icon>
        AI客服
      </el-menu-item>
    </div>

    <div class="nav-right">
      <template v-if="userStore.isLoggedIn()">
        <router-link to="/cart" class="cart-btn">
          <el-badge :value="cartStore.cartCount" :hidden="cartStore.cartCount === 0">
            <el-icon :size="22"><ShoppingCart /></el-icon>
          </el-badge>
        </router-link>
        <el-dropdown>
          <span class="user-dropdown">
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
        <el-button type="primary" size="small" @click="$router.push('/login')">登录</el-button>
        <el-button size="small" @click="$router.push('/register')">注册</el-button>
      </template>
    </div>
  </el-menu>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()

const activeIndex = computed(() => route.path)

onMounted(() => {
  if (userStore.isLoggedIn()) {
    cartStore.fetchCart(userStore.userInfo.userId)
  }
})

function handleLogout() {
  userStore.logout()
  location.reload()
}
</script>

<style scoped>
.navbar {
  position: fixed; top: 0; left: 0; right: 0; z-index: 1000;
  display: flex; align-items: center; padding: 0 20px;
  height: 60px; border-bottom: 1px solid #e6e6e6;
}

.nav-left .logo {
  display: flex; align-items: center; gap: 6px;
  font-size: 20px; font-weight: bold; color: #409eff;
  margin-right: 30px;
}

.nav-center { display: flex; flex: 1; }

.nav-right {
  display: flex; align-items: center; gap: 15px; margin-left: auto;
}

.cart-btn { color: #555; display: flex; align-items: center; }
.user-dropdown { cursor: pointer; display: flex; align-items: center; gap: 4px; color: #555; }
</style>
