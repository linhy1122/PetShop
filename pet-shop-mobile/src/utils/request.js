/**
 * UniApp 请求封装 - 基于 uni.request
 * 保留与原项目 axios 封装相同的拦截器逻辑
 *
 * ========================================
 * 微信小程序开发者：修改下面的 API_HOST 为你电脑的局域网 IP
 * 运行命令 ipconfig (Windows) 或 ifconfig (Mac) 查看本机 IP
 * ========================================
 */

// ====== 在这里修改你的后端地址 ======
const API_HOST = '192.168.57.147'
const API_PORT = '9090'
// ===================================

// 不同平台的 API 基础路径
// #ifdef H5
const BASE_URL = '/api'
// #endif

// #ifdef MP-WEIXIN
const BASE_URL = `http://${API_HOST}:${API_PORT}/api`
// #endif

// #ifdef APP-PLUS
const BASE_URL = `http://${API_HOST}:${API_PORT}/api`
// #endif

const TIMEOUT = 15000

/**
 * 请求拦截器：注入 token
 */
function requestInterceptor(config) {
  const token = uni.getStorageSync('token')
  if (token) {
    config.header = config.header || {}
    config.header['Authorization'] = `Bearer ${token}`
  }
  return config
}

/**
 * 响应拦截器：统一处理 code !== 200
 */
function responseInterceptor(response) {
  const res = response.data
  if (res.code !== 200) {
    uni.showToast({ title: res.message || '请求失败', icon: 'none' })
    return Promise.reject(new Error(res.message || '请求失败'))
  }
  return Promise.resolve(res)
}

/**
 * 统一请求方法
 */
function request(config) {
  return new Promise((resolve, reject) => {
    // 请求拦截
    config = requestInterceptor(config)

    let fullUrl = BASE_URL + config.url
    const method = config.method || 'GET'

    // params → 拼接到 URL 查询字符串（GET/POST/PUT/DELETE 都适用）
    // 后端统一使用 @RequestParam，参数必须走 query string
    if (config.params) {
      const qs = Object.entries(config.params)
        .filter(([, v]) => v !== undefined)
        .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`)
        .join('&')
      if (qs) fullUrl += '?' + qs
    }

    // data → POST/PUT 请求体（仅当有实际 body 数据时）
    let body = config.data
    if (body && typeof body === 'object') {
      // 过滤 undefined 值
      for (const key of Object.keys(body)) {
        if (body[key] === undefined) delete body[key]
      }
    }

    console.log('[REQ]', method, fullUrl)

    uni.request({
      url: fullUrl,
      method: method,
      data: body || undefined,
      header: config.header || { 'Content-Type': 'application/json' },
      timeout: config.timeout || TIMEOUT,
      success: (res) => {
        console.log('[RES]', res.statusCode, fullUrl)
        responseInterceptor(res).then(resolve).catch(reject)
      },
      fail: (err) => {
        console.error('[FAIL]', fullUrl, err.errMsg)
        uni.showToast({ title: err.errMsg || '网络错误', icon: 'none' })
        reject(err)
      }
    })
  })
}

// 便捷方法
request.get = function(url, config = {}) {
  return request({ ...config, url, method: 'GET' })
}

request.post = function(url, data, config = {}) {
  return request({ ...config, url, method: 'POST', data })
}

request.put = function(url, data, config = {}) {
  return request({ ...config, url, method: 'PUT', data })
}

request.delete = function(url, config = {}) {
  return request({ ...config, url, method: 'DELETE' })
}

// 导出 BASE_URL 以便 uniapp.uploadFile 等需要完整 URL 的场景使用
request.baseURL = BASE_URL

export default request
