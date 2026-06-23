<template>
  <AdminShell>
    <div class="admin-page">
      <section class="admin-table-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">视频管理</h2>
            <p class="section-subtitle">查看视频、搜索视频并预览关联信息。</p>
          </div>
        </div>

        <div class="toolbar-card">
          <div class="toolbar-actions">
            <el-input v-model="keyword" placeholder="搜索视频标题" clearable style="width: 280px" @change="fetchData" />
          </div>
          <el-button type="primary" @click="fetchData">刷新数据</el-button>
        </div>

        <el-table :data="tableData" v-loading="loading" border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" min-width="240" />
          <el-table-column prop="viewCount" label="播放量" width="100" />
          <el-table-column prop="likeCount" label="点赞数" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'warning'">
                {{ row.status === 1 ? '已发布' : '待审核' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160">
            <template #default="{ row }">
              <el-button size="small" @click="showDetail(row)">详情</el-button>
              <el-button size="small" type="danger" plain @click="placeholder('删除')">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>

      <el-drawer v-model="drawerVisible" title="视频详情" size="420px">
        <div v-if="currentRow" class="drawer-content">
          <img :src="currentRow.cover || '/vite.svg'" class="drawer-image" />
          <el-descriptions :column="1" border>
            <el-descriptions-item label="标题">{{ currentRow.title }}</el-descriptions-item>
            <el-descriptions-item label="播放量">{{ currentRow.viewCount }}</el-descriptions-item>
            <el-descriptions-item label="点赞数">{{ currentRow.likeCount }}</el-descriptions-item>
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
import { getVideoListApi } from '@/api/video'

const tableData = ref([])
const loading = ref(false)
const keyword = ref('')
const drawerVisible = ref(false)
const currentRow = ref(null)

onMounted(fetchData)

async function fetchData() {
  loading.value = true
  try {
    const response = await getVideoListApi({ size: 100 })
    const list = response.data?.records || []
    const search = keyword.value.trim().toLowerCase()
    tableData.value = search ? list.filter((item) => String(item.title || '').toLowerCase().includes(search)) : list
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
</style>
