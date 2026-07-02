<template>
  <view class="dashboard-page">
    <view class="page-title">管理控制台</view>

    <view class="stats-grid">
      <view class="stat-card">
        <text class="stat-num">{{ stats.products }}</text>
        <text class="stat-label">商品总数</text>
      </view>
      <view class="stat-card">
        <text class="stat-num">{{ stats.stores }}</text>
        <text class="stat-label">店铺总数</text>
      </view>
      <view class="stat-card">
        <text class="stat-num">{{ stats.orders }}</text>
        <text class="stat-label">订单总数</text>
      </view>
      <view class="stat-card">
        <text class="stat-num">{{ stats.users }}</text>
        <text class="stat-label">用户总数</text>
      </view>
    </view>

    <!-- 管理菜单 -->
    <view class="admin-menu">
      <view class="menu-item" @click="goPage('/pages/admin/product/product')">
        <text class="menu-icon">📦</text>
        <text class="menu-label">商品管理</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goPage('/pages/admin/order/order')">
        <text class="menu-icon">📋</text>
        <text class="menu-label">订单管理</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goPage('/pages/admin/video/video')">
        <text class="menu-icon">🎬</text>
        <text class="menu-label">视频管理</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { adminStats } from '@/mock'

const stats = reactive({ products: 0, stores: 0, orders: 0, users: 0 })

onMounted(async () => {
  try {
    const [products, stores, orders, users] = await Promise.all([
      request.get('/product/list', { params: { size: 1 } }),
      request.get('/store/list', { params: { size: 1 } }),
      request.get('/order/list', { params: { size: 1 } }),
      request.get('/user/list', { params: { size: 1 } })
    ])
    stats.products = products.data?.total || 0
    stats.stores = stores.data?.total || 0
    stats.orders = orders.data?.total || 0
    stats.users = users.data?.total || 0
  } catch (e) {
    Object.assign(stats, adminStats)
  }
})

function goPage(url) {
  uni.navigateTo({ url })
}
</script>

<style scoped>
.dashboard-page {
  padding: 24rpx;
}

.page-title {
  font-size: 36rpx;
  font-weight: 700;
  margin-bottom: 32rpx;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  margin-bottom: 32rpx;
}

.stat-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 40rpx 20rpx;
  text-align: center;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04);
}

.stat-num {
  font-size: 52rpx;
  font-weight: 800;
  color: #FF6B35;
  display: block;
}

.stat-label {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
  display: block;
}

.admin-menu {
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 32rpx 24rpx;
  border-bottom: 1rpx solid #f5f5f5;
  gap: 20rpx;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  font-size: 40rpx;
}

.menu-label {
  flex: 1;
  font-size: 30rpx;
}

.menu-arrow {
  font-size: 40rpx;
  color: #ccc;
}
</style>
