# PetShop 宠物商店 - UniApp 移动端

基于 Vue 3 宠物商店 Web 应用转换的 UniApp 移动端应用。

## 技术栈

- **框架**: UniApp (Vue 3 + Vite)
- **状态管理**: Pinia
- **HTTP 请求**: uni.request (封装)
- **UI**: 自定义组件 (uni.scss 主题系统)

## 项目结构

```
pet-shop-mobile/
├── pages.json          # 页面路由 + tabBar + globalStyle
├── manifest.json       # 应用配置
├── App.vue             # 根组件
├── main.js             # 入口
├── uni.scss            # 全局 SCSS 主题变量
├── api/                # API 模块 (8个)
├── stores/             # Pinia stores (user, cart)
├── utils/              # 工具函数 (request, image)
├── components/         # 公共组件 (6个)
├── pages/              # 页面 (19个)
├── static/             # 静态资源
└── vite.config.js      # Vite 配置
```

## 页面列表

### TabBar 页面 (5个)
- 🏠 首页 - Banner 轮播 + 分类 + 热门商品
- 🛍️ 商品 - 类型筛选 + 搜索 + 分类标签 + 商品网格
- 📍 商店 - 地图 + 店铺列表 + 城市搜索
- 🛒 购物车 - 购物车管理 + 结算
- 👤 我的 - 用户中心 + 个人信息 + 地址管理

### 二级页面 (10个)
- 商品详情 - 图片轮播 + 信息 + 加购/购买 + 评价
- 店铺详情 - Banner + 信息 + 本店商品
- 登录 / 注册 - 滑块验证码 + GitHub OAuth
- 订单列表 / 详情 - 状态筛选 + 操作
- AI客服 - 聊天界面 + 快捷问题
- 视频列表 / 播放 - 视频网格 + 点赞 + 关联商品
- OAuth 回调

### 管理后台 (4个)
- 控制台 - 统计概览
- 商品管理 - 列表 + 筛选 + 新增/编辑 + 图片上传 + 上下架
- 订单管理 - 列表 + 发货 + 退货审核
- 视频管理 - 列表 + 上传/编辑

## 快速开始

### 前置条件
- 后端 API 服务运行在 `localhost:8080`
- 已安装 HBuilderX 或 Node.js 18+

### 开发运行
```bash
# 安装依赖
npm install

# H5 开发模式
npm run dev:h5

# 微信小程序开发
npm run dev:mp-weixin
```

### 使用 HBuilderX
1. 打开 HBuilderX
2. 文件 → 导入 → 从本地目录导入
3. 选择 `pet-shop-mobile` 目录
4. 运行 → 运行到浏览器 / 运行到小程序模拟器

## 注意事项

1. **TabBar 图标**: 当前使用 emoji 文字图标，可替换为 `static/tab/` 下的 PNG 图标文件
2. **地图**: 使用 UniApp 原生 `<map>` 组件，需在 manifest.json 中配置高德地图 key
3. **滑块验证码**: 使用 touch 事件实现，适配移动端交互
4. **图片上传**: 使用 `uni.chooseImage` + `uni.uploadFile`
5. **API 代理**: H5 模式需在 `vite.config.js` 中配置代理，小程序/app 需配置完整 URL

## 与原 Web 项目的区别

| 特性 | Web 版 | UniApp 版 |
|------|--------|-----------|
| UI 框架 | Element Plus | 自定义 (uni.scss) |
| 路由 | vue-router | pages.json |
| 导航 | NavBar 组件 | 原生导航栏 + TabBar |
| 分页 | el-pagination | 滚动加载更多 |
| 地图 | 高德 JS API | `<map>` 原生组件 |
| 存储 | localStorage | uni.getStorageSync |
| 弹窗 | el-dialog | 自定义底部弹出 |
| 图片预览 | el-image | uni.previewImage |
