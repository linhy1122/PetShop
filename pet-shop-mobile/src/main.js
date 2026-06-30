import { createSSRApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'

// ====== 修改为你电脑的局域网 IP ======
const API_HOST = '192.168.57.147'
const API_PORT = '9090'
// ===================================
const BASE = `http://${API_HOST}:${API_PORT}`

// 全局图片 URL 转换（相对路径 → 完整 URL）
uni.fixImgUrl = function(url) {
  if (!url) return ''
  if (url.startsWith('data:') || url.startsWith('http://') || url.startsWith('https://') || url.startsWith('wxfile://')) return url
  return `${BASE}${url}`
}

// ====== 图片预加载（微信小程序 <image> 必须 HTTPS / 本地路径） ======
const imgCache = new Map()

function downloadImage(url) {
  if (!url || url.startsWith('data:') || url.startsWith('https://') || url.startsWith('wxfile://')) {
    return Promise.resolve(url)
  }
  const fullUrl = url.startsWith('http://') ? url : `${BASE}${url}`
  if (imgCache.has(fullUrl)) return Promise.resolve(imgCache.get(fullUrl))
  return new Promise((resolve) => {
    wx.downloadFile({
      url: fullUrl,
      success: (res) => {
        if (res.statusCode === 200 && res.tempFilePath) {
          imgCache.set(fullUrl, res.tempFilePath)
          resolve(res.tempFilePath)
        } else {
          resolve('')
        }
      },
      fail: () => resolve('')
    })
  })
}

uni.preloadProductImages = async function(products) {
  if (!products) return products
  const list = Array.isArray(products) ? products : [products]
  await Promise.all(list.map(async (p) => {
    if (!p) return
    if (p.mainImage) p.mainImage = await downloadImage(p.mainImage)
    if (p.images) {
      try {
        const arr = typeof p.images === 'string' ? JSON.parse(p.images) : p.images
        if (Array.isArray(arr) && arr.length > 0) {
          p.images = JSON.stringify(await Promise.all(arr.map(u => downloadImage(u))))
        }
      } catch { /* ignore */ }
    }
  }))
  return Array.isArray(products) ? products : list[0]
}

uni.preloadCartImages = async function(cartItems) {
  if (!cartItems || !cartItems.length) return cartItems
  await Promise.all(cartItems.map(async (item) => {
    if (item.productImage) item.productImage = await downloadImage(item.productImage)
  }))
  return cartItems
}

uni.preloadStoreImages = async function(store) {
  if (!store) return store
  if (store.image) store.image = await downloadImage(store.image)
  return store
}

export function createApp() {
  const app = createSSRApp(App)
  const pinia = createPinia()
  app.use(pinia)
  return { app, pinia }
}
