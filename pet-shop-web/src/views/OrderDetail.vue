<template>
  <div class="order-detail-page">
    <div class="container" v-loading="loading">
      <h2>订单详情</h2>
      <el-card v-if="order">
        <p><strong>订单编号：</strong>{{ order.orderNo }}</p>
        <p><strong>订单状态：</strong><el-tag>{{ statusMap[order.status] }}</el-tag></p>
        <p><strong>总金额：</strong><span class="price">¥{{ order.totalAmount }}</span></p>
        <p><strong>实付金额：</strong>¥{{ order.payAmount }}</p>
        <p><strong>支付方式：</strong>{{ order.payMethod || '未支付' }}</p>
        <p><strong>创建时间：</strong>{{ order.createTime }}</p>
        <p v-if="order.logisticsCompany"><strong>物流公司：</strong>{{ order.logisticsCompany }}</p>
        <p v-if="order.logisticsNo"><strong>物流单号：</strong>{{ order.logisticsNo }}</p>
        <div class="order-actions" style="margin-top: 20px">
          <el-button v-if="order.status === 0" type="primary" @click="$emit('pay', order.id)">去支付</el-button>
          <el-button v-if="order.status === 0" @click="$emit('cancel', order.id)">取消订单</el-button>
          <el-button v-if="order.status === 2" type="success" @click="$emit('receive', order.id)">确认收货</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getOrderDetailApi } from '@/api/order'

const route = useRoute()
const order = ref(null)
const loading = ref(false)
const statusMap = { 0: '待支付', 1: '待发货', 2: '待收货', 3: '待评价', 4: '已完成', '-1': '已取消', '-2': '退单中', '-3': '退单完成', '-4': '已退单' }

onMounted(async () => {
  loading.value = true
  try {
    order.value = (await getOrderDetailApi(route.params.id)).data
  } finally { loading.value = false }
})
</script>

<style scoped>
.container { max-width: 800px; margin: 0 auto; padding: 20px; }
h2 { margin-bottom: 20px; }
p { line-height: 2.5; }
.price { color: #f56c6c; font-weight: bold; font-size: 18px; }
</style>
