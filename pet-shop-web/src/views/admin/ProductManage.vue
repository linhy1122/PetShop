<template>
  <div>
    <h3>商品管理</h3>

    <!-- 工具栏 -->
    
    <div class="toolbar">
      <el-select v-model="filterCategory" placeholder="分类筛选" clearable @change="fetchData" style="width:140px">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-select v-model="filterStatus" placeholder="状态筛选" clearable @change="fetchData" style="width:120px">
        <el-option label="上架" :value="1" />
        <el-option label="下架" :value="0" />
      </el-select>
      <el-input v-model="keyword" placeholder="搜索商品名" clearable style="width:200px"
                @keyup.enter="fetchData" @clear="fetchData" />
      <el-button type="primary" @click="openDialog(null)">新增商品</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" style="margin-top: 16px" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="主图" width="80" align="center">
        <template #default="{ row }">
          <el-image v-if="row.mainImage" :src="row.mainImage"
                    :preview-src-list="[row.mainImage]" fit="cover"
                    class="table-thumb" />
          <span v-else class="no-image">—</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名" min-width="140" show-overflow-tooltip />
      <el-table-column label="分类" width="90">
        <template #default="{ row }">{{ getCategoryName(row.categoryId) }}</template>
      </el-table-column>
      <el-table-column label="类型" width="70">
        <template #default="{ row }">{{ row.productType === 1 ? '宠物' : '周边' }}</template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="90" />
      <el-table-column prop="stock" label="库存" width="70" />
      <el-table-column prop="sales" label="销量" width="70" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 1" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="page.total > page.size">
      <el-pagination background layout="total, prev, pager, next"
                     :total="page.total" :page-size="page.size"
                     v-model:current-page="page.current" @current-change="onPageChange" />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '新增商品'"
               width="720px" destroy-on-close @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <!-- 分类选择（分组） -->
        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" @change="onCategoryChange">
            <el-option-group
              v-for="group in categoryGroups"
              :key="group.label"
              :label="group.label"
            >
              <el-option
                v-for="c in group.children"
                :key="c.id"
                :label="c.name"
                :value="c.id"
              />
            </el-option-group>
          </el-select>
        </el-form-item>

        <!-- 通用字段 -->
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" maxlength="200" />
        </el-form-item>
        <el-form-item label="所属店铺" prop="storeId">
          <el-select v-model="form.storeId" placeholder="请选择店铺">
            <el-option v-for="s in stores" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" maxlength="500" show-word-limit />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" prop="price" label-width="80px">
              <el-input-number v-model="form.price" :min="0.01" :precision="2" :step="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存" prop="stock" label-width="60px">
              <el-input-number v-model="form.stock" :min="0" :step="1" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0"
                     active-text="上架" inactive-text="下架" />
        </el-form-item>

        <!-- 商品主图 -->
        <el-form-item label="商品主图">
          <el-upload :action="uploadUrl" :headers="uploadHeaders"
                     name="file" :show-file-list="false" :on-success="onMainImageSuccess"
                     :on-error="onUploadError" :before-upload="beforeUpload" accept="image/*">
            <img v-if="form.mainImage" :src="form.mainImage"
                 style="width:120px;height:120px;object-fit:cover;border-radius:6px;cursor:pointer" />
            <el-button v-else type="primary" :icon="Plus">上传主图</el-button>
          </el-upload>
          <el-button v-if="form.mainImage" size="small" type="danger" style="margin-top:6px"
                     @click="form.mainImage=''">移除</el-button>
        </el-form-item>

        <!-- 商品图集（多图） -->
        <el-form-item label="商品图集">
          <el-upload :action="uploadUrl" :headers="uploadHeaders"
                     name="file" list-type="picture-card" :file-list="imageFileList"
                     :on-success="onImagesSuccess" :on-error="onUploadError"
                     :before-upload="beforeUpload"
                     :on-remove="onImageRemove" :auto-upload="true"
                     accept="image/*" multiple>
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <!-- 商品详情（简易富文本） -->
        <el-form-item label="商品详情">
          <div class="editor-wrap">
            <div class="editor-toolbar">
              <el-button size="small" @click="execCmd('bold')"><b>B</b></el-button>
              <el-button size="small" @click="execCmd('italic')"><i>I</i></el-button>
              <el-button size="small" @click="execCmd('underline')"><u>U</u></el-button>
              <span class="toolbar-sep" />
              <el-button size="small" @click="execCmd('formatBlock', 'h2')">标题</el-button>
              <el-button size="small" @click="execCmd('insertUnorderedList')">列表</el-button>
              <span class="toolbar-sep" />
              <el-button size="small" @click="execCmd('justifyLeft')">左对齐</el-button>
              <el-button size="small" @click="execCmd('justifyCenter')">居中</el-button>
              <span class="toolbar-sep" />
              <el-button size="small" @click="insertImageByUrl">插图片</el-button>
              <el-color-picker size="small" @change="execCmd('foreColor', $event)" />
            </div>
            <div ref="editorRef" contenteditable="true" class="editor-body"
                 @input="onEditorInput"></div>
          </div>
        </el-form-item>

        <!-- 宠物专属字段（仅宠物分类） -->
        <template v-if="form.productType === 1">
          <el-divider content-position="left">宠物信息</el-divider>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="品种" label-width="60px">
                <el-input v-model="form.breed" maxlength="50" placeholder="如: 布偶猫" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="年龄" label-width="60px">
                <el-input v-model="form.age" maxlength="20" placeholder="如: 3个月" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="性别" label-width="60px">
                <el-radio-group v-model="form.gender">
                  <el-radio :value="0">未知</el-radio>
                  <el-radio :value="1">公</el-radio>
                  <el-radio :value="2">母</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCategoryListApi } from '@/api/category'
