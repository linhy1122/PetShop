<template>
  <div class="admin-page">
    <el-container>
      <el-aside width="200px">
        <el-menu router :default-active="route.path" class="admin-menu">
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
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<style scoped>
.admin-page { min-height: calc(100vh - 260px); }
.admin-menu { height: 100%; }
</style>
