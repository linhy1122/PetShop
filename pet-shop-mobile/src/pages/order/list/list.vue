<template>
  <view class="order-list-page">
    <!-- 状态 Tab -->
    <scroll-view scroll-x class="tabs-scroll">
      <view class="tabs">
        <view v-for="tab in tabs" :key="tab.value" class="tab"
              :class="{ active: activeTab === tab.value }"
              @click="switchTab(tab.value)">
          {{ tab.label }}
        </view>
      </view>
    </scroll-view>

    <!-- 订单列表 -->
    <view class="order-list" v-if="!loading">
      <OrderCard v-for="order in orders" :key="order.id" :order="order"
                 @pay="handlePay" @cancel="handleCancel"
                 @receive="handleReceive" @refund="handleRefund" />
      <EmptyState v-if="orders.length === 0" description="暂无订单" />
    </view>
    <view v-else class="loading-wrap"><text>加载中...</text></view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import { getUserOrdersApi, payOrderApi, cancelOrderApi, confirmReceiveApi, applyRefundApi } from '@/api/order'
import { orders as mockOrders } from '@/mock'
import OrderCard from '@/components/OrderCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const userStore = useUserStore()
const orders = ref([])
const activeTab = ref(undefined)
const loading = ref(false)

const tabs = [
  { label: '全部', value: undefined },
  { label: '待支付', value: 0 },
  { label: '待发货', value: 1 },
  { label: '待收货', value: 2 },
  { label: '待评价', value: 3 },
  { label: '已完成', value: 4 }
]

onLoad((options) => {
  if (options && options.status !== undefined) {
    activeTab.value = Number(options.status)
  }
  if (!userStore.isLoggedIn()) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    setTimeout(() => uni.switchTab({ url: '/pages/user/user' }), 1500)
    return
  }
})

onShow(() => {
  if (userStore.isLoggedIn()) {
    fetchOrders()
  }
})

function switchTab(status) {
  activeTab.value = status
  fetchOrders()
}

async function fetchOrders() {
  loading.value = true
  try {
    const res = await getUserOrdersApi(userStore.userInfo.userId, { status: activeTab.value, size: 100 })
    orders.value = res.data?.records || []
    if (!orders.value.length) orders.value = filterMockOrders()
  } catch (e) { orders.value = filterMockOrders() }
  finally { loading.value = false }
}

function filterMockOrders() {
  if (activeTab.value === undefined) return mockOrders
  return mockOrders.filter(o => o.status === activeTab.value)
}

async function handlePay(order) {
  try {
    await payOrderApi(order.id)
    uni.showToast({ title: '支付成功', icon: 'success' })
    fetchOrders()
  } catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
}

async function handleCancel(order) {
  const res = await uni.showModal({ title: '取消订单', content: '确定取消该订单？' })
  if (res.confirm) {
    await cancelOrderApi(order.id, '用户取消')
    uni.showToast({ title: '已取消', icon: 'success' })
    fetchOrders()
  }
}

async function handleReceive(order) {
  const res = await uni.showModal({ title: '确认收货', content: '确认已收到商品？' })
  if (res.confirm) {
    await confirmReceiveApi(order.id)
    uni.showToast({ title: '已确认收货', icon: 'success' })
    fetchOrders()
  }
}

async function handleRefund(order) {
  // 简化：退单弹窗
  await applyRefundApi(order.id, '用户申请退单')
  uni.showToast({ title: '退单申请已提交', icon: 'success' })
  fetchOrders()
}
</script>

<style scoped>
.tabs-scroll {
  white-space: nowrap;
  background: #fff;
  padding: 16rpx 0;
}

.tabs {
  display: inline-flex;
  padding: 0 24rpx;
  gap: 12rpx;
}

.tab {
  display: inline-block;
  padding: 12rpx 28rpx;
  font-size: 26rpx;
  color: #666;
  border-radius: 28rpx;
  background: #f5f5f5;
}

.tab.active {
  background: #FF6B35;
  color: #fff;
}

.order-list {
  padding: 24rpx;
}

.loading-wrap {
  display: flex;
  justify-content: center;
  padding: 200rpx 0;
  color: #999;
}
</style>
