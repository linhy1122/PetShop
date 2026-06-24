<template>
  <div>
    <h3>商品管理</h3>
    <el-button type="primary" @click="dialogVisible = true">新增商品</el-button>
    <el-table :data="tableData" style="margin-top: 16px" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="商品名" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 1" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="editProduct(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteProduct(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- TODO: 商品编辑对话框 -->
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await request.get('/product/list', { params: { size: 100 } })
    tableData.value = res.data?.records || []
  } finally { loading.value = false }
}

async function toggleStatus(row) {
  await request.put('/product/status', null, { params: { productId: row.id, status: row.status === 1 ? 0 : 1 } })
  ElMessage.success('已更新')
  fetchData()
}

function editProduct(row) { /* TODO */ }

async function deleteProduct(id) {
  await ElMessageBox.confirm('确定删除该商品？', '提示', { type: 'warning' })
  await request.delete(`/product/${id}`)
  ElMessage.success('已删除')
  fetchData()
}
</script>
