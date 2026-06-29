<template>
  <div>
    <h3>订单管理</h3>

    <!-- 搜索筛选栏 -->
    <el-card class="filter-card" shadow="never">
      <el-row :gutter="16" align="middle">
        <el-col :span="4">
          <el-select v-model="filters.status" placeholder="订单状态" clearable @change="onStatusChange">
            <el-option label="全部" value="" />
            <el-option v-for="(label, val) in statusMap" :key="val" :label="label" :value="Number(val)" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <div style="display: flex; align-items: center; gap: 8px">
            <input
              v-model="filters.startTime"
              type="datetime-local"
              class="native-date-input"
              @change="fetchOrders"
            />
            <span style="color: #999; flex-shrink: 0">至</span>
            <input
              v-model="filters.endTime"
              type="datetime-local"
              class="native-date-input"
              @change="fetchOrders"
            />
          </div>
        </el-col>
        <el-col :span="4">
          <el-input v-model="filters.keyword" placeholder="订单号/用户ID" clearable
                    @keyup.enter="fetchOrders" @clear="fetchOrders" />
        </el-col>
        <el-col :span="2">
          <el-button type="primary" @click="fetchOrders">搜索</el-button>
        </el-col>
        <el-col :span="2">
          <el-button @click="resetFilters">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 订单表格 -->
    <el-table :data="tableData" v-loading="loading" style="margin-top: 16px">
      <el-table-column prop="orderNo" label="订单编号" width="180" />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column label="金额" width="110">
        <template #default="{ row }">
          <span style="color: #f56c6c; font-weight: bold">¥{{ row.totalAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="180" />
      <el-table-column prop="payTime" label="支付时间" width="180">
        <template #default="{ row }">{{ row.payTime || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" min-width="320" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row)">详情</el-button>
          <el-button v-if="row.status === 1" size="small" type="primary"
                     @click="handleDeliver(row)">发货</el-button>
          <el-button v-if="row.status === -2" size="small" type="success"
                     @click="handleAudit(row, true)">同意退单</el-button>
          <el-button v-if="row.status === -2" size="small" type="danger"
                     @click="handleAudit(row, false)">拒绝退单</el-button>
          <el-button v-if="row.status === 3" size="small" type="warning"
                     @click="handleAdminRefund(row)">强制退单</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="pagination.page"
        :page-size="pagination.size"
        :total="pagination.total"
        layout="total, prev, pager, next"
        background
        @current-change="fetchOrders"
      />
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <template v-if="detailOrder">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="订单编号">{{ detailOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(detailOrder.status)">{{ statusText(detailOrder.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ detailOrder.userId }}</el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ detailOrder.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ detailOrder.payMethod || '-' }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ detailOrder.payTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="物流公司">{{ detailOrder.logisticsCompany || '-' }}</el-descriptions-item>
          <el-descriptions-item label="物流单号">{{ detailOrder.logisticsNo || '-' }}</el-descriptions-item>
          <el-descriptions-item v-if="detailOrder.cancelReason" label="取消/退款原因" :span="2">
            {{ detailOrder.cancelReason || detailOrder.refundReason }}
          </el-descriptions-item>
          <el-descriptions-item v-if="detailOrder.auditRemark" label="审核意见" :span="2">
            {{ detailOrder.auditRemark }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 订单商品 -->
        <h4 style="margin: 16px 0 8px">商品列表</h4>
        <div class="dialog-item" v-for="item in detailItems" :key="item.id">
          <span>{{ item.productName }}</span>
          <span>¥{{ item.price }} × {{ item.quantity }}</span>
          <span style="font-weight: bold">¥{{ item.subtotal }}</span>
        </div>
      </template>
    </el-dialog>

    <!-- 发货对话框 -->
    <el-dialog v-model="deliverVisible" title="订单发货" width="480px">
      <el-form :model="deliverForm" :rules="deliverRules" ref="deliverFormRef" label-width="90px">
        <el-form-item label="订单编号">
          <span>{{ deliverForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="物流公司" prop="logisticsCompany">
          <el-select v-model="deliverForm.logisticsCompany" placeholder="请选择物流公司" filterable allow-create style="width: 100%">
            <el-option v-for="c in logisticsCompanies" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号" prop="logisticsNo">
          <el-input v-model="deliverForm.logisticsNo" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deliverVisible = false">取消</el-button>
        <el-button type="primary" :loading="delivering" @click="confirmDeliver">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAdminOrdersApi, getAdminOrderDetailApi, deliverOrderApi, auditRefundApi, adminRefundApi } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const loading = ref(false)

const filters = reactive({
  status: '',
  keyword: '',
  startTime: null,
  endTime: null
})

const pagination = reactive({ page: 1, size: 10, total: 0 })

// 详情对话框
const detailVisible = ref(false)
const detailOrder = ref(null)
const detailItems = ref([])

// 发货对话框
const deliverVisible = ref(false)
const delivering = ref(false)
const deliverFormRef = ref(null)
const deliverForm = reactive({
  orderId: null,
  orderNo: '',
  logisticsCompany: '',
  logisticsNo: ''
})
const deliverRules = {
  logisticsCompany: [{ required: true, message: '请选择或输入物流公司', trigger: 'blur' }],
  logisticsNo: [{ required: true, message: '请输入物流单号', trigger: 'blur' }]
}
const logisticsCompanies = ['顺丰速运', '中通快递', '圆通速递', '韵达快递', '申通快递', '京东物流', 'EMS', '德邦物流']

const statusMap = { 0: '待支付', 1: '待发货', 2: '待收货', 3: '待评价',
  4: '已完成', '-1': '已取消', '-2': '退单中', '-3': '退单通过', '-4': '已退单' }
const statusTypeMap = { 0: 'warning', 1: 'primary', 2: 'info', 3: 'success', 4: '',
  '-1': 'info', '-2': 'warning', '-3': 'success', '-4': 'info' }

function statusText(s) { return statusMap[s] || '未知' }
function statusType(s) { return statusTypeMap[s] || 'info' }

onMounted(() => {
  fetchOrders()
})

function onStatusChange() {
  fetchOrders()
}

async function fetchOrders() {
  loading.value = true
  try {
    const params = { page: pagination.page, size: pagination.size }
    if (filters.status !== '') {
      params.status = filters.status
    }
    if (filters.keyword) {
      params.keyword = filters.keyword
    }
    if (filters.startTime) {
      params.startTime = filters.startTime.replace('T', ' ') + ':00'
    }
    if (filters.endTime) {
      params.endTime = filters.endTime.replace('T', ' ') + ':00'
    }
    const res = await getAdminOrdersApi(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (e) {
    // 请求失败时表格置空，显示错误信息
    tableData.value = []
    pagination.total = 0
    // 仅当不是用户取消操作时提示
    if (e !== 'cancel' && e !== 'close') {
      console.error('加载订单列表失败', e)
    }
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  filters.status = ''
  filters.keyword = ''
  filters.startTime = null
  filters.endTime = null
  pagination.page = 1
  fetchOrders()
}

async function viewDetail(order) {
  detailVisible.value = true
  try {
    const res = await getAdminOrderDetailApi(order.id)
    detailOrder.value = res.data?.order || order
    detailItems.value = res.data?.items || []
  } catch (e) {
    detailOrder.value = order
    detailItems.value = []
  }
}

function handleDeliver(order) {
  deliverForm.orderId = order.id
  deliverForm.orderNo = order.orderNo
  deliverForm.logisticsCompany = ''
  deliverForm.logisticsNo = ''
  deliverVisible.value = true
  setTimeout(() => deliverFormRef.value?.resetFields(), 0)
}

async function confirmDeliver() {
  const valid = await deliverFormRef.value.validate().catch(() => false)
  if (!valid) return
  delivering.value = true
  try {
    await deliverOrderApi(deliverForm.orderId, deliverForm.logisticsCompany, deliverForm.logisticsNo)
    ElMessage.success('发货成功')
    deliverVisible.value = false
    fetchOrders()
  } catch (e) {
    ElMessage.error(e.message || '发货失败')
  } finally {
    delivering.value = false
  }
}

async function handleAudit(order, approved) {
  try {
    const promptText = approved ? '请输入审核意见（可选）' : '请输入拒绝原因'
    const { value } = await ElMessageBox.prompt(promptText, approved ? '同意退单' : '拒绝退单')
    await auditRefundApi(order.id, approved, value || '')
    ElMessage.success(approved ? '退单审核通过，已退款' : '已拒绝退单')
    fetchOrders()
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') {
      ElMessage.error(e.message || '操作失败')
    }
  }
}

async function handleAdminRefund(order) {
  try {
    const { value } = await ElMessageBox.prompt('请输入退单原因', '强制退单', { inputType: 'textarea' })
    if (value) {
      await adminRefundApi(order.id, value)
      ElMessage.success('退单成功，已退款')
      fetchOrders()
    }
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') {
      ElMessage.error(e.message || '操作失败')
    }
  }
}
</script>

<style scoped>
.filter-card { margin-bottom: 4px; }
.filter-card :deep(.el-card__body) { padding: 16px; }
.dialog-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 8px 0; border-bottom: 1px solid #f0f0f0;
}
.native-date-input {
  flex: 1;
  height: 32px;
  padding: 0 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
  background: #fff;
  outline: none;
  transition: border-color .2s;
}
.native-date-input:focus { border-color: #409eff; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }
</style>
