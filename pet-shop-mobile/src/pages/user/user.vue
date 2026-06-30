<template>
  <view class="user-page">
    <!-- 用户头部 -->
    <view class="user-header" v-if="userStore.isLoggedIn()">
      <image src="https://picsum.photos/seed/avatar/200/200" mode="aspectFill" class="avatar" />
      <view class="user-info">
        <text class="nickname">{{ userStore.userInfo?.nickname || '用户' }}</text>
        <text class="user-id">ID: {{ userStore.userInfo?.userId }}</text>
      </view>
    </view>
    <view class="user-header not-login" v-else @click="goLogin">
      <image src="https://picsum.photos/seed/avatar/200/200" mode="aspectFill" class="avatar" />
      <view class="user-info">
        <text class="nickname">点击登录</text>
        <text class="user-id">登录后享受更多服务</text>
      </view>
    </view>

    <!-- 菜单列表 -->
    <view class="menu-section" v-if="userStore.isLoggedIn()">
      <view class="menu-group">
        <view class="menu-title">我的订单</view>
        <view class="menu-item" @click="goOrderList()">
          <text>全部订单</text>
          <text class="arrow">›</text>
        </view>
        <view class="order-status-bar">
          <view class="status-item" @click="goOrderList(0)">
            <text class="status-icon">💰</text>
            <text class="status-label">待支付</text>
          </view>
          <view class="status-item" @click="goOrderList(1)">
            <text class="status-icon">📦</text>
            <text class="status-label">待发货</text>
          </view>
          <view class="status-item" @click="goOrderList(2)">
            <text class="status-icon">🚚</text>
            <text class="status-label">待收货</text>
          </view>
          <view class="status-item" @click="goOrderList(3)">
            <text class="status-icon">⭐</text>
            <text class="status-label">待评价</text>
          </view>
        </view>
      </view>

      <view class="menu-group">
        <view class="menu-title">其他服务</view>
        <view class="menu-item" @click="goPage('/pages/ai/ai')">
          <text>AI 智能客服</text>
          <text class="arrow">›</text>
        </view>
        <view class="menu-item" @click="goPage('/pages/video/list/list')">
          <text>宠物视频</text>
          <text class="arrow">›</text>
        </view>
        <view class="menu-item" v-if="userStore.isAdmin()" @click="goPage('/pages/admin/dashboard/dashboard')">
          <text>管理后台</text>
          <text class="arrow">›</text>
        </view>
      </view>

      <view class="menu-group">
        <view class="menu-title">个人设置</view>
        <view class="menu-item" @click="showProfile">
          <text>个人信息</text>
          <text class="arrow">›</text>
        </view>
        <view class="menu-item" @click="showAddresses">
          <text>收货地址</text>
          <text class="arrow">›</text>
        </view>
        <view class="menu-item" @click="showPassword">
          <text>修改密码</text>
          <text class="arrow">›</text>
        </view>
      </view>

      <view class="logout-btn" @click="handleLogout">退出登录</view>
    </view>

    <!-- 未登录提示 -->
    <view class="menu-section" v-else>
      <view class="menu-group">
        <view class="menu-item" @click="goPage('/pages/ai/ai')">
          <text>AI 智能客服</text>
          <text class="arrow">›</text>
        </view>
        <view class="menu-item" @click="goPage('/pages/video/list/list')">
          <text>宠物视频</text>
          <text class="arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 个人信息弹窗 -->
    <view class="modal-mask" v-if="profileVisible" @click="profileVisible = false">
      <view class="modal-content" @click.stop>
        <view class="modal-title">个人信息</view>
        <view class="modal-body">
          <view class="form-item">
            <text class="form-label">用户名</text>
            <input class="form-input" :value="userStore.userInfo?.nickname" disabled />
          </view>
          <view class="form-item">
            <text class="form-label">昵称</text>
            <input class="form-input" v-model="profileForm.nickname" />
          </view>
          <view class="form-item">
            <text class="form-label">手机号</text>
            <input class="form-input" v-model="profileForm.phone" />
          </view>
          <view class="form-item">
            <text class="form-label">邮箱</text>
            <input class="form-input" v-model="profileForm.email" />
          </view>
        </view>
        <view class="modal-footer">
          <button class="cancel-btn" @click="profileVisible = false">取消</button>
          <button class="submit-btn" @click="saveProfile">保存</button>
        </view>
      </view>
    </view>

    <!-- 修改密码弹窗 -->
    <view class="modal-mask" v-if="pwdVisible" @click="pwdVisible = false">
      <view class="modal-content" @click.stop>
        <view class="modal-title">修改密码</view>
        <view class="modal-body">
          <view class="form-item">
            <text class="form-label">原密码</text>
            <input class="form-input" v-model="pwdForm.oldPassword" type="password" />
          </view>
          <view class="form-item">
            <text class="form-label">新密码</text>
            <input class="form-input" v-model="pwdForm.newPassword" type="password" />
          </view>
          <view class="form-item">
            <text class="form-label">确认密码</text>
            <input class="form-input" v-model="pwdForm.confirmPassword" type="password" />
          </view>
        </view>
        <view class="modal-footer">
          <button class="cancel-btn" @click="pwdVisible = false">取消</button>
          <button class="submit-btn" @click="changePassword">修改</button>
        </view>
      </view>
    </view>

    <!-- 地址管理弹窗 -->
    <view class="modal-mask" v-if="addrVisible" @click="addrVisible = false">
      <view class="modal-content addr-modal" @click.stop>
        <view class="modal-title">
          收货地址
          <button class="add-btn" size="mini" @click="showAddrForm(null)">+ 新增</button>
        </view>
        <scroll-view scroll-y class="addr-list">
          <view class="addr-card" v-for="addr in addresses" :key="addr.id">
            <view class="addr-info">
              <text class="addr-contact">{{ addr.receiverName }} {{ addr.receiverPhone }}</text>
              <text v-if="addr.isDefault === 1" class="default-tag">默认</text>
              <text class="addr-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}</text>
            </view>
            <view class="addr-actions">
              <text @click="showAddrForm(addr)">编辑</text>
              <text v-if="addr.isDefault !== 1" @click="setDefault(addr)">设为默认</text>
              <text class="del" @click="deleteAddr(addr.id)">删除</text>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>

    <!-- 地址编辑弹窗 -->
    <view class="modal-mask" v-if="addrFormVisible" @click="addrFormVisible = false">
      <view class="modal-content" @click.stop>
        <view class="modal-title">{{ editingAddr?.id ? '编辑地址' : '新增地址' }}</view>
        <view class="modal-body">
          <view class="form-item">
            <text class="form-label">收货人</text>
            <input class="form-input" v-model="addrForm.receiverName" />
          </view>
          <view class="form-item">
            <text class="form-label">手机号</text>
            <input class="form-input" v-model="addrForm.receiverPhone" />
          </view>
          <view class="form-item">
            <text class="form-label">省份</text>
            <input class="form-input" v-model="addrForm.province" />
          </view>
          <view class="form-item">
            <text class="form-label">城市</text>
            <input class="form-input" v-model="addrForm.city" />
          </view>
          <view class="form-item">
            <text class="form-label">区/县</text>
            <input class="form-input" v-model="addrForm.district" />
          </view>
          <view class="form-item">
            <text class="form-label">详细地址</text>
            <input class="form-input" v-model="addrForm.detail" />
          </view>
        </view>
        <view class="modal-footer">
          <button class="cancel-btn" @click="addrFormVisible = false">取消</button>
          <button class="submit-btn" @click="saveAddr">保存</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import { updateProfileApi, updatePasswordApi } from '@/api/user'
