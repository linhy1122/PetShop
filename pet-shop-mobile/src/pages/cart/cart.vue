<template>
  <view class="cart-page">
    <EmptyState v-if="!loading && cartItems.length === 0" description="购物车是空的" showBtn btn-text="去逛逛"
                @action="goShopping" />

    <template v-else>
      <!-- 购物车列表 -->
      <view class="cart-list">
        <view class="cart-item" v-for="item in cartItems" :key="item.id">
          <view class="item-check" @click="handleCheck(item)">
            <view class="checkbox" :class="{ checked: item.checked === 1 }">
              <text v-if="item.checked === 1">✓</text>
            </view>
          </view>
          <image :src="item.productImage || 'https://picsum.photos/seed/placeholder/200/200'" mode="aspectFill" class="item-img" />
          <view class="item-info">
            <view class="item-name text-ellipsis-2">{{ item.productName }}</view>
            <view class="item-price">¥{{ item.price }}</view>
          </view>
          <view class="item-quantity">
            <view class="qty-btn" @click="changeQty(item, -1)">-</view>
            <text class="qty-value">{{ item.quantity }}</text>
            <view class="qty-btn" @click="changeQty(item, 1)">+</view>
          </view>
          <view class="item-del" @click="handleRemove(item)">🗑</view>
        </view>
      </view>

      <!-- 底部结算栏 -->
      <view class="cart-footer">
        <view class="footer-check" @click="handleCheckAll">
          <view class="checkbox" :class="{ checked: allChecked }">
            <text v-if="allChecked">✓</text>
          </view>
          <text>全选</text>
        </view>
        <view class="footer-total">
          合计：<text class="total-price">¥{{ totalPrice }}</text>
        </view>
        <button class="checkout-btn" :disabled="totalPrice === '0.00'" @click="handleCheckout">去结算</button>
      </view>
    </template>

    <!-- 结算弹窗 -->
    <view class="modal-mask" v-if="dialogVisible" @click="dialogVisible = false">
      <view class="modal-content" @click.stop>
        <view class="modal-title">确认订单</view>
        <view class="modal-body">
          <view class="form-item">
            <text class="form-label">收货地址</text>
            <picker :range="addresses" range-key="label" @change="onAddressChange">
              <view class="form-picker">
                {{ selectedAddressLabel || '请选择收货地址' }}
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">订单备注</text>
            <input class="form-input" v-model="remark" placeholder="选填" />
          </view>
          <view class="form-item">
            <text class="form-label">应付金额</text>
            <text class="form-price">¥{{ totalPrice }}</text>
          </view>
        </view>
        <view class="modal-footer">
          <button class="cancel-btn" @click="dialogVisible = false">取消</button>
          <button class="submit-btn" :loading="submitting" @click="submitOrder">提交订单</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { getCartApi, updateCartCheckApi, updateCartQuantityApi, removeCartItemApi } from '@/api/cart'
import { createOrderApi } from '@/api/order'
import { cartItems as mockCartItems, addresses as mockAddresses } from '@/mock'
import request from '@/utils/request'
import EmptyState from '@/components/EmptyState.vue'

const userStore = useUserStore()
const cartStore = useCartStore()

const cartItems = ref([])
const addresses = ref([])
const dialogVisible = ref(false)
const selectedAddressId = ref(null)
const selectedAddressLabel = ref('')
const remark = ref('')
const submitting = ref(false)
const loading = ref(false)

const allChecked = computed(() =>
  cartItems.value.length > 0 && cartItems.value.every(i => i.checked === 1)
)

const totalPrice = computed(() => {
  return cartItems.value
    .filter(i => i.checked === 1)
    .reduce((sum, i) => sum + i.price * i.quantity, 0)
    .toFixed(2)
})

onShow(async () => {
  if (!userStore.isLoggedIn()) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    setTimeout(() => uni.switchTab({ url: '/pages/user/user' }), 1500)
    return
  }
  await loadCart()
  await loadAddresses()
})

async function loadCart() {
  loading.value = true
  try {
    cartItems.value = (await getCartApi(userStore.userInfo.userId)).data || []
    if (!cartItems.value.length) cartItems.value = mockCartItems
  } catch (e) { cartItems.value = mockCartItems }
  finally { loading.value = false }
}

async function loadAddresses() {
  try {
    const list = (await request.get(`/address/user/${userStore.userInfo.userId}`)).data || []
    const addrList = list.length ? list : mockAddresses
    addresses.value = addrList.map(a => ({
      ...a,
      label: `${a.receiverName} - ${a.province}${a.city}${a.district} ${a.detail}`
    }))
    const defaultAddr = addresses.value.find(a => a.isDefault === 1)
    if (defaultAddr) {
      selectedAddressId.value = defaultAddr.id
      selectedAddressLabel.value = defaultAddr.label
    } else if (addresses.value.length > 0) {
      selectedAddressId.value = addresses.value[0].id
      selectedAddressLabel.value = addresses.value[0].label
    }
  } catch (e) {
    addresses.value = mockAddresses.map(a => ({ ...a, label: `${a.receiverName} - ${a.province}${a.city}${a.district} ${a.detail}` }))
    if (addresses.value.length) {
      selectedAddressId.value = addresses.value[0].id
      selectedAddressLabel.value = addresses.value[0].label
    }
  }
}

