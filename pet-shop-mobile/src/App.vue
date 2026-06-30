<script>
export default {
  onLaunch() {
    console.log('PetShop Mobile App Launched')
  },
  onShow() {
    console.log('PetShop Mobile App Show')
  },
  onHide() {
    console.log('PetShop Mobile App Hide')
  }
}
</script>

<style lang="scss">
/* 全局样式 */
@import '@/uni.scss';

page {
  font-family: 'PingFang SC', 'Microsoft YaHei', system-ui, sans-serif;
  color: #1A1A2E;
  background: #FDFBF7;
  -webkit-font-smoothing: antialiased;
  line-height: 1.6;
  font-size: 28rpx;
}

/* 通用容器 */
.page-container {
  padding: 24rpx 32rpx;
  min-height: 100vh;
}

/* 通用价格样式 */
.price-tag {
  font-weight: 800;
  color: $primary-color;
  font-size: 36rpx;
}

.price-tag::before {
  content: '¥';
  font-size: 24rpx;
  font-weight: 600;
}

/* 通用文字省略 */
.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.text-ellipsis-2 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* Flex 布局 */
.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

.flex-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.flex-wrap {
  display: flex;
  flex-wrap: wrap;
}
</style>
