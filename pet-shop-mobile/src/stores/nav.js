/**
 * 导航参数传递 Store
 * uni.switchTab 不支持 query 参数，此 store 作为参数桥梁
 * 跳转前 setProductFilter → switchTab → 目标页 onShow 中 consumeProductFilter
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useNavStore = defineStore('nav', () => {
  const productFilter = ref({
    type: null,       // 1=宠物, 2=周边
    categoryId: null,
    keyword: ''
  })

  function setProductFilter(filter) {
    productFilter.value = {
      type: filter.type ?? null,
      categoryId: filter.categoryId ?? null,
      keyword: filter.keyword ?? ''
    }
  }

  function consumeProductFilter() {
    const f = { ...productFilter.value }
    productFilter.value = { type: null, categoryId: null, keyword: '' }
    return f
  }

  function clearProductFilter() {
    productFilter.value = { type: null, categoryId: null, keyword: '' }
  }

  return { productFilter, setProductFilter, consumeProductFilter, clearProductFilter }
})
