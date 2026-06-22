<template>
  <div class="admin-page">
    <el-container>
      <el-aside width="200px">
        <el-menu router default-active="/admin/dashboard" class="admin-menu">
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataBoard /></el-icon> 控制台
          </el-menu-item>
          <el-menu-item index="/admin/product">
            <el-icon><Goods /></el-icon> 商品管理
          </el-menu-item>
          <el-menu-item index="/admin/store">
            <el-icon><Shop /></el-icon> 店铺管理
          </el-menu-item>
          <el-menu-item index="/admin/order">
            <el-icon><Document /></el-icon> 订单管理
          </el-menu-item>
          <el-menu-item index="/admin/user">
            <el-icon><UserFilled /></el-icon> 用户管理
          </el-menu-item>
          <el-menu-item index="/admin/video">
            <el-icon><VideoCamera /></el-icon> 视频管理
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <h3>管理控制台</h3>
        <el-row :gutter="20" style="margin-top: 20px">
          <el-col :span="6">
            <el-statistic title="商品总数" :value="stats.products" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="店铺总数" :value="stats.stores" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="订单总数" :value="stats.orders" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="用户总数" :value="stats.users" />
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const stats = reactive({ products: 0, stores: 0, orders: 0, users: 0 })

// TODO: 从后端API加载统计数据
</script>

<style scoped>
.admin-page { min-height: calc(100vh - 260px); }
.admin-menu { height: 100%; }
</style>
