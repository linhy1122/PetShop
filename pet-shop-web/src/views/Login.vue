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
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
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
</style>
