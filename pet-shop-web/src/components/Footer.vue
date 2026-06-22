<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-section">
        <h4>PetShop 宠物商店</h4>
        <p>为您和您的爱宠提供最好的产品和服务</p>
      </div>
      <div class="footer-section">
        <h4>快速链接</h4>
        <router-link to="/product/list">宠物商城</router-link>
        <router-link to="/store/map">附近商店</router-link>
        <router-link to="/video/list">宠物视频</router-link>
      </div>
      <div class="footer-section">
        <h4>联系我们</h4>
        <p>客服电话：400-123-4567</p>
        <p>邮箱：service@petshop.cn</p>
      </div>
    </div>
    <div class="footer-bottom">
      <p>© 2025 PetShop 宠物商店 - 程序设计实训项目</p>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.footer { background: #2c3e50; color: #ecf0f1; padding: 40px 20px 0; margin-top: 40px; }
.footer-content { max-width: 1200px; margin: 0 auto; display: flex; justify-content: space-between; flex-wrap: wrap; gap: 30px; }
.footer-section h4 { margin-bottom: 12px; font-size: 16px; }
.footer-section p, .footer-section a { display: block; color: #bdc3c7; font-size: 14px; line-height: 2; }
.footer-bottom { text-align: center; padding: 20px 0; margin-top: 30px; border-top: 1px solid #34495e; font-size: 13px; color: #7f8c8d; }
</style>
