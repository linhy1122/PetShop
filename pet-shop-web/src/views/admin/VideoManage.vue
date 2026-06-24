<template>
  <div>
    <h3>视频管理</h3>

    <div class="toolbar">
      <el-button type="primary" @click="openDialog(null)">上传视频</el-button>
    </div>

    <el-table :data="tableData" style="margin-top: 16px" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="封面" width="90" align="center">
        <template #default="{ row }">
          <el-image v-if="row.cover" :src="row.cover"
                    :preview-src-list="[row.cover]" fit="cover"
                    class="table-thumb" />
          <span v-else class="no-image">—</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
      <el-table-column label="分类" width="90">
        <template #default="{ row }">{{ getCategoryName(row.categoryId) }}</template>
      </el-table-column>
      <el-table-column prop="viewCount" label="播放量" width="80" />
      <el-table-column prop="likeCount" label="点赞" width="70" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusMap[row.status]?.type">
            {{ statusMap[row.status]?.text || '未知' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 上传 / 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑视频' : '上传视频'"
               width="640px" destroy-on-close @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="视频标题" prop="title">
          <el-input v-model="form.title" maxlength="200" placeholder="请输入视频标题" />
        </el-form-item>
        <el-form-item label="视频描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3"
                    maxlength="500" show-word-limit placeholder="请输入视频描述" />
        </el-form-item>
        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width:100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>

        <!-- 封面图上传 -->
        <el-form-item label="封面图">
          <el-upload :action="uploadUrl" :headers="uploadHeaders"
                     name="file" :show-file-list="false"
                     :on-success="onCoverSuccess" :on-error="onUploadError"
                     :before-upload="beforeImageUpload" accept="image/*">
            <img v-if="form.cover" :src="form.cover"
                 style="width:180px;height:110px;object-fit:cover;border-radius:6px;cursor:pointer" />
            <el-button v-else type="primary" :icon="Plus">上传封面</el-button>
          </el-upload>
          <el-button v-if="form.cover" size="small" type="danger" style="margin-top:6px"
                     @click="form.cover = ''">移除封面</el-button>
        </el-form-item>

        <!-- 视频文件上传 -->
        <el-form-item label="视频文件" prop="videoUrl">
          <el-upload :action="uploadUrl" :headers="uploadHeaders"
                     name="file" :show-file-list="false"
                     :on-success="onVideoSuccess" :on-error="onUploadError"
                     :before-upload="beforeVideoUpload" accept="video/*">
            <el-button type="primary" :icon="VideoCamera" :loading="videoUploading">
              {{ videoUploading ? '上传中...' : (form.videoUrl ? '重新上传' : '选择视频') }}
            </el-button>
          </el-upload>
          <span v-if="form.videoUrl" class="video-hint">
            已上传：{{ form.videoUrl.split('/').pop() }}
          </span>
        </el-form-item>

        <!-- 关联商品 -->
        <el-form-item label="关联商品">
          <el-select v-model="form.productId" placeholder="可选关联商品" clearable
                     filterable style="width:100%">
            <el-option v-for="p in products" :key="p.id"
                       :label="`[${p.id}] ${p.name}`" :value="p.id" />
          </el-select>
        </el-form-item>

        <!-- 状态 -->
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">审核中</el-radio>
            <el-radio :value="1">已发布</el-radio>
            <el-radio :value="2">已下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>

    <!-- 隐藏元素：用于提取视频第一帧 -->
    <video ref="frameVideoRef" style="display:none" muted preload="metadata" />
    <canvas ref="frameCanvasRef" style="display:none" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, VideoCamera } from '@element-plus/icons-vue'
import { getCategoryListApi } from '@/api/category'
import { getProductListApi } from '@/api/product'
import { getVideoListApi, createVideoApi, updateVideoApi, deleteVideoApi } from '@/api/video'

// ============== 表格 ==============
const tableData = ref([])
const loading = ref(false)
const categories = ref([])
const products = ref([])

const statusMap = {
  0: { type: 'warning', text: '审核中' },
  1: { type: 'success', text: '已发布' },
  2: { type: 'info', text: '已下架' }
}

onMounted(() => {
  fetchData()
  fetchCategories()
  fetchProducts()
})

async function fetchData() {
  loading.value = true
  try {
    // 管理端查看所有状态的视频（不传 status 参数）
    const res = await getVideoListApi({ page: 1, size: 100 })
    tableData.value = res.data?.records || []
  } catch {
    ElMessage.error('加载失败')
  } finally { loading.value = false }
}

async function fetchCategories() {
  try {
    const res = await getCategoryListApi()
    categories.value = res.data || []
  } catch { /* ignore */ }
}

async function fetchProducts() {
  try {
    const res = await getProductListApi({ size: 200 })
    products.value = res.data?.records || []
  } catch { /* ignore */ }
}

function getCategoryName(id) {
  return categories.value.find(c => c.id === id)?.name || ''
}

// ============== 对话框 ==============
const dialogVisible = ref(false)
const formRef = ref(null)
const submitting = ref(false)
const videoUploading = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const frameVideoRef = ref(null)
const frameCanvasRef = ref(null)

const form = reactive({
  title: '',
  description: '',
  cover: '',
  videoUrl: '',
  productId: null,
  categoryId: null,
  status: 0
})

const uploadUrl = '/api/file/upload'
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

const formRules = {
  title: [{ required: true, message: '请输入视频标题', trigger: 'blur' }],
  videoUrl: [{ required: true, message: '请上传视频文件', trigger: 'change' }]
}

function openDialog(row) {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    form.title = row.title
    form.description = row.description || ''
    form.cover = row.cover || ''
    form.videoUrl = row.videoUrl || ''
    form.productId = row.productId
    form.categoryId = row.categoryId
    form.status = row.status
  } else {
    isEdit.value = false
    editId.value = null
    resetForm()
  }
  dialogVisible.value = true
}

