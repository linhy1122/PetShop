<template>
  <div class="order-list-page">
    <div class="container">
      <h2>我的订单</h2>
      <el-tabs v-model="activeTab" @tab-change="fetchOrders">
        <el-tab-pane label="全部" :value="undefined" />
        <el-tab-pane label="待支付" :value="0" />
        <el-tab-pane label="待发货" :value="1" />
        <el-tab-pane label="待收货" :value="2" />
        <el-tab-pane label="待评价" :value="3" />
        <el-tab-pane label="已完成" :value="4" />
      </el-tabs>

      <el-empty v-if="orders.length === 0" description="暂无订单" />
      <el-card v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <span>订单号：{{ order.orderNo }}</span>
          <el-tag :type="statusType(order.status)">{{ statusText(order.status) }}</el-tag>
        </div>
        <div class="order-body">
          <p>总金额：<span class="price">¥{{ order.totalAmount }}</span></p>
          <p>下单时间：{{ order.createTime }}</p>
        </div>
        <div class="order-actions">
          <el-button v-if="order.status === 0" type="primary" size="small"
                     @click="handlePay(order)">去支付</el-button>
          <el-button v-if="order.status === 0" size="small"
                     @click="handleCancel(order)">取消订单</el-button>
          <el-button v-if="order.status === 2" type="success" size="small"
                     @click="handleReceive(order)">确认收货</el-button>
          <el-button v-if="[2,3].includes(order.status)" size="small"
                     @click="handleRefund(order)">申请退单</el-button>
          <el-button size="small" @click="$router.push(`/order/${order.id}`)">
            查看详情
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserOrdersApi, payOrderApi, cancelOrderApi, confirmReceiveApi, applyRefundApi } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const orders = ref([])
const activeTab = ref(undefined)

const statusMap = { 0: '待支付', 1: '待发货', 2: '待收货', 3: '待评价', 4: '已完成',
  '-1': '已取消', '-2': '退单中', '-3': '退单完成', '-4': '已退单' }
const statusTypeMap = { 0: 'warning', 1: 'primary', 2: 'info', 3: 'success', 4: '', '-1': 'info', '-2': 'warning', '-3': 'success', '-4': 'info' }

function statusText(s) { return statusMap[s] || '未知' }
function statusType(s) { return statusTypeMap[s] || 'info' }

onMounted(() => {
  if (!userStore.isLoggedIn()) { router.push('/login'); return }
  fetchOrders()
})

async function fetchOrders() {
  try {
    const res = await getUserOrdersApi(userStore.userInfo.userId, { status: activeTab.value, size: 100 })
    orders.value = res.data?.records || []
  } catch (e) { orders.value = [] }
}

async function handlePay(order) {
  try {
    await payOrderApi(order.id)
    ElMessage.success('支付成功')
    fetchOrders()
  } catch (e) { ElMessage.error(e.message) }
}

async function handleCancel(order) {
  try {
    const { value } = await ElMessageBox.prompt('请输入取消原因', '取消订单', { inputType: 'textarea' })
    await cancelOrderApi(order.id, value || '用户取消')
    ElMessage.success('已取消')
    fetchOrders()
  } catch (e) { /* 取消操作 */ }
}

async function handleReceive(order) {
  await ElMessageBox.confirm('确认已收到商品？', '确认收货')
  await confirmReceiveApi(order.id)
  ElMessage.success('已确认收货')
  fetchOrders()
}

async function handleRefund(order) {
  try {
    const { value } = await ElMessageBox.prompt('请输入退单原因', '申请退单', { inputType: 'textarea' })
    await applyRefundApi(order.id, value || '用户申请退单')
    ElMessage.success('退单申请已提交')
    fetchOrders()
  } catch (e) { /* 取消 */ }
}
</script>

<style scoped>
.container { max-width: 900px; margin: 0 auto; padding: 20px; }
h2 { margin-bottom: 20px; }
.order-card { margin-bottom: 16px; }
.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.order-body p { line-height: 2; color: #666; }
.order-body .price { color: #f56c6c; font-weight: bold; }
.order-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 12px; }
</style>
