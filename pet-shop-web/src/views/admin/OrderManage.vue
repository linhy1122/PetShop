<template>
  <div>
    <h3>订单管理</h3>
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="orderNo" label="订单编号" width="180" />
      <el-table-column prop="totalAmount" label="金额" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="180" />
      <el-table-column label="操作" min-width="300">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" size="small" type="primary"
                     @click="handleDeliver(row)">发货</el-button>
          <el-button v-if="row.status === -2" size="small" type="success"
                     @click="handleAudit(row, true)">同意退货</el-button>
          <el-button v-if="row.status === -2" size="small" type="danger"
                     @click="handleAudit(row, false)">拒绝退货</el-button>
          <el-button v-if="row.status === 3" size="small" type="warning"
                     @click="handleAdminRefund(row)">强制退单</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const loading = ref(false)

const statusMap = { 0: '待支付', 1: '待发货', 2: '待收货', 3: '待评价',
  4: '已完成', '-1': '已取消', '-2': '退单中', '-3': '退单通过', '-4': '已退单' }
const statusTypeMap = { 0: 'warning', 1: 'primary', 2: 'info', 3: 'success', '-1': 'info' }

function statusText(s) { return statusMap[s] || '未知' }
function statusType(s) { return statusTypeMap[s] || 'info' }

onMounted(() => fetchOrders())

async function fetchOrders() {
  loading.value = true
  try {
    const res = await request.get('/order/list', { params: { size: 100 } })
    tableData.value = res.data?.records || []
  } finally { loading.value = false }
}

async function handleDeliver(order) {
  try {
    const { value } = await ElMessageBox.prompt('请输入物流公司和单号（格式：公司,单号）', '发货')
    if (value) {
      const [company, no] = value.split(',')
      await request.put(`/order/${order.id}/deliver`, null, {
        params: { logisticsCompany: company?.trim(), logisticsNo: no?.trim() }
      })
      ElMessage.success('发货成功')
      fetchOrders()
    }
  } catch (e) { /* 取消 */ }
}

async function handleAudit(order, approved) {
  await request.put(`/order/${order.id}/audit-refund`, null, { params: { approved } })
  ElMessage.success(approved ? '退货审核通过' : '退货审核拒绝')
  fetchOrders()
}

async function handleAdminRefund(order) {
  const { value } = await ElMessageBox.prompt('请输入退单原因', '强制退单', { inputType: 'textarea' })
  if (value) {
    await request.put(`/order/${order.id}/admin-refund`, null, { params: { reason: value } })
    ElMessage.success('退单成功')
    fetchOrders()
  }
}
</script>
