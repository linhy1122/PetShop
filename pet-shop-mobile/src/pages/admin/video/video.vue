<template>
  <view class="admin-video-page">
    <view class="toolbar">
      <button class="add-btn" size="mini" @click="openDialog(null)">上传视频</button>
    </view>

    <view class="video-list">
      <view class="video-card" v-for="v in tableData" :key="v.id">
        <image :src="v.cover || 'https://picsum.photos/seed/av/100/100'" mode="aspectFill" class="cover" />
        <view class="info">
          <text class="title">{{ v.title }}</text>
          <text class="meta">👁 {{ v.viewCount }} | 👍 {{ v.likeCount }}</text>
          <text class="meta status" :class="'vs-' + v.status">{{ statusMap[v.status]?.text || '未知' }}</text>
        </view>
        <view class="actions">
          <button size="mini" @click="openDialog(v)">编辑</button>
          <button size="mini" class="del-btn" @click="handleDelete(v.id)">删除</button>
        </view>
      </view>
      <EmptyState v-if="!loading && tableData.length === 0" description="暂无视频" />
    </view>

    <!-- 上传/编辑弹窗 -->
    <view class="modal-mask" v-if="dialogVisible" @click="dialogVisible = false">
      <view class="modal-content" @click.stop>
        <scroll-view scroll-y style="max-height: 70vh">
          <view class="modal-title">{{ isEdit ? '编辑视频' : '上传视频' }}</view>
          <view class="form-item">
            <text class="form-label">标题 *</text>
            <input class="form-input" v-model="form.title" placeholder="请输入视频标题" />
          </view>
          <view class="form-item">
            <text class="form-label">描述</text>
            <textarea class="form-textarea" v-model="form.description" placeholder="请输入视频描述" />
          </view>
          <view class="form-item">
            <text class="form-label">分类</text>
            <picker :range="categoryOptions" range-key="name" @change="onCatChange">
              <view class="form-picker">{{ catName || '请选择分类' }}</view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">封面图</text>
            <view class="upload-row">
              <image v-if="form.cover" :src="form.cover" mode="aspectFill" class="upload-img" @click="chooseCover" />
              <button v-else size="mini" @click="chooseCover">上传封面</button>
            </view>
          </view>
          <view class="form-item">
            <text class="form-label">视频文件</text>
            <button size="mini" @click="chooseVideo">{{ form.videoUrl ? '重新上传' : '选择视频' }}</button>
            <text v-if="form.videoUrl" class="video-hint">已上传</text>
          </view>
          <view class="form-item">
            <text class="form-label">状态</text>
            <view class="radio-row">
              <label v-for="opt in statusOptions" :key="opt.value" style="margin-right: 24rpx">
                <radio :value="opt.value" :checked="form.status === opt.value"
                       @click="form.status = opt.value" color="#FF6B35" />{{ opt.label }}
              </label>
            </view>
          </view>
        </scroll-view>
        <view class="modal-footer">
          <button class="cancel-btn" @click="dialogVisible = false">取消</button>
          <button class="submit-btn" :loading="submitting" @click="submitForm">保存</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCategoryListApi } from '@/api/category'
import { getVideoListApi, createVideoApi, updateVideoApi, deleteVideoApi } from '@/api/video'
import EmptyState from '@/components/EmptyState.vue'

const tableData = ref([])
const categories = ref([])
const loading = ref(false)

const statusMap = { 0: { type: 'warning', text: '审核中' }, 1: { type: 'success', text: '已发布' }, 2: { type: 'info', text: '已下架' } }
const statusOptions = [{ label: '审核中', value: 0 }, { label: '已发布', value: 1 }, { label: '已下架', value: 2 }]
const categoryOptions = ref([])
const catName = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitting = ref(false)

const form = reactive({
  title: '', description: '', cover: '', videoUrl: '', productId: null,
  categoryId: null, status: 0
})

onMounted(() => {
  fetchData()
  fetchCategories()
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getVideoListApi({ page: 1, size: 100 })
    tableData.value = res.data?.records || []
  } finally { loading.value = false }
}

async function fetchCategories() {
  try {
    const res = await getCategoryListApi()
    categories.value = res.data || []
    categoryOptions.value = res.data || []
  } catch { /* ignore */ }
}

function onCatChange(e) {
  const cat = categoryOptions.value[e.detail.value]
  form.categoryId = cat.id
  catName.value = cat.name
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
    const cat = categories.value.find(c => c.id === row.categoryId)
    catName.value = cat?.name || ''
  } else {
    isEdit.value = false
    editId.value = null
    form.title = ''
    form.description = ''
    form.cover = ''
    form.videoUrl = ''
    form.productId = null
    form.categoryId = null
    form.status = 0
    catName.value = ''
  }
  dialogVisible.value = true
}

