<template>
  <view class="oauth-page">
    <view class="oauth-loading">
      <view class="spinner" />
      <text class="loading-text">正在登录，请稍候...</text>
    </view>
  </view>
</template>

<script setup>
import { onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

onLoad((options) => {
  const { token, userId, nickname, role } = options

  if (token && userId) {
    userStore.saveOAuthToken(token, userId, nickname || 'GitHub用户', role || 'user')
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 800)
  } else {
    // 如果有授权 URL，使用 web-view 加载
    // #ifdef APP-PLUS
    const authUrl = options.url
    if (authUrl) {
      // 在 APP 中，OAuth 通过 WebView 或浏览器完成
      // 简化处理：通过后端重定向
      uni.showToast({ title: '请在浏览器中完成授权', icon: 'none' })
    }
    // #endif

    uni.showToast({ title: options.error || '登录失败，请重试', icon: 'none' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1000)
  }
})
</script>

<style scoped>
.oauth-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
}

.oauth-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32rpx;
}

.spinner {
  width: 80rpx;
  height: 80rpx;
  border: 4rpx solid #f0f0f0;
  border-top-color: #FF6B35;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  font-size: 28rpx;
  color: #666;
}
</style>
