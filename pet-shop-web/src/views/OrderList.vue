<template>
  <div class="page-shell order-page">
    <section class="hero-card order-hero">
      <div>
        <div class="status-pill">Order center</div>
        <h1 class="hero-title" style="margin-top: 12px;">我的订单</h1>
        <p class="hero-desc">按状态筛选订单，查看待支付、待发货、待收货、待评价和已完成订单。</p>
      </div>
      <div class="order-summary">
        <div class="hero-meta-card">
          <div class="hero-meta-label">订单总数</div>
          <div class="hero-meta-value">{{ orders.length }}</div>
        </div>
        <div class="hero-meta-card">
          <div class="hero-meta-label">当前状态</div>
          <div class="hero-meta-value">{{ statusText(activeTab) }}</div>
        </div>
      </div>
    </section>

    <section class="page-section panel-card">
      <el-tabs v-model="activeTab" @tab-change="fetchOrders">
        <el-tab-pane label="全部" :name="undefined" />
        <el-tab-pane label="待支付" :name="0" />
        <el-tab-pane label="待发货" :name="1" />
        <el-tab-pane label="待收货" :name="2" />
        <el-tab-pane label="待评价" :name="3" />
        <el-tab-pane label="已完成" :name="4" />
      </el-tabs>

      <el-empty v-if="orders.length === 0" description="暂无订单" />

      <div class="order-list">
        <article v-for="order in orders" :key="order.id" class="pet-card order-card">
          <div class="order-top">
            <div>
              <strong>{{ order.orderNo }}</strong>
              <div class="muted">{{ order.createTime }}</div>
            </div>
            <el-tag :type="statusType(order.status)">{{ statusText(order.status) }}</el-tag>
          </div>
          <div class="order-body">
            <div class="order-price">¥{{ order.totalAmount }}</div>
            <p class="muted">实付：¥{{ order.payAmount || order.totalAmount }}</p>
            <p class="muted">{{ order.remark || '暂无备注' }}</p>
          </div>
          <div class="order-actions">
            <el-button v-if="order.status === 0" type="primary" size="small" @click="handlePay(order)">去支付</el-button>
            <el-button v-if="order.status === 0" size="small" @click="handleCancel(order)">取消订单</el-button>
            <el-button v-if="order.status === 2" type="success" size="small" @click="handleReceive(order)">确认收货</el-button>
            <el-button v-if="[2,3].includes(order.status)" size="small" @click="handleRefund(order)">申请退款</el-button>
            <el-button size="small" @click="$router.push(`/order/${order.id}`)">查看详情</el-button>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getUserOrdersApi, payOrderApi, cancelOrderApi, confirmReceiveApi, applyRefundApi } from '@/api/order'

const router = useRouter()
const userStore = useUserStore()
const orders = ref([])
const activeTab = ref(undefined)

const statusMap = {
  0: '待支付',
  1: '待发货',
  2: '待收货',
  3: '待评价',
  4: '已完成',
  '-1': '已取消',
  '-2': '退款中',
  '-3': '退款完成',
  '-4': '已退款'
}

const statusTypeMap = {
  0: 'warning',
  1: 'primary',
  2: 'info',
  3: 'success',
  4: 'success',
  '-1': 'info',
  '-2': 'warning',
  '-3': 'success',
  '-4': 'info'
}

function statusText(status) {
  return statusMap[status] || '未知'
}

function statusType(status) {
  return statusTypeMap[status] || 'info'
}

onMounted(() => {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }
  fetchOrders()
})

async function fetchOrders() {
  try {
    const response = await getUserOrdersApi(userStore.userInfo.userId, { status: activeTab.value, size: 100 })
    orders.value = response.data?.records || []
  } catch (error) {
    orders.value = []
  }
}

async function handlePay(order) {
  await payOrderApi(order.id)
  ElMessage.success('支付成功')
  fetchOrders()
}

async function handleCancel(order) {
  try {
    const { value } = await ElMessageBox.prompt('请输入取消原因', '取消订单', { inputType: 'textarea' })
    await cancelOrderApi(order.id, value || '用户取消')
    ElMessage.success('已取消订单')
    fetchOrders()
  } catch (error) {
    /* cancel */
  }
}

async function handleReceive(order) {
  await ElMessageBox.confirm('确认已收到商品吗？', '确认收货')
  await confirmReceiveApi(order.id)
  ElMessage.success('已确认收货')
  fetchOrders()
}

async function handleRefund(order) {
  try {
    const { value } = await ElMessageBox.prompt('请输入退款原因', '申请退款', { inputType: 'textarea' })
    await applyRefundApi(order.id, value || '用户申请退款')
    ElMessage.success('退款申请已提交')
    fetchOrders()
  } catch (error) {
    /* cancel */
  }
}
</script>

<style scoped>
.order-page {
  padding-bottom: 24px;
}

.order-hero {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: end;
}

.order-summary {
  display: flex;
  gap: 12px;
}

.order-list {
  display: grid;
  gap: 14px;
}

.order-card {
  padding: 18px;
}

.order-top,
.order-actions {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  align-items: center;
}

.order-body {
  margin: 16px 0;
}

.order-price {
  font-size: 24px;
  font-weight: 800;
  color: var(--pet-primary);
}

.order-actions {
  flex-wrap: wrap;
}

@media (max-width: 1024px) {
  .order-hero {
    flex-direction: column;
    align-items: start;
  }

  .order-summary {
    width: 100%;
    flex-direction: column;
  }
}
</style>
