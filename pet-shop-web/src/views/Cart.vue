<template>
  <div class="cart-page">
    <div class="container">
      <h2>我的购物车</h2>
      <el-empty v-if="cartItems.length === 0" description="购物车是空的">
        <el-button type="primary" @click="$router.push('/product/list')">去逛逛</el-button>
      </el-empty>

      <template v-else>
        <el-card v-for="item in cartItems" :key="item.id"
                 class="cart-item" :class="{ offline: item.productStatus === 0 }">
          <div class="item-row">
            <el-checkbox v-model="item.checked" :true-value="1" :false-value="0"
                         :disabled="item.productStatus === 0"
                         @change="handleCheck(item)" />
            <img :src="item.productImage || '/vite.svg'" class="item-img" />
            <div class="item-info">
              <h4>
                {{ item.productName }}
                <el-tag v-if="item.productStatus === 0" type="danger" size="small">已下架</el-tag>
                <el-tag v-else-if="item.productType === 1" type="warning" size="small">宠物</el-tag>
              </h4>
              <p class="item-price">¥{{ item.price }}</p>
              <p v-if="item.productType === 2 && item.quantity > item.stock"
                 class="stock-warn">⚠ 库存不足（剩余 {{ item.stock }}）</p>
            </div>
            <el-input-number v-model="item.quantity" :min="1"
                             :max="item.productType === 1 ? 1 : item.stock"
                             size="small"
                             @change="handleQuantity(item)" />
            <span class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            <el-button type="danger" size="small" circle :icon="Delete"
                       @click="handleRemove(item)" />
          </div>
        </el-card>

        <div class="cart-footer">
          <div class="footer-left">
            <el-checkbox v-model="allChecked" @change="handleCheckAll">全选</el-checkbox>
            <el-button type="danger" plain size="small"
                       :disabled="checkedCount === 0"
                       @click="handleBatchRemove">删除选中</el-button>
            <el-button type="warning" plain size="small"
                       :disabled="offlineCount === 0"
                       @click="handleClearOffline">清除已下架</el-button>
          </div>
          <div class="footer-right">
            <div class="total">
              合计：<span class="total-price">¥{{ totalPrice }}</span>
            </div>
            <el-button type="primary" size="large" :disabled="totalPrice === 0"
                       @click="handleCheckout">去结算</el-button>
          </div>
        </div>
      </template>

      <!-- 结算对话框 -->
      <el-dialog v-model="dialogVisible" title="确认订单" width="500px">
        <el-form label-width="80px">
          <el-form-item label="收货地址">
            <el-select v-model="selectedAddressId" placeholder="请选择收货地址">
              <el-option v-for="addr in addresses" :key="addr.id"
                         :label="`${addr.receiverName} - ${addr.province}${addr.city}${addr.district} ${addr.detail}`"
                         :value="addr.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="订单备注">
            <el-input v-model="remark" type="textarea" placeholder="选填" />
          </el-form-item>
          <el-form-item label="应付金额">
            <span style="font-size: 22px; color: #f56c6c; font-weight: bold;">¥{{ totalPrice }}</span>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="submitOrder">提交订单</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { getCartApi, updateCartCheckApi, updateCartQuantityApi, removeCartItemApi, batchRemoveCartApi, clearOfflineCartApi } from '@/api/cart'
import { createOrderApi } from '@/api/order'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const cartItems = ref([])
const addresses = ref([])
const dialogVisible = ref(false)
const selectedAddressId = ref(null)
const remark = ref('')
const submitting = ref(false)

const allChecked = computed({
  get: () => cartItems.value.length > 0 && cartItems.value.every(i => i.checked === 1),
  set: (val) => { /* handled by handleCheckAll */ }
})

const totalPrice = computed(() => {
  return cartItems.value
    .filter(i => i.checked === 1 && i.productStatus !== 0)
    .reduce((sum, i) => sum + i.price * i.quantity, 0)
    .toFixed(2)
})

const checkedCount = computed(() =>
  cartItems.value.filter(i => i.checked === 1).length
)

const offlineCount = computed(() =>
  cartItems.value.filter(i => i.productStatus === 0).length
)

