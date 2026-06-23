<template>
  <div class="page-shell user-page">
    <section class="hero-card user-hero">
      <div>
        <div class="status-pill">My profile</div>
        <h1 class="hero-title" style="margin-top: 12px;">个人中心</h1>
        <p class="hero-desc">管理个人资料、地址、密码和我的评价。这里是你的 PetShop 个人控制台。</p>
      </div>
      <div class="user-shortcuts">
        <router-link to="/order/list" class="shortcut-card">我的订单</router-link>
        <router-link to="/cart" class="shortcut-card">购物车</router-link>
        <router-link to="/ai/chat" class="shortcut-card">AI 助手</router-link>
      </div>
    </section>

    <section class="page-section panel-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="个人资料" name="profile">
          <el-form :model="userForm" label-position="top" class="profile-form">
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
            <el-button type="primary" @click="saveProfile">保存资料</el-button>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="收货地址" name="address">
          <div class="address-header">
            <el-button type="primary" @click="showAddrDialog(null)">新增地址</el-button>
          </div>
          <div class="address-list">
            <article v-for="addr in addresses" :key="addr.id" class="soft-card address-card">
              <div>
                <strong>{{ addr.receiverName }}</strong>
                <span class="muted"> {{ addr.receiverPhone }}</span>
                <el-tag v-if="addr.isDefault === 1" size="small" type="success" style="margin-left: 8px;">默认</el-tag>
              </div>
              <p class="muted">{{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}</p>
              <div class="address-actions">
                <el-button size="small" @click="showAddrDialog(addr)">编辑</el-button>
                <el-button v-if="addr.isDefault !== 1" size="small" @click="setDefault(addr)">设为默认</el-button>
                <el-button size="small" type="danger" @click="deleteAddr(addr.id)">删除</el-button>
              </div>
            </article>
          </div>
        </el-tab-pane>

        <el-tab-pane label="修改密码" name="password">
          <el-form :model="pwdForm" label-position="top" class="profile-form">
            <el-form-item label="原密码">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="pwdForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-button type="primary" @click="changePassword">修改密码</el-button>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="我的评价" name="reviews">
          <el-empty description="当前版本暂无用户评价聚合接口">
            <el-button type="primary" @click="$router.push('/product/list')">去评价商品</el-button>
          </el-empty>
        </el-tab-pane>
      </el-tabs>
    </section>

    <el-dialog v-model="addrDialog" :title="editingAddr?.id ? '编辑地址' : '新增地址'" width="520px">
      <el-form :model="addrForm" label-position="top">
        <el-form-item label="收货人"><el-input v-model="addrForm.receiverName" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="addrForm.receiverPhone" /></el-form-item>
        <el-form-item label="省份"><el-input v-model="addrForm.province" /></el-form-item>
        <el-form-item label="城市"><el-input v-model="addrForm.city" /></el-form-item>
        <el-form-item label="区县"><el-input v-model="addrForm.district" /></el-form-item>
        <el-form-item label="详细地址"><el-input v-model="addrForm.detail" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addrDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAddr">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { updateProfileApi, updatePasswordApi } from '@/api/user'

const userStore = useUserStore()
const activeTab = ref('profile')
const addresses = ref([])
const addrDialog = ref(false)
const editingAddr = ref(null)

const userForm = reactive({ nickname: '', phone: '', email: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const addrForm = reactive({ receiverName: '', receiverPhone: '', province: '', city: '', district: '', detail: '' })

onMounted(async () => {
  await loadUserInfo()
  await loadAddresses()
})

async function loadUserInfo() {
  try {
    const response = await request.get(`/user/info?userId=${userStore.userInfo.userId}`)
    const user = response.data || {}
    userForm.nickname = user.nickname || ''
    userForm.phone = user.phone || ''
    userForm.email = user.email || ''
  } catch (error) {
    /* ignore */
  }
}

async function loadAddresses() {
  try {
    addresses.value = (await request.get(`/address/user/${userStore.userInfo.userId}`)).data || []
  } catch (error) {
    addresses.value = []
  }
}

async function saveProfile() {
  await updateProfileApi(userForm)
  ElMessage.success('资料已保存')
}

async function changePassword() {
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    ElMessage.error('两次密码不一致')
    return
  }
  await updatePasswordApi(userStore.userInfo.userId, pwdForm.oldPassword, pwdForm.newPassword)
  ElMessage.success('密码已修改')
}

function showAddrDialog(addr) {
  editingAddr.value = addr
  Object.assign(
    addrForm,
    addr
      ? addr
      : { receiverName: '', receiverPhone: '', province: '', city: '', district: '', detail: '' }
  )
  addrDialog.value = true
}

async function saveAddr() {
  try {
    const payload = { ...addrForm, userId: userStore.userInfo.userId }
    if (editingAddr.value?.id) {
      payload.id = editingAddr.value.id
      await request.put('/address', payload)
    } else {
      await request.post('/address', payload)
    }
    ElMessage.success('地址已保存')
    addrDialog.value = false
    await loadAddresses()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  }
}

async function setDefault(addr) {
  await request.put(`/address/${addr.id}/default`, null, { params: { userId: userStore.userInfo.userId } })
  ElMessage.success('已设为默认地址')
  await loadAddresses()
}

async function deleteAddr(id) {
  await ElMessageBox.confirm('确定删除该地址吗？', '提示', { type: 'warning' })
  await request.delete(`/address/${id}`)
  ElMessage.success('地址已删除')
  await loadAddresses()
}
</script>

<style scoped>
.user-page {
  padding-bottom: 24px;
}

.user-hero {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: end;
}

.user-shortcuts {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.shortcut-card {
  padding: 12px 16px;
  border-radius: 999px;
  background: rgba(138, 108, 255, 0.1);
  color: #5c4a91;
  font-weight: 600;
}

.profile-form {
  max-width: 520px;
}

.address-header {
  margin-bottom: 16px;
}

.address-list {
  display: grid;
  gap: 12px;
}

.address-card {
  display: grid;
  gap: 10px;
}

.address-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

@media (max-width: 1024px) {
  .user-hero {
    flex-direction: column;
    align-items: start;
  }
}
</style>
