<template>
  <footer class="footer">
    <div class="footer-inner">
      <div class="footer-brand">
        <div class="footer-logo">PetShop</div>
        <p>为宠物和主人提供更清晰、更温暖的一站式购物体验。</p>
      </div>

      <div class="footer-links">
        <div>
          <h4>前台入口</h4>
          <router-link to="/product/list">商品商城</router-link>
          <router-link to="/store/list">商店列表</router-link>
          <router-link to="/video/list">视频推荐</router-link>
          <router-link to="/ai/chat">AI 助手</router-link>
        </div>
        <div>
          <h4>用户服务</h4>
          <router-link to="/cart">购物车</router-link>
          <router-link to="/order/list">我的订单</router-link>
          <router-link to="/user/center">个人中心</router-link>
        </div>
        <div>
          <h4>联系支持</h4>
          <p>客服热线：400-123-4567</p>
          <p>邮箱：service@petshop.cn</p>
        </div>
      </div>
    </div>

    <div class="footer-bottom">
      <span>© 2025 PetShop 宠物商城</span>
      <span>程序设计实训项目 · Vue 3 + Spring Boot</span>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.footer {
  margin-top: 42px;
  padding: 40px 0 18px;
  background: linear-gradient(180deg, rgba(33, 26, 53, 0.95), #171324);
  color: #f4f2ff;
}

.footer-inner {
  width: min(1240px, calc(100vw - 32px));
  margin: 0 auto;
  display: grid;
  grid-template-columns: 0.9fr 1.2fr;
  gap: 28px;
}

.footer-brand p {
  max-width: 320px;
  margin-top: 14px;
  color: rgba(255, 255, 255, 0.72);
  line-height: 1.9;
}

.footer-logo {
  display: inline-flex;
  padding: 10px 16px;
  border-radius: 999px;
  font-weight: 800;
  letter-spacing: 0.6px;
  background: linear-gradient(135deg, #ff8f6b, #8a6cff);
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
}

.footer-links h4 {
  margin-bottom: 12px;
  font-size: 16px;
}

.footer-links a,
.footer-links p {
  display: block;
  margin-top: 10px;
  color: rgba(255, 255, 255, 0.72);
  line-height: 1.8;
}

.footer-bottom {
  width: min(1240px, calc(100vw - 32px));
  margin: 28px auto 0;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: space-between;
  gap: 16px;
  color: rgba(255, 255, 255, 0.58);
  font-size: 13px;
}

@media (max-width: 1024px) {
  .footer-inner,
  .footer-links {
    grid-template-columns: 1fr;
  }

  .footer-bottom {
    flex-direction: column;
  }
}
</style>
