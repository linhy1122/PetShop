<template>
  <div class="auth-page page-shell">
    <div class="auth-layout">
      <section class="auth-hero hero-card">
        <div class="status-pill">Join us</div>
        <h1>创建你的 PetShop 账号，开启更顺滑的购物体验</h1>
        <p>
          注册后可以收藏宠物商品、查看订单状态、保存地址信息，还能和 AI 助手继续聊宠物知识。
        </p>
        <div class="auth-highlights">
          <div class="hero-meta-card">
            <div class="hero-meta-label">会员权益</div>
            <div class="hero-meta-value">地址管理、订单追踪、评价管理</div>
          </div>
          <div class="hero-meta-card">
            <div class="hero-meta-label">手机号验证</div>
            <div class="hero-meta-value">保留验证码 UI，后续可直接接短信服务</div>
          </div>
        </div>
      </section>

      <section class="auth-form-panel panel-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">用户注册</h2>
            <p class="section-subtitle">注册成功后即可用用户名密码登录。</p>
          </div>
          <router-link to="/login" class="mini-link">已有账号？去登录</router-link>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          class="auth-form"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="请输入密码"
              size="large"
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              show-password
              placeholder="请再次输入密码"
              size="large"
            />
          </el-form-item>

          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" size="large" />
          </el-form-item>

          <el-form-item label="验证码">
            <div class="code-row">
              <el-input v-model="form.code" placeholder="请输入验证码" size="large" />
              <el-button :disabled="countdown > 0" size="large" @click="handleSendCode">
                {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
              </el-button>
            </div>
          </el-form-item>

          <el-alert
            title="验证码目前仅保留前端界面，后续可无缝接入短信接口。"
            type="warning"
            :closable="false"
            show-icon
          />

          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="submit-btn"
            @click="handleRegister"
          >
            立即注册
          </el-button>
        </el-form>
      </section>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const countdown = ref(0)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  code: ''
})

const confirmValidator = (_rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为 3-20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: confirmValidator, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

function handleSendCode() {
  if (!form.phone) {
    ElMessage.warning('请先输入手机号')
    return
  }

  if (countdown.value > 0) return

  ElMessage.success('验证码已发送（界面占位）')
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value -= 1
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.register(form.username, form.password, form.phone)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  padding-top: 24px;
  padding-bottom: 24px;
}

.auth-layout {
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  gap: 20px;
  align-items: stretch;
}

.auth-hero {
  min-height: 640px;
  padding: 32px;
  background:
    radial-gradient(circle at top right, rgba(138, 108, 255, 0.18), transparent 28%),
    radial-gradient(circle at bottom left, rgba(255, 143, 107, 0.18), transparent 24%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.92), rgba(255, 247, 243, 0.96));
}

.auth-hero h1 {
  margin: 16px 0;
  font-size: clamp(32px, 3.6vw, 52px);
  line-height: 1.08;
}

.auth-hero p {
  max-width: 560px;
  color: var(--pet-text-soft);
  font-size: 16px;
  line-height: 1.9;
}

.auth-highlights {
  display: grid;
  gap: 14px;
  margin-top: 28px;
}

.auth-form-panel {
  padding: 30px;
}

.auth-form {
  margin-top: 20px;
}

.code-row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
}

.submit-btn {
  width: 100%;
  margin-top: 16px;
}

@media (max-width: 1024px) {
  .auth-layout {
    grid-template-columns: 1fr;
  }
}
</style>
