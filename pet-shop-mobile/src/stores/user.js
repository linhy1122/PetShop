import { defineStore } from 'pinia'
import { ref } from 'vue'
import { loginApi, registerApi, wxLoginApi, getGithubAuthUrl, getCaptchaApi } from '@/api/user'

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
      role: res.data.role,
      avatar: res.data.avatar || ''
    }
    uni.setStorageSync('token', token.value)
    uni.setStorageSync('userInfo', JSON.stringify(userInfo.value))
    return res
  }

  // 微信小程序一键登录
  async function wxLogin() {
    // 1. 调用微信登录获取 code
    const loginRes = await new Promise((resolve, reject) => {
      uni.login({
        success: (res) => {
          console.log('[wxLogin] uni.login success, code:', res.code)
          resolve(res)
        },
        fail: (err) => {
          console.error('[wxLogin] uni.login fail:', JSON.stringify(err))
          reject(new Error('微信授权失败'))
        }
      })
    })
    if (!loginRes.code) throw new Error('获取微信登录凭证失败')

    // 2. 发送 code 到后端换取 token
    const res = await wxLoginApi(loginRes.code)
    console.log('[wxLogin] backend response:', JSON.stringify(res.data))

    token.value = res.data.token
    userInfo.value = {
      userId: res.data.userId,
      nickname: res.data.nickname,
      role: res.data.role,
      avatar: res.data.avatar || ''
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
    userInfo.value = { userId, nickname, role, avatar: '' }
    uni.setStorageSync('token', t)
    uni.setStorageSync('userInfo', JSON.stringify({ userId, nickname, role, avatar: '' }))
  }

  // GitHub OAuth 登录 URL
  function getGithubAuthUrl() {
    return 'http://localhost:8080/oauth/github/authorize'
  }

  // 更新头像
  function setAvatar(avatarUrl) {
    if (userInfo.value) {
      userInfo.value.avatar = avatarUrl
      uni.setStorageSync('userInfo', JSON.stringify(userInfo.value))
    }
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

  return { token, userInfo, login, wxLogin, register, saveOAuthToken, getGithubAuthUrl, setAvatar, logout, isLoggedIn, isAdmin }
})
