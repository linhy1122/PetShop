<template>
  <view class="admin-order-page">
    <view class="order-list">
      <view class="order-card" v-for="order in tableData" :key="order.id">
        <view class="order-header">
          <text class="order-no">{{ order.orderNo }}</text>
          <text class="order-status" :class="'s-' + order.status">{{ statusText(order.status) }}</text>
        </view>
        <view class="order-body">
          <text>金额：¥{{ order.totalAmount }}</text>
          <text>时间：{{ order.createTime }}</text>
        </view>
        <view class="order-actions">
          <button v-if="order.status === 1" size="mini" class="btn-primary" @click="handleDeliver(order)">发货</button>
          <button v-if="order.status === -2" size="mini" class="btn-success" @click="handleAudit(order, true)">同意退货</button>
          <button v-if="order.status === -2" size="mini" class="btn-danger" @click="handleAudit(order, false)">拒绝退货</button>
          <button v-if="order.status === 3" size="mini" class="btn-warning" @click="handleAdminRefund(order)">强制退单</button>
        </view>
      </view>
      <EmptyState v-if="!loading && tableData.length === 0" description="暂无订单" />
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import EmptyState from '@/components/EmptyState.vue'

const tableData = ref([])
const loading = ref(false)

const statusMap = { 0: '待支付', 1: '待发货', 2: '待收货', 3: '待评价', 4: '已完成', '-1': '已取消', '-2': '退单中', '-3': '退单通过', '-4': '已退单' }

function statusText(s) { return statusMap[s] || '未知' }

onMounted(() => fetchOrders())

async function fetchOrders() {
  loading.value = true
  try {
    const res = await request.get('/order/list', { params: { size: 100 } })
    tableData.value = res.data?.records || []
  } finally { loading.value = false }
}

async function handleDeliver(order) {
  const res = await uni.showModal({ title: '发货', content: '确认发货？' })
  if (res.confirm) {
    await request.put(`/order/${order.id}/deliver`, null, {
      params: { logisticsCompany: '顺丰快递', logisticsNo: 'SF' + Date.now() }
    })
    uni.showToast({ title: '发货成功', icon: 'success' })
    fetchOrders()
  }
}

async function handleAudit(order, approved) {
  await request.put(`/order/${order.id}/audit-refund`, null, { params: { approved } })
  uni.showToast({ title: approved ? '退货审核通过' : '退货审核拒绝', icon: 'success' })
  fetchOrders()
}

async function handleAdminRefund(order) {
  const res = await uni.showModal({ title: '强制退单', content: '确定强制退单？' })
  if (res.confirm) {
    await request.put(`/order/${order.id}/admin-refund`, null, { params: { reason: '管理员操作' } })
    uni.showToast({ title: '退单成功', icon: 'success' })
    fetchOrders()
  }
}
</script>

<style scoped>
.admin-order-page {
  padding: 24rpx;
}

.order-card {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.order-no {
  font-size: 26rpx;
  color: #666;
}

.order-status {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
}

.s-0 { background: #FFF3E0; color: #F59E0B; }
.s-1 { background: #E3F2FD; color: #3B82F6; }
.s-2 { background: #E8F5E9; color: #10B981; }
.s-3 { background: #F3E5F5; color: #9C27B0; }
.s-4, .s--1, .s--3, .s--4 { background: #ECEFF1; color: #607D8B; }
.s--2 { background: #FCE4EC; color: #E91E63; }

.order-body text {
  display: block;
  font-size: 26rpx;
  color: #666;
  line-height: 1.8;
}

.order-actions {
  display: flex;
  gap: 12rpx;
  margin-top: 16rpx;
  flex-wrap: wrap;
}

.btn-primary { background: #FF6B35; color: #fff; border: none; }
.btn-success { background: #10B981; color: #fff; border: none; }
.btn-danger { background: #EF4444; color: #fff; border: none; }
.btn-warning { background: #F59E0B; color: #fff; border: none; }
</style>
