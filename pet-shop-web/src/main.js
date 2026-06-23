import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '@/styles/theme.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import { onImageError } from '@/utils/image'

const app = createApp(App)

for (const [name, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(name, component)
}

// 全局监听：所有图片加载失败自动替换为占位图
document.addEventListener('error', (e) => {
  if (e.target.tagName === 'IMG') {
    onImageError(e)
  }
}, true)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')
