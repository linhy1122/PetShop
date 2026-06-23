<template>
  <div class="auth-page page-shell">
    <div class="auth-layout">
      <section class="auth-hero hero-card">
        <div class="status-pill">Welcome back</div>
        <h1>回到 PetShop，继续照顾你的毛孩子</h1>
        <p>
          一站式浏览商品、查看订单、获取 AI 宠物顾问建议。我们把登录入口做得更轻松，也更清晰。
        </p>
        <div class="auth-highlights">
          <div class="hero-meta-card">
            <div class="hero-meta-label">购物体验</div>
            <div class="hero-meta-value">收藏、加购、下单，一路顺滑</div>
          </div>
          <div class="hero-meta-card">
            <div class="hero-meta-label">AI 助手</div>
            <div class="hero-meta-value">宠物知识、商品推荐、售后咨询</div>
          </div>
        </div>
      </section>

      <section class="auth-form-panel panel-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">用户登录</h2>
            <p class="section-subtitle">支持用户名密码登录，验证码登录保留 UI 占位。</p>
          </div>
          <router-link to="/register" class="mini-link">没有账号？去注册</router-link>
        </div>

        <el-tabs v-model="mode">
          <el-tab-pane label="密码登录" name="password" />
          <el-tab-pane label="验证码登录" name="sms" />
        </el-tabs>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          class="auth-form"
        >
          <template v-if="mode === 'password'">
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
                @keyup.enter="handlePasswordLogin"
              />
            </el-form-item>
          </template>

          <template v-else>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" size="large" />
            </el-form-item>
            <el-form-item label="验证码" prop="code">
              <div class="code-row">
                <el-input v-model="form.code" placeholder="请输入验证码" size="large" />
                <el-button :disabled="countdown > 0" size="large" @click="handleSendCode">
                  {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-alert
              title="验证码登录暂未接入后端短信服务，当前仅保留界面结构。"
              type="info"
              :closable="false"
              show-icon
            />
          </template>

          <el-button
            v-if="mode === 'password'"
            type="primary"
            size="large"
            :loading="loading"
            class="submit-btn"
            @click="handlePasswordLogin"
          >
            立即登录
          </el-button>

          <el-button
            v-else
            type="primary"
            size="large"
            class="submit-btn"
            @click="handleSmsPlaceholder"
          >
            体验验证码登录界面
          </el-button>
        </el-form>

        <div class="auth-footer-links">
          <router-link to="/admin/login">管理员登录</router-link>
          <router-link to="/ai/chat">先去体验 AI 助手</router-link>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const mode = ref('password')
const countdown = ref(0)

const form = reactive({
  username: '',
  password: '',
  phone: '',
  code: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

watch(mode, () => {
  formRef.value?.clearValidate()
})

async function handlePasswordLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

function handleSendCode() {
  if (!form.phone) {
    ElMessage.warning('请先输入手机号')
    return
  }

  if (countdown.value > 0) return

  ElMessage.success('验证码已发送（当前为界面占位）')
  countdown.value = 60

  const timer = setInterval(() => {
    countdown.value -= 1
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

function handleSmsPlaceholder() {
  ElMessage.info('当前版本保留验证码登录界面，正式短信校验待后端接入。')
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

.auth-footer-links {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-top: 18px;
  font-size: 14px;
}

.auth-footer-links a {
  color: var(--pet-primary-2);
  font-weight: 600;
}

@media (max-width: 1024px) {
  .auth-layout {
    grid-template-columns: 1fr;
  }
}
</style>