import request from '@/utils/request'

const userStore = useUserStore()

const addresses = ref([])
const profileVisible = ref(false)
const pwdVisible = ref(false)
const addrVisible = ref(false)
const addrFormVisible = ref(false)
const editingAddr = ref(null)

const profileForm = reactive({ nickname: '', phone: '', email: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const addrForm = reactive({ receiverName: '', receiverPhone: '', province: '', city: '', district: '', detail: '' })

onShow(() => {
  if (userStore.isLoggedIn()) {
    loadUserInfo()
    loadAddresses()
  }
})

async function loadUserInfo() {
  try {
    const res = await request.get(`/user/info?userId=${userStore.userInfo.userId}`)
    const u = res.data
    profileForm.nickname = u.nickname || ''
    profileForm.phone = u.phone || ''
    profileForm.email = u.email || ''
  } catch (e) { /* ignore */ }
}

async function loadAddresses() {
  try {
    addresses.value = (await request.get(`/address/user/${userStore.userInfo.userId}`)).data || []
  } catch (e) { addresses.value = [] }
}

async function saveProfile() {
  await updateProfileApi(profileForm)
  uni.showToast({ title: '保存成功', icon: 'success' })
  profileVisible.value = false
}

async function changePassword() {
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    uni.showToast({ title: '两次密码不一致', icon: 'none' })
    return
  }
  await updatePasswordApi(userStore.userInfo.userId, pwdForm.oldPassword, pwdForm.newPassword)
  uni.showToast({ title: '密码修改成功', icon: 'success' })
  pwdVisible.value = false
}

function showProfile() { profileVisible.value = true }
function showPassword() { pwdVisible.value = true }

function showAddresses() {
  loadAddresses()
  addrVisible.value = true
}

function showAddrForm(addr) {
  editingAddr.value = addr
  if (addr) {
    Object.assign(addrForm, addr)
  } else {
    Object.assign(addrForm, { receiverName: '', receiverPhone: '', province: '', city: '', district: '', detail: '' })
  }
  addrFormVisible.value = true
}

async function saveAddr() {
  try {
    addrForm.userId = userStore.userInfo.userId
    if (editingAddr.value?.id) {
      addrForm.id = editingAddr.value.id
      await request.put('/address', { ...addrForm })
    } else {
      await request.post('/address', { ...addrForm })
    }
    uni.showToast({ title: '保存成功', icon: 'success' })
    addrFormVisible.value = false
    await loadAddresses()
  } catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
}

async function setDefault(addr) {
  await request.put(`/address/${addr.id}/default`, null, { params: { userId: userStore.userInfo.userId } })
  uni.showToast({ title: '已设为默认地址', icon: 'success' })
  await loadAddresses()
}

async function deleteAddr(id) {
  const res = await uni.showModal({ title: '提示', content: '确定删除该地址？' })
  if (res.confirm) {
    await request.delete(`/address/${id}`)
    uni.showToast({ title: '已删除', icon: 'success' })
    await loadAddresses()
  }
}

function goLogin() {
  uni.navigateTo({ url: '/pages/login/login' })
}

function goOrderList(status) {
  const url = status !== undefined
    ? `/pages/order/list/list?status=${status}`
    : '/pages/order/list/list'
  uni.navigateTo({ url })
}

function goPage(url) {
  uni.navigateTo({ url })
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定退出登录？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.showToast({ title: '已退出登录', icon: 'success' })
      }
    }
  })
}
</script>

