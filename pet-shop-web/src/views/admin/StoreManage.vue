<template>
  <div class="store-manage">
    <div class="page-header">
      <h3>店铺管理</h3>
      <el-button type="primary" @click="openCreateDialog">新增店铺</el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="filters.name"
        placeholder="请输入店铺名称"
        clearable
        style="width: 220px"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
      <el-input
        v-model="filters.city"
        placeholder="请输入城市"
        clearable
        style="width: 180px"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
      <el-select
        v-model="filters.status"
        placeholder="营业状态"
        clearable
        style="width: 140px"
      >
        <el-option label="营业中" :value="0" />
        <el-option label="休息中" :value="1" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <el-table
      :data="tableData"
      v-loading="loading"
      border
      empty-text="暂无店铺数据"
      style="width: 100%"
    >
      <el-table-column prop="id" label="ID" width="70" fixed="left" />
      <el-table-column label="店铺图片" width="100" align="center">
        <template #default="{ row }">
          <el-image
            v-if="row.image"
            :src="row.image"
            :preview-src-list="[row.image]"
            preview-teleported
            fit="cover"
            class="store-image"
          />
          <span v-else class="empty-text">暂无图片</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="店铺名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="phone" label="电话" width="135" />
      <el-table-column prop="province" label="省份" width="100" />
      <el-table-column prop="city" label="城市" width="100" />
      <el-table-column prop="district" label="区县" width="100" />
      <el-table-column prop="address" label="详细地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="businessHours" label="营业时间" width="140" />
      <el-table-column label="评分" width="80" align="center">
        <template #default="{ row }">{{ formatRating(row.rating) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="125" align="center">
        <template #default="{ row }">
          <el-switch
            :model-value="row.status === 0"
            inline-prompt
            active-text="营业"
            inactive-text="休息"
            :loading="statusChangingId === row.id"
            @change="value => handleStatusChange(row, value)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="170">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right" align="center">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @current-change="fetchStoreList"
        @size-change="handleSizeChange"
      />
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑店铺' : '新增店铺'"
      width="760px"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="95px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="店铺名称" prop="name">
              <el-input v-model="form.name" maxlength="100" placeholder="请输入店铺名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" maxlength="20" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="店铺描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
            placeholder="请输入店铺描述"
          />
        </el-form-item>

        <el-form-item label="店铺图片URL" prop="image">
          <el-input v-model="form.image" maxlength="500" placeholder="请输入图片 URL" />
          <el-image
            v-if="form.image"
            :src="form.image"
            :preview-src-list="[form.image]"
            preview-teleported
            fit="cover"
            class="image-preview"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="营业时间" prop="businessHours">
              <el-input v-model="form.businessHours" maxlength="100" placeholder="例如：09:00-21:00" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :value="0">营业中</el-radio>
                <el-radio :value="1">休息中</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="省份" prop="province">
              <el-input v-model="form.province" maxlength="50" placeholder="请输入省份" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市" prop="city">
              <el-input v-model="form.city" maxlength="50" placeholder="请输入城市" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区县" prop="district">
              <el-input v-model="form.district" maxlength="50" placeholder="请输入区县" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" maxlength="255" placeholder="请输入详细地址" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="经度" prop="longitude">
              <el-input-number
                v-model="form.longitude"
                :min="-180"
                :max="180"
                :precision="6"
                controls-position="right"
                placeholder="经度"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="纬度" prop="latitude">
              <el-input-number
                v-model="form.latitude"
                :min="-90"
                :max="90"
                :precision="6"
                controls-position="right"
                placeholder="纬度"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="评分" prop="rating">
              <el-input-number
                v-model="form.rating"
                :min="0"
                :max="5"
                :step="0.1"
                :precision="1"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createStoreApi,
  deleteStoreApi,
  getAdminStoreListApi,
  updateStoreApi,
  updateStoreStatusApi
} from '@/api/store'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const statusChangingId = ref(null)
const formRef = ref(null)

