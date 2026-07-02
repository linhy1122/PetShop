<template>
  <view class="order-detail-page">
    <view v-if="loading" class="loading-wrap"><text>加载中...</text></view>

    <view v-if="order" class="detail-card">
      <view class="detail-item">
        <text class="label">订单编号</text>
        <text class="value">{{ order.orderNo }}</text>
      </view>
      <view class="detail-item">
        <text class="label">订单状态</text>
        <text class="value status-badge" :class="'status-' + order.status">{{ statusMap[order.status] || '未知' }}</text>
      </view>
      <view class="detail-item">
        <text class="label">总金额</text>
        <text class="value price">¥{{ order.totalAmount }}</text>
      </view>
      <view class="detail-item">
        <text class="label">实付金额</text>
        <text class="value">¥{{ order.payAmount || order.totalAmount }}</text>
      </view>
      <view class="detail-item">
        <text class="label">支付方式</text>
        <text class="value">{{ order.payMethod || '未支付' }}</text>
      </view>
      <view class="detail-item">
        <text class="label">创建时间</text>
        <text class="value">{{ order.createTime }}</text>
      </view>
      <view class="detail-item" v-if="order.logisticsCompany">
        <text class="label">物流公司</text>
        <text class="value">{{ order.logisticsCompany }}</text>
      </view>
      <view class="detail-item" v-if="order.logisticsNo">
        <text class="label">物流单号</text>
        <text class="value">{{ order.logisticsNo }}</text>
      </view>

      <view class="actions">
        <button v-if="order.status === 0" class="btn-primary" @click="$emit('pay', order.id)">去支付</button>
        <button v-if="order.status === 0" class="btn-default" @click="$emit('cancel', order.id)">取消订单</button>
        <button v-if="order.status === 2" class="btn-success" @click="$emit('receive', order.id)">确认收货</button>
        <button class="btn-default" @click="goBack">返回</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getOrderDetailApi } from '@/api/order'
import { orders as mockOrders } from '@/mock'

const order = ref(null)
const loading = ref(false)

const statusMap = { 0: '待支付', 1: '待发货', 2: '待收货', 3: '待评价', 4: '已完成', '-1': '已取消', '-2': '退单中', '-3': '退单完成', '-4': '已退单' }

onLoad((options) => {
  fetchData(options.id)
})

async function fetchData(id) {
  loading.value = true
  try {
    order.value = (await getOrderDetailApi(id)).data
  } catch (e) {
    order.value = mockOrders.find(o => o.id == id) || mockOrders[0]
  } finally { loading.value = false }
}

function goBack() {
  uni.navigateBack()
}
</script>

<style scoped>
.loading-wrap {
  display: flex;
  justify-content: center;
  padding: 200rpx 0;
  color: #999;
}

.detail-card {
  background: #fff;
  margin: 24rpx;
  padding: 32rpx;
  border-radius: 16rpx;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.label {
  font-size: 28rpx;
  color: #666;
}

.value {
  font-size: 28rpx;
  color: #333;
}

.price {
  color: #FF6B35;
  font-weight: 700;
  font-size: 32rpx;
}

.status-badge {
  padding: 6rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: 600;
}

.status-0 { background: #FFF3E0; color: #F59E0B; }
.status-1 { background: #E3F2FD; color: #3B82F6; }
.status-2 { background: #E8F5E9; color: #10B981; }
.status-3 { background: #F3E5F5; color: #9C27B0; }
.status-4 { background: #ECEFF1; color: #607D8B; }

.actions {
  display: flex;
  gap: 16rpx;
  margin-top: 40rpx;
  flex-wrap: wrap;
}

.btn-primary {
  background: #FF6B35;
  color: #fff;
  border: none;
  padding: 20rpx 40rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.btn-default {
  background: #f0f0f0;
  color: #333;
  border: none;
  padding: 20rpx 40rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.btn-success {
  background: #10B981;
  color: #fff;
  border: none;
  padding: 20rpx 40rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
}
</style>
