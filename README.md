# 🐾 PetShop 宠物商店

> 程序设计实训项目 | 2025-2026-2 | Vue 3 + Spring Boot 2.7

## 📁 项目结构

```
PetShop/
├── pet-shop-server/          # Spring Boot 2.7 后端
│   ├── src/main/java/com/petshop/
│   │   ├── PetShopApplication.java     # 启动类
│   │   ├── config/           # 配置（CORS、Redis、MyBatis-Plus）
│   │   ├── common/           # 通用类（Result、BaseEntity、异常处理）
│   │   ├── entity/           # 实体类（映射数据表）
│   │   ├── dto/              # 数据传输对象
│   │   ├── controller/       # REST API 控制器
│   │   ├── service/          # 业务逻辑接口
│   │   ├── service/impl/     # 业务逻辑实现
│   │   └── mapper/           # MyBatis-Plus Mapper
│   └── src/main/resources/
│       ├── application.yml   # 主配置
│       └── db/schema.sql     # 数据库初始化脚本
│
├── pet-shop-web/              # Vue 3 前端
│   └── src/
│       ├── router/           # Vue Router 路由
│       ├── stores/           # Pinia 状态管理
│       ├── api/              # API 接口封装
│       ├── views/            # 页面组件
│       ├── components/       # 公共组件
│       └── utils/            # 工具函数
└── README.md
```

## 🛠 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + Element Plus + Pinia + Axios |
| 后端 | Spring Boot 2.7 + MyBatis-Plus + Redis |
| 数据库 | MySQL 8.0 |
| 地图 | 高德地图 API |
| AI | 通义千问 API |
| 工具 | JDK 17 / Maven / Git |

## 🚀 快速启动

### 1. 初始化数据库
执行 `pet-shop-server/src/main/resources/db/schema.sql` 创建数据库和表。

### 2. 启动后端
```bash
cd pet-shop-server
mvn spring-boot:run
```
后端启动于 `http://localhost:8080`
API文档：`http://localhost:8080/doc.html`

### 3. 启动前端
```bash
cd pet-shop-web
npm install
npm run dev
```
前端启动于 `http://localhost:3000`

### 4. 配置
- 数据库：修改 `application.yml` 中的 `datasource` 配置
- Redis：修改 `application.yml` 中的 `redis` 配置
- 高德地图 Key：修改 `application.yml` 中的 `amap.key`
- AI API Key：修改 `application.yml` 中的 `ai.api-key`

## 📋 功能模块

- ✅ 用户注册/登录
- ✅ 个人中心（信息、地址管理）
- ✅ 商品浏览（宠物 + 宠物周边）
- ✅ 附近商店（地图）
- ✅ 购物车
- ✅ 完整订单流程（下单→支付→发货→收货→评价）
- ✅ 退单流程
- ✅ 宠物视频
- ✅ AI 智能客服
- ✅ 管理后台（商品/店铺/订单/用户/视频管理）

## 👥 团队成员

| 角色 | 姓名 | 分工 |
|------|------|------|
| 组长 | | |
| 组员 | | |
| 组员 | | |
| 组员 | | |
