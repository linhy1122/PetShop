<template>
  <div>
    <h3>用户管理</h3>
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="memberLevel" label="会员等级" width="100">
        <template #default="{ row }">
          <el-tag>{{ ['普通', '银卡', '金卡', '钻石'][row.memberLevel] || '普通' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 0" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="deleteUser(row.id)">删除</el-button>
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

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await request.get('/user/list', { params: { size: 100 } })
    tableData.value = res.data?.records || []
  } finally { loading.value = false }
}

async function toggleStatus(row) {
  await request.put('/user/status', null, { params: { userId: row.id, status: row.status === 0 ? 1 : 0 } })
  ElMessage.success('已更新')
  fetchData()
}

async function deleteUser(id) {
  await ElMessageBox.confirm('确定删除该用户？', '提示', { type: 'warning' })
  await request.delete(`/user/${id}`)
  ElMessage.success('已删除')
  fetchData()
}
</script>
