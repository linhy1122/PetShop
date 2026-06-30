<template>
  <view class="register-page">
    <view class="register-card">
      <text class="register-title">注册账号</text>

      <view class="form">
        <view class="form-item">
          <text class="form-label">用户名</text>
          <input class="form-input" v-model="form.username" placeholder="请输入用户名" />
        </view>
        <view class="form-item">
          <text class="form-label">密码</text>
          <input class="form-input" v-model="form.password" type="password" placeholder="请输入密码" />
        </view>
        <view class="form-item">
          <text class="form-label">确认密码</text>
          <input class="form-input" v-model="form.confirmPassword" type="password" placeholder="请确认密码" />
        </view>
        <view class="form-item">
          <text class="form-label">手机号（选填）</text>
          <input class="form-input" v-model="form.phone" placeholder="请输入手机号" />
        </view>

        <!-- 滑块验证码 -->
        <view class="form-item">
          <text class="form-label">验证码</text>
          <slider-captcha ref="captchaRef" @verified="onCaptchaVerified" />
        </view>

        <button class="register-btn" :loading="loading" @click="handleRegister">注 册</button>
      </view>

      <view class="login-link">
        已有账号？<text class="link" @click="goLogin">立即登录</text>
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
const captchaVerified = ref(false)
const captchaData = ref({ captchaKey: '', captchaX: 0 })

const form = reactive({
  username: '', password: '', confirmPassword: '', phone: ''
})

function onCaptchaVerified(data) {
  captchaVerified.value = true
  captchaData.value = data
}

function validate() {
  if (!form.username || form.username.length < 3) {
    uni.showToast({ title: '用户名至少3个字符', icon: 'none' })
    return false
  }
  if (!form.password || form.password.length < 6) {
    uni.showToast({ title: '密码至少6位', icon: 'none' })
    return false
  }
  if (form.password !== form.confirmPassword) {
    uni.showToast({ title: '两次密码不一致', icon: 'none' })
    return false
  }
  if (form.phone && !/^1[3-9]\d{9}$/.test(form.phone)) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return false
  }
  if (!captchaVerified.value) {
    uni.showToast({ title: '请完成滑块验证', icon: 'none' })
    return false
  }
  return true
}

async function handleRegister() {
  if (!validate()) return
  loading.value = true
  try {
    await userStore.register(form.username, form.password, form.phone, captchaData.value.captchaKey, captchaData.value.captchaX)
    uni.showToast({ title: '注册成功，请登录', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1000)
  } catch (e) {
    uni.showToast({ title: e.message || '注册失败', icon: 'none' })
    captchaRef.value?.reset()
    captchaVerified.value = false
  } finally {
    loading.value = false
  }
}

function goLogin() {
  uni.navigateBack()
}
</script>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 48rpx;
}

.register-card {
  width: 100%;
  max-width: 640rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 48rpx 40rpx;
  box-shadow: 0 8rpx 40rpx rgba(0,0,0,0.08);
}

.register-title {
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

.register-btn {
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

.login-link {
  text-align: center;
  margin-top: 32rpx;
  font-size: 28rpx;
  color: #999;
}

.link {
  color: #FF6B35;
}
</style>
