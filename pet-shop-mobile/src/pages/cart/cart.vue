<template>
  <view class="cart-page">
    <EmptyState v-if="!loading && cartItems.length === 0" description="购物车是空的" showBtn
                btn-text="去逛逛" @action="goShopping" />

    <template v-else>
      <!-- 购物车列表 -->
      <view class="cart-list">
        <view class="cart-item" v-for="item in cartItems" :key="item.id"
              :class="{ offline: item.productStatus === 0 }">
          <!-- 复选框 -->
          <view class="item-check" @click="item.productStatus !== 0 && handleCheck(item)">
            <view class="checkbox" :class="{
              checked: item.checked === 1,
              disabled: item.productStatus === 0
            }">
              <text v-if="item.checked === 1">✓</text>
            </view>
          </view>

          <!-- 商品图片 -->
          <image :src="getItemImg(item)" mode="aspectFill" class="item-img"
                 @error="onItemImgError(item)" />

          <!-- 商品信息 -->
          <view class="item-info">
            <view class="item-name-row">
              <text class="item-name text-ellipsis-2">{{ item.productName }}</text>
              <text class="offline-tag" v-if="item.productStatus === 0">已下架</text>
              <text class="type-tag tag-pet" v-else-if="item.productType === 1">宠物</text>
            </view>
            <text class="item-price">¥{{ item.price }}</text>
            <!-- 库存不足警告 -->
            <text class="stock-warn" v-if="item.productType === 2 && item.quantity > (item.stock || 0)">
              ⚠ 库存不足（剩余 {{ item.stock }} 件）
            </text>
            <text class="item-subtotal">小计：¥{{ (item.price * item.quantity).toFixed(2) }}</text>
          </view>

          <!-- 数量 -->
          <view class="item-quantity">
            <view class="qty-btn" @click="changeQty(item, -1)">-</view>
            <text class="qty-value">{{ item.quantity }}</text>
            <view class="qty-btn" @click="changeQty(item, 1)">+</view>
          </view>

          <!-- 删除 -->
          <view class="item-del" @click="handleRemove(item)">🗑</view>
        </view>
      </view>

      <!-- 底部结算栏 -->
      <view class="cart-footer">
        <view class="footer-top" v-if="checkedCount > 0 || offlineCount > 0">
          <view class="footer-check" @click="handleCheckAll">
            <view class="checkbox" :class="{ checked: allChecked }">
              <text v-if="allChecked">✓</text>
            </view>
            <text>全选</text>
          </view>
          <view class="footer-actions">
            <text class="footer-action" v-if="checkedCount > 0" @click="handleBatchRemove">
              删除选中({{ checkedCount }})
            </text>
            <text class="footer-action" v-if="offlineCount > 0" @click="handleClearOffline">
              清除已下架({{ offlineCount }})
            </text>
          </view>
        </view>
        <view class="footer-bottom">
          <view class="footer-total">
            合计：<text class="total-price">¥{{ totalPrice }}</text>
          </view>
          <button class="checkout-btn" :disabled="totalPrice === '0.00'" @click="handleCheckout">
            去结算
          </button>
        </view>
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
import {
  getCartApi, updateCartCheckApi, updateCartQuantityApi,
  removeCartItemApi, batchRemoveCartApi, clearOfflineCartApi
} from '@/api/cart'
import { createOrderApi } from '@/api/order'
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

const FALLBACK_IMG = 'https://picsum.photos/seed/cart-item/200/200'

function getItemImg(item) {
  return uni.fixImgUrl(item.productImage) || FALLBACK_IMG
}

function onItemImgError(item) {
  // handled by fallback
}

const allChecked = computed(() =>
  cartItems.value.filter(i => i.productStatus !== 0).length > 0 &&
  cartItems.value.filter(i => i.productStatus !== 0).every(i => i.checked === 1)
)

const checkedCount = computed(() =>
  cartItems.value.filter(i => i.checked === 1).length
)

const offlineCount = computed(() =>
  cartItems.value.filter(i => i.productStatus === 0).length
)

const totalPrice = computed(() => {
  return cartItems.value
    .filter(i => i.checked === 1 && i.productStatus !== 0)
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
    const items = (await getCartApi(userStore.userInfo.userId)).data || []
    if (items.length) {
      await uni.preloadCartImages(items)
    }
    cartItems.value = items
  } catch (e) {
    uni.showToast({ title: '加载购物车失败', icon: 'none' })
    cartItems.value = []
  } finally {
    loading.value = false
  }
}

