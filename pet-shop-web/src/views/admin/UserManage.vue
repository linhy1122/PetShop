<template>
  <AdminShell>
    <div class="admin-page">
      <section class="admin-table-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">用户管理</h2>
            <p class="section-subtitle">查看用户、会员等级和基础资料。</p>
          </div>
        </div>

        <div class="toolbar-card">
          <div class="toolbar-actions">
            <el-input v-model="keyword" placeholder="搜索用户名或昵称" clearable style="width: 280px" @change="loadUsers" />
          </div>
          <el-button type="primary" @click="loadUsers">刷新数据</el-button>
        </div>

        <el-table :data="tableData" v-loading="loading" border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" min-width="180" />
          <el-table-column prop="nickname" label="昵称" min-width="180" />
          <el-table-column prop="phone" label="手机号" width="140" />
          <el-table-column prop="memberLevel" label="会员等级" width="120">
            <template #default="{ row }">
              <el-tag>{{ memberMap[row.memberLevel] || '普通会员' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 0 ? 'success' : 'info'">{{ row.status === 0 ? '正常' : '禁用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" @click="showDetail(row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>

      <el-drawer v-model="drawerVisible" title="用户详情" size="420px">
        <div v-if="currentRow" class="drawer-content">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户名">{{ currentRow.username }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ currentRow.nickname }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ currentRow.phone }}</el-descriptions-item>
            <el-descriptions-item label="会员等级">{{ memberMap[currentRow.memberLevel] || '普通会员' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-drawer>
    </div>
  </AdminShell>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import AdminShell from '@/components/AdminShell.vue'
import request from '@/utils/request'

const tableData = ref([])
const loading = ref(false)
const keyword = ref('')
const drawerVisible = ref(false)
const currentRow = ref(null)

const memberMap = ['普通会员', '银卡会员', '金卡会员', '钻石会员']

const fallbackUsers = [
  { id: 1, username: 'alice', nickname: '小爱', phone: '13800000001', memberLevel: 2, status: 0 },
  { id: 2, username: 'bob', nickname: '豆包', phone: '13800000002', memberLevel: 1, status: 0 },
  { id: 3, username: 'coco', nickname: '可可', phone: '13800000003', memberLevel: 0, status: 1 }
]

onMounted(loadUsers)

async function loadUsers() {
  loading.value = true
  try {
    const response = await request.get('/user/list', { params: { size: 100 } })
    const list = response.data?.records || []
    const search = keyword.value.trim().toLowerCase()
    tableData.value = search
      ? list.filter((item) => [item.username, item.nickname].some((value) => String(value || '').toLowerCase().includes(search)))
      : list
  } catch (error) {
    const search = keyword.value.trim().toLowerCase()
    tableData.value = search
      ? fallbackUsers.filter((item) => [item.username, item.nickname].some((value) => String(value).toLowerCase().includes(search)))
      : fallbackUsers
  } finally {
    loading.value = false
  }
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
