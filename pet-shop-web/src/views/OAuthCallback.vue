<template>
  <div class="oauth-callback">
    <el-icon class="loading-icon" :size="48"><Loading /></el-icon>
    <p>正在登录，请稍候...</p>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

onMounted(() => {
  const { token, userId, nickname, role } = route.query
  if (token && userId) {
    userStore.token = token
    userStore.userInfo = { userId, nickname, role }
    localStorage.setItem('token', token)
    localStorage.setItem('userInfo', JSON.stringify({ userId, nickname, role }))
    ElMessage.success('GitHub登录成功')
    router.replace('/')
  } else {
    ElMessage.error(route.query.error || '登录失败，请重试')
    router.replace('/login')
  }
})
</script>

<style scoped>
.oauth-callback {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 260px);
  color: #666;
}
.loading-icon {
  animation: spin 1s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
