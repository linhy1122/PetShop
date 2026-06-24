<template>
  <div>
    <h3>店铺管理</h3>
    <el-button type="primary" @click="dialogVisible = true">新增店铺</el-button>
    <el-table :data="tableData" style="margin-top: 16px" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="店铺名称" />
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column prop="city" label="城市" width="100" />
      <el-table-column prop="rating" label="评分" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : 'info'">
            {{ row.status === 0 ? '营业中' : '休息中' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="editStore(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteStore(row.id)">删除</el-button>
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
const dialogVisible = ref(false)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await request.get('/store/list', { params: { size: 100 } })
    tableData.value = res.data?.records || []
  } finally { loading.value = false }
}

function editStore(row) { /* TODO */ }

async function deleteStore(id) {
  await ElMessageBox.confirm('确定删除该店铺？', '提示', { type: 'warning' })
  await request.delete(`/store/${id}`)
  ElMessage.success('已删除')
  fetchData()
}
</script>
