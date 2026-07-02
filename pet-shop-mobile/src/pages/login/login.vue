<template>
  <view class="login-page">
    <view class="login-card">
      <text class="login-title">登录账号</text>

      <view class="form">
        <view class="form-item">
          <text class="form-label">用户名</text>
          <input class="form-input" v-model="form.username" placeholder="请输入用户名" />
        </view>
        <view class="form-item">
          <text class="form-label">密码</text>
          <input class="form-input" v-model="form.password" type="password" placeholder="请输入密码"
                 @confirm="handleLogin" />
        </view>

        <!-- 滑块验证码 -->
        <view class="form-item">
          <text class="form-label">验证码</text>
          <slider-captcha ref="captchaRef" @verified="onCaptchaVerified" />
        </view>

        <button class="login-btn" :loading="loading" @click="handleLogin">登 录</button>
      </view>

      <!-- GitHub OAuth：仅 H5 浏览器支持 -->
      <!-- #ifdef H5 -->
      <view class="oauth-divider">
        <text>其他方式登录</text>
      </view>
      <view class="oauth-buttons">
        <button class="github-btn" @click="handleGithubLogin">
          <text>使用 GitHub 登录</text>
        </button>
      </view>
      <!-- #endif -->
      <!-- #ifndef H5 -->
      <view class="oauth-divider">
        <text>快捷登录</text>
      </view>
      <button class="wechat-btn" :loading="wxLoading" @click="handleWxLogin">
        <text>微信一键登录</text>
      </button>
      <!-- #endif -->

      <view class="register-link">
        还没有账号？<text class="link" @click="goRegister">立即注册</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import SliderCaptcha from '@/components/SliderCaptcha.vue'

const userStore = useUserStore()
const captchaRef = ref(null)
const loading = ref(false)
const wxLoading = ref(false)
const captchaVerified = ref(false)
const captchaData = ref({ captchaKey: '', captchaX: 0 })

const form = reactive({ username: '', password: '' })

function onCaptchaVerified(data) {
  captchaVerified.value = true
  captchaData.value = data
}

async function handleLogin() {
  if (!form.username || !form.password) {
    uni.showToast({ title: '请填写用户名和密码', icon: 'none' })
    return
  }
  if (!captchaVerified.value) {
    uni.showToast({ title: '请完成滑块验证', icon: 'none' })
    return
  }
  loading.value = true
  try {
    await userStore.login(form.username, form.password, captchaData.value.captchaKey, captchaData.value.captchaX)
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 1000)
  } catch (e) {
    uni.showToast({ title: e.message || '登录失败', icon: 'none' })
    captchaRef.value?.reset()
    captchaVerified.value = false
  } finally {
    loading.value = false
  }
}

async function handleWxLogin() {
  wxLoading.value = true
  try {
    await userStore.wxLogin()
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 1000)
  } catch (e) {
    uni.showToast({ title: e.message || '微信登录失败', icon: 'none' })
  } finally {
    wxLoading.value = false
  }
}

function handleGithubLogin() {
  window.location.href = userStore.getGithubAuthUrl()
}

function goRegister() {
  uni.navigateTo({ url: '/pages/register/register' })
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 48rpx;
}

.login-card {
  width: 100%;
  max-width: 640rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 48rpx 40rpx;
  box-shadow: 0 8rpx 40rpx rgba(0,0,0,0.08);
}

.login-title {
  font-size: 40rpx;
  font-weight: 700;
  text-align: center;
  display: block;
  margin-bottom: 48rpx;
}

.form-item {
  margin-bottom: 32rpx;
}

.form-label {
  font-size: 28rpx;
  color: #333;
  display: block;
  margin-bottom: 12rpx;
}

.form-input {
  background: #f5f5f5;
  padding: 20rpx 24rpx;
  border-radius: 12rpx;
  font-size: 30rpx;
}

.login-btn {
  width: 100%;
  background: linear-gradient(135deg, #FF6B35, #FF8F5E);
  color: #fff;
  border: none;
  padding: 24rpx;
  border-radius: 40rpx;
  font-size: 32rpx;
  font-weight: 600;
  margin-top: 16rpx;
}

.oauth-divider {
  display: flex;
  align-items: center;
  margin: 32rpx 0 24rpx;
  color: #bbb;
  font-size: 26rpx;
}

.oauth-divider::before,
.oauth-divider::after {
  content: '';
  flex: 1;
  height: 1rpx;
  background: #e8e8e8;
}

.oauth-divider text {
  padding: 0 24rpx;
}

.github-btn {
  width: 100%;
  background: #24292e;
  color: #fff;
  border: none;
  padding: 24rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.wechat-btn {
  width: 100%;
  background: #07C160;
  color: #fff;
  border: none;
  padding: 24rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.oauth-hint {
  display: block;
  text-align: center;
  font-size: 24rpx;
  color: #999;
  margin-bottom: 16rpx;
}

.register-link {
  text-align: center;
  margin-top: 32rpx;
  font-size: 28rpx;
  color: #999;
}

.link {
  color: #FF6B35;
}
</style>
