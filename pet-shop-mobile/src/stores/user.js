import { defineStore } from 'pinia'
import { ref } from 'vue'
import { loginApi, registerApi, getGithubAuthUrl, getCaptchaApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(uni.getStorageSync('token') || '')
  const userInfo = ref(JSON.parse(uni.getStorageSync('userInfo') || 'null'))

  // 登录
  async function login(username, password, captchaKey, captchaX) {
    const res = await loginApi(username, password, captchaKey, captchaX)
    token.value = res.data.token
    userInfo.value = {
      userId: res.data.userId,
      nickname: res.data.nickname,
      role: res.data.role
    }
    uni.setStorageSync('token', token.value)
    uni.setStorageSync('userInfo', JSON.stringify(userInfo.value))
    return res
  }

  // 注册
  async function register(username, password, phone, captchaKey, captchaX) {
    return await registerApi(username, password, phone, captchaKey, captchaX)
  }

  // OAuth 登录处理（保存从回调获取的 token）
  function saveOAuthToken(t, userId, nickname, role) {
    token.value = t
    userInfo.value = { userId, nickname, role }
    uni.setStorageSync('token', t)
    uni.setStorageSync('userInfo', JSON.stringify({ userId, nickname, role }))
  }

  // GitHub OAuth 登录 URL
  function getGithubAuthUrl() {
    return 'http://localhost:8080/oauth/github/authorize'
  }

  // 退出
  function logout() {
    token.value = ''
    userInfo.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
  }

  // 判断是否登录
  const isLoggedIn = () => !!token.value
  const isAdmin = () => userInfo.value?.role === 'admin'

  return { token, userInfo, login, register, saveOAuthToken, getGithubAuthUrl, logout, isLoggedIn, isAdmin }
})