async function loadAddresses() {
  try {
    const res = await request.get(`/address/user/${userStore.userInfo.userId}`)
    const list = res.data || []
    if (list.length) {
      addresses.value = list.map(a => ({
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
    } else {
      addresses.value = []
    }
  } catch (e) {
    addresses.value = []
  }
}

function goShopping() {
  uni.switchTab({ url: '/pages/product/list/list' })
}

async function handleCheck(item) {
  if (item.productStatus === 0) return
  item.checked = item.checked === 1 ? 0 : 1
  try {
    await updateCartCheckApi(item.id, item.checked)
  } catch (e) {
    item.checked = item.checked === 1 ? 0 : 1 // 回滚
  }
}

async function handleCheckAll() {
  const newVal = allChecked.value ? 0 : 1
  const activeItems = cartItems.value.filter(i => i.productStatus !== 0)
  try {
    await Promise.all(activeItems.map(i => updateCartCheckApi(i.id, newVal)))
    activeItems.forEach(i => i.checked = newVal)
  } catch (e) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

async function changeQty(item, delta) {
  item.quantity += delta
  if (item.quantity < 1) item.quantity = 1
  if (item.productType === 1 && item.quantity > 1) item.quantity = 1
  try {
    await updateCartQuantityApi(item.id, item.quantity)
  } catch (e) {
    uni.showToast({ title: e.message || '修改失败', icon: 'none' })
    await loadCart()
  }
}

async function handleRemove(item) {
  const res = await uni.showModal({ title: '提示', content: `确定要删除「${item.productName}」吗？` })
  if (!res.confirm) return
  try {
    await removeCartItemApi(item.id)
    await loadCart()
    cartStore.fetchCart(userStore.userInfo.userId)
    uni.showToast({ title: '已删除', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '删除失败', icon: 'none' })
  }
}

async function handleBatchRemove() {
  if (checkedCount.value === 0) return
  const res = await uni.showModal({
    title: '提示',
    content: `确定要删除选中的 ${checkedCount.value} 件商品吗？`
  })
  if (!res.confirm) return
  try {
    const ids = cartItems.value.filter(i => i.checked === 1).map(i => i.id)
    await batchRemoveCartApi(ids)
    await loadCart()
    cartStore.fetchCart(userStore.userInfo.userId)
    uni.showToast({ title: '已删除', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '删除失败', icon: 'none' })
  }
}

async function handleClearOffline() {
  if (offlineCount.value === 0) return
  const res = await uni.showModal({
    title: '提示',
    content: `确定要清除 ${offlineCount.value} 件已下架商品吗？`
  })
  if (!res.confirm) return
  try {
    await clearOfflineCartApi(userStore.userInfo.userId)
    await loadCart()
    cartStore.fetchCart(userStore.userInfo.userId)
    uni.showToast({ title: '已清除', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '清除失败', icon: 'none' })
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
  if (submitting.value) return
  submitting.value = true
  try {
    await createOrderApi(userStore.userInfo.userId, selectedAddressId.value, remark.value)
    uni.showToast({ title: '订单创建成功', icon: 'success' })
    dialogVisible.value = false
    cartStore.fetchCart(userStore.userInfo.userId)
    setTimeout(() => uni.navigateTo({ url: '/pages/order/list/list' }), 1000)
  } catch (e) {
    uni.showToast({ title: e.message || '下单失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.cart-page { padding-bottom: 160rpx; min-height: 100vh; }

.cart-list { padding: 20rpx 24rpx; }

.cart-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
  transition: opacity 0.3s;
}

.cart-item.offline {
  opacity: 0.55;
  background: #fafafa;
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

.checkbox.checked { background: #FF6B35; border-color: #FF6B35; }
.checkbox.disabled { background: #eee; border-color: #ddd; }

.item-img {
  width: 140rpx;
  height: 140rpx;
  border-radius: 12rpx;
  background: #f5f5f5;
  flex-shrink: 0;
}

.item-info { flex: 1; overflow: hidden; }

.item-name-row { display: flex; align-items: flex-start; gap: 8rpx; }

.item-name { font-size: 28rpx; color: #333; margin-bottom: 8rpx; flex: 1; }

.offline-tag {
  font-size: 20rpx;
  padding: 2rpx 8rpx;
  border-radius: 4rpx;
  background: #FFEBEE;
  color: #F44336;
  flex-shrink: 0;
}

.type-tag {
  font-size: 20rpx;
  padding: 2rpx 8rpx;
  border-radius: 4rpx;
  flex-shrink: 0;
}

.tag-pet { background: #FFF3E0; color: #F59E0B; }

.item-price {
  font-size: 30rpx;
  font-weight: 700;
  color: #FF6B35;
  display: block;
}

.stock-warn {
  font-size: 22rpx;
  color: #F59E0B;
  display: block;
  margin-top: 4rpx;
}

.item-subtotal {
  font-size: 24rpx;
  color: #9CA3AF;
  display: block;
  margin-top: 4rpx;
}

.item-quantity { display: flex; align-items: center; gap: 8rpx; }

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

.qty-value { font-size: 28rpx; font-weight: 600; min-width: 48rpx; text-align: center; }

.item-del { font-size: 36rpx; padding: 8rpx; }

/* Footer */
.cart-footer {
  position: fixed;
  bottom: 100rpx;
  left: 0;
  right: 0;
  background: #fff;
  box-shadow: 0 -2rpx 12rpx rgba(0,0,0,0.06);
}

.footer-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12rpx 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.footer-check { display: flex; align-items: center; gap: 8rpx; font-size: 26rpx; }

.footer-actions { display: flex; gap: 24rpx; }

.footer-action { font-size: 24rpx; color: #F44336; }

.footer-bottom {
  display: flex;
  align-items: center;
  padding: 16rpx 24rpx;
  gap: 16rpx;
}

.footer-total { flex: 1; text-align: right; font-size: 26rpx; }

.total-price { font-size: 40rpx; font-weight: 800; color: #FF6B35; }

.checkout-btn {
  background: #FF6B35;
  color: #fff;
  border: none;
  padding: 16rpx 40rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 600;
}

.checkout-btn[disabled] { background: #ccc; }

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

.modal-title { font-size: 34rpx; font-weight: 700; text-align: center; margin-bottom: 32rpx; }

.modal-body { margin-bottom: 32rpx; }

.form-item { margin-bottom: 24rpx; }

.form-label { font-size: 28rpx; color: #666; display: block; margin-bottom: 8rpx; }

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

.form-price { font-size: 40rpx; font-weight: 800; color: #FF6B35; }

.modal-footer { display: flex; gap: 20rpx; }

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
