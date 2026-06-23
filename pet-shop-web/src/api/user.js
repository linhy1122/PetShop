import request from '@/utils/request'

/** 登录 */
export function loginApi(username, password) {
  return request.post('/user/login', { username, password })
}

/** 注册 */
export function registerApi(username, password, phone) {
  return request.post('/user/register', { username, password, phone })
}

/** 获取用户信息 */
export function getUserInfoApi(userId) {
  return request.get('/user/info', { params: { userId } })
}

/** 更新个人信息 */
export function updateProfileApi(data) {
  return request.put('/user/profile', data)
}

/** 修改密码 */
export function updatePasswordApi(userId, oldPassword, newPassword) {
  return request.put('/user/password', null, { params: { userId, oldPassword, newPassword } })
}

/** GitHub OAuth 授权地址 */
export function getGithubAuthUrl() {
  return 'http://localhost:8080/oauth/github/authorize'
}
