<p align="center">
  <img src="https://img.shields.io/badge/Vue-3.4-4FC08D?logo=vue.js" alt="Vue">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7-6DB33F?logo=springboot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/UniApp-3.0-2B9939?logo=uniapp" alt="UniApp">
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql" alt="MySQL">
  <img src="https://img.shields.io/badge/Redis-7.0-DC382D?logo=redis" alt="Redis">
</p>

<h1 align="center">🐾 PetShop 宠物商店</h1>
<p align="center"><strong>全栈宠物电商平台</strong> · Vue 3 + Spring Boot + UniApp 跨端商城</p>
<p align="center">程序设计实训项目 · 2025-2026-2</p>

---

## 📖 目录

- [项目简介](#-项目简介)
- [项目架构](#-项目架构)
- [技术栈](#-技术栈)
- [项目结构](#-项目结构)
- [功能矩阵](#-功能矩阵)
- [核心亮点](#-核心亮点)
- [快速启动](#-快速启动)
- [API 文档](#-api-文档)
- [数据库设计](#-数据库设计)

---

## 🎯 项目简介

PetShop 是一个**前后端分离**的全栈宠物电商平台，覆盖 **PC 浏览器** 和 **微信小程序** 两端。

用户可以在线浏览宠物及宠物周边商品、加入购物车、下单购买、查看附近实体店铺；平台内置 **AI 智能客服**（基于通义千问大模型 + 数据库实时上下文注入）回答商品咨询，并实现了**基于协同过滤的个性化推荐算法**。

项目包含三个子工程：

| 子项目 | 技术框架 | 运行环境 | 说明 |
|--------|---------|---------|------|
| `pet-shop-server` | Spring Boot + MyBatis-Plus | JDK 17 / Maven | 统一后端 API + 业务逻辑 |
| `pet-shop-web` | Vue 3 + Vite + Element Plus | Node 16+ / 浏览器 | PC 端商城 + 管理后台 |
| `pet-shop-mobile` | UniApp (Vue 3) + Pinia | 微信开发者工具 | 微信小程序端 |

---

## 🏗 项目架构

```
┌──────────────────────────────────────────────────────────┐
│                      客户端层                            │
│  ┌──────────────────┐  ┌──────────────────────────────┐  │
│  │  pet-shop-web     │  │  pet-shop-mobile             │  │
│  │  (Vue 3 + Vite)   │  │  (UniApp → 微信小程序)       │  │
│  │  PC 浏览器        │  │  手机微信扫码                │  │
│  └───────┬──────────┘  └──────────────┬───────────────┘  │
│          │  Axios / uni.request       │                  │
│          └──────────┬─────────────────┘                  │
│                     ▼ HTTP RESTful                       │
├──────────────────────────────────────────────────────────┤
│                      服务层                              │
│  ┌────────────────────────────────────────────────────┐  │
│  │  pet-shop-server (Spring Boot 2.7)                 │  │
│  │                                                    │  │
│  │  Controller  →  Service  →  Mapper  →  Database    │  │
│  │       ↑                              ↓             │  │
│  │  GlobalExceptionHandler         MySQL + Redis       │  │
│  │  CORS Filter + Token Auth                           │  │
│  └────────────────────────────────────────────────────┘  │
│                     │                                    │
│                     ▼                                    │
│  ┌────────────────────────────────────────────────────┐  │
│  │  外部服务                                          │  │
│  │  · 通义千问 API (AI 客服)                          │  │
│  │  · 高德地图 API (附近店铺)                         │  │
│  │  · 微信 code2session (小程序登录)                  │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

---

## 🛠 技术栈

### 后端 (`pet-shop-server`)

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.7.18 | 应用框架 |
| MyBatis-Plus | 3.5.3 | ORM + 分页 + 自动填充 |
| MySQL | 8.0 | 关系数据库 |
| Redis | 7.0 | 缓存（验证码、会话） |
| Knife4j | 4.1 | API 文档自动生成 |
| Hutool | 5.8 | 工具库（HTTP、加密） |
| Lombok | 1.18 | 代码简化 |

### Web 前端 (`pet-shop-web`)

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.4 | MVVM 框架 |
| Vite | 5.4 | 构建工具 |
| Element Plus | 2.x | UI 组件库 |
| Pinia | 2.x | 状态管理 |
| Axios | 1.x | HTTP 请求 |
| Vue Router | 4.x | 路由管理 |

### 小程序 (`pet-shop-mobile`)

| 技术 | 版本 | 用途 |
|------|------|------|
| UniApp | 3.0 | 跨端框架 |
| Vue | 3.4 | Composition API |
| Pinia | 2.x | 状态管理 |
| 微信小程序 SDK | 3.x | 原生能力（登录、地图、上传） |

---

## 📁 项目结构

```
PetShop/
│
├── pet-shop-server/                          # 🔧 后端服务
│   ├── pom.xml                               #    Maven 依赖
│   └── src/main/
│       ├── java/com/petshop/
│       │   ├── PetShopApplication.java       #    启动入口
│       │   ├── common/
│       │   │   ├── Result.java               #      统一响应体 {code, message, data}
│       │   │   ├── BaseEntity.java           #      实体基类 (id, createTime, updateTime)
│       │   │   └── GlobalExceptionHandler    #      全局异常处理
│       │   ├── config/
│       │   │   ├── CorsConfig.java           #      跨域配置
│       │   │   ├── MybatisPlusConfig.java    #      MyBatis 分页插件
│       │   │   ├── MyMetaObjectHandler.java  #      自动填充处理
│       │   │   └── WechatProperties.java     #      微信小程序配置
│       │   ├── entity/                       #      实体类（11张表）
│       │   ├── dto/                          #      请求 DTO
│       │   ├── controller/                   #      REST 控制器（12个模块）
│       │   ├── service/                      #      业务接口
│       │   ├── service/impl/                 #      业务实现
│       │   └── mapper/                       #      MyBatis Mapper
│       └── resources/
│           ├── application.yml               #    主配置
│           └── sql/                          #    数据库脚本
│
├── pet-shop-web/                             # 🖥 PC 前端
│   ├── vite.config.js                        #    Vite 配置（代理、端口）
│   ├── package.json
│   └── src/
│       ├── App.vue                           #    根组件
│       ├── main.js                           #    入口
│       ├── router/index.js                   #    路由（11个页面）
│       ├── stores/                           #    Pinia Store（user, cart）
│       ├── api/                              #    API 封装（10个模块）
│       ├── views/                            #    页面组件
│       │   ├── Home.vue                      #      首页（个性化推荐）
│       │   ├── ProductList.vue               #      商品列表（搜索+排序+筛选）
│       │   ├── ProductDetail.vue             #      商品详情
│       │   ├── Cart.vue                      #      购物车
│       │   ├── StoreMap.vue                  #      附近店铺（高德地图）
│       │   ├── StoreDetail.vue               #      店铺详情
│       │   ├── AiChat.vue                    #      AI 智能客服
│       │   ├── UserCenter.vue                #      个人中心
│       │   ├── Login.vue / Register.vue      #      登录注册
│       │   └── admin/                        #      管理后台
│       ├── components/                       #    公共组件
│       └── utils/request.js                  #    Axios 封装
│
├── pet-shop-mobile/                          # 📱 小程序
│   ├── package.json
│   ├── pages.json                            #    页面路由配置
│   └── src/
│       ├── App.vue
│       ├── main.js                           #    全局函数（fixImgUrl, preloadImages）
│       ├── pages/
│       │   ├── index/index.vue               #      首页
│       │   ├── product/list/list.vue         #      商品列表
│       │   ├── product/detail/detail.vue     #      商品详情
│       │   ├── cart/cart.vue                 #      购物车
│       │   ├── store/map/map.vue             #      店铺地图
│       │   ├── store/detail/detail.vue       #      店铺详情
│       │   ├── video/list/list.vue           #      视频列表
│       │   ├── video/player/player.vue       #      视频播放
│       │   ├── ai/ai.vue                     #      AI 客服
│       │   ├── login/login.vue               #      登录
│       │   ├── register/register.vue         #      注册
│       │   ├── user/user.vue                 #      个人中心
│       │   └── order/list/list.vue           #      订单列表
│       ├── components/                       #    公共组件
│       ├── api/                              #    API 封装
│       ├── stores/                           #    Pinia Store
│       ├── utils/request.js                  #    uni.request 封装
│       ├── static/                           #    静态资源（图标）
│       └── mock/index.js                     #    开发用模拟数据
│
└── README.md
```

---

## 🧩 功能矩阵

### 🏪 商品系统

| 功能 | Web | 小程序 | 描述 |
|------|:--:|:--:|------|
| 首页 Banner 轮播 | ✅ | ✅ | 自动滚动 + 指示器 |
| 商品分类导航 | ✅ | ✅ | 8 个分类，PNG 图标 |
| 热门推荐 | ✅ | ✅ | Web 端支持个性化推荐 |
| 商品列表 | ✅ | ✅ | 分页加载 + 类型筛选 + 关键词搜索 |
| 商品排序 | ✅ | ✅ | 综合（销量↓）/ 价格↑ / 价格↓ / 销量↓ |
| 商品详情 | ✅ | ✅ | 图片轮播、宠物参数、库存显示 |
| 加入购物车 | ✅ | ✅ | 数量选择 + 一键加购 |

### 🛒 交易系统

| 功能 | Web | 小程序 | 描述 |
|------|:--:|:--:|------|
| 购物车管理 | ✅ | ✅ | 单选、全选、批量删除、清除已下架 |
| 库存预警 | ✅ | ✅ | 库存不足时显示警告 |
| 下单结算 | ✅ | ✅ | 选择地址 + 备注 + 提交订单 |
| 订单列表 | ✅ | ✅ | 按状态筛选：待支付/待发货/待收货/待评价 |
| 订单操作 | ✅ | ✅ | 支付、取消、确认收货、评价 |
| 退单退款 | ✅ | ✅ | 申请退单 + 管理员审核 |

### 🏪 店铺系统

| 功能 | Web | 小程序 | 描述 |
|------|:--:|:--:|------|
| 附近店铺 | ✅ | ✅ | 高德地图定位 + 标记点 |
| 店铺搜索 | ✅ | ✅ | 按城市搜索 |
| 距离排序 | ✅ | ✅ | Haversine 公式计算 + 排序 |
| 导航到店 | ✅ | ✅ | 调用系统地图 |
| 店铺详情 | ✅ | ✅ | 店铺信息 + 本店商品 |
| 一键拨号 | ✅ | ✅ | 调用系统拨号 |

### 👤 用户系统

| 功能 | Web | 小程序 | 描述 |
|------|:--:|:--:|------|
| 用户名密码登录 | ✅ | ✅ | Web 带滑块验证码 |
| 微信一键登录 | — | ✅ | wx.login + 后端 code2session |
| GitHub OAuth | ✅ | — | 第三方登录 |
| 头像上传 | ✅ | ✅ | 小程序支持微信原生头像选择器 |
| 资料编辑 | ✅ | ✅ | 昵称、手机号、邮箱 |
| 密码修改 | ✅ | ✅ | 原密码验证 |
| 收货地址 | ✅ | ✅ | 增删改查 + 默认地址 |

### 🎬 内容与智能

| 功能 | Web | 小程序 | 描述 |
|------|:--:|:--:|------|
| 宠物视频 | ✅ | ✅ | 视频列表 + 播放 + 点赞 + 关联商品 |
| AI 智能客服 | ✅ | ✅ | 通义千问 + 数据库上下文注入 |
| 商品链接跳转 | ✅ | — | AI 回复中商品可点击直达详情页 |
| 个性化推荐 | ✅ | — | 协同过滤算法（Jaccard 相似度） |

### 🔧 管理后台 (Web)

| 功能 | 描述 |
|------|------|
| 商品管理 | 新增、编辑、删除、上下架 |
| 店铺管理 | 店铺 CRUD + 营业状态 |
| 订单管理 | 查看、发货、退单审核 |
| 用户管理 | 列表、启用/禁用、创建管理员、删除 |
| 视频管理 | 视频上传、编辑、删除 |

---

## 💡 核心亮点

### 1. 协同过滤个性化推荐

```
用户A 购买了 {狗粮, 玩具}
用户B 购买了 {狗粮, 猫粮}

Jaccard(A, B) = |{狗粮}| / |{狗粮, 玩具, 猫粮}| = 0.33

为目标用户A推荐：猫粮（用户B买了但A没买）
```

- **算法**：基于用户的 Jaccard 相似度协同过滤
- **数据源**：`t_order` + `t_order_item` 中的购买历史
- **冷启动**：新用户无历史记录时降级为全站热销排行
- **接口**：`GET /api/product/recommend?userId=X&limit=8`

### 2. AI 智能客服（Prompt-based RAG）

```
用户："有什么狗狗在卖？"
  ↓ 意图识别（关键词匹配）
  ↓ 查询 t_product WHERE product_type=1 AND status=1
  ↓ 构建动态上下文：金毛 ¥2500 | 柯基 ¥3200 | ...
  ↓ 注入 System Prompt + 发送给通义千问
  ↓ AI："我们有金毛幼犬2500元、柯基3200元..."
  ↓ 前端解析 [查看详情](product:5) → 可点击跳转
```

- **模型**：通义千问 (Qwen-Turbo)
- **无向量数据库**：纯 SQL 查询 + Prompt 拼接实现
- **9 种意图识别**：宠物、周边、价格、店铺、订单、分类、热门、养护、问候
- **链接解析**：AI 回复中商品信息自动转为可点击链接

### 3. 微信小程序跨端兼容

- **条件编译**：`#ifdef H5` / `#ifdef MP-WEIXIN` 实现多端差异化
- **图片处理**：`padding-bottom` 比例容器（替代 `aspect-ratio`）+ `fixImgUrl` 全局 URL 转换
- **滑块验证码**：小程序端完全移除，后端自动跳过
- **微信登录**：`uni.login()` → `code2session` → 自动注册/登录
- **头像上传**：`chooseAvatar` 原生能力 + 本地即时预览

### 4. 完整订单生命周期

```
下单 → 待支付 → 已支付(待发货) → 已发货(待收货) → 已收货(待评价) → 已完成
  ↓         ↓                      ↓
取消订单   超时取消              申请退单 → 已退单(待审核) → 已退款
```

支持订单日志记录所有状态变更，商品快照防止商品信息变更影响历史订单。

---

## 🚀 快速启动

### 环境要求

| 组件 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 17+ | 后端运行环境 |
| Maven | 3.6+ | 后端构建工具 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | 可选，验证码缓存 |
| Node.js | 16+ | 前端构建 |
| 微信开发者工具 | 最新稳定版 | 小程序调试 |

### 第一步：数据库初始化

```sql
-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS petshop
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- 2. 导入表结构（在 MySQL 客户端中执行）
SOURCE pet-shop-server/src/main/resources/db/schema.sql;

-- 3. 如需微信登录功能
ALTER TABLE t_user ADD COLUMN openid VARCHAR(100) DEFAULT NULL;
ALTER TABLE t_user ADD UNIQUE INDEX idx_openid (openid);
```

### 第二步：配置并启动后端

编辑 `pet-shop-server/src/main/resources/application.yml`：

```yaml
server:
  port: 9090                        # 后端端口

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/petshop?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的MySQL密码

  redis:
    host: localhost
    port: 6379
    password:                       # Redis 密码，无则留空

# 以下为可选配置
amap:
  key: 你的高德地图Key               # 附近店铺功能需要

wechat:
  mini-program:
    app-id: wx你的AppID              # 小程序登录需要
    app-secret: 你的AppSecret

ai:
  api-key: 你的阿里云DashScope密钥    # AI 客服需要
```

启动后端：

```bash
cd pet-shop-server
mvn spring-boot:run
```

启动成功标志：
```
PetShopApplication : Started PetShopApplication in X.XXX seconds
```

访问 `http://localhost:9090/doc.html` 查看 Knife4j API 文档。

### 第三步：启动 Web 前端

```bash
cd pet-shop-web
npm install
npm run dev
```

访问 `http://localhost:3000` 即可打开商城首页。

> **代理说明**：Web 前端 Vite 配置了代理，`/api/*` 请求自动转发到 `http://localhost:9090`。如果后端端口不是 9090，需修改 `vite.config.js`。

### 第四步：启动小程序

```bash
cd pet-shop-mobile
npm install
npm run dev:mp-weixin     # 编译微信小程序
```

1. 打开**微信开发者工具**
2. 导入项目 → 选择 `pet-shop-mobile/dist/build/mp-weixin`
3. 在「详情 → 本地设置」中勾选 ✅ **不校验合法域名**
4. 修改 `src/utils/request.js` 中的 `API_HOST` 为你的局域网 IP

```javascript
// pet-shop-mobile/src/utils/request.js
const API_HOST = '192.168.xxx.xxx'   // 改为你电脑的局域网 IP
const API_PORT = '9090'
```

> 查看本机 IP：Windows 执行 `ipconfig`，Mac/Linux 执行 `ifconfig`

---

## 📡 API 文档

启动后端后访问 `http://localhost:9090/doc.html` 可查看完整 API 文档（Knife4j 生成）。

### 接口一览

| 模块 | 前缀 | 主要接口 |
|------|------|---------|
| 用户 | `/api/user` | `POST /login` `POST /register` `POST /wx-login` `PUT /profile` `PUT /password` |
| 商品 | `/api/product` | `GET /list` `GET /{id}` `GET /hot` `GET /recommend` `POST /` `PUT /status` |
| 购物车 | `/api/cart` | `GET /list` `POST /add` `PUT /{id}/quantity` `PUT /{id}/check` `DELETE /batch` |
| 订单 | `/api/order` | `POST /create` `PUT /{id}/pay` `PUT /{id}/deliver` `PUT /{id}/receive` `PUT /{id}/cancel` |
| 店铺 | `/api/store` | `GET /list` `GET /nearby` `GET /{id}` |
| 地址 | `/api/address` | `GET /user/{id}` `POST /` `PUT /` `DELETE /{id}` `PUT /{id}/default` |
| 视频 | `/api/video` | `GET /list` `GET /{id}` `PUT /{id}/like` `POST /` `DELETE /{id}` |
| AI | `/api/ai` | `POST /chat` — 智能客服对话 |
| 文件 | `/api/file` | `POST /upload` — 图片/视频上传 |
| 分类 | `/api/category` | `GET /list` — 商品分类 |
| 评价 | `/api/review` | `GET /product/{id}` — 商品评价 |
| 验证码 | `/api/captcha` | `GET /` `POST /verify` — 滑块验证码 |

### 统一响应格式

```json
{
  "code": 200,         // 200=成功, 400=参数错误, 500=服务器错误
  "message": "success",
  "data": { ... }      // 具体数据
}
```

---

## 🗄 数据库设计

### ER 图概要

```
t_user ──┬── t_order ──── t_order_item ──── t_product ──── t_category
  │       │      │                              │
  │       │   t_order_log                       ├── t_review ─── t_user
  │       │                                     │
  │       └── t_address                         ├── t_cart ─── t_user
  │                                             │
  ├── t_review                                  ├── t_store
  │                                             │
  └── t_cart                                    └── t_video
```

### 核心表说明

| 表名 | 说明 | 核心字段 |
|------|------|---------|
| `t_user` | 用户 | username, password(BCrypt), nickname, phone, email, avatar, openid, role |
| `t_product` | 商品 | name, description, detail, mainImage, price, stock, sales, productType, breed, age, gender |
| `t_category` | 分类 | name, parentId, type(1=宠物/2=周边), sort, icon |
| `t_order` | 订单 | orderNo, userId, totalAmount, status, payMethod, logisticsNo |
| `t_order_item` | 订单项 | orderId, productId, productName(快照), quantity, price(快照) |
| `t_cart` | 购物车 | userId, productId, quantity, checked |
| `t_store` | 店铺 | name, phone, address, longitude, latitude, rating, businessHours |
| `t_address` | 收货地址 | userId, receiverName, receiverPhone, province, city, district, detail, isDefault |
| `t_review` | 评价 | userId, productId, rating(1-5), content |
| `t_video` | 视频 | title, cover, videoUrl, viewCount, likeCount, status |
| `t_order_log` | 订单日志 | orderId, operator, fromStatus, toStatus, remark |

---

<p align="center">
  <sub>Built with ❤️ by PetShop Team · 2025-2026</sub>
</p>
