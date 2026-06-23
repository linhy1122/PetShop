<template>
  <AdminShell>
    <div class="admin-page">
      <section class="admin-table-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">商品管理</h2>
            <p class="section-subtitle">支持搜索、筛选与详情预览。编辑/删除按钮暂作占位。</p>
          </div>
        </div>

        <div class="toolbar-card">
          <div class="toolbar-actions">
            <el-input v-model="keyword" placeholder="搜索商品名称" clearable style="width: 260px" @change="fetchData" />
            <el-select v-model="productType" placeholder="商品类型" clearable style="width: 180px" @change="fetchData">
              <el-option label="宠物" :value="1" />
              <el-option label="宠物周边" :value="2" />
            </el-select>
          </div>
          <el-button type="primary" @click="fetchData">刷新数据</el-button>
        </div>

        <el-table :data="tableData" v-loading="loading" border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="商品名称" min-width="220" />
          <el-table-column prop="price" label="价格" width="110" />
          <el-table-column prop="stock" label="库存" width="90" />
          <el-table-column prop="sales" label="销量" width="90" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">
                {{ row.status === 1 ? '上架' : '下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="showDetail(row)">详情</el-button>
              <el-button size="small" type="primary" plain @click="placeholder('编辑')">编辑</el-button>
              <el-button size="small" type="danger" plain @click="placeholder('删除')">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>

      <el-drawer v-model="drawerVisible" title="商品详情" size="420px">
        <div v-if="currentRow" class="drawer-content">
          <img :src="currentRow.mainImage || '/vite.svg'" class="drawer-image" />
          <el-descriptions :column="1" border>
            <el-descriptions-item label="商品名称">{{ currentRow.name }}</el-descriptions-item>
            <el-descriptions-item label="价格">¥{{ currentRow.price }}</el-descriptions-item>
            <el-descriptions-item label="库存">{{ currentRow.stock }}</el-descriptions-item>
            <el-descriptions-item label="销量">{{ currentRow.sales }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ currentRow.status === 1 ? '上架' : '下架' }}</el-descriptions-item>
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
import { getProductListApi } from '@/api/product'

const tableData = ref([])
const loading = ref(false)
const keyword = ref('')
const productType = ref(undefined)
const drawerVisible = ref(false)
const currentRow = ref(null)

onMounted(fetchData)

async function fetchData() {
  loading.value = true
  try {
    const response = await getProductListApi({ size: 100, keyword: keyword.value || undefined, productType: productType.value })
    tableData.value = response.data?.records || []
  } catch (error) {
    tableData.value = []
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
