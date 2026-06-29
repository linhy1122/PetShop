<template>
  <div class="login-page">
    <el-card class="login-card" shadow="always">
      <h2>登录账号</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码"
                    prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item label="验证码" prop="captchaVerified">
          <div class="slider-captcha">
            <div class="slider-bg-wrap" :style="{ width: bgWidth + 'px' }">
              <img :src="bgImage" class="slider-bg" @dragstart.prevent />
              <div class="slider-piece"
                   :style="{ left: pieceLeft + 'px', top: sliderY + 'px', backgroundImage: 'url(' + sliderImage + ')' }" />
            </div>
            <div class="slider-track" :style="{ width: bgWidth + 'px' }">
              <div class="slider-track-bg" :style="{ width: pieceLeft + 25 + 'px' }" />
              <div class="slider-handle"
                   :class="{ dragging: isDragging, success: isVerified, error: isError }"
                   :style="{ left: pieceLeft + 'px' }"
                   @mousedown="startDrag"
                   @touchstart.prevent="startDrag">
                <span v-if="isVerified">✓</span><span v-else-if="isError">✗</span><span v-else>⇋</span>
              </div>
              <span class="slider-tip" v-if="!isVerified && !isError">请按住滑块，拖动到正确位置</span>
              <span class="slider-tip error-tip" v-if="isError">位置错误，请重试</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%"
                     @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="oauth-divider">
        <span>其他方式登录</span>
      </div>
      <div class="oauth-buttons">
        <el-button class="github-btn" @click="handleGithubLogin">
          <svg class="github-icon" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
            <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
          </svg>
          使用 GitHub 登录
        </el-button>
      </div>
      <p class="register-link">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCaptchaApi, verifyCaptchaApi } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', password: '' })

// 滑块验证码状态
const bgWidth = ref(300)
const bgImage = ref('')
const sliderImage = ref('')
const captchaKey = ref('')
const captchaX = ref(0)
const pieceLeft = ref(0)
const isDragging = ref(false)
const isVerified = ref(false)
const isError = ref(false)
const sliderY = ref(62)

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function refreshCaptcha() {
  try {
    const res = await getCaptchaApi()
    captchaKey.value = res.data.captchaKey
    bgImage.value = res.data.bgImage
    sliderImage.value = res.data.sliderImage
    sliderY.value = res.data.sliderY || 62
    pieceLeft.value = 0
    captchaX.value = 0
    isVerified.value = false
    isError.value = false
  } catch (e) { /* ignore */ }
}

onMounted(() => { refreshCaptcha() })

// 滑块拖拽
let startX = 0
const maxLeft = () => bgWidth.value - 50

function startDrag(e) {
  if (isVerified.value || isError.value) return
  isDragging.value = true
  startX = (e.touches ? e.touches[0].clientX : e.clientX) - pieceLeft.value
  window.addEventListener('mousemove', onDrag)
  window.addEventListener('mouseup', endDrag)
  window.addEventListener('touchmove', onDrag, { passive: false })
  window.addEventListener('touchend', endDrag)
}

function onDrag(e) {
  if (!isDragging.value) return
  e.preventDefault()
  const clientX = e.touches ? e.touches[0].clientX : e.clientX
  let left = clientX - startX
  if (left < 0) left = 0
  if (left > maxLeft()) left = maxLeft()
  pieceLeft.value = left
}

async function endDrag() {
  if (!isDragging.value) return
  isDragging.value = false
  window.removeEventListener('mousemove', onDrag)
  window.removeEventListener('mouseup', endDrag)
  window.removeEventListener('touchmove', onDrag)
  window.removeEventListener('touchend', endDrag)
  captchaX.value = Math.round(pieceLeft.value)

  // 即时校验滑块位置
  try {
    const res = await verifyCaptchaApi(captchaKey.value, captchaX.value)
    if (res.data.passed) {
      isVerified.value = true
      isError.value = false
    } else {
      showError()
    }
  } catch (e) {
    showError()
  }
}

function showError() {
  isError.value = true
  setTimeout(() => {
    pieceLeft.value = 0
    captchaX.value = 0
    isError.value = false
  }, 800)
}

onUnmounted(() => {
  window.removeEventListener('mousemove', onDrag)
  window.removeEventListener('mouseup', endDrag)
})

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (!isVerified.value) {
    ElMessage.warning('请完成滑块验证')
    return
  }
  loading.value = true
  try {
    await userStore.login(form.username, form.password, captchaKey.value, captchaX.value)
    ElMessage.success('登录成功')
    const redirect = route.query.redirect
    router.push(redirect || '/')
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

function handleGithubLogin() {
  userStore.githubLogin()
}
</script>

<style scoped>
.login-page {
  display: flex; justify-content: center; align-items: center;
  min-height: calc(100vh - 260px);
}
.login-card { width: 400px; padding: 20px; }
.login-card h2 { text-align: center; margin-bottom: 24px; }
.register-link { text-align: center; margin-top: 16px; color: #999; font-size: 14px; }
.register-link a { color: #409eff; }
.oauth-divider {
  display: flex;
  align-items: center;
  margin: 20px 0 16px;
  color: #bbb;
  font-size: 13px;
}
.oauth-divider::before,
.oauth-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e8e8e8;
}
.oauth-divider span {
  padding: 0 12px;
}
.oauth-buttons {
  text-align: center;
  margin-bottom: 8px;
}
.github-btn {
  width: 100%;
  background: #24292e;
  border-color: #24292e;
  color: #fff;
}
.github-btn:hover {
  background: #1b1f23;
  border-color: #1b1f23;
  color: #fff;
}
.github-icon {
  margin-right: 6px;
  vertical-align: middle;
}
.slider-captcha {
  user-select: none;
}
.slider-bg-wrap {
  position: relative;
  height: 160px;
  margin: 0 auto;
  border-radius: 6px;
  overflow: hidden;
}
.slider-bg {
  width: 100%;
  height: 100%;
  display: block;
}
.slider-piece {
  position: absolute;
  width: 50px;
  height: 50px;
  background-size: 50px 50px;
  background-repeat: no-repeat;
  box-shadow: 0 0 6px rgba(0,0,0,.3);
  border-radius: 4px;
  transition: none;
}
.slider-track {
  position: relative;
  height: 40px;
  margin: 10px auto 0;
  background: #eee;
  border-radius: 20px;
  overflow: hidden;
}
.slider-track-bg {
  height: 100%;
  background: #c8e6c9;
  border-radius: 20px 0 0 20px;
  transition: width 0.1s;
}
.slider-handle {
  position: absolute;
  top: 0;
  width: 50px;
  height: 40px;
  background: #fff;
  border: 2px solid #ddd;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  font-size: 18px;
  color: #999;
  z-index: 2;
  transition: background .2s;
}
.slider-handle.dragging { border-color: #409eff; color: #409eff; }
.slider-handle.success { border-color: #67c23a; background: #67c23a; color: #fff; cursor: default; }
.slider-handle.error { border-color: #f56c6c; background: #f56c6c; color: #fff; animation: shake 0.4s; }
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}
.slider-tip {
  position: absolute;
  left: 60px;
  line-height: 40px;
  color: #bbb;
  font-size: 13px;
  pointer-events: none;
}
.slider-tip.error-tip { color: #f56c6c; }
</style>