const filters = reactive({
  name: '',
  city: '',
  status: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const createInitialForm = () => ({
  name: '',
  description: '',
  image: '',
  phone: '',
  businessHours: '',
  province: '',
  city: '',
  district: '',
  address: '',
  longitude: null,
  latitude: null,
  rating: 5,
  status: 0
})

const form = reactive(createInitialForm())

const numberValidator = (fieldName, min, max) => (_rule, value, callback) => {
  if (value === null || value === undefined || value === '') {
    callback()
    return
  }
  const number = Number(value)
  if (!Number.isFinite(number)) {
    callback(new Error(`${fieldName}必须为数字`))
  } else if (number < min || number > max) {
    callback(new Error(`${fieldName}必须在 ${min} 到 ${max} 之间`))
  } else {
    callback()
  }
}

const formRules = {
  name: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
  status: [{ required: true, message: '请选择店铺状态', trigger: 'change' }],
  longitude: [{ validator: numberValidator('经度', -180, 180), trigger: 'change' }],
  latitude: [{ validator: numberValidator('纬度', -90, 90), trigger: 'change' }],
  rating: [{ validator: numberValidator('评分', 0, 5), trigger: 'change' }]
}

onMounted(fetchStoreList)

async function fetchStoreList() {
  loading.value = true
  try {
    const res = await getAdminStoreListApi({
      page: pagination.page,
      size: pagination.size,
      name: filters.name.trim() || undefined,
      city: filters.city.trim() || undefined,
      status: filters.status ?? undefined
    })
    tableData.value = res.data?.records || []
    pagination.total = Number(res.data?.total || 0)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchStoreList()
}

function handleReset() {
  Object.assign(filters, { name: '', city: '', status: null })
  pagination.page = 1
  fetchStoreList()
}

function handleSizeChange() {
  pagination.page = 1
  fetchStoreList()
}

function openCreateDialog() {
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row) {
  resetForm()
  isEdit.value = true
  editingId.value = row.id
  Object.assign(form, {
    name: row.name || '',
    description: row.description || '',
    image: row.image || '',
    phone: row.phone || '',
    businessHours: row.businessHours || '',
    province: row.province || '',
    city: row.city || '',
    district: row.district || '',
    address: row.address || '',
    longitude: toOptionalNumber(row.longitude),
    latitude: toOptionalNumber(row.latitude),
    rating: toOptionalNumber(row.rating) ?? 5,
    status: row.status
  })
  dialogVisible.value = true
}

function resetForm() {
  isEdit.value = false
  editingId.value = null
  Object.assign(form, createInitialForm())
  formRef.value?.clearValidate()
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  const payload = {
    name: form.name.trim(),
    description: form.description.trim(),
    image: form.image.trim(),
    phone: form.phone.trim(),
    businessHours: form.businessHours.trim(),
    province: form.province.trim(),
    city: form.city.trim(),
    district: form.district.trim(),
    address: form.address.trim(),
    longitude: form.longitude,
    latitude: form.latitude,
    rating: form.rating,
    status: form.status
  }

  try {
    if (isEdit.value) {
      await updateStoreApi(editingId.value, payload)
      ElMessage.success('店铺修改成功')
    } else {
      await createStoreApi(payload)
      ElMessage.success('店铺新增成功')
    }
    dialogVisible.value = false
    await fetchStoreList()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定删除店铺“${row.name}”吗？`,
      '删除确认',
      { type: 'warning', confirmButtonText: '确定删除' }
    )
  } catch (action) {
    if (action === 'cancel' || action === 'close') return
    throw action
  }

  try {
    await deleteStoreApi(row.id)
    ElMessage.success('店铺删除成功')
    if (tableData.value.length === 1 && pagination.page > 1) {
      pagination.page--
    }
    await fetchStoreList()
  } catch (_error) {
    // 后端业务错误由公共响应拦截器统一展示。
  }
}

async function handleStatusChange(row, isOpen) {
  const nextStatus = isOpen ? 0 : 1
  statusChangingId.value = row.id
  try {
    await updateStoreStatusApi(row.id, nextStatus)
    row.status = nextStatus
    ElMessage.success(nextStatus === 0 ? '店铺已设为营业中' : '店铺已设为休息中')
  } catch (_error) {
    // 保留原状态，错误信息由公共响应拦截器统一展示。
  } finally {
    statusChangingId.value = null
  }
}

function toOptionalNumber(value) {
  if (value === null || value === undefined || value === '') return null
  const number = Number(value)
  return Number.isFinite(number) ? number : null
}

function formatRating(value) {
  const number = Number(value)
  return Number.isFinite(number) ? number.toFixed(1) : '—'
}

function formatDateTime(value) {
  if (!value) return '—'
  return String(value).replace('T', ' ').slice(0, 19)
}
</script>

<style scoped>
.store-manage {
  min-width: 0;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.page-header h3 {
  margin: 0;
}

.search-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 16px;
}

.store-image {
  width: 64px;
  height: 48px;
  border-radius: 6px;
}

.image-preview {
  width: 100px;
  height: 75px;
  margin-top: 10px;
  border-radius: 6px;
}

.empty-text {
  color: #a8abb2;
  font-size: 12px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}
</style>
