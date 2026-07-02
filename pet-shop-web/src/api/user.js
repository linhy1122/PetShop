import request from '@/utils/request'

/** 登录 */
export function loginApi(username, password, captchaKey, captchaX) {
  return request.post('/user/login', { username, password, captchaKey, captchaX })
}

/** 注册 */
export function registerApi(username, password, phone, captchaKey, captchaX) {
  return request.post('/user/register', { username, password, phone, captchaKey, captchaX })
}

/** 获取图形验证码 */
export function getCaptchaApi() {
  return request.get('/captcha')
}

/** 校验滑块位置 */
export function verifyCaptchaApi(captchaKey, captchaX) {
  return request.post('/captcha/verify', { captchaKey, captchaX })
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
  return 'http://localhost:9090/oauth/github/authorize'
}

/** 管理端用户分页列表 */
export function getAdminUserListApi(params) {
  return request.get('/user/admin/list', { params })
}

/** 管理端用户详情 */
export function getAdminUserDetailApi(id) {
  return request.get(`/user/admin/${id}`)
}

/** 管理端编辑用户 */
export function updateAdminUserApi(id, data) {
  return request.put(`/user/admin/${id}`, data)
}

/** 管理端启用或禁用用户 */
export function updateAdminUserStatusApi(id, status) {
  return request.put(`/user/admin/${id}/status`, null, { params: { status } })
}

/** 管理端删除用户 */
export function deleteAdminUserApi(id) {
  return request.delete(`/user/admin/${id}`)
}
