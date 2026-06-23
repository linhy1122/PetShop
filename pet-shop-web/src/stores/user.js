import { defineStore } from 'pinia'
import { ref } from 'vue'
import { loginApi, registerApi, getUserInfoApi, getGithubAuthUrl, getCaptchaApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 登录
  async function login(username, password, captchaKey, captchaX) {
    const res = await loginApi(username, password, captchaKey, captchaX)
    token.value = res.data.token
    userInfo.value = {
      userId: res.data.userId,
      nickname: res.data.nickname,
      role: res.data.role
    }
    localStorage.setItem('token', token.value)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    return res
  }

  // 注册
  async function register(username, password, phone, captchaKey, captchaX) {
    return await registerApi(username, password, phone, captchaKey, captchaX)
  }

  // GitHub OAuth 登录（跳转到GitHub授权页）
  function githubLogin() {
    window.location.href = getGithubAuthUrl()
  }

  // 退出
  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 判断是否登录
  const isLoggedIn = () => !!token.value
  const isAdmin = () => userInfo.value?.role === 'admin'

  return { token, userInfo, login, register, githubLogin, logout, isLoggedIn, isAdmin }
})
