<template>
  <div class="auth-page page-shell">
    <div class="auth-layout admin-layout">
      <section class="auth-hero hero-card">
        <div class="status-pill">Admin only</div>
        <h1>管理员后台登录</h1>
        <p>用于运营管理、内容审核和数据统计。请使用管理员账号登录后进入后台。</p>
        <div class="auth-highlights">
          <div class="hero-meta-card">
            <div class="hero-meta-label">Dashboard</div>
            <div class="hero-meta-value">商品、订单、用户、视频一屏掌握</div>
          </div>
          <div class="hero-meta-card">
            <div class="hero-meta-label">权限验证</div>
            <div class="hero-meta-value">普通用户会被自动拦截，避免误入后台</div>
          </div>
        </div>
      </section>

      <section class="auth-form-panel panel-card">
        <div class="section-heading">
          <div>
            <h2 class="section-title">管理员登录</h2>
            <p class="section-subtitle">使用平台管理员账号登录管理后台。</p>
          </div>
          <router-link to="/login" class="mini-link">返回用户登录</router-link>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="auth-form">
          <el-form-item label="管理员账号" prop="username">
            <el-input v-model="form.username" placeholder="请输入管理员账号" size="large" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="请输入密码"
              size="large"
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-button type="primary" size="large" :loading="loading" class="submit-btn" @click="handleLogin">
            登录后台
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
const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    if (!userStore.isAdmin()) {
      userStore.logout()
      ElMessage.warning('当前账号不是管理员')
      return
    }
    ElMessage.success('管理员登录成功')
    router.push('/admin/dashboard')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-layout .auth-hero {
  min-height: 540px;
}

.admin-layout .auth-form-panel {
  min-height: 540px;
}
</style>
