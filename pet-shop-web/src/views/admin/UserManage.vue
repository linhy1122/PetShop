<template>
  <div class="user-manage">
    <div class="page-header">
      <h3>用户管理 / 会员管理</h3>
    </div>

    <div class="search-bar">
      <el-input
        v-model="filters.username"
        placeholder="用户名"
        clearable
        style="width: 160px"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
      <el-input
        v-model="filters.nickname"
        placeholder="昵称"
        clearable
        style="width: 160px"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
      <el-input
        v-model="filters.phone"
        placeholder="手机号"
        clearable
        style="width: 170px"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
      <el-select v-model="filters.status" placeholder="用户状态" clearable style="width: 125px">
        <el-option label="正常" :value="0" />
        <el-option label="禁用" :value="1" />
      </el-select>
      <el-select v-model="filters.role" placeholder="用户角色" clearable style="width: 130px">
        <el-option label="普通用户" value="user" />
        <el-option label="管理员" value="admin" />
      </el-select>
      <el-select v-model="filters.memberLevel" placeholder="会员等级" clearable style="width: 140px">
        <el-option
          v-for="item in memberLevelOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <el-table
      :data="tableData"
      v-loading="loading"
      border
      empty-text="暂无用户数据"
      style="width: 100%"
    >
      <el-table-column label="序号" width="80" fixed="left" align="center">
        <template #default="{ $index }">{{ getUserRowIndex($index) }}</template>
      </el-table-column>
      <el-table-column label="头像" width="80" align="center">
        <template #default="{ row }">
          <el-avatar :size="42" :src="row.avatar || ''">
            {{ getAvatarText(row) }}
          </el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" min-width="130" show-overflow-tooltip />
      <el-table-column prop="nickname" label="昵称" min-width="120" show-overflow-tooltip>
        <template #default="{ row }">{{ row.nickname || '—' }}</template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="135">
        <template #default="{ row }">{{ row.phone || '—' }}</template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip>
        <template #default="{ row }">{{ row.email || '—' }}</template>
      </el-table-column>
      <el-table-column label="角色" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'" effect="plain">
            {{ formatRole(row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="会员等级" width="110" align="center">
        <template #default="{ row }">
          <el-tag type="warning" effect="plain">{{ formatMemberLevel(row.memberLevel) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="120" align="center">
        <template #default="{ row }">
          <el-switch
            :model-value="row.status === 0"
            :disabled="row.role === 'admin'"
            :loading="statusChangingId === row.id"
            inline-prompt
            active-text="正常"
            inactive-text="禁用"
            @change="value => handleStatusChange(row, value)"
          />
        </template>
      </el-table-column>
      <el-table-column label="GitHub绑定ID" width="130" align="center">
        <template #default="{ row }">{{ row.githubId || '未绑定' }}</template>
      </el-table-column>
      <el-table-column label="创建时间" width="170">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right" align="center">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button
            size="small"
            type="danger"
            :disabled="row.role === 'admin'"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
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
        @current-change="fetchUserList"
        @size-change="handleSizeChange"
      />
    </div>

    <el-dialog
      v-model="dialogVisible"
      title="编辑用户"
      width="650px"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        v-loading="detailLoading"
        :model="form"
        :rules="formRules"
        label-width="95px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名">
              <el-input v-model="readonlyInfo.username" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-input :model-value="formatRole(readonlyInfo.role)" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" maxlength="50" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" maxlength="11" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" maxlength="100" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="头像URL" prop="avatar">
          <el-input v-model="form.avatar" maxlength="500" placeholder="请输入头像 URL" />
          <el-avatar v-if="form.avatar" :size="64" :src="form.avatar" class="avatar-preview" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="会员等级" prop="memberLevel">
              <el-select v-model="form.memberLevel" placeholder="请选择会员等级" style="width: 100%">
                <el-option
                  v-for="item in memberLevelOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status" :disabled="readonlyInfo.role === 'admin'">
                <el-radio :value="0">正常</el-radio>
                <el-radio :value="1">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="GitHub ID">
              <el-input :model-value="readonlyInfo.githubId || '未绑定'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建时间">
              <el-input :model-value="formatDateTime(readonlyInfo.createTime)" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" :disabled="detailLoading" @click="handleSubmit">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteAdminUserApi,
  getAdminUserDetailApi,
  getAdminUserListApi,
  updateAdminUserApi,
  updateAdminUserStatusApi
} from '@/api/user'

const memberLevelOptions = [
  { value: 0, label: '普通会员' },
  { value: 1, label: '银卡会员' },
  { value: 2, label: '金卡会员' },
  { value: 3, label: '钻石会员' }
]

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const detailLoading = ref(false)
const submitting = ref(false)
const editingId = ref(null)
const statusChangingId = ref(null)
const formRef = ref(null)

const filters = reactive({
  username: '',
  nickname: '',
  phone: '',
  status: null,
  role: '',
  memberLevel: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const createInitialForm = () => ({
  nickname: '',
  phone: '',
  email: '',
  avatar: '',
  memberLevel: 0,
  status: 0
})

const createInitialReadonlyInfo = () => ({
  username: '',
  role: '',
  githubId: null,
  createTime: ''
})

const form = reactive(createInitialForm())
const readonlyInfo = reactive(createInitialReadonlyInfo())

const validatePhone = (_rule, value, callback) => {
  if (!value || /^1[3-9]\d{9}$/.test(value)) {
    callback()
  } else {
    callback(new Error('请输入正确的11位手机号'))
  }
}

const validateEmail = (_rule, value, callback) => {
  if (!value || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback()
  } else {
    callback(new Error('请输入正确的邮箱地址'))
  }
}

const validateMemberLevel = (_rule, value, callback) => {
  if (Number.isInteger(value) && value >= 0 && value <= 3) {
    callback()
  } else {
    callback(new Error('请选择合法的会员等级'))
  }
}

const validateStatus = (_rule, value, callback) => {
  if (value === 0 || value === 1) {
    callback()
  } else {
    callback(new Error('请选择合法的用户状态'))
  }
}

const formRules = {
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  email: [{ validator: validateEmail, trigger: 'blur' }],
  memberLevel: [
    { required: true, message: '请选择会员等级', trigger: 'change' },
    { validator: validateMemberLevel, trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择用户状态', trigger: 'change' },
    { validator: validateStatus, trigger: 'change' }
  ]
}

onMounted(fetchUserList)

async function fetchUserList() {
  loading.value = true
  try {
    const res = await getAdminUserListApi({
      page: pagination.page,
      size: pagination.size,
      username: filters.username.trim() || undefined,
      nickname: filters.nickname.trim() || undefined,
      phone: filters.phone.trim() || undefined,
      status: filters.status ?? undefined,
      role: filters.role || undefined,
      memberLevel: filters.memberLevel ?? undefined
    })
    tableData.value = res.data?.records || []
    pagination.total = Number(res.data?.total || 0)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchUserList()
}

function handleReset() {
  Object.assign(filters, {
    username: '', nickname: '', phone: '', status: null, role: '', memberLevel: null
  })
  pagination.page = 1
  fetchUserList()
}

function handleSizeChange() {
  pagination.page = 1
  fetchUserList()
}

function getUserRowIndex(index) {
  return (pagination.page - 1) * pagination.size + index + 1
}

async function openEditDialog(row) {
  resetForm()
  editingId.value = row.id
  dialogVisible.value = true
  detailLoading.value = true
  try {
    const res = await getAdminUserDetailApi(row.id)
    const user = res.data || row
    Object.assign(form, {
      nickname: user.nickname || '',
      phone: user.phone || '',
      email: user.email || '',
      avatar: user.avatar || '',
      memberLevel: Number(user.memberLevel ?? 0),
      status: Number(user.status ?? 0)
    })
    Object.assign(readonlyInfo, {
      username: user.username || '',
      role: user.role || '',
      githubId: user.githubId,
      createTime: user.createTime || ''
    })
  } catch (_error) {
    dialogVisible.value = false
  } finally {
    detailLoading.value = false
  }
}

function resetForm() {
  editingId.value = null
  Object.assign(form, createInitialForm())
  Object.assign(readonlyInfo, createInitialReadonlyInfo())
  formRef.value?.clearValidate()
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid || !editingId.value) return

  submitting.value = true
  try {
    await updateAdminUserApi(editingId.value, {
      nickname: form.nickname.trim(),
      phone: form.phone.trim(),
      email: form.email.trim(),
      avatar: form.avatar.trim(),
      memberLevel: form.memberLevel,
      status: form.status
    })
    ElMessage.success('用户信息修改成功')
    dialogVisible.value = false
    await fetchUserList()
  } finally {
    submitting.value = false
  }
}

async function handleStatusChange(row, enabled) {
  if (row.role === 'admin') return
  const nextStatus = enabled ? 0 : 1
  statusChangingId.value = row.id
  try {
    await updateAdminUserStatusApi(row.id, nextStatus)
    row.status = nextStatus
    ElMessage.success(nextStatus === 0 ? '用户已启用' : '用户已禁用')
  } catch (_error) {
    // 保留原状态，后端业务错误由公共响应拦截器统一展示。
  } finally {
    statusChangingId.value = null
  }
}

async function handleDelete(row) {
  if (row.role === 'admin') return
  try {
    await ElMessageBox.confirm(
      `确定删除用户“${row.username}”吗？`,
      '删除确认',
      { type: 'warning', confirmButtonText: '确定删除' }
    )
  } catch (action) {
    if (action === 'cancel' || action === 'close') return
    throw action
  }

  try {
    await deleteAdminUserApi(row.id)
    ElMessage.success('用户删除成功')
    if (tableData.value.length === 1 && pagination.page > 1) {
      pagination.page--
    }
    await fetchUserList()
  } catch (_error) {
    // 管理员保护和订单关联提示由公共响应拦截器统一展示。
  }
}

function formatRole(role) {
  return role === 'admin' ? '管理员' : '普通用户'
}

function formatMemberLevel(level) {
  return memberLevelOptions.find(item => item.value === Number(level))?.label || '普通会员'
}

function formatDateTime(value) {
  if (!value) return '—'
  return String(value).replace('T', ' ').slice(0, 19)
}

function getAvatarText(row) {
  return String(row.nickname || row.username || '用').slice(0, 1)
}
</script>

<style scoped>
.user-manage {
  min-width: 0;
}

.page-header {
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

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}

.avatar-preview {
  margin-top: 10px;
}
</style>
