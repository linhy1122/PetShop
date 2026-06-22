<template>
  <div class="admin-page">
    <el-container>
      <el-aside width="200px" />
      <el-main>
        <h3>视频管理</h3>
        <el-button type="primary" @click="dialogVisible = true">上传视频</el-button>
        <el-table :data="tableData" style="margin-top: 16px" v-loading="loading">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="viewCount" label="播放量" width="80" />
          <el-table-column prop="likeCount" label="点赞" width="80" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'warning'">
                {{ ['审核中', '已发布', '已下架'][row.status] || '未知' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button size="small" @click="editVideo(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteVideo(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-main>
    </el-container>
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
    const res = await request.get('/video/list', { params: { size: 100 } })
    tableData.value = res.data?.records || []
  } finally { loading.value = false }
}

async function deleteVideo(id) {
  await ElMessageBox.confirm('确定删除该视频？', '提示', { type: 'warning' })
  await request.delete(`/video/${id}`)
  ElMessage.success('已删除')
  fetchData()
}

function editVideo(row) { /* TODO */ }
</script>
