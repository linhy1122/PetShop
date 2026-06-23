<template>
  <AdminShell>
    <div class="admin-page">
      <section class="admin-table-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">订单管理</h2>
            <p class="section-subtitle">支持按状态筛选，包含发货、退款审核与强制退款操作。</p>
          </div>
        </div>

        <div class="toolbar-card">
          <div class="toolbar-actions">
            <el-select v-model="status" clearable placeholder="订单状态" style="width: 180px" @change="fetchOrders">
              <el-option label="待支付" :value="0" />
              <el-option label="待发货" :value="1" />
              <el-option label="待收货" :value="2" />
              <el-option label="待评价" :value="3" />
              <el-option label="已完成" :value="4" />
              <el-option label="退款中" :value="-2" />
            </el-select>
          </div>
          <el-button type="primary" @click="fetchOrders">刷新订单</el-button>
        </div>

        <el-table :data="tableData" v-loading="loading" border>
          <el-table-column prop="orderNo" label="订单号" min-width="200" />
          <el-table-column prop="totalAmount" label="金额" width="100" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="下单时间" min-width="180" />
          <el-table-column label="操作" min-width="260" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.status === 1" size="small" type="primary" @click="handleDeliver(row)">发货</el-button>
              <el-button v-if="row.status === -2" size="small" type="success" @click="handleAudit(row, true)">同意退款</el-button>
              <el-button v-if="row.status === -2" size="small" type="danger" @click="handleAudit(row, false)">拒绝退款</el-button>
              <el-button v-if="row.status === 3" size="small" type="warning" @click="handleAdminRefund(row)">强制退款</el-button>
              <el-button size="small" @click="showDetail(row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>

      <el-drawer v-model="drawerVisible" title="订单详情" size="420px">
        <div v-if="currentRow" class="drawer-content">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="订单号">{{ currentRow.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="金额">¥{{ currentRow.totalAmount }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ statusText(currentRow.status) }}</el-descriptions-item>
            <el-descriptions-item label="备注">{{ currentRow.remark || '无' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-drawer>
    </div>
  </AdminShell>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AdminShell from '@/components/AdminShell.vue'
import request from '@/utils/request'

const tableData = ref([])
const loading = ref(false)
const status = ref(undefined)
const drawerVisible = ref(false)
const currentRow = ref(null)

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

function statusText(value) {
  return statusMap[value] || '未知'
}

function statusType(value) {
  return statusTypeMap[value] || 'info'
}

onMounted(fetchOrders)

async function fetchOrders() {
  loading.value = true
  try {
    const response = await request.get('/order/list', { params: { size: 100, status: status.value } })
    tableData.value = response.data?.records || []
  } catch (error) {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

async function handleDeliver(order) {
  try {
    const { value } = await ElMessageBox.prompt('请输入物流公司和单号，格式：公司,单号', '发货')
    if (!value) return
    const [company, no] = value.split(',')
    await request.put(`/order/${order.id}/deliver`, null, {
      params: { logisticsCompany: company?.trim(), logisticsNo: no?.trim() }
    })
    ElMessage.success('发货成功')
    fetchOrders()
  } catch (error) {
    /* cancel */
  }
}

async function handleAudit(order, approved) {
  await request.put(`/order/${order.id}/audit-refund`, null, { params: { approved } })
  ElMessage.success(approved ? '已同意退款' : '已拒绝退款')
  fetchOrders()
}

async function handleAdminRefund(order) {
  const { value } = await ElMessageBox.prompt('请输入强制退款原因', '强制退款', { inputType: 'textarea' })
  if (!value) return
  await request.put(`/order/${order.id}/admin-refund`, null, { params: { reason: value } })
  ElMessage.success('强制退款成功')
  fetchOrders()
}

function showDetail(row) {
  currentRow.value = row
  drawerVisible.value = true
}
</script>

<style scoped>
.admin-page {
  display: grid;
  gap: 20px;
}

.admin-table-card {
  padding: 20px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(94, 77, 133, 0.1);
  box-shadow: 0 14px 32px rgba(88, 74, 126, 0.08);
}
</style>
