<template>
  <div class="register-page">
    <el-card class="register-card" shadow="always">
      <h2>注册账号</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码"
                    prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password"
                    placeholder="请确认密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号（选填）" prefix-icon="Phone" />
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
                     @click="handleRegister">注 册</el-button>
        </el-form-item>
      </el-form>
      <p class="login-link">
        已有账号？<router-link to="/login">立即登录</router-link>
      </p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCaptchaApi, verifyCaptchaApi } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '', password: '', confirmPassword: '', phone: ''
})

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

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' },
             { min: 3, max: 20, message: '用户名长度3-20个字符', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' },
             { min: 6, message: '密码不能少于6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' },
                    { validator: validateConfirmPassword, trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }]
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

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (!isVerified.value) {
    ElMessage.warning('请完成滑块验证')
    return
  }
  loading.value = true
  try {
    await userStore.register(form.username, form.password, form.phone, captchaKey.value, captchaX.value)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    ElMessage.error(e.message || '注册失败')
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  display: flex; justify-content: center; align-items: center;
  min-height: calc(100vh - 260px);
}
.register-card { width: 400px; padding: 20px; }
.register-card h2 { text-align: center; margin-bottom: 24px; }
.login-link { text-align: center; margin-top: 16px; color: #999; font-size: 14px; }
.login-link a { color: #409eff; }
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
