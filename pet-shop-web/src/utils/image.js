/**
 * 图片加载失败时的默认占位图
 */
export const PLACEHOLDER = 'data:image/svg+xml,' + encodeURIComponent(`
<svg xmlns="http://www.w3.org/2000/svg" width="400" height="300" viewBox="0 0 400 300">
  <rect fill="#f0f2f5" width="400" height="300"/>
  <text fill="#c0c4cc" font-family="sans-serif" font-size="18" text-anchor="middle" x="200" y="155">暂无图片</text>
</svg>`)

/**
 * 图片加载失败处理
 */
export function onImageError(e) {
  if (e.target.src !== PLACEHOLDER) {
    e.target.src = PLACEHOLDER
  }
}
