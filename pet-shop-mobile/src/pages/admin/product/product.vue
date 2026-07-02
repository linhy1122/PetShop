<template>
  <view class="admin-product-page">
    <!-- 搜索栏 -->
    <view class="toolbar">
      <input class="search-input" v-model="keyword" placeholder="搜索商品名..." @confirm="fetchData" />
      <button class="add-btn" size="mini" @click="openDialog(null)">新增</button>
    </view>

    <!-- 筛选 -->
    <view class="filter-row">
      <picker :range="typeOptions" range-key="label" @change="onTypeChange">
        <view class="filter-tag">{{ typeLabel }}</view>
      </picker>
    </view>

    <!-- 商品列表 -->
    <view class="product-list">
      <view class="product-row" v-for="item in tableData" :key="item.id">
        <image :src="item.mainImage || 'https://picsum.photos/seed/ap/100/100'" mode="aspectFill" class="thumb" />
        <view class="info">
          <text class="name">{{ item.name }}</text>
          <text class="meta">¥{{ item.price }} | 库存:{{ item.stock }} | 销量:{{ item.sales }}</text>
          <text class="meta">{{ getCategoryName(item.categoryId) }} | {{ item.productType === 1 ? '宠物' : '周边' }}</text>
        </view>
        <view class="actions">
          <switch :checked="item.status === 1" @change="toggleStatus(item)" color="#FF6B35" />
          <button size="mini" @click="openDialog(item)">编</button>
          <button size="mini" class="del-btn" @click="handleDelete(item.id)">删</button>
        </view>
      </view>
      <EmptyState v-if="!loading && tableData.length === 0" description="暂无商品" />
    </view>

    <!-- 新增/编辑弹窗 -->
    <view class="modal-mask" v-if="dialogVisible" @click="dialogVisible = false">
      <view class="modal-content" @click.stop>
        <scroll-view scroll-y style="max-height: 70vh">
          <view class="modal-title">{{ isEdit ? '编辑商品' : '新增商品' }}</view>
          <view class="form-item">
            <text class="form-label">商品名称 *</text>
            <input class="form-input" v-model="form.name" placeholder="请输入商品名称" />
          </view>
          <view class="form-item">
            <text class="form-label">分类 *</text>
            <picker :range="categoryOptions" range-key="name" @change="onCategoryChange">
              <view class="form-picker">{{ categoryName || '请选择分类' }}</view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">价格 *</text>
            <input class="form-input" v-model.number="form.price" type="digit" placeholder="请输入价格" />
          </view>
          <view class="form-item">
            <text class="form-label">库存</text>
            <input class="form-input" v-model.number="form.stock" type="number" placeholder="请输入库存" />
          </view>
          <view class="form-item">
            <text class="form-label">描述</text>
            <textarea class="form-textarea" v-model="form.description" placeholder="商品描述" />
          </view>
          <view class="form-item">
            <text class="form-label">主图</text>
            <view class="upload-row">
              <image v-if="form.mainImage" :src="form.mainImage" mode="aspectFill" class="upload-img"
                     @click="chooseImage" />
              <button v-else size="mini" @click="chooseImage">选择图片</button>
              <button v-if="form.mainImage" size="mini" class="del-btn" @click="form.mainImage = ''">移除</button>
            </view>
          </view>
          <view class="form-item" v-if="form.productType === 1">
            <text class="form-label">品种</text>
            <input class="form-input" v-model="form.breed" placeholder="如: 布偶猫" />
            <text class="form-label" style="margin-top: 16rpx">年龄</text>
            <input class="form-input" v-model="form.age" placeholder="如: 3个月" />
            <text class="form-label" style="margin-top: 16rpx">性别</text>
            <view class="radio-row">
              <label v-for="opt in genderOptions" :key="opt.value" style="margin-right: 32rpx">
                <radio :value="opt.value" :checked="form.gender === opt.value"
                       @click="form.gender = opt.value" color="#FF6B35" />{{ opt.label }}
              </label>
            </view>
          </view>
          <view class="form-item">
            <text class="form-label">上架</text>
            <switch :checked="form.status === 1" @change="form.status = form.status === 1 ? 0 : 1" color="#FF6B35" />
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
import { getProductListApi, createProductApi, updateProductApi, deleteProductApi, toggleProductStatusApi } from '@/api/product'
import EmptyState from '@/components/EmptyState.vue'

const tableData = ref([])
const categories = ref([])
const loading = ref(false)
const keyword = ref('')
const filterType = ref(undefined)

const typeOptions = [{ label: '全部', value: undefined }, { label: '宠物', value: 1 }, { label: '周边', value: 2 }]
const typeLabel = ref('全部')

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitting = ref(false)