function chooseCover() {
  uni.chooseImage({
    count: 1,
    success: (res) => {
      const filePath = res.tempFilePaths[0]
      uni.uploadFile({
        url: '/api/file/upload',
        filePath,
        name: 'file',
        header: { Authorization: `Bearer ${uni.getStorageSync('token')}` },
        success: (uploadRes) => {
          const data = JSON.parse(uploadRes.data)
          if (data.data) form.cover = data.data
        }
      })
    }
  })
}

function chooseVideo() {
  uni.chooseVideo({
    sourceType: ['album', 'camera'],
    success: (res) => {
      uni.uploadFile({
        url: '/api/file/upload',
        filePath: res.tempFilePath,
        name: 'file',
        header: { Authorization: `Bearer ${uni.getStorageSync('token')}` },
        success: (uploadRes) => {
          const data = JSON.parse(uploadRes.data)
          if (data.data) form.videoUrl = data.data
        }
      })
    }
  })
}

async function submitForm() {
  if (!form.title) {
    uni.showToast({ title: '请输入标题', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateVideoApi(editId.value, { ...form })
      uni.showToast({ title: '修改成功', icon: 'success' })
    } else {
      await createVideoApi({ ...form })
      uni.showToast({ title: '上传成功', icon: 'success' })
    }
    dialogVisible.value = false
    fetchData()
  } finally { submitting.value = false }
}

async function handleDelete(id) {
  const res = await uni.showModal({ title: '提示', content: '确定删除该视频？' })
  if (res.confirm) {
    await deleteVideoApi(id)
    uni.showToast({ title: '已删除', icon: 'success' })
    fetchData()
  }
}
</script>

<style scoped>
.admin-video-page {
  padding: 24rpx;
}

.toolbar {
  margin-bottom: 16rpx;
}

.add-btn {
  background: #FF6B35;
  color: #fff;
  border: none;
  padding: 10rpx 28rpx;
  border-radius: 8rpx;
}

.video-card {
  display: flex;
  gap: 16rpx;
  background: #fff;
  padding: 20rpx;
  border-radius: 12rpx;
  margin-bottom: 16rpx;
  align-items: center;
}

.cover {
  width: 120rpx;
  height: 80rpx;
  border-radius: 8rpx;
  flex-shrink: 0;
}

.info {
  flex: 1;
  min-width: 0;
}

.title {
  font-size: 28rpx;
  font-weight: 600;
  display: block;
  margin-bottom: 4rpx;
}

.meta {
  font-size: 22rpx;
  color: #999;
  display: block;
}

.vs-0 { color: #F59E0B; }
.vs-1 { color: #10B981; }
.vs-2 { color: #9CA3AF; }

.actions {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.del-btn {
  color: #EF4444;
  background: none;
  border: none;
}

/* Modal */
.modal-mask {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: flex-end;
  z-index: 999;
}

.modal-content {
  width: 100%;
  max-height: 85vh;
  background: #fff;
  border-radius: 32rpx 32rpx 0 0;
  padding: 40rpx 32rpx;
}

.modal-title {
  font-size: 34rpx;
  font-weight: 700;
  text-align: center;
  margin-bottom: 32rpx;
}

.form-item {
  margin-bottom: 24rpx;
}

.form-label {
  font-size: 26rpx;
  color: #666;
  display: block;
  margin-bottom: 8rpx;
}

.form-input, .form-picker {
  background: #f5f5f5;
  padding: 16rpx 24rpx;
  border-radius: 12rpx;
  font-size: 28rpx;
}

.form-textarea {
  background: #f5f5f5;
  padding: 16rpx 24rpx;
  border-radius: 12rpx;
  font-size: 28rpx;
  height: 120rpx;
  width: 100%;
}

.upload-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.upload-img {
  width: 160rpx;
  height: 100rpx;
  border-radius: 8rpx;
}

.video-hint {
  margin-left: 16rpx;
  color: #10B981;
  font-size: 24rpx;
}

.modal-footer {
  display: flex;
  gap: 20rpx;
  margin-top: 32rpx;
}

.cancel-btn {
  flex: 1;
  background: #f0f0f0;
  color: #333;
  border: none;
  padding: 24rpx;
  border-radius: 16rpx;
  font-size: 30rpx;
}

.submit-btn {
  flex: 1;
  background: #FF6B35;
  color: #fff;
  border: none;
  padding: 24rpx;
  border-radius: 16rpx;
  font-size: 30rpx;
}

.radio-row label {
  font-size: 28rpx;
  display: inline-flex;
  align-items: center;
}
</style>