function goShopping() {
  uni.switchTab({ url: '/pages/product/list/list' })
}

async function handleCheck(item) {
  item.checked = item.checked === 1 ? 0 : 1
  await updateCartCheckApi(item.id, item.checked)
}

async function handleCheckAll() {
  const newVal = allChecked.value ? 0 : 1
  const promises = cartItems.value.map(i => updateCartCheckApi(i.id, newVal))
  await Promise.all(promises)
  cartItems.value.forEach(i => i.checked = newVal)
}

async function changeQty(item, delta) {
  item.quantity += delta
  if (item.quantity < 1) item.quantity = 1
  await updateCartQuantityApi(item.id, item.quantity)
}

async function handleRemove(item) {
  const res = await uni.showModal({ title: '提示', content: '确定要移除该商品吗？' })
  if (res.confirm) {
    await removeCartItemApi(item.id)
    await loadCart()
    cartStore.fetchCart(userStore.userInfo.userId)
    uni.showToast({ title: '已移除', icon: 'success' })
  }
}

function onAddressChange(e) {
  const idx = e.detail.value
  const addr = addresses.value[idx]
  if (addr) {
    selectedAddressId.value = addr.id
    selectedAddressLabel.value = addr.label
  }
}

function handleCheckout() {
  if (!selectedAddressId.value) {
    uni.showToast({ title: '请先选择收货地址', icon: 'none' })
    return
  }
  dialogVisible.value = true
}

async function submitOrder() {
  submitting.value = true
  try {
    await createOrderApi(userStore.userInfo.userId, selectedAddressId.value, remark.value)
    uni.showToast({ title: '订单创建成功', icon: 'success' })
    dialogVisible.value = false
    uni.navigateTo({ url: '/pages/order/list/list' })
  } catch (e) {
    uni.showToast({ title: e.message || '下单失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.cart-page {
  padding-bottom: 140rpx;
  min-height: 100vh;
}

.cart-list {
  padding: 20rpx 24rpx;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}

.checkbox {
  width: 40rpx;
  height: 40rpx;
  border: 2rpx solid #ddd;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  color: #fff;
}

.checkbox.checked {
  background: #FF6B35;
  border-color: #FF6B35;
}

.item-img {
  width: 140rpx;
  height: 140rpx;
  border-radius: 12rpx;
  background: #f5f5f5;
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  overflow: hidden;
}

.item-name {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 8rpx;
}

.item-price {
  font-size: 30rpx;
  font-weight: 700;
  color: #FF6B35;
}

.item-quantity {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.qty-btn {
  width: 48rpx;
  height: 48rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: #333;
}

.qty-value {
  font-size: 28rpx;
  font-weight: 600;
  min-width: 48rpx;
  text-align: center;
}

.item-del {
  font-size: 36rpx;
  padding: 8rpx;
}

.cart-footer {
  position: fixed;
  bottom: 100rpx;
  left: 0;
  right: 0;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 20rpx 24rpx;
  box-shadow: 0 -2rpx 12rpx rgba(0,0,0,0.06);
  gap: 16rpx;
}

.footer-check {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 26rpx;
}

.footer-total {
  flex: 1;
  text-align: right;
  font-size: 26rpx;
}

.total-price {
  font-size: 36rpx;
  font-weight: 800;
  color: #FF6B35;
}

.checkout-btn {
  background: #FF6B35;
  color: #fff;
  border: none;
  padding: 16rpx 40rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 600;
}

.checkout-btn[disabled] {
  background: #ccc;
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
  background: #fff;
  border-radius: 32rpx 32rpx 0 0;
  padding: 40rpx 32rpx;
  animation: slideUp 0.3s;
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.modal-title {
  font-size: 34rpx;
  font-weight: 700;
  text-align: center;
  margin-bottom: 32rpx;
}

.modal-body {
  margin-bottom: 32rpx;
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

.form-picker {
  background: #f5f5f5;
  padding: 16rpx 24rpx;
  border-radius: 12rpx;
  font-size: 26rpx;
  color: #333;
}

.form-input {
  background: #f5f5f5;
  padding: 16rpx 24rpx;
  border-radius: 12rpx;
  font-size: 26rpx;
}

.form-price {
  font-size: 40rpx;
  font-weight: 800;
  color: #FF6B35;
}

.modal-footer {
  display: flex;
  gap: 20rpx;
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
</style>