function resetForm() {
  form.title = ''
  form.description = ''
  form.cover = ''
  form.videoUrl = ''
  form.productId = null
  form.categoryId = null
  form.status = 0
  videoUploading.value = false
  formRef.value?.clearValidate()
}

// ============== 上传回调 ==============
function beforeImageUpload(file) {
  const allowed = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!allowed.includes(file.type)) {
    ElMessage.error('仅支持 JPG / PNG / GIF / WebP 格式')
    return false
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

function beforeVideoUpload(file) {
  if (!file.type.startsWith('video/')) {
    ElMessage.error('请选择视频文件')
    return false
  }
  if (file.size > 100 * 1024 * 1024) {
    ElMessage.error('视频大小不能超过 100MB')
    return false
  }
  videoUploading.value = true
  // 未手动选择封面时，自动提取视频第一帧
  if (!form.cover) {
    captureFirstFrame(file)
  }
  return true
}

/** 提取视频第一帧并上传为封面 */
function captureFirstFrame(file) {
  const video = frameVideoRef.value
  const canvas = frameCanvasRef.value
  if (!video || !canvas) return

  const url = URL.createObjectURL(file)
  let cleaned = false
  const cleanup = () => {
    if (cleaned) return
    cleaned = true
    URL.revokeObjectURL(url)
    video.removeAttribute('src')
    video.load()
  }

  video.onloadeddata = () => {
    // 略过可能的黑帧
    video.currentTime = 0.1
  }

  video.onseeked = () => {
    const ctx = canvas.getContext('2d')
    canvas.width = video.videoWidth
    canvas.height = video.videoHeight
    ctx.drawImage(video, 0, 0)
    canvas.toBlob(async (blob) => {
      cleanup()
      if (!blob) return
      try {
        const fd = new FormData()
        fd.append('file', blob, 'cover.jpg')
        const token = localStorage.getItem('token')
        const headers = token ? { Authorization: `Bearer ${token}` } : {}
        const res = await fetch(uploadUrl, { method: 'POST', headers, body: fd })
        const json = await res.json()
        if (json.data) {
          form.cover = json.data
        }
      } catch { /* 帧提取失败不影响视频上传 */ }
    }, 'image/jpeg', 0.85)
  }

  video.onerror = () => cleanup()
  video.src = url
}

function onCoverSuccess(res) {
  if (res.data) form.cover = res.data
}

function onVideoSuccess(res) {
  videoUploading.value = false
  if (res.data) form.videoUrl = res.data
}

function onUploadError(err) {
  videoUploading.value = false
  const msg = err?.message || JSON.stringify(err) || '上传失败'
  ElMessage.error('上传失败: ' + msg)
}

// ============== 提交 ==============
async function submitForm() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const payload = { ...form }
    if (isEdit.value) {
      await updateVideoApi(editId.value, payload)
      ElMessage.success('修改成功')
    } else {
      await createVideoApi(payload)
      ElMessage.success('上传成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('操作失败')
  } finally { submitting.value = false }
}

// ============== 删除 ==============
async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除该视频？', '提示', { type: 'warning' })
  try {
    await deleteVideoApi(id)
    ElMessage.success('已删除')
    fetchData()
  } catch {
    ElMessage.error('删除失败')
  }
}
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
}
.table-thumb {
  width: 60px;
  height: 40px;
  border-radius: 4px;
  cursor: pointer;
  display: block;
}
.table-thumb :deep(img) {
  object-fit: cover;
  border-radius: 4px;
}
.no-image {
  color: #c0c4cc;
  font-size: 14px;
}
.video-hint {
  margin-left: 12px;
  color: #67c23a;
  font-size: 13px;
}
</style>
