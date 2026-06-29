<template>
  <div class="order-detail-page">
    <div class="container" v-loading="loading">
      <div class="header">
        <el-button :icon="ArrowLeft" @click="$router.back()">返回</el-button>
        <h2>订单详情</h2>
      </div>

      <!-- 订单基本信息 -->
      <el-card v-if="order" class="section">
        <template #header>订单信息</template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单编号">{{ order.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="statusType(order.status)">{{ statusMap[order.status] }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总金额">
            <span class="price">¥{{ order.totalAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="实付金额">¥{{ order.payAmount || '0.00' }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ order.payMethod || '未支付' }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ order.payTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ order.createTime }}</el-descriptions-item>
          <el-descriptions-item label="订单备注">{{ order.remark || '-' }}</el-descriptions-item>
          <el-descriptions-item v-if="order.logisticsCompany" label="物流公司">{{ order.logisticsCompany }}</el-descriptions-item>
          <el-descriptions-item v-if="order.logisticsNo" label="物流单号">{{ order.logisticsNo }}</el-descriptions-item>
          <el-descriptions-item v-if="order.cancelReason" label="取消原因" :span="2">
            {{ order.cancelReason }}
          </el-descriptions-item>
          <el-descriptions-item v-if="order.refundReason" label="退款原因" :span="2">
            {{ order.refundReason }}
          </el-descriptions-item>
          <el-descriptions-item v-if="order.refundMoney" label="退款金额">
            <span class="price">¥{{ order.refundMoney }}</span>
          </el-descriptions-item>
          <el-descriptions-item v-if="order.auditRemark" label="审核意见">{{ order.auditRemark }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 订单商品 -->
      <el-card v-if="items.length > 0" class="section">
        <template #header>商品列表</template>
        <div class="item-row" v-for="item in items" :key="item.id">
          <img :src="item.productImage || '/vite.svg'" class="item-img" />
          <div class="item-info">
            <h4>{{ item.productName }}</h4>
            <span class="item-price">¥{{ item.price }} × {{ item.quantity }}</span>
          </div>
          <span class="item-subtotal">¥{{ item.subtotal }}</span>
        </div>
        <div class="item-summary">
          共 {{ items.length }} 件商品，合计：<span class="price">¥{{ order?.totalAmount }}</span>
        </div>
      </el-card>

      <!-- 操作按钮 -->
      <el-card v-if="order" class="section">
        <template #header>操作</template>
        <div class="order-actions">
          <el-button v-if="order.status === 0" type="primary" @click="handlePay">去支付</el-button>
          <el-button v-if="order.status === 0" @click="handleCancel">取消订单</el-button>
          <el-button v-if="order.status === 2" type="success" @click="handleReceive">确认收货</el-button>
          <el-button v-if="[2,3].includes(order.status)" type="warning" @click="handleRefund">申请退单</el-button>
        </div>
      </el-card>

      <!-- 订单时间轴 -->
      <el-card v-if="logs.length > 0" class="section">
        <template #header>订单动态</template>
        <el-timeline>
          <el-timeline-item
            v-for="log in logs" :key="log.id"
            :timestamp="log.createTime"
            :type="logType(log.toStatus)"
            placement="top"
          >
            <p>{{ log.remark }}</p>
            <p class="log-operator">操作人：{{ log.operator }}</p>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getOrderDetailApi, payOrderApi, cancelOrderApi, confirmReceiveApi, applyRefundApi } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const order = ref(null)
const items = ref([])
const logs = ref([])
const loading = ref(false)

const statusMap = { 0: '待支付', 1: '待发货', 2: '待收货', 3: '待评价', 4: '已完成',
  '-1': '已取消', '-2': '退单中', '-3': '退单通过', '-4': '已退单' }
const statusTypeMap = { 0: 'warning', 1: 'primary', 2: 'info', 3: 'success', 4: '',
  '-1': 'info', '-2': 'warning', '-3': 'success', '-4': 'info' }

function statusType(s) { return statusTypeMap[s] || 'info' }

function logType(toStatus) {
  if (toStatus === null || toStatus === undefined) return 'info'
  if (toStatus === -1 || toStatus === -2 || toStatus === -4) return 'danger'
  if (toStatus === 4) return 'success'
  if (toStatus === 0) return 'warning'
  return 'primary'
}

onMounted(async () => {
  if (!userStore.isLoggedIn()) { router.push('/login'); return }
  loading.value = true
  try {
    const res = await getOrderDetailApi(route.params.id)
    order.value = res.data?.order
    items.value = res.data?.items || []
    logs.value = res.data?.logs || []
  } catch (e) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
})

async function handlePay() {
  try {
    await payOrderApi(order.value.id)
    ElMessage.success('支付成功')
    location.reload()
  } catch (e) { ElMessage.error(e.message) }
}

async function handleCancel() {
  try {
    const { value } = await ElMessageBox.prompt('请输入取消原因', '取消订单', { inputType: 'textarea' })
    await cancelOrderApi(order.value.id, value || '用户取消', 'USER')
    ElMessage.success('已取消')
    location.reload()
  } catch (e) { /* 取消操作 */ }
}

async function handleReceive() {
  await ElMessageBox.confirm('确认已收到商品？', '确认收货')
  await confirmReceiveApi(order.value.id)
  ElMessage.success('已确认收货')
  location.reload()
}

async function handleRefund() {
  try {
    const { value } = await ElMessageBox.prompt('请输入退单原因', '申请退单', { inputType: 'textarea' })
    if (value) {
      await applyRefundApi(order.value.id, value)
      ElMessage.success('退单申请已提交')
      location.reload()
    }
  } catch (e) { /* 取消 */ }
}
</script>

<style scoped>
.container { max-width: 900px; margin: 0 auto; padding: 20px; }
.header { display: flex; align-items: center; gap: 16px; margin-bottom: 20px; }
.header h2 { margin: 0; }
.section { margin-bottom: 20px; }
.price { color: #f56c6c; font-weight: bold; font-size: 16px; }
.item-row { display: flex; align-items: center; gap: 16px; padding: 12px 0; border-bottom: 1px solid #f0f0f0; }
.item-row:last-child { border-bottom: none; }
.item-img { width: 64px; height: 64px; object-fit: cover; border-radius: 8px; }
.item-info { flex: 1; }
.item-info h4 { font-size: 14px; margin-bottom: 4px; }
.item-price { color: #999; font-size: 13px; }
.item-subtotal { font-weight: bold; min-width: 80px; text-align: right; }
.item-summary { text-align: right; padding-top: 12px; font-size: 15px; }
.order-actions { display: flex; gap: 10px; }
.log-operator { color: #999; font-size: 12px; margin-top: 4px; }
</style>
