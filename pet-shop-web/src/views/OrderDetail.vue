<template>
  <div class="page-shell order-detail-page" v-loading="loading">
    <section v-if="order" class="hero-card order-detail-hero">
      <div>
        <div class="status-pill">Order detail</div>
        <h1 class="hero-title" style="margin-top: 12px;">订单详情</h1>
        <p class="hero-desc">商品信息、收货地址、订单状态和物流信息都在这里。</p>
      </div>
      <el-tag :type="statusType(order.status)" size="large">{{ statusText(order.status) }}</el-tag>
    </section>

    <section class="page-section split-layout">
      <div class="panel-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">订单信息</h2>
            <p class="section-subtitle">查看订单编号、金额、支付方式和创建时间。</p>
          </div>
        </div>

        <div class="detail-stack">
          <div class="soft-card detail-row"><span>订单编号</span><strong>{{ order.orderNo }}</strong></div>
          <div class="soft-card detail-row"><span>订单金额</span><strong>¥{{ order.totalAmount }}</strong></div>
          <div class="soft-card detail-row"><span>实付金额</span><strong>¥{{ order.payAmount || order.totalAmount }}</strong></div>
          <div class="soft-card detail-row"><span>支付方式</span><strong>{{ order.payMethod || '未支付' }}</strong></div>
          <div class="soft-card detail-row"><span>创建时间</span><strong>{{ order.createTime }}</strong></div>
          <div class="soft-card detail-row" v-if="order.logisticsCompany"><span>物流公司</span><strong>{{ order.logisticsCompany }}</strong></div>
          <div class="soft-card detail-row" v-if="order.logisticsNo"><span>物流单号</span><strong>{{ order.logisticsNo }}</strong></div>
        </div>

        <div class="action-row" style="margin-top: 18px;">
          <el-button v-if="order.status === 0" type="primary" @click="handlePay">去支付</el-button>
          <el-button v-if="order.status === 0" @click="handleCancel">取消订单</el-button>
          <el-button v-if="order.status === 2" type="success" @click="handleReceive">确认收货</el-button>
          <el-button v-if="[2,3].includes(order.status)" @click="handleRefund">申请退款</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </div>

      <div class="panel-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">商品与物流</h2>
            <p class="section-subtitle">订单商品项与状态流转。</p>
          </div>
        </div>

        <el-timeline>
          <el-timeline-item :timestamp="order.createTime" placement="top">订单创建</el-timeline-item>
          <el-timeline-item v-if="order.payTime" :timestamp="order.payTime" placement="top">支付完成</el-timeline-item>
          <el-timeline-item v-if="order.deliveryTime" :timestamp="order.deliveryTime" placement="top">商家发货</el-timeline-item>
          <el-timeline-item v-if="order.receiveTime" :timestamp="order.receiveTime" placement="top">用户收货</el-timeline-item>
          <el-timeline-item v-if="order.finishTime" :timestamp="order.finishTime" placement="top">订单完成</el-timeline-item>
        </el-timeline>

        <div class="order-items">
          <article v-if="orderItems.length === 0" class="soft-card empty-item">
            <p class="muted">订单商品项暂未由后端返回。</p>
          </article>
          <article v-for="item in orderItems" :key="item.id" class="soft-card item-row">
            <img :src="item.productImage || '/vite.svg'" class="item-thumb" />
            <div>
              <strong>{{ item.productName }}</strong>
              <p class="muted">数量 {{ item.quantity }} · 单价 ¥{{ item.price }}</p>
            </div>
            <div class="item-price">¥{{ item.subtotal }}</div>
          </article>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetailApi, payOrderApi, cancelOrderApi, confirmReceiveApi, applyRefundApi } from '@/api/order'

const route = useRoute()
const loading = ref(false)
const order = ref(null)
const orderItems = ref([])

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

onMounted(async () => {
  loading.value = true
  try {
    const response = await getOrderDetailApi(route.params.id)
    order.value = response.data
    orderItems.value = response.data?.orderItems || response.data?.items || []
  } catch (error) {
    ElMessage.error('加载订单详情失败')
  } finally {
    loading.value = false
  }
})

async function handlePay() {
  await payOrderApi(order.value.id)
  ElMessage.success('支付成功')
}

async function handleCancel() {
  try {
    const { value } = await ElMessageBox.prompt('请输入取消原因', '取消订单', { inputType: 'textarea' })
    await cancelOrderApi(order.value.id, value || '用户取消')
    ElMessage.success('已取消订单')
  } catch (error) {
    /* cancel */
  }
}

async function handleReceive() {
  await ElMessageBox.confirm('确认已收到商品吗？', '确认收货')
  await confirmReceiveApi(order.value.id)
  ElMessage.success('已确认收货')
}

async function handleRefund() {
  try {
    const { value } = await ElMessageBox.prompt('请输入退款原因', '申请退款', { inputType: 'textarea' })
    await applyRefundApi(order.value.id, value || '用户申请退款')
    ElMessage.success('退款申请已提交')
  } catch (error) {
    /* cancel */
  }
}
</script>

<style scoped>
.order-detail-hero {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: end;
}

.detail-stack {
  display: grid;
  gap: 12px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}

.order-items {
  display: grid;
  gap: 12px;
  margin-top: 18px;
}

.item-row {
  display: grid;
  grid-template-columns: 72px 1fr auto;
  gap: 14px;
  align-items: center;
}

.item-thumb {
  width: 72px;
  height: 72px;
  object-fit: cover;
  border-radius: 18px;
}

.item-price {
  font-weight: 800;
  color: var(--pet-primary);
}

.empty-item {
  padding: 18px;
}

@media (max-width: 1024px) {
  .order-detail-hero {
    flex-direction: column;
    align-items: start;
  }

  .item-row {
    grid-template-columns: 1fr;
  }
}
</style>
