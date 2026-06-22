-- =============================================
-- 宠物商店 数据库初始化脚本
-- =============================================

CREATE DATABASE IF NOT EXISTS petshop
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE petshop;

-- ==================== 用户表 ====================
CREATE TABLE IF NOT EXISTS t_user (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    username    VARCHAR(50)     NOT NULL UNIQUE             COMMENT '用户名',
    password    VARCHAR(255)    NOT NULL                    COMMENT '密码（BCrypt加密）',
    nickname    VARCHAR(50)     DEFAULT ''                  COMMENT '昵称',
    phone       VARCHAR(20)     DEFAULT ''                  COMMENT '手机号',
    email       VARCHAR(100)    DEFAULT ''                  COMMENT '邮箱',
    avatar      VARCHAR(500)    DEFAULT ''                  COMMENT '头像URL',
    member_level TINYINT        DEFAULT 0                   COMMENT '会员等级：0-普通, 1-银卡, 2-金卡, 3-钻石',
    role        VARCHAR(20)     DEFAULT 'user'              COMMENT '角色：user-用户, admin-管理员',
    status      TINYINT         DEFAULT 0                   COMMENT '状态：0-正常, 1-禁用',
    create_time DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_time DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT         DEFAULT 0                   COMMENT '逻辑删除：0-否, 1-是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ==================== 收货地址表 ====================
CREATE TABLE IF NOT EXISTS t_address (
    id             BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    user_id        BIGINT       NOT NULL                    COMMENT '用户ID',
    receiver_name  VARCHAR(50)  NOT NULL                    COMMENT '收货人姓名',
    receiver_phone VARCHAR(20)  NOT NULL                    COMMENT '收货人手机号',
    province       VARCHAR(50)  DEFAULT ''                  COMMENT '省份',
    city           VARCHAR(50)  DEFAULT ''                  COMMENT '城市',
    district       VARCHAR(50)  DEFAULT ''                  COMMENT '区/县',
    detail         VARCHAR(255) NOT NULL                    COMMENT '详细地址',
    is_default     TINYINT      DEFAULT 0                   COMMENT '是否默认：0-否, 1-是',
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted        TINYINT      DEFAULT 0                   COMMENT '逻辑删除：0-否, 1-是',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收货地址表';

-- ==================== 商品分类表 ====================
CREATE TABLE IF NOT EXISTS t_category (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    name        VARCHAR(50)     NOT NULL                    COMMENT '分类名称',
    parent_id   BIGINT          DEFAULT 0                   COMMENT '父分类ID',
    type        TINYINT         DEFAULT 1                   COMMENT '类型：1-宠物, 2-宠物周边',
    sort        INT             DEFAULT 0                   COMMENT '排序',
    icon        VARCHAR(500)    DEFAULT ''                  COMMENT '图标URL',
    create_time DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_time DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT         DEFAULT 0                   COMMENT '逻辑删除：0-否, 1-是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- ==================== 宠物商店表 ====================
CREATE TABLE IF NOT EXISTS t_store (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    name            VARCHAR(100)    NOT NULL                    COMMENT '店铺名称',
    description     VARCHAR(500)    DEFAULT ''                  COMMENT '店铺简介',
    image           VARCHAR(500)    DEFAULT ''                  COMMENT '店铺图片',
    phone           VARCHAR(20)     DEFAULT ''                  COMMENT '联系电话',
    business_hours  VARCHAR(100)    DEFAULT ''                  COMMENT '营业时间',
    province        VARCHAR(50)     DEFAULT ''                  COMMENT '省份',
    city            VARCHAR(50)     DEFAULT ''                  COMMENT '城市',
    district        VARCHAR(50)     DEFAULT ''                  COMMENT '区/县',
    address         VARCHAR(255)    NOT NULL                    COMMENT '详细地址',
    longitude       DECIMAL(10,6)   DEFAULT 0                   COMMENT '经度',
    latitude        DECIMAL(10,6)   DEFAULT 0                   COMMENT '纬度',
    rating          DECIMAL(2,1)    DEFAULT 5.0                 COMMENT '店铺评分',
    status          TINYINT         DEFAULT 0                   COMMENT '状态：0-营业中, 1-休息中',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                   COMMENT '逻辑删除：0-否, 1-是',
    INDEX idx_city (city),
    INDEX idx_location (longitude, latitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物商店表';

-- ==================== 商品表 ====================
CREATE TABLE IF NOT EXISTS t_product (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    name        VARCHAR(200)    NOT NULL                    COMMENT '商品名称',
    description VARCHAR(500)    DEFAULT ''                  COMMENT '商品描述',
    detail      TEXT                                        COMMENT '商品详情（富文本）',
    main_image  VARCHAR(500)    DEFAULT ''                  COMMENT '商品主图',
    images      TEXT                                        COMMENT '商品图片列表（JSON数组）',
    category_id BIGINT          NOT NULL                    COMMENT '分类ID',
    store_id    BIGINT          NOT NULL                    COMMENT '店铺ID',
    product_type TINYINT        DEFAULT 1                   COMMENT '类型：1-宠物（唯一）, 2-宠物周边',
    price       DECIMAL(10,2)   NOT NULL                    COMMENT '价格',
    stock       INT             DEFAULT 0                   COMMENT '库存（宠物固定为1）',
    sales       INT             DEFAULT 0                   COMMENT '销量',
    status      TINYINT         DEFAULT 1                   COMMENT '状态：0-下架, 1-上架',
    breed       VARCHAR(50)     DEFAULT ''                  COMMENT '宠物品种',
    age         VARCHAR(20)     DEFAULT ''                  COMMENT '宠物年龄',
    gender      TINYINT         DEFAULT 0                   COMMENT '宠物性别：0-未知, 1-公, 2-母',
    create_time DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_time DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT         DEFAULT 0                   COMMENT '逻辑删除：0-否, 1-是',
    INDEX idx_category (category_id),
    INDEX idx_store (store_id),
    INDEX idx_type (product_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- ==================== 购物车表 ====================
CREATE TABLE IF NOT EXISTS t_cart (
    id          BIGINT      PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    user_id     BIGINT      NOT NULL                    COMMENT '用户ID',
    product_id  BIGINT      NOT NULL                    COMMENT '商品ID',
    quantity    INT         DEFAULT 1                   COMMENT '数量',
    checked     TINYINT     DEFAULT 1                   COMMENT '是否选中：0-否, 1-是',
    create_time DATETIME    DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_time DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT     DEFAULT 0                   COMMENT '逻辑删除：0-否, 1-是',
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- ==================== 订单表 ====================
CREATE TABLE IF NOT EXISTS t_order (
    id                 BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    order_no           VARCHAR(32)     NOT NULL UNIQUE             COMMENT '订单编号',
    user_id            BIGINT          NOT NULL                    COMMENT '用户ID',
    address_id         BIGINT          NOT NULL                    COMMENT '收货地址ID',
    total_amount       DECIMAL(10,2)   NOT NULL                    COMMENT '订单总金额',
    pay_amount         DECIMAL(10,2)   DEFAULT 0                   COMMENT '实付金额',
    pay_method         VARCHAR(20)     DEFAULT ''                  COMMENT '支付方式',
    pay_time           DATETIME        DEFAULT NULL                COMMENT '支付时间',
    status             INT             DEFAULT 0                   COMMENT '订单状态：0待支付/1待发货/2待收货/3待评价/4已完成/-1已取消/-2退单中/-3退单通过/-4管理员退单',
    remark             VARCHAR(500)    DEFAULT ''                  COMMENT '订单备注',
    cancel_reason      VARCHAR(500)    DEFAULT ''                  COMMENT '取消/退单原因',
    logistics_company  VARCHAR(50)     DEFAULT ''                  COMMENT '物流公司',
    logistics_no       VARCHAR(50)     DEFAULT ''                  COMMENT '物流单号',
    delivery_time      DATETIME        DEFAULT NULL                COMMENT '发货时间',
    receive_time       DATETIME        DEFAULT NULL                COMMENT '收货时间',
    finish_time        DATETIME        DEFAULT NULL                COMMENT '完成时间',
    create_time        DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_time        DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted            TINYINT         DEFAULT 0                   COMMENT '逻辑删除：0-否, 1-是',
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ==================== 订单项表 ====================
CREATE TABLE IF NOT EXISTS t_order_item (
    id            BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    order_id      BIGINT          NOT NULL                    COMMENT '订单ID',
    product_id    BIGINT          NOT NULL                    COMMENT '商品ID',
    product_name  VARCHAR(200)    NOT NULL                    COMMENT '商品名称（快照）',
    product_image VARCHAR(500)    DEFAULT ''                  COMMENT '商品图片（快照）',
    quantity      INT             NOT NULL                    COMMENT '数量',
    price         DECIMAL(10,2)   NOT NULL                    COMMENT '单价（快照）',
    subtotal      DECIMAL(10,2)   NOT NULL                    COMMENT '小计',
    create_time   DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_time   DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT         DEFAULT 0                   COMMENT '逻辑删除：0-否, 1-是',
    INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';

-- ==================== 视频表 ====================
CREATE TABLE IF NOT EXISTS t_video (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    title       VARCHAR(200)    NOT NULL                    COMMENT '视频标题',
    description VARCHAR(500)    DEFAULT ''                  COMMENT '视频描述',
    cover       VARCHAR(500)    DEFAULT ''                  COMMENT '封面图URL',
    video_url   VARCHAR(500)    NOT NULL                    COMMENT '视频URL',
    product_id  BIGINT          DEFAULT NULL                COMMENT '关联商品ID',
    category_id BIGINT          DEFAULT NULL                COMMENT '宠物分类ID',
    duration    INT             DEFAULT 0                   COMMENT '视频时长（秒）',
    view_count  INT             DEFAULT 0                   COMMENT '播放量',
    like_count  INT             DEFAULT 0                   COMMENT '点赞数',
    status      TINYINT         DEFAULT 0                   COMMENT '状态：0-审核中, 1-已发布, 2-已下架',
    create_time DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_time DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT         DEFAULT 0                   COMMENT '逻辑删除：0-否, 1-是',
    INDEX idx_category (category_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频表';

-- ==================== 评价表 ====================
CREATE TABLE IF NOT EXISTS t_review (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    user_id     BIGINT       NOT NULL                    COMMENT '用户ID',
    order_id    BIGINT       NOT NULL                    COMMENT '订单ID',
    product_id  BIGINT       NOT NULL                    COMMENT '商品ID',
    rating      TINYINT      NOT NULL                    COMMENT '评分（1-5星）',
    content     TEXT                                     COMMENT '评价内容',
    images      TEXT                                     COMMENT '评价图片（JSON数组）',
    reply       TEXT                                     COMMENT '商家回复',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      DEFAULT 0                   COMMENT '逻辑删除：0-否, 1-是',
    INDEX idx_product (product_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- ==================== 消息通知表（选做） ====================
CREATE TABLE IF NOT EXISTS t_message (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    user_id     BIGINT          DEFAULT NULL                COMMENT '用户ID（NULL表示全局消息）',
    title       VARCHAR(200)    NOT NULL                    COMMENT '消息标题',
    content     TEXT                                        COMMENT '消息内容',
    type        TINYINT         DEFAULT 1                   COMMENT '类型：1-系统消息, 2-订单消息, 3-促销消息',
    is_read     TINYINT         DEFAULT 0                   COMMENT '是否已读：0-否, 1-是',
    create_time DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_time DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT         DEFAULT 0                   COMMENT '逻辑删除：0-否, 1-是',
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通知表';

-- ==================== 初始数据 ====================
-- 管理员账号（密码：admin123，BCrypt加密）
INSERT INTO t_user (username, password, nickname, role, member_level) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '系统管理员', 'admin', 3);

-- 默认商品分类
INSERT INTO t_category (name, type, sort) VALUES
('狗狗', 1, 1),
('猫咪', 1, 2),
('小宠', 1, 3),
('水族', 1, 4),
('鸟类', 1, 5),
('狗粮', 2, 10),
('猫粮', 2, 11),
('零食', 2, 12),
('玩具', 2, 13),
('窝具', 2, 14),
('服饰', 2, 15),
('清洁', 2, 16),
('医疗保健', 2, 17);