const form = reactive({
  name: '', categoryId: null, storeId: null, description: '', detail: '',
  mainImage: '', productType: 1, price: 0.01, stock: 0, status: 1,
  breed: '', age: '', gender: 0, images: '[]'
})

const categoryOptions = ref([])
const categoryName = ref('')
const genderOptions = [{ label: '未知', value: 0 }, { label: '公', value: 1 }, { label: '母', value: 2 }]

onMounted(() => {
  fetchData()
  fetchCategories()
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getProductListApi({
      page: 1, size: 50,
      productType: filterType.value,
      keyword: keyword.value || undefined
    })
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

function getCategoryName(id) {
  return categories.value.find(c => c.id === id)?.name || ''
}

function onTypeChange(e) {
  const opt = typeOptions[e.detail.value]
  filterType.value = opt.value
  typeLabel.value = opt.label
  fetchData()
}

function onCategoryChange(e) {
  const cat = categoryOptions.value[e.detail.value]
  form.categoryId = cat.id
  form.productType = cat.type
  categoryName.value = cat.name
}

function openDialog(row) {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    Object.assign(form, {
      name: row.name, categoryId: row.categoryId, description: row.description || '',
      detail: row.detail || '', mainImage: row.mainImage || '',
      productType: row.productType, price: row.price, stock: row.stock,
      status: row.status, breed: row.breed || '', age: row.age || '',
      gender: row.gender != null ? row.gender : 0, images: row.images || '[]'
    })
    const cat = categories.value.find(c => c.id === row.categoryId)
    categoryName.value = cat?.name || ''
  } else {
    isEdit.value = false
    editId.value = null
    Object.assign(form, {
      name: '', categoryId: null, description: '', detail: '', mainImage: '',
      productType: 1, price: 0.01, stock: 0, status: 1, breed: '', age: '',
      gender: 0, images: '[]'
    })
    categoryName.value = ''
  }
  dialogVisible.value = true
}

function chooseImage() {
  uni.chooseImage({
    count: 1,
    success: (res) => {
      const filePath = res.tempFilePaths[0]
      uni.uploadFile({
        url: '/api/file/upload',
        filePath,
        name: 'file',
        header: {
          Authorization: `Bearer ${uni.getStorageSync('token')}`
        },
        success: (uploadRes) => {
          const data = JSON.parse(uploadRes.data)
          if (data.data) form.mainImage = data.data
        }
      })
    }
  })
}

async function submitForm() {
  if (!form.name || !form.categoryId) {
    uni.showToast({ title: '请填写必填项', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateProductApi(editId.value, { ...form })
      uni.showToast({ title: '修改成功', icon: 'success' })
    } else {
      await createProductApi({ ...form })
      uni.showToast({ title: '添加成功', icon: 'success' })
    }
    dialogVisible.value = false
    fetchData()
  } finally { submitting.value = false }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await toggleProductStatusApi(row.id, newStatus)
  uni.showToast({ title: '状态已更新', icon: 'success' })
  fetchData()
}

async function handleDelete(id) {
  const res = await uni.showModal({ title: '提示', content: '确定删除该商品？' })
  if (res.confirm) {
    await deleteProductApi(id)
    uni.showToast({ title: '已删除', icon: 'success' })
    fetchData()
  }
}
</script>

<style scoped>
.admin-product-page {
  padding: 24rpx;
}

.toolbar {
  display: flex;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.search-input {
  flex: 1;
  height: 68rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  padding: 0 20rpx;
  font-size: 26rpx;
}

.add-btn {
  background: #FF6B35;
  color: #fff;
  border: none;
  padding: 10rpx 28rpx;
  border-radius: 8rpx;
}

.filter-tag {
  display: inline-block;
  background: #f0f0f0;
  padding: 8rpx 24rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
  margin-bottom: 16rpx;
}

.product-row {
  display: flex;
  gap: 16rpx;
  background: #fff;
  padding: 20rpx;
  border-radius: 12rpx;
  margin-bottom: 16rpx;
  align-items: flex-start;
}

.thumb {
  width: 100rpx;
  height: 100rpx;
  border-radius: 8rpx;
  flex-shrink: 0;
}

.info {
  flex: 1;
  min-width: 0;
}

.name {
  font-size: 28rpx;
  font-weight: 600;
  display: block;
  margin-bottom: 4rpx;
}

.meta {
  font-size: 22rpx;
  color: #999;
  display: block;
  line-height: 1.5;
}

.actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  flex-shrink: 0;
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
  width: 120rpx;
  height: 120rpx;
  border-radius: 8rpx;
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

.radio-row {
  margin-top: 8rpx;
}

.radio-row label {
  font-size: 28rpx;
  display: inline-flex;
  align-items: center;
}
</style>
