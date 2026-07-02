<template>
  <view class="order-card" @click="goDetail">
    <view class="order-header">
      <text class="order-no">订单号：{{ order.orderNo }}</text>
      <text class="order-status" :class="'status-' + order.status">{{ statusText }}</text>
    </view>
    <view class="order-body">
      <text class="order-amount">总金额：<text class="price">¥{{ order.totalAmount }}</text></text>
      <text class="order-time">{{ order.createTime }}</text>
    </view>
    <view class="order-actions" @click.stop>
      <button v-if="order.status === 0" class="btn btn-primary" size="mini" @click="$emit('pay', order)">去支付</button>
      <button v-if="order.status === 0" class="btn btn-default" size="mini" @click="$emit('cancel', order)">取消</button>
      <button v-if="order.status === 2" class="btn btn-success" size="mini" @click="$emit('receive', order)">确认收货</button>
      <button v-if="[2,3].includes(order.status)" class="btn btn-warning" size="mini" @click="$emit('refund', order)">申请退单</button>
      <button class="btn btn-default" size="mini" @click="goDetail">查看详情</button>
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  order: { type: Object, required: true }
})

defineEmits(['pay', 'cancel', 'receive', 'refund'])

const statusMap = { 0: '待支付', 1: '待发货', 2: '待收货', 3: '待评价', 4: '已完成', '-1': '已取消', '-2': '退单中', '-3': '退单完成', '-4': '已退单' }

const statusText = computed(() => statusMap[props.order.status] || '未知')

import { computed } from 'vue'

function goDetail() {
  uni.navigateTo({ url: `/pages/order/detail/detail?id=${props.order.id}` })
}
</script>

<style scoped>
.order-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
  font-weight: 600;
}

.status-0 { background: #FFF3E0; color: #F59E0B; }
.status-1 { background: #E3F2FD; color: #3B82F6; }
.status-2 { background: #E8F5E9; color: #10B981; }
.status-3 { background: #F3E5F5; color: #9C27B0; }
.status-4 { background: #ECEFF1; color: #607D8B; }

.order-body {
  margin-bottom: 16rpx;
}

.order-body text {
  display: block;
  line-height: 1.8;
  font-size: 26rpx;
  color: #666;
}

.price {
  color: #FF6B35;
  font-weight: bold;
}

.order-actions {
  display: flex;
  gap: 12rpx;
  justify-content: flex-end;
  flex-wrap: wrap;
}

.btn {
  border: none;
  padding: 10rpx 24rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.btn-primary { background: #FF6B35; color: #fff; }
.btn-default { background: #f0f0f0; color: #333; }
.btn-success { background: #10B981; color: #fff; }
.btn-warning { background: #F59E0B; color: #fff; }
</style>
