<template>
  <div class="user-center-page">
    <div class="container">
      <h2>个人中心</h2>
      <el-tabs>
        <el-tab-pane label="个人信息">
          <el-form :model="userForm" label-width="80px" style="max-width: 400px">
            <el-form-item label="用户名">
              <el-input :model-value="userStore.userInfo?.nickname" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="userForm.nickname" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userForm.phone" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userForm.email" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="修改密码">
          <el-form :model="pwdForm" label-width="100px" style="max-width: 400px">
            <el-form-item label="原密码">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="pwdForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="changePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="收货地址">
          <el-button type="primary" size="small" @click="showAddrDialog(null)">新增地址</el-button>
          <div class="addr-list">
            <el-card v-for="addr in addresses" :key="addr.id" class="addr-card">
              <div class="address-header">
                <strong class="address-name">{{ addr.receiverName }}</strong>
                <span class="address-phone">{{ addr.receiverPhone }}</span>
                <span class="address-default">
                  <el-tag v-if="addr.isDefault === 1" size="small" type="success">默认</el-tag>
                </span>
              </div>
              <p class="address-detail">{{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detail }}</p>
              <div class="addr-actions">
                <el-button size="small" @click="showAddrDialog(addr)">编辑</el-button>
                <el-button v-if="addr.isDefault !== 1" size="small"
                           @click="setDefault(addr)">设为默认</el-button>
                <el-button size="small" type="danger" @click="deleteAddr(addr.id)">删除</el-button>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>

      <!-- 地址编辑对话框 -->
      <el-dialog v-model="addrDialog" :title="editingAddr?.id ? '编辑地址' : '新增地址'" width="500px">
        <el-form :model="addrForm" label-width="80px">
          <el-form-item label="收货人"><el-input v-model="addrForm.receiverName" /></el-form-item>
          <el-form-item label="手机号"><el-input v-model="addrForm.receiverPhone" /></el-form-item>
          <el-form-item label="所在地区">
            <el-cascader
              v-model="areaValue"
              :options="regionData"
              placeholder="请选择省 / 市 / 区县"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="详细地址"><el-input v-model="addrForm.detail" type="textarea" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="addrDialog = false">取消</el-button>
          <el-button type="primary" @click="saveAddr">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { updateProfileApi, updatePasswordApi } from '@/api/user'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { regionData } from '@/utils/regionData'

const userStore = useUserStore()
const addresses = ref([])
const addrDialog = ref(false)
const editingAddr = ref(null)
const areaValue = ref([])

const userForm = reactive({ nickname: '', phone: '', email: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const addrForm = reactive({ receiverName: '', receiverPhone: '', province: '', city: '', district: '', detail: '' })

onMounted(async () => {
  await loadUserInfo()
  await loadAddresses()
})

async function loadUserInfo() {
  try {
    const res = await request.get(`/user/info?userId=${userStore.userInfo.userId}`)
    const u = res.data
    userForm.nickname = u.nickname || ''
    userForm.phone = u.phone || ''
    userForm.email = u.email || ''
  } catch (e) { /* ignore */ }
}

async function loadAddresses() {
  try {
    addresses.value = (await request.get(`/address/user/${userStore.userInfo.userId}`)).data || []
  } catch (e) { addresses.value = [] }
}

async function saveProfile() {
  await updateProfileApi(userForm)
  ElMessage.success('保存成功')
}

async function changePassword() {
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    ElMessage.error('两次密码不一致')
    return
  }
  await updatePasswordApi(userStore.userInfo.userId, pwdForm.oldPassword, pwdForm.newPassword)
  ElMessage.success('密码修改成功')
}

function showAddrDialog(addr) {
  editingAddr.value = addr
  if (addr) {
    Object.assign(addrForm, addr)
    areaValue.value = addr.province && addr.city && addr.district
      ? [addr.province, addr.city, addr.district]
      : []
  } else {
    Object.assign(addrForm, { receiverName: '', receiverPhone: '', province: '', city: '', district: '', detail: '' })
    areaValue.value = []
  }
  addrDialog.value = true
}

async function saveAddr() {
  try {
    if (!validateAddrForm()) return

    const [province, city, district] = areaValue.value
    addrForm.province = province
    addrForm.city = city
    addrForm.district = district
    addrForm.userId = userStore.userInfo.userId
    if (editingAddr.value?.id) {
      addrForm.id = editingAddr.value.id
      await request.put('/address', { ...addrForm })
    } else {
      await request.post('/address', { ...addrForm })
    }
    ElMessage.success('保存成功')
    addrDialog.value = false
    await loadAddresses()
  } catch (e) { ElMessage.error(e.message) }
}

function validateAddrForm() {
  if (!addrForm.receiverName?.trim()) {
    ElMessage.error('收货人不能为空')
    return false
  }
  if (!addrForm.receiverPhone?.trim()) {
    ElMessage.error('手机号不能为空')
    return false
  }
  if (!/^1[3-9]\d{9}$/.test(addrForm.receiverPhone.trim())) {
    ElMessage.error('请输入正确的11位手机号')
    return false
  }
  if (!Array.isArray(areaValue.value) || areaValue.value.length !== 3) {
    ElMessage.error('请选择完整的省 / 市 / 区县')
    return false
  }
  if (!addrForm.detail?.trim()) {
    ElMessage.error('详细地址不能为空')
    return false
  }
  return true
}

async function setDefault(addr) {
  await request.put(`/address/${addr.id}/default`, null, { params: { userId: userStore.userInfo.userId } })
  ElMessage.success('已设为默认地址')
  await loadAddresses()
}

async function deleteAddr(id) {
  await ElMessageBox.confirm('确定删除该地址？', '提示', { type: 'warning' })
  await request.delete(`/address/${id}`)
  ElMessage.success('已删除')
  await loadAddresses()
}
</script>

<style scoped>
.container { max-width: 900px; margin: 0 auto; padding: 20px; }
h2 { margin-bottom: 20px; }
.addr-list { margin-top: 16px; display: flex; flex-direction: column; gap: 14px; }
.addr-card { border: 1px solid #ebeef5; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04); }
.addr-card :deep(.el-card__body) { padding: 24px 32px; text-align: left; }
.address-header { display: flex; align-items: center; flex-wrap: wrap; gap: 8px 14px; line-height: 1.4; }
.address-name { color: #303133; font-weight: 700; }
.address-phone { color: #303133; font-weight: 400; }
.address-default { display: inline-flex; align-items: center; }
.address-detail { margin: 12px 0 0; color: #606266; line-height: 1.7; word-break: break-word; }
.addr-actions { display: flex; justify-content: flex-start; flex-wrap: wrap; gap: 8px 10px; margin-top: 18px; }
.addr-actions :deep(.el-button + .el-button) { margin-left: 0; }
@media (max-width: 600px) {
  .addr-card :deep(.el-card__body) { padding: 18px 20px; }
}
</style>
