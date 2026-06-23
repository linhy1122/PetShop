<template>
  <el-container class="admin-shell">
    <el-aside width="248px" class="admin-aside">
      <div class="admin-brand">
        <div class="brand-mark">P</div>
        <div>
          <div class="brand-name">PetShop Admin</div>
          <div class="brand-subtitle">运营管理中心</div>
        </div>
      </div>

      <el-menu
        :default-active="activeMenu"
        class="admin-menu"
        router
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>Dashboard</span>
        </el-menu-item>
        <el-menu-item index="/admin/stats">
          <el-icon><TrendCharts /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item index="/admin/user">
          <el-icon><UserFilled /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/store">
          <el-icon><Shop /></el-icon>
          <span>商店管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/product">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/order">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/video">
          <el-icon><VideoCamera /></el-icon>
          <span>视频管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/ai">
          <el-icon><ChatDotRound /></el-icon>
          <span>AI 客服</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="admin-content">
      <el-header class="admin-header">
        <div>
          <div class="header-kicker">PetShop 管理后台</div>
          <div class="header-title">{{ pageTitle }}</div>
        </div>
        <div class="header-actions">
          <router-link to="/" class="ghost-link">返回前台</router-link>
          <el-dropdown>
            <span class="admin-user">
              {{ userStore.userInfo?.nickname || '管理员' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="go('/admin/dashboard')">Dashboard</el-dropdown-item>
                <el-dropdown-item @click="handleLogout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="admin-main">
        <slot />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title || '后台管理')

function go(path) {
  router.push(path)
}

function handleLogout() {
  userStore.logout()
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-shell {
  min-height: 100vh;
  background: linear-gradient(180deg, #f9f7ff 0%, #eef3ff 100%);
}

.admin-aside {
  padding: 20px 14px;
  background: linear-gradient(180deg, #211a35 0%, #2f2550 100%);
  color: #fff;
}

.admin-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 14px 20px;
}

.brand-mark {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #ff8f6b 0%, #8a6cff 100%);
  font-size: 20px;
  font-weight: 800;
}

.brand-name {
  font-weight: 700;
  letter-spacing: 0.2px;
}

.brand-subtitle {
  margin-top: 4px;
  color: rgba(255, 255, 255, 0.65);
  font-size: 12px;
}

.admin-menu {
  border: none;
  background: transparent;
}

.admin-menu :deep(.el-menu-item) {
  margin: 6px 0;
  border-radius: 14px;
  color: rgba(255, 255, 255, 0.82);
}

.admin-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.14);
  color: #fff;
}

.admin-content {
  min-height: 100vh;
}

.admin-header {
  height: 92px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(18px);
  border-bottom: 1px solid rgba(94, 77, 133, 0.08);
}

.header-kicker {
  color: #8a6cff;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.header-title {
  margin-top: 6px;
  font-size: 22px;
  font-weight: 700;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 18px;
}

.ghost-link {
  padding: 10px 14px;
  border-radius: 999px;
  color: #fff;
  background: linear-gradient(135deg, #ff8f6b, #8a6cff);
  box-shadow: 0 10px 22px rgba(138, 108, 255, 0.22);
}

.admin-user {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #3a3451;
  font-weight: 600;
}

.admin-main {
  padding: 24px 28px 32px;
}

@media (max-width: 1024px) {
  .admin-shell {
    display: block;
  }

  .admin-aside {
    width: 100% !important;
  }

  .admin-header {
    padding: 0 18px;
  }

  .admin-main {
    padding: 18px;
  }
}
</style>
