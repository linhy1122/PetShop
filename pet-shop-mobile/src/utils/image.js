/**
 * 图片工具 - 适配多平台图片 URL
 */

// 后端地址（非 H5 平台需要补全相对路径）
// #ifdef H5
const IMG_BASE = ''
// #endif
// #ifndef H5
const IMG_BASE = 'http://localhost:8080'
// #endif

/** 默认占位图 */
export const PLACEHOLDER = 'data:image/svg+xml,' + encodeURIComponent(`
<svg xmlns="http://www.w3.org/2000/svg" width="400" height="300" viewBox="0 0 400 300">
  <rect fill="#f0f2f5" width="400" height="300"/>
  <text fill="#c0c4cc" font-family="sans-serif" font-size="18" text-anchor="middle" x="200" y="155">暂无图片</text>
</svg>`)

/**
 * 补全图片 URL：相对路径加后端域名，data-url / http 开头原样返回
 * 小程序中 <image> 组件必须使用完整 URL
 */
export function fixImgUrl(url) {
  if (!url) return PLACEHOLDER
  if (url.startsWith('data:') || url.startsWith('http://') || url.startsWith('https://')) return url
  return IMG_BASE + url
}

/**
 * 图片加载失败处理 - UniApp 版本
 * 在 <image> 组件的 @error 事件中调用
 */
export function onImageError(e) {
  // UniApp 的 image error 事件处理
  // 模板中: @error="e => handleImageError(e)"
}

/**
 * UniApp 模板中图片 error 事件处理函数
 * 使用方法: <image :src="url" @error="imgError" />
 * 在 script 中: import { imgError } from '@/utils/image'
 *             function imgError(e) { e.target.src = PLACEHOLDER }
 */