<style scoped>
.user-page {
  min-height: 100vh;
  padding-bottom: 100rpx;
}

.user-header {
  background: linear-gradient(135deg, #FF6B35, #FF8F5E);
  padding: 60rpx 40rpx;
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.user-header.not-login {
  cursor: pointer;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(255,255,255,0.4);
  background: rgba(255,255,255,0.3);
}

.nickname {
  font-size: 36rpx;
  font-weight: 700;
  color: #fff;
  display: block;
}

.user-id {
  font-size: 24rpx;
  color: rgba(255,255,255,0.8);
  display: block;
  margin-top: 4rpx;
}

.menu-section {
  margin-top: 24rpx;
  padding: 0 24rpx;
}

.menu-group {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
}

.menu-title {
  font-size: 26rpx;
  color: #9CA3AF;
  padding: 20rpx 24rpx 8rpx;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 24rpx;
  border-bottom: 1rpx solid #f5f5f5;
  font-size: 30rpx;
}

.menu-item:last-child {
  border-bottom: none;
}

.arrow {
  font-size: 36rpx;
  color: #ccc;
}

.order-status-bar {
  display: flex;
  padding: 20rpx 0;
}

.status-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.status-icon {
  font-size: 40rpx;
}

.status-label {
  font-size: 24rpx;
  color: #666;
}

.logout-btn {
  text-align: center;
  background: #fff;
  color: #EF4444;
  padding: 28rpx;
  border-radius: 16rpx;
  font-size: 32rpx;
  margin-top: 24rpx;
}

/* Modal 通用样式 */
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
  max-height: 80vh;
  background: #fff;
  border-radius: 32rpx 32rpx 0 0;
  padding: 40rpx 32rpx;
  overflow-y: auto;
}

.modal-title {
  font-size: 34rpx;
  font-weight: 700;
  text-align: center;
  margin-bottom: 32rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20rpx;
}

.add-btn {
  background: #FF6B35;
  color: #fff;
  border: none;
  font-size: 24rpx;
  padding: 8rpx 20rpx;
  border-radius: 8rpx;
}

.form-item {
  margin-bottom: 24rpx;
}

.form-label {
  font-size: 28rpx;
  color: #666;
  display: block;
  margin-bottom: 8rpx;
}

.form-input {
  background: #f5f5f5;
  padding: 16rpx 24rpx;
  border-radius: 12rpx;
  font-size: 28rpx;
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

/* 地址列表 */
.addr-list {
  max-height: 600rpx;
}

.addr-card {
  padding: 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.addr-contact {
  font-size: 30rpx;
  font-weight: 600;
  margin-right: 12rpx;
}

.default-tag {
  font-size: 20rpx;
  background: #10B981;
  color: #fff;
  padding: 2rpx 12rpx;
  border-radius: 8rpx;
}

.addr-detail {
  font-size: 26rpx;
  color: #666;
  display: block;
  margin-top: 8rpx;
}

.addr-actions {
  display: flex;
  gap: 24rpx;
  margin-top: 16rpx;
  font-size: 26rpx;
  color: #3B82F6;
}

.addr-actions .del {
  color: #EF4444;
}
</style>
