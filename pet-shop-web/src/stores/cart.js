import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCartApi } from '@/api/cart'

export const useCartStore = defineStore('cart', () => {
  const cartList = ref([])
  const cartCount = ref(0)

  async function fetchCart(userId) {
    try {
      const res = await getCartApi(userId)
      cartList.value = res.data || []
      cartCount.value = cartList.value.length
    } catch (e) {
      cartList.value = []
      cartCount.value = 0
    }
  }

  return { cartList, cartCount, fetchCart }
})
