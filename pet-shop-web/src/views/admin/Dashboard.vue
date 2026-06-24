<template>
  <div>
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
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const stats = reactive({ products: 0, stores: 0, orders: 0, users: 0 })

// TODO: 从后端API加载统计数据
</script>