onMounted(async () => {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }
  await loadCart()
  await loadAddresses()
})

async function loadCart() {
  try {
    cartItems.value = (await getCartApi(userStore.userInfo.userId)).data || []
  } catch (e) { cartItems.value = [] }
}

async function loadAddresses() {
  try {
    addresses.value = (await request.get(`/address/user/${userStore.userInfo.userId}`)).data || []
    const defaultAddr = addresses.value.find(a => a.isDefault === 1)
    if (defaultAddr) selectedAddressId.value = defaultAddr.id
    else if (addresses.value.length > 0) selectedAddressId.value = addresses.value[0].id
  } catch (e) { addresses.value = [] }
}

async function handleQuantity(item) {
  try {
    await updateCartQuantityApi(item.id, item.quantity)
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '修改失败')
    await loadCart()
  }
}

async function handleCheck(item) {
  await updateCartCheckApi(item.id, item.checked)
  cartStore.fetchCart(userStore.userInfo.userId)
}

async function handleCheckAll(val) {
  const promises = cartItems.value
    .filter(i => i.productStatus !== 0)
    .map(i => updateCartCheckApi(i.id, val ? 1 : 0))
  await Promise.all(promises)
  cartStore.fetchCart(userStore.userInfo.userId)
  await loadCart()
}

async function handleRemove(item) {
  await ElMessageBox.confirm('确定要移除该商品吗？', '提示', { type: 'warning' })
  await removeCartItemApi(item.id)
  await loadCart()
  cartStore.fetchCart(userStore.userInfo.userId)
  ElMessage.success('已移除')
}

async function handleBatchRemove() {
  const ids = cartItems.value.filter(i => i.checked === 1).map(i => i.id)
  if (ids.length === 0) return
  await ElMessageBox.confirm(`确定要删除选中的 ${ids.length} 件商品吗？`, '批量删除', { type: 'warning' })
  await batchRemoveCartApi(ids)
  await loadCart()
  cartStore.fetchCart(userStore.userInfo.userId)
  ElMessage.success(`已删除 ${ids.length} 件商品`)
}

async function handleClearOffline() {
  await ElMessageBox.confirm('确定要清除所有已下架的商品吗？', '清除已下架', { type: 'warning' })
  await clearOfflineCartApi(userStore.userInfo.userId)
  await loadCart()
  cartStore.fetchCart(userStore.userInfo.userId)
  ElMessage.success('已清除下架商品')
}

function handleCheckout() {
  if (!selectedAddressId.value) {
    ElMessage.warning('请先选择收货地址')
    return
  }
  dialogVisible.value = true
}

async function submitOrder() {
  submitting.value = true
  try {
    await createOrderApi(userStore.userInfo.userId, selectedAddressId.value, remark.value)
    ElMessage.success('订单创建成功，请前往支付')
    dialogVisible.value = false
    router.push('/order/list')
  } catch (e) {
    ElMessage.error(e.message || '下单失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.container { max-width: 900px; margin: 0 auto; padding: 20px; }
h2 { margin-bottom: 20px; }
.cart-item { margin-bottom: 12px; }
.item-row { display: flex; align-items: center; gap: 16px; }
.item-img { width: 80px; height: 80px; object-fit: cover; border-radius: 8px; }
.item-info { flex: 1; }
.item-info h4 { font-size: 15px; margin-bottom: 4px; display: flex; align-items: center; gap: 6px; }
.item-price { color: #f56c6c; font-weight: bold; }
.stock-warn { color: #e6a23c; font-size: 12px; }
.cart-item.offline { opacity: 0.6; background: #f5f5f5; }
.item-subtotal { font-weight: bold; min-width: 80px; text-align: right; }
.cart-footer {
  display: flex; align-items: center; justify-content: space-between;
  padding: 20px; background: #fff; border-radius: 12px; margin-top: 20px;
  flex-wrap: wrap; gap: 12px;
}
.footer-left { display: flex; align-items: center; gap: 12px; }
.footer-right { display: flex; align-items: center; gap: 16px; }
.total-price { font-size: 24px; color: #f56c6c; font-weight: bold; }
</style>
