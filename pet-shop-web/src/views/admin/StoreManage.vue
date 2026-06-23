<template>
  <AdminShell>
    <div class="admin-page">
      <section class="admin-table-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">商店管理</h2>
            <p class="section-subtitle">浏览店铺、筛选城市、查看详情。</p>
          </div>
        </div>

        <div class="toolbar-card">
          <div class="toolbar-actions">
            <el-input v-model="keyword" placeholder="搜索店铺名称" clearable style="width: 260px" @change="fetchData" />
            <el-input v-model="city" placeholder="按城市筛选" clearable style="width: 180px" @change="fetchData" />
          </div>
          <el-button type="primary" @click="fetchData">刷新数据</el-button>
        </div>

        <el-table :data="tableData" v-loading="loading" border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="商店名称" min-width="220" />
          <el-table-column prop="phone" label="电话" width="140" />
          <el-table-column prop="city" label="城市" width="110" />
          <el-table-column prop="rating" label="评分" width="90" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 0 ? 'success' : 'info'">{{ row.status === 0 ? '营业中' : '休息中' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="showDetail(row)">详情</el-button>
              <el-button size="small" type="primary" plain @click="placeholder('编辑')">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>

      <el-drawer v-model="drawerVisible" title="商店详情" size="420px">
        <div v-if="currentRow" class="drawer-content">
          <img :src="currentRow.image || '/vite.svg'" class="drawer-image" />
          <el-descriptions :column="1" border>
            <el-descriptions-item label="商店名称">{{ currentRow.name }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ currentRow.phone }}</el-descriptions-item>
            <el-descriptions-item label="地址">{{ [currentRow.province, currentRow.city, currentRow.district].filter(Boolean).join('') }} {{ currentRow.address }}</el-descriptions-item>
            <el-descriptions-item label="评分">{{ currentRow.rating }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-drawer>
    </div>
  </AdminShell>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import AdminShell from '@/components/AdminShell.vue'
import { getStoreListApi } from '@/api/store'

const tableData = ref([])
const loading = ref(false)
const keyword = ref('')
const city = ref('')
const drawerVisible = ref(false)
const currentRow = ref(null)

onMounted(fetchData)

async function fetchData() {
  loading.value = true
  try {
    const response = await getStoreListApi({ size: 100, city: city.value || undefined })
    const list = response.data?.records || []
    const search = keyword.value.trim().toLowerCase()
    tableData.value = search ? list.filter((item) => String(item.name || '').toLowerCase().includes(search)) : list
  } catch (error) {
    tableData.value = [
      { id: 1, name: '宠物星球店', phone: '400-100-1001', city: '深圳', rating: 4.9, status: 0, address: '南山区科技园' },
      { id: 2, name: '喵喵生活馆', phone: '400-100-1002', city: '杭州', rating: 4.8, status: 0, address: '滨江区' }
    ]
  } finally {
    loading.value = false
  }
}

function showDetail(row) {
  currentRow.value = row
  drawerVisible.value = true
}

function placeholder(action) {
  ElMessage.info(`${action}功能待后端接口接入`)
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

.drawer-content {
  display: grid;
  gap: 16px;
}

.drawer-image {
  width: 100%;
  height: 220px;
  object-fit: cover;
  border-radius: 20px;
}
</style>
