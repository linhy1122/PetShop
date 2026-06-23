<template>
  <div class="page-shell cart-page" v-loading="loading">
    <section class="hero-card cart-hero">
      <div>
        <div class="status-pill">Shopping cart</div>
        <h1 class="hero-title" style="margin-top: 12px;">我的购物车</h1>
        <p class="hero-desc">确认商品、修改数量、结算下单，一步到位。</p>
      </div>
      <div class="cart-summary-top">
        <div class="hero-meta-card">
          <div class="hero-meta-label">选中商品</div>
          <div class="hero-meta-value">{{ selectedCount }}</div>
        </div>
        <div class="hero-meta-card">
          <div class="hero-meta-label">应付金额</div>
          <div class="hero-meta-value">¥{{ totalPrice }}</div>
        </div>
      </div>
    </section>

    <section class="page-section split-layout">
      <div class="panel-card">
        <el-empty v-if="cartItems.length === 0" description="购物车还是空的">
          <el-button type="primary" @click="$router.push('/product/list')">去逛逛</el-button>
        </el-empty>

        <template v-else>
          <article v-for="item in cartItems" :key="item.id" class="soft-card cart-item">
            <el-checkbox v-model="item.checked" :true-value="1" :false-value="0" @change="handleCheck(item)" />
            <img :src="item.productImage || '/vite.svg'" class="item-image" />
            <div class="item-info">
              <strong>{{ item.productName }}</strong>
              <p class="muted">单价 ¥{{ item.price }}</p>
            </div>
            <el-input-number v-model="item.quantity" :min="1" size="small" @change="handleQuantity(item)" />
            <div class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
            <el-button type="danger" circle :icon="Delete" @click="handleRemove(item)" />
          </article>
        </template>
      </div>

      <div class="panel-card sticky-side">
        <div class="section-heading">
          <div>
            <h2 class="section-title">结算信息</h2>
            <p class="section-subtitle">选择默认地址后即可提交订单。</p>
          </div>
        </div>

        <el-form label-position="top">
          <el-form-item label="收货地址">
            <el-select v-model="selectedAddressId" placeholder="请选择收货地址" class="full-width">
              <el-option
                v-for="addr in addresses"
                :key="addr.id"
                :label="`${addr.receiverName} - ${addr.province}${addr.city}${addr.district} ${addr.detail}`"
                :value="addr.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="订单备注">
            <el-input v-model="remark" type="textarea" :rows="4" placeholder="给卖家留言（选填）" />
          </el-form-item>
        </el-form>

        <div class="checkout-box">
          <div class="checkout-row">
            <span class="muted">选中件数</span>
            <strong>{{ selectedCount }}</strong>
          </div>
          <div class="checkout-row">
            <span class="muted">应付金额</span>
            <strong class="price">¥{{ totalPrice }}</strong>
          </div>
        </div>

        <el-button type="primary" size="large" class="checkout-btn" :disabled="selectedCount === 0" @click="handleCheckout">
          提交订单
        </el-button>
      </div>
    </section>

    <el-dialog v-model="dialogVisible" title="确认订单" width="520px">
      <el-form label-position="top">
        <el-form-item label="收货地址">
          <el-select v-model="selectedAddressId" class="full-width">
            <el-option
              v-for="addr in addresses"
              :key="addr.id"
              :label="`${addr.receiverName} - ${addr.province}${addr.city}${addr.district} ${addr.detail}`"
              :value="addr.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="订单备注">
          <el-input v-model="remark" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitOrder">确认提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { getCartApi, updateCartCheckApi, updateCartQuantityApi, removeCartItemApi } from '@/api/cart'
import { createOrderApi } from '@/api/order'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const cartItems = ref([])
const addresses = ref([])
const dialogVisible = ref(false)
const selectedAddressId = ref(null)
const remark = ref('')
const loading = ref(false)
const submitting = ref(false)

const selectedCount = computed(() => cartItems.value.filter((item) => item.checked === 1).length)
const totalPrice = computed(() =>
  cartItems.value
    .filter((item) => item.checked === 1)
    .reduce((sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 0), 0)
    .toFixed(2)
)

onMounted(async () => {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }
  await Promise.all([loadCart(), loadAddresses()])
})

async function loadCart() {
  loading.value = true
  try {
    cartItems.value = (await getCartApi(userStore.userInfo.userId)).data || []
    await cartStore.fetchCart(userStore.userInfo.userId)
  } catch (error) {
    cartItems.value = []
  } finally {
    loading.value = false
  }
}

async function loadAddresses() {
  try {
    addresses.value = (await request.get(`/address/user/${userStore.userInfo.userId}`)).data || []
    const defaultAddress = addresses.value.find((address) => address.isDefault === 1)
    selectedAddressId.value = defaultAddress?.id || addresses.value[0]?.id || null
  } catch (error) {
    addresses.value = []
  }
}

async function handleQuantity(item) {
  await updateCartQuantityApi(item.id, item.quantity)
}

async function handleCheck(item) {
  await updateCartCheckApi(item.id, item.checked)
}

async function handleRemove(item) {
  await ElMessageBox.confirm('确定删除该商品吗？', '提示', { type: 'warning' })
  await removeCartItemApi(item.id)
  ElMessage.success('已删除')
  await loadCart()
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
    ElMessage.success('订单创建成功')
    dialogVisible.value = false
    router.push('/order/list')
  } catch (error) {
    ElMessage.error(error.message || '下单失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.cart-page {
  padding-bottom: 24px;
}

.cart-hero {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: end;
}

.cart-summary-top {
  display: flex;
  gap: 12px;
}

.cart-item {
  display: grid;
  grid-template-columns: auto 86px 1fr auto auto auto;
  gap: 16px;
  align-items: center;
}

.item-image {
  width: 86px;
  height: 86px;
  object-fit: cover;
  border-radius: 18px;
}

.item-subtotal {
  color: var(--pet-primary);
  font-size: 18px;
  font-weight: 800;
}

.full-width {
  width: 100%;
}

.checkout-box {
  display: grid;
  gap: 12px;
  margin: 18px 0;
  padding: 16px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(255, 143, 107, 0.1), rgba(138, 108, 255, 0.1));
}

.checkout-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.price {
  color: var(--pet-primary);
  font-size: 24px;
}

.checkout-btn {
  width: 100%;
}

@media (max-width: 1024px) {
  .cart-hero {
    flex-direction: column;
    align-items: start;
  }

  .cart-item {
    grid-template-columns: 1fr;
    justify-items: start;
  }
}
</style>
