<template>
  <div class="admin-page">
    <el-container>
      <el-aside width="200px"><!-- 复用Dashboard的sidebar --></el-aside>
      <el-main>
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
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProductListApi } from '@/api/product'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getProductListApi({ size: 100 })
    tableData.value = res.data?.records || []
  } finally { loading.value = false }
}

async function toggleStatus(row) {
  await request.put(`/product/${row.id}`, { ...row, status: row.status === 1 ? 0 : 1 })
  ElMessage.success('状态已更新')
  fetchData()
}

async function deleteProduct(id) {
  await ElMessageBox.confirm('确定要删除该商品吗？', '提示', { type: 'warning' })
  await request.delete(`/product/${id}`)
  ElMessage.success('已删除')
  fetchData()
}

function editProduct(row) { /* TODO */ }
</script>
