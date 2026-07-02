<template>
  <view class="slider-captcha">
    <view class="slider-bg-wrap" id="captchaBgWrap">
      <image :src="bgImage" class="slider-bg" mode="aspectFill" />
      <view class="slider-piece"
            :style="{ left: pieceLeft + 'px', top: sliderY + 'px', backgroundImage: 'url(' + sliderImage + ')' }" />
    </view>
    <view class="slider-track" id="captchaTrack">
      <view class="slider-track-bg" :style="{ width: pieceLeft + 25 + 'px' }" />
      <view class="slider-handle"
            :class="{ success: isVerified, error: isError }"
            :style="{ left: pieceLeft + 'px' }"
            @touchstart="startDrag"
            @touchmove.stop.prevent="onDrag"
            @touchend="endDrag">
        <text v-if="isVerified">✓</text>
        <text v-else-if="isError">✗</text>
        <text v-else>⇋</text>
      </view>
      <text class="slider-tip" v-if="!isVerified && !isError">请按住滑块，拖动到正确位置</text>
      <text class="slider-tip error-tip" v-if="isError">位置错误，请重试</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { getCaptchaApi, verifyCaptchaApi } from '@/api/user'

const emit = defineEmits(['verified'])

// 后端图片基准宽度 300px，滑块 50px → 最大可拖距离 250px
const BACKEND_BG_WIDTH = 300
const HANDLE_WIDTH = 50

// 后端地址（小程序中图片相对路径需要补全）
// #ifdef H5
const IMG_BASE = ''
// #endif
// #ifndef H5
const IMG_BASE = 'http://localhost:8080'
// #endif

function fixImgUrl(url) {
  if (!url) return ''
  if (url.startsWith('data:') || url.startsWith('http')) return url
  return IMG_BASE + url
}

const bgImage = ref('')
const sliderImage = ref('')
const captchaKey = ref('')
const pieceLeft = ref(0)
const isDragging = ref(false)
const isVerified = ref(false)
const isError = ref(false)
const sliderY = ref(62)

// 实际渲染的轨道宽度（px）与后端基准宽度的比例
let trackRealWidth = 300
let scaleRatio = 1

let startX = 0
const maxPieceLeft = () => trackRealWidth - HANDLE_WIDTH * scaleRatio

/** 获取轨道实际渲染宽度，计算缩放比 */
function measureTrack() {
  return new Promise((resolve) => {
    const query = uni.createSelectorQuery()
    query.select('#captchaTrack').boundingClientRect((rect) => {
      if (rect) {
        trackRealWidth = rect.width
        scaleRatio = trackRealWidth / BACKEND_BG_WIDTH
      }
      resolve()
    }).exec()
  })
}

async function refreshCaptcha() {
  try {
    const res = await getCaptchaApi()
    captchaKey.value = res.data.captchaKey
    bgImage.value = fixImgUrl(res.data.bgImage)
    sliderImage.value = fixImgUrl(res.data.sliderImage)
    sliderY.value = res.data.sliderY || 62
    pieceLeft.value = 0
    isVerified.value = false
    isError.value = false

    // 重新测量轨道宽度（图片可能刚渲染完）
    await nextTick()
    await measureTrack()
  } catch (e) { /* ignore */ }
}

onMounted(async () => {
  await nextTick()
  await measureTrack()
  await refreshCaptcha()
})

function startDrag(e) {
  if (isVerified.value || isError.value) return
  isDragging.value = true
  const touch = e.touches[0]
  startX = touch.clientX - pieceLeft.value
}

function onDrag(e) {
  if (!isDragging.value) return
  const touch = e.touches[0]
  let left = touch.clientX - startX
  if (left < 0) left = 0
  const max = maxPieceLeft()
  if (left > max) left = max
  pieceLeft.value = left
}

async function endDrag() {
  if (!isDragging.value) return
  isDragging.value = false

  // 将屏幕像素坐标折算回后端 300px 基准坐标
  const captchaX = Math.round(pieceLeft.value / scaleRatio)

  try {
    const res = await verifyCaptchaApi(captchaKey.value, captchaX)
    if (res.data.passed) {
      isVerified.value = true
      isError.value = false
      emit('verified', { captchaKey: captchaKey.value, captchaX })
    } else {
      showError()
    }
  } catch (e) {
    showError()
  }
}

function showError() {
  isError.value = true
  setTimeout(() => {
    pieceLeft.value = 0
    isError.value = false
  }, 800)
}

function reset() {
  refreshCaptcha()
}

defineExpose({ reset })
</script>

<style scoped>
.slider-captcha {
  user-select: none;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.slider-bg-wrap {
  position: relative;
  width: 600rpx;
  height: 320rpx;
  border-radius: 12rpx;
  overflow: hidden;
}

.slider-bg {
  width: 100%;
  height: 100%;
}

.slider-piece {
  position: absolute;
  width: 100rpx;
  height: 100rpx;
  background-size: 100rpx 100rpx;
  background-repeat: no-repeat;
  box-shadow: 0 0 12rpx rgba(0,0,0,.3);
  border-radius: 8rpx;
}

.slider-track {
  position: relative;
  width: 600rpx;
  height: 80rpx;
  margin-top: 20rpx;
  background: #eee;
  border-radius: 40rpx;
  overflow: hidden;
}

.slider-track-bg {
  height: 100%;
  background: #c8e6c9;
  border-radius: 40rpx 0 0 40rpx;
}

.slider-handle {
  position: absolute;
  top: 0;
  width: 100rpx;
  height: 80rpx;
  background: #fff;
  border: 4rpx solid #ddd;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  color: #999;
  z-index: 2;
}

.slider-handle.success {
  border-color: #67c23a;
  background: #67c23a;
  color: #fff;
}

.slider-handle.error {
  border-color: #f56c6c;
  background: #f56c6c;
  color: #fff;
  animation: shake 0.4s;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-10rpx); }
  75% { transform: translateX(10rpx); }
}

.slider-tip {
  position: absolute;
  left: 120rpx;
  line-height: 80rpx;
  color: #bbb;
  font-size: 26rpx;
  pointer-events: none;
}

.error-tip { color: #f56c6c; }
</style>