import { getStoreListApi } from '@/api/store'
import {
  getProductListApi, createProductApi, updateProductApi,
  deleteProductApi, toggleProductStatusApi
} from '@/api/product'

// ==================== 查询筛选 ====================
const tableData = ref([])
const loading = ref(false)
const categories = ref([])
const stores = ref([])
const filterCategory = ref(null)
const filterStatus = ref(null)
const keyword = ref('')
const page = reactive({ current: 1, size: 10, total: 0 })

// ==================== 对话框 ====================
const dialogVisible = ref(false)
const formRef = ref(null)
const editorRef = ref(null)
const submitting = ref(false)
const isEdit = ref(false)
const editId = ref(null)

const form = reactive({
  categoryId: null, storeId: null, name: '', description: '', detail: '',
  mainImage: '', images: '[]', productType: 1, price: 0.01, stock: 0,
  status: 1, breed: '', age: '', gender: 0
})

const imageFileList = ref([])

const uploadUrl = '/api/file/upload'
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

const formRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  storeId: [{ required: true, message: '请选择店铺', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

// ==================== 分类分组 ====================
const categoryGroups = computed(() => {
  const pets = categories.value.filter(c => c.type === 1)
  const accs = categories.value.filter(c => c.type === 2)
  return [
    { label: '宠物分类', children: pets },
    { label: '宠物周边分类', children: accs }
  ]
})

// ==================== 生命周期 ====================
onMounted(() => {
  fetchData()
  fetchCategories()
  fetchStores()
})

// ==================== 数据加载 ====================
async function fetchData() {
  loading.value = true
  try {
    const res = await getProductListApi({
      page: page.current, size: page.size,
      categoryId: filterCategory.value || undefined,
      status: filterStatus.value !== null ? filterStatus.value : undefined,
      keyword: keyword.value || undefined
    })
    tableData.value = res.data?.records || []
    page.total = res.data?.total || 0
  } finally { loading.value = false }
}

async function fetchCategories() {
  try {
    const res = await getCategoryListApi()
    categories.value = res.data || []
  } catch { /* ignore */ }
}

async function fetchStores() {
  try {
    const res = await getStoreListApi({ size: 200 })
    stores.value = res.data?.records || []
  } catch { /* ignore */ }
}

function onPageChange() {
  fetchData()
}

function getCategoryName(id) {
  return categories.value.find(c => c.id === id)?.name || ''
}

// ==================== 分类联动 ====================
function onCategoryChange(categoryId) {
  const cat = categories.value.find(c => c.id === categoryId)
  if (cat) {
    form.productType = cat.type
    if (cat.type !== 1) {
      form.breed = ''
      form.age = ''
      form.gender = 0
    }
  }
}

// ==================== 对话框打开/关闭 ====================
function openDialog(row) {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    form.categoryId = row.categoryId
    form.storeId = row.storeId
    form.name = row.name
    form.description = row.description || ''
    form.detail = row.detail || ''
    form.mainImage = row.mainImage || ''
    form.productType = row.productType
    form.price = row.price
    form.stock = row.stock
    form.status = row.status
    form.breed = row.breed || ''
    form.age = row.age || ''
    form.gender = row.gender != null ? row.gender : 0

    const imgs = (() => {
      try { return JSON.parse(row.images) || [] } catch { return [] }
    })()
    imageFileList.value = Array.isArray(imgs) ? imgs.map((url, i) => ({ name: `img_${i}`, url })) : []

    nextTick(() => {
      if (editorRef.value) editorRef.value.innerHTML = row.detail || ''
    })
  } else {
    isEdit.value = false
    editId.value = null
    resetForm()
  }
  dialogVisible.value = true
}

function resetForm() {
  form.categoryId = null
  form.storeId = null
  form.name = ''
  form.description = ''
  form.detail = ''
  form.mainImage = ''
  form.productType = 1
  form.price = 0.01
  form.stock = 0
  form.status = 1
  form.breed = ''
  form.age = ''
  form.gender = 0
  imageFileList.value = []
  if (editorRef.value) editorRef.value.innerHTML = ''
  formRef.value?.clearValidate()
}

// ==================== 表单提交 ====================
async function submitForm() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  form.detail = editorRef.value?.innerHTML || ''
  form.images = JSON.stringify(imageFileList.value.map(f => f.url || ''))

  submitting.value = true
  try {
    const payload = { ...form }
    if (isEdit.value) {
      await updateProductApi(editId.value, payload)
      ElMessage.success('修改成功')
    } else {
      await createProductApi(payload)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally { submitting.value = false }
}

// ==================== 图片上传 ====================
function beforeUpload(file) {
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

function onMainImageSuccess(res) {
  if (res.data) form.mainImage = res.data
}

function onImagesSuccess(res, _file, fileList) {
  // 优先取服务器返回 URL，其次取已有 url（已有图片没有 response）
  imageFileList.value = fileList.map(f => ({
    name: f.name,
    url: f.response?.data || f.url || ''
  }))
}

function onUploadError(err) {
  const msg = err?.message || JSON.stringify(err) || '上传失败'
  ElMessage.error('图片上传失败: ' + msg)
}

function onImageRemove(_file, fileList) {
  imageFileList.value = fileList.map(f => ({
    name: f.name,
    url: f.response?.data || f.url || ''
  }))
}

// ==================== 富文本 ====================
function execCmd(command, value = null) {
  document.execCommand(command, false, value)
  editorRef.value?.focus()
}

function insertImageByUrl() {
  const url = prompt('请输入图片链接地址：')
  if (url) document.execCommand('insertImage', false, url)
}

function onEditorInput() {
  form.detail = editorRef.value?.innerHTML || ''
}

// ==================== 状态切换 / 删除 ====================
async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await toggleProductStatusApi(row.id, newStatus)
  ElMessage.success('状态已更新')
  fetchData()
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除该商品？', '提示', { type: 'warning' })
  await deleteProductApi(id)
  ElMessage.success('已删除')
  fetchData()
}
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
.table-thumb {
  width: 56px;
  height: 56px;
  border-radius: 6px;
  cursor: pointer;
  display: block;
}
.table-thumb :deep(img) {
  object-fit: cover;
  border-radius: 6px;
}
.no-image {
  color: #c0c4cc;
  font-size: 16px;
}
.editor-wrap {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}
.editor-toolbar {
  display: flex;
  gap: 4px;
  align-items: center;
  flex-wrap: wrap;
  padding: 6px 10px;
  border-bottom: 1px solid #dcdfe6;
  background: #fafafa;
}
.toolbar-sep {
  display: inline-block;
  width: 1px;
  height: 20px;
  background: #dcdfe6;
  margin: 0 4px;
}
.editor-body {
  min-height: 200px;
  max-height: 400px;
  overflow-y: auto;
  padding: 12px;
  outline: none;
  line-height: 1.7;
}
.editor-body:focus {
  box-shadow: inset 0 0 0 1px #409eff;
}
</style>
