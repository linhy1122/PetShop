/*
 Navicat Premium Dump SQL

 Source Server         : myDB
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : petshop

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 29/06/2026 11:14:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_address
-- ----------------------------
DROP TABLE IF EXISTS `t_address`;
CREATE TABLE `t_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人手机号',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '区/县',
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认：0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-否, 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_address
-- ----------------------------
INSERT INTO `t_address` VALUES (1, 2, '张三', '13800000001', '福建省', '厦门市', '集美区', '理工路600号厦门理工学院', 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_address` VALUES (2, 3, '李四', '13800000002', '福建省', '厦门市', '思明区', '思明南路100号', 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_address` VALUES (3, 4, '王五', '13800000003', '福建省', '福州市', '鼓楼区', '五四路200号', 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);

-- ----------------------------
-- Table structure for t_cart
-- ----------------------------
DROP TABLE IF EXISTS `t_cart`;
CREATE TABLE `t_cart`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NULL DEFAULT 1 COMMENT '数量',
  `checked` tinyint NULL DEFAULT 1 COMMENT '是否选中：0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-否, 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_cart
-- ----------------------------
INSERT INTO `t_cart` VALUES (1, 2, 15, 12, 1, '2026-06-22 16:46:12', '2026-06-23 14:20:06', 1);
INSERT INTO `t_cart` VALUES (2, 2, 6, 7, 1, '2026-06-23 14:19:50', '2026-06-23 14:20:02', 1);
INSERT INTO `t_cart` VALUES (3, 7, 27, 4, 1, '2026-06-24 09:09:51', '2026-06-24 09:09:51', 0);
INSERT INTO `t_cart` VALUES (4, 2, 27, 3, 1, '2026-06-24 11:18:07', '2026-06-29 08:47:32', 1);
INSERT INTO `t_cart` VALUES (5, 2, 38, 1, 1, '2026-06-24 17:09:20', '2026-06-29 08:47:32', 1);
INSERT INTO `t_cart` VALUES (6, 2, 21, 1, 1, '2026-06-29 08:48:44', '2026-06-29 08:48:50', 1);
INSERT INTO `t_cart` VALUES (7, 7, 7, 1, 1, '2026-06-29 09:05:03', '2026-06-29 09:05:03', 0);
INSERT INTO `t_cart` VALUES (8, 2, 22, 1, 1, '2026-06-29 09:20:06', '2026-06-29 09:23:02', 1);
INSERT INTO `t_cart` VALUES (9, 2, 21, 1, 1, '2026-06-29 09:23:07', '2026-06-29 09:23:23', 1);
INSERT INTO `t_cart` VALUES (10, 2, 21, 1, 1, '2026-06-29 09:26:46', '2026-06-29 10:22:40', 1);
INSERT INTO `t_cart` VALUES (11, 2, 21, 1, 1, '2026-06-29 10:22:48', '2026-06-29 10:22:52', 1);
INSERT INTO `t_cart` VALUES (12, 2, 22, 1, 1, '2026-06-29 10:29:57', '2026-06-29 10:30:02', 1);
INSERT INTO `t_cart` VALUES (13, 2, 27, 1, 1, '2026-06-29 10:54:09', '2026-06-29 10:54:13', 1);
INSERT INTO `t_cart` VALUES (14, 2, 27, 1, 1, '2026-06-29 10:54:28', '2026-06-29 10:54:33', 1);
INSERT INTO `t_cart` VALUES (15, 2, 27, 1, 1, '2026-06-29 11:06:42', '2026-06-29 11:06:45', 1);

-- ----------------------------
-- Table structure for t_category
-- ----------------------------
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID',
  `type` tinyint NULL DEFAULT 1 COMMENT '类型：1-宠物, 2-宠物周边',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `icon` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '图标URL',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-否, 1-是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_category
-- ----------------------------
INSERT INTO `t_category` VALUES (1, '狗狗', 0, 1, 1, '', '2026-06-22 15:49:48', '2026-06-22 15:49:48', 0);
INSERT INTO `t_category` VALUES (2, '猫咪', 0, 1, 2, '', '2026-06-22 15:49:48', '2026-06-22 15:49:48', 0);
INSERT INTO `t_category` VALUES (3, '小宠', 0, 1, 3, '', '2026-06-22 15:49:48', '2026-06-22 15:49:48', 0);
INSERT INTO `t_category` VALUES (4, '水族', 0, 1, 4, '', '2026-06-22 15:49:48', '2026-06-22 15:49:48', 0);
INSERT INTO `t_category` VALUES (5, '鸟类', 0, 1, 5, '', '2026-06-22 15:49:48', '2026-06-22 15:49:48', 0);
INSERT INTO `t_category` VALUES (6, '狗粮', 0, 2, 10, '', '2026-06-22 15:49:48', '2026-06-22 15:49:48', 0);
INSERT INTO `t_category` VALUES (7, '猫粮', 0, 2, 11, '', '2026-06-22 15:49:48', '2026-06-22 15:49:48', 0);
INSERT INTO `t_category` VALUES (8, '零食', 0, 2, 12, '', '2026-06-22 15:49:48', '2026-06-22 15:49:48', 0);
INSERT INTO `t_category` VALUES (9, '玩具', 0, 2, 13, '', '2026-06-22 15:49:48', '2026-06-22 15:49:48', 0);
INSERT INTO `t_category` VALUES (10, '窝具', 0, 2, 14, '', '2026-06-22 15:49:48', '2026-06-22 15:49:48', 0);
INSERT INTO `t_category` VALUES (11, '服饰', 0, 2, 15, '', '2026-06-22 15:49:48', '2026-06-22 15:49:48', 0);
INSERT INTO `t_category` VALUES (12, '清洁', 0, 2, 16, '', '2026-06-22 15:49:48', '2026-06-22 15:49:48', 0);
INSERT INTO `t_category` VALUES (13, '医疗保健', 0, 2, 17, '', '2026-06-22 15:49:48', '2026-06-22 15:49:48', 0);

-- ----------------------------
-- Table structure for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID（NULL表示全局消息）',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '消息内容',
  `type` tinyint NULL DEFAULT 1 COMMENT '类型：1-系统消息, 2-订单消息, 3-促销消息',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读：0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-否, 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_message
-- ----------------------------
INSERT INTO `t_message` VALUES (1, NULL, '宠物商店盛大开业', '欢迎来到PetShop宠物商店！新用户注册即享9折优惠，快来为你的爱宠选购吧！', 1, 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_message` VALUES (2, 2, '订单发货通知', '您的订单#20250601001已发货，物流单号：SF1234567890，请注意查收。', 2, 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_message` VALUES (3, 3, '会员升级通知', '恭喜您升级为银卡会员！现在购物可享受95折优惠！', 3, 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `address_id` bigint NOT NULL COMMENT '收货地址ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '实付金额',
  `pay_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '支付方式',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `status` int NULL DEFAULT 0 COMMENT '订单状态：0待支付/1待发货/2待收货/3待评价/4已完成/-1已取消/-2退单中/-3退单通过/-4管理员退单',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '订单备注',
  `cancel_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '取消/退单原因',
  `logistics_company` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '物流公司',
  `logistics_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '物流单号',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime NULL DEFAULT NULL COMMENT '收货时间',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-否, 1-是',
  `cancel_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '取消类型：USER-用户, SYSTEM-系统, ADMIN-管理员',
  `refund_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '退款原因',
  `refund_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '退款说明',
  `refund_money` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime NULL DEFAULT NULL COMMENT '退款时间',
  `audit_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '审核意见',
  `previous_status` int NULL DEFAULT NULL COMMENT '退单前的原状态（审核拒绝后恢复用）',
  `comment_time` datetime NULL DEFAULT NULL COMMENT '评价时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES (1, '20250601001', 2, 1, 1958.00, 1958.00, 'alipay', '2025-06-01 10:30:00', 4, '请尽快发货', '', '', '', NULL, '2025-06-03 14:00:00', '2025-06-05 09:00:00', '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (2, '20250601002', 3, 2, 1768.00, 1768.00, 'wechat', '2025-06-01 11:00:00', 4, '', '', '', '', NULL, '2025-06-04 10:00:00', '2025-06-06 15:00:00', '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (3, '20250601003', 4, 3, 2522.00, 2522.00, 'alipay', '2025-06-02 09:00:00', 4, '送朋友的礼物', '', '', '', NULL, '2025-06-05 16:00:00', '2025-06-07 11:00:00', '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (4, '20250602001', 2, 1, 68.00, 68.00, 'alipay', '2025-06-02 14:00:00', 4, '', '', '', '', NULL, '2025-06-05 18:00:00', '2025-06-06 10:00:00', '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (5, '20250603001', 3, 2, 39.00, 39.00, 'wechat', '2025-06-03 08:00:00', 4, '', '', '', '', NULL, '2025-06-06 12:00:00', '2025-06-07 08:00:00', '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (6, '20250628001', 2, 1, 2500.00, 2500.00, '', NULL, 0, '柯基犬订单', '', '', '', NULL, NULL, NULL, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (7, '20250628002', 3, 2, 1600.00, 1600.00, 'alipay', '2025-06-28 14:00:00', 1, '英短蓝猫', '', '', '', NULL, NULL, NULL, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (8, '20250628003', 4, 3, 168.00, 168.00, 'wechat', '2025-06-28 16:00:00', 2, '猫粮急用', '', '顺丰速运', 'SF1234567890', '2025-06-28 18:00:00', NULL, NULL, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, '', '', '', NULL, NULL, '', NULL, NULL);

-- ----------------------------
-- Table structure for t_order_item
-- ----------------------------
DROP TABLE IF EXISTS `t_order_item`;
CREATE TABLE `t_order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称（快照）',
  `product_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '商品图片（快照）',
  `quantity` int NOT NULL COMMENT '数量',
  `price` decimal(10, 2) NOT NULL COMMENT '单价（快照）',
  `subtotal` decimal(10, 2) NOT NULL COMMENT '小计',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-否, 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order_item
-- ----------------------------
INSERT INTO `t_order_item` VALUES (10, 1, 1, '纯种金毛幼犬', '', 1, 1800.00, 1800.00, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0);
INSERT INTO `t_order_item` VALUES (11, 1, 17, '皇家小型犬成犬粮2kg', '', 1, 158.00, 158.00, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0);
INSERT INTO `t_order_item` VALUES (12, 2, 6, '英短蓝猫', '', 1, 1600.00, 1600.00, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0);
INSERT INTO `t_order_item` VALUES (13, 2, 20, '皇家室内成猫粮2kg', '', 1, 168.00, 168.00, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0);
INSERT INTO `t_order_item` VALUES (14, 3, 2, '柯基犬幼崽', '', 1, 2500.00, 2500.00, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0);
INSERT INTO `t_order_item` VALUES (15, 3, 24, '狗狗飞盘', '', 1, 22.00, 22.00, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0);
INSERT INTO `t_order_item` VALUES (16, 4, 25, '四季通用狗窝M号', '', 1, 68.00, 68.00, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0);
INSERT INTO `t_order_item` VALUES (17, 5, 29, '宠物专用沐浴露500ml', '', 1, 39.00, 39.00, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0);
INSERT INTO `t_order_item` VALUES (18, 6, 2, '柯基犬幼崽', '', 1, 2500.00, 2500.00, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0);
INSERT INTO `t_order_item` VALUES (19, 7, 6, '英短蓝猫', '', 1, 1600.00, 1600.00, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0);
INSERT INTO `t_order_item` VALUES (20, 8, 20, '皇家室内成猫粮2kg', '', 1, 168.00, 168.00, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0);

-- ----------------------------
-- Table structure for t_order_log
-- ----------------------------
DROP TABLE IF EXISTS `t_order_log`;
CREATE TABLE `t_order_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `operator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作人（用户名/系统）',
  `from_status` int NULL DEFAULT NULL COMMENT '原状态',
  `to_status` int NOT NULL COMMENT '新状态',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order_log
-- ----------------------------
INSERT INTO `t_order_log` VALUES (1, 3, '用户', NULL, 0, '提交订单', '2026-06-29 10:22:39');
INSERT INTO `t_order_log` VALUES (2, 3, 'USER', 0, -1, '用户取消', '2026-06-29 10:22:45');
INSERT INTO `t_order_log` VALUES (3, 4, '用户', NULL, 0, '提交订单', '2026-06-29 10:22:52');
INSERT INTO `t_order_log` VALUES (4, 4, '用户', 0, 1, '支付成功，方式：alipay', '2026-06-29 10:22:54');
INSERT INTO `t_order_log` VALUES (5, 5, '用户', NULL, 0, '提交订单', '2026-06-29 10:30:01');
INSERT INTO `t_order_log` VALUES (6, 5, '用户', 0, 1, '支付成功，方式：alipay', '2026-06-29 10:30:07');
INSERT INTO `t_order_log` VALUES (7, 6, '用户', NULL, 0, '提交订单', '2026-06-29 10:54:13');
INSERT INTO `t_order_log` VALUES (8, 6, 'USER', 0, -1, '用户取消', '2026-06-29 10:54:22');
INSERT INTO `t_order_log` VALUES (9, 7, '用户', NULL, 0, '提交订单', '2026-06-29 10:54:32');
INSERT INTO `t_order_log` VALUES (10, 7, '用户', 0, 1, '支付成功，方式：alipay', '2026-06-29 10:54:33');
INSERT INTO `t_order_log` VALUES (11, 8, '用户', NULL, 0, '提交订单', '2026-06-29 11:06:45');
INSERT INTO `t_order_log` VALUES (12, 8, '用户', 0, 1, '支付成功，方式：alipay', '2026-06-29 11:06:46');

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '商品描述',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '商品详情（富文本）',
  `main_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '商品主图',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '商品图片列表（JSON数组）',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `store_id` bigint NOT NULL COMMENT '店铺ID',
  `product_type` tinyint NULL DEFAULT 1 COMMENT '类型：1-宠物（唯一）, 2-宠物周边',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `stock` int NULL DEFAULT 0 COMMENT '库存（宠物固定为1）',
  `sales` int NULL DEFAULT 0 COMMENT '销量',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-下架, 1-上架',
  `breed` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '宠物品种',
  `age` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '宠物年龄',
  `gender` tinyint NULL DEFAULT 0 COMMENT '宠物性别：0-未知, 1-公, 2-母',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-否, 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category_id` ASC) USING BTREE,
  INDEX `idx_store`(`store_id` ASC) USING BTREE,
  INDEX `idx_type`(`product_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_product
-- ----------------------------
INSERT INTO `t_product` VALUES (1, '纯种金毛幼犬', '血统纯正金毛寻回犬，性格温顺，适合家庭陪伴，已接种两针疫苗', NULL, 'https://ts4.tc.mm.bing.net/th/id/OIP-C.QAZmI98t1kQp5AV4B0ep1wHaHa?r=0&cb=thfvnextfalcon2&rs=1&pid=ImgDetMain&o=7&rm=3', NULL, 1, 1, 1, 1800.00, 1, 12, 1, '金毛寻回犬', '3个月', 1, '2026-06-22 16:24:59', '2026-06-22 16:28:08', 0);
INSERT INTO `t_product` VALUES (2, '柯基犬幼崽', '双色柯基，短腿翘臀，活泼可爱，已驱虫已疫苗', NULL, 'https://picsum.photos/seed/corgi/400/300', NULL, 1, 3, 1, 2500.00, 1, 8, 1, '彭布罗克柯基', '2个月', 2, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (3, '泰迪贵宾犬', '玩具泰迪，体型小巧，不掉毛，适合公寓饲养', NULL, 'https://picsum.photos/seed/tady/400/300', NULL, 1, 1, 1, 1500.00, 1, 15, 1, '贵宾犬', '4个月', 2, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (4, '哈士奇幼犬', '蓝眼三把火，品相极佳，精力充沛，适合活跃家庭', NULL, 'https://picsum.photos/seed/husky/400/300', NULL, 1, 3, 1, 2200.00, 1, 6, 1, '西伯利亚哈士奇', '3个月', 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (5, '拉布拉多幼犬', '奶油色拉布拉多，智商高易训练，导盲犬首选品种', NULL, 'https://picsum.photos/seed/lab/400/300', NULL, 1, 1, 1, 2000.00, 1, 10, 1, '拉布拉多寻回犬', '3个月', 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (6, '英短蓝猫', '英国短毛猫，圆脸大眼，性格安静温顺，品相一流', NULL, 'https://picsum.photos/seed/bluecat/400/300', NULL, 2, 4, 1, 1600.00, 1, 20, 1, '英国短毛猫', '4个月', 2, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (7, '布偶猫', '双色布偶，仙女颜值，性格粘人像狗狗，适合陪伴', NULL, 'https://picsum.photos/seed/ragdoll/400/300', NULL, 2, 4, 1, 3500.00, 1, 5, 1, '布偶猫', '5个月', 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (8, '美短虎斑', '美国短毛猫银虎斑，体格健壮，花纹清晰美观', NULL, 'https://picsum.photos/seed/americat/400/300', NULL, 2, 2, 1, 1200.00, 1, 9, 1, '美国短毛猫', '3个月', 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (9, '暹罗猫', '挖煤工暹罗，蓝眼重点色，聪明活泼爱撒娇', NULL, 'https://picsum.photos/seed/siamese/400/300', NULL, 2, 2, 1, 1000.00, 1, 7, 1, '暹罗猫', '4个月', 2, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (10, '荷兰垂耳兔', '萌萌垂耳兔，温顺可爱，适合小朋友饲养', NULL, 'https://picsum.photos/seed/rabbit/400/300', NULL, 3, 2, 1, 200.00, 1, 25, 1, '荷兰垂耳兔', '2个月', 2, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (11, '龙猫', '标灰色龙猫，绒毛超级柔软，夜间活跃白天安静', NULL, 'https://picsum.photos/seed/chinchilla/400/300', NULL, 3, 5, 1, 800.00, 1, 4, 1, '龙猫', '6个月', 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (12, '锦鲤', '红白锦鲤，色泽鲜艳，寓意吉祥，适合庭院鱼池', NULL, 'https://picsum.photos/seed/koi/400/300', NULL, 4, 5, 1, 300.00, 1, 11, 1, '红白锦鲤', '1岁', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (13, '泰国斗鱼', '半月斗鱼，颜色绚丽，好养耐活，适合桌面鱼缸', NULL, 'https://picsum.photos/seed/betta/400/300', NULL, 4, 2, 1, 50.00, 1, 30, 1, '泰国斗鱼', '成鱼', 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (14, '虎皮鹦鹉', '蓝黄虎皮鹦鹉，活泼好动，可训练上手，适合新手饲养', NULL, 'https://picsum.photos/seed/budgie/400/300', NULL, 5, 5, 1, 80.00, 1, 18, 1, '虎皮鹦鹉', '3个月', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (15, '皇家小型犬成犬粮2kg', '法国皇家狗粮，精准营养配方，适口性佳', NULL, 'https://picsum.photos/seed/dogfood1/400/300', NULL, 6, 1, 2, 158.00, 188, 168, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (16, '比瑞吉天然全价狗粮1.5kg', '天然食材，无谷低敏，小颗粒易咀嚼', NULL, 'https://picsum.photos/seed/dogfood2/400/300', NULL, 6, 3, 2, 128.00, 150, 89, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (17, '麦富迪牛肉双拼狗粮2kg', '鲜牛肉+冻干双拼，高肉含量，适口性极佳', NULL, 'https://picsum.photos/seed/dogfood3/400/300', NULL, 6, 2, 2, 89.00, 300, 234, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (18, '皇家室内成猫粮2kg', '针对室内猫咪配方，帮助化毛，促进消化', NULL, 'https://picsum.photos/seed/catfood1/400/300', NULL, 7, 4, 2, 168.00, 180, 145, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (19, '渴望六种鱼猫粮1.8kg', '加拿大进口，6种深海鱼，85%含肉量', NULL, 'https://picsum.photos/seed/catfood2/400/300', NULL, 7, 4, 2, 320.00, 80, 67, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (20, '网易严选全价猫粮1.8kg', '网易自营品牌，70%鲜肉含量，性价比之选', NULL, 'https://picsum.photos/seed/catfood3/400/300', NULL, 7, 2, 2, 79.00, 250, 312, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (21, '鸡肉绕钙奶棒100g', '天然鸡肉制成，磨牙洁齿，补充钙质', NULL, 'https://ts1.tc.mm.bing.net/th/id/R-C.b87358632e859eee9000d182a08364d8?rik=Su7QRvdNRTRUfg&riu=http%3a%2f%2fwww.gzkyz.com.cn%2fimg%2f2022%2f05%2f06%2f78383e5ea38ffc958d262eb566b9bd6a.jpg&ehk=e4MomLwwjIlX29mp9jB2Fc6DviaAaPZtThcqBHDSO2o%3d&risl=&pid=ImgRaw&r=0', NULL, 8, 1, 2, 25.00, 499, 452, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-24 11:48:18', 0);
INSERT INTO `t_product` VALUES (22, '猫咪猫薄荷饼干80g', '天然猫薄荷+鸡肉配方，猫咪最爱，帮助消化', NULL, 'https://picsum.photos/seed/snack2/400/300', NULL, 8, 4, 2, 19.00, 399, 381, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (23, '冻干三文鱼块50g', '纯肉冻干技术，锁住营养和鲜味，猫狗通用', NULL, 'https://picsum.photos/seed/snack3/400/300', NULL, 8, 2, 2, 35.00, 350, 290, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (24, '耐咬橡胶磨牙球', '天然橡胶材质，内置发声器，耐咬耐玩', NULL, 'https://picsum.photos/seed/toy1/400/300', NULL, 9, 1, 2, 29.00, 300, 267, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (25, '电动仿真鱼', '触碰感应自动摇摆，内含猫薄荷，猫咪疯狂追逐', NULL, 'https://picsum.photos/seed/toy2/400/300', NULL, 9, 4, 2, 39.00, 200, 189, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (26, '狗狗飞盘', '软胶材质不伤牙龈，户外互动必备', NULL, 'https://picsum.photos/seed/toy3/400/300', NULL, 9, 3, 2, 22.00, 400, 356, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (27, '猫咪逗猫棒套装', '3件套组合装，羽毛+铃铛+激光笔，主子玩不腻', NULL, 'https://ts3.tc.mm.bing.net/th/id/OIP-C.K9ijpWmwgaCwUmlP6axzRAHaHS?r=0&cb=thfvnextfalcon2&rs=1&pid=ImgDetMain&o=7&rm=3', NULL, 9, 4, 2, 18.00, 495, 484, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-24 11:48:17', 0);
INSERT INTO `t_product` VALUES (28, '四季通用狗窝M号', '可拆洗，加厚PP棉填充，柔软舒适保暖', NULL, 'https://picsum.photos/seed/bed1/400/300', NULL, 10, 1, 2, 68.00, 120, 98, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (29, '封闭式猫窝', '半封闭设计给猫咪安全感，内附软垫可拆洗', NULL, 'https://picsum.photos/seed/bed2/400/300', NULL, 10, 4, 2, 79.00, 100, 87, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (30, '宠物凉席垫子L号', '冰丝凉席面料，夏季降温神器，大狗也能用', NULL, 'https://picsum.photos/seed/bed3/400/300', NULL, 10, 2, 2, 45.00, 180, 145, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (31, '狗狗纯棉T恤M号', '纯棉透气，卡通印花，可机洗不掉色', NULL, 'https://picsum.photos/seed/cloth1/400/300', NULL, 11, 2, 2, 35.00, 200, 156, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (32, '宠物雨衣', '防水速干面料，带反光条，雨天遛狗必备', NULL, 'https://picsum.photos/seed/cloth2/400/300', NULL, 11, 1, 2, 55.00, 150, 89, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (33, '宠物专用沐浴露500ml', '温和配方不刺激，留香持久，毛发蓬松顺滑', NULL, 'https://picsum.photos/seed/clean1/400/300', NULL, 12, 2, 2, 39.00, 350, 312, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (34, '猫砂盆全封闭式', '防外溅设计，活性炭过滤除臭，抽屉式易清理', NULL, 'https://picsum.photos/seed/clean2/400/300', NULL, 12, 4, 2, 89.00, 90, 78, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (35, '豆腐猫砂6L', '天然豆腐渣制成，可冲马桶，结团快吸臭强', NULL, 'https://picsum.photos/seed/clean3/400/300', NULL, 12, 4, 2, 29.00, 400, 389, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-24 11:48:18', 0);
INSERT INTO `t_product` VALUES (36, '宠物驱虫药体内外一体', '广谱驱虫，一月一次，安全有效适用于猫狗', NULL, 'https://picsum.photos/seed/med1/400/300', NULL, 13, 1, 2, 68.00, 300, 256, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (37, '宠物益生菌粉30包', '调理肠胃，改善软便拉稀，提高免疫力', NULL, 'https://picsum.photos/seed/med2/400/300', NULL, 13, 2, 2, 58.00, 250, 198, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_product` VALUES (38, '小狗', '这是🐷', '', '/uploads/2e734bba-c81a-4abc-a2eb-31a4c75a6a92.jpg', '[\"/uploads/b2c45085-4a48-47bd-b7b5-08c8d5c104ec.png\",\"/uploads/192e41cd-483c-4dcf-a75a-01bc877a6c9c.jpg\",\"/uploads/cf017d06-991c-437f-979f-a532d3c9914f.jpg\",\"/uploads/0a082ac5-8a33-4327-8cda-bf41d3af061c.jpg\",\"/uploads/8a221ff2-a8c9-4be3-8bf2-0990e4504b8c.jpg\"]', 2, 4, 1, 100.00, 1, 0, 1, '', '3个月', 2, '2026-06-24 11:49:04', '2026-06-29 09:11:39', 0);
INSERT INTO `t_product` VALUES (39, '宇宙第一的狗粮', '', '', '/uploads/ab7f766c-cddd-452f-875f-5ac29cb3c898.png', '[]', 6, 1, 2, 2888.00, 1, 0, 1, '', '', 0, '2026-06-24 17:16:31', '2026-06-25 10:34:12', 0);

-- ----------------------------
-- Table structure for t_review
-- ----------------------------
DROP TABLE IF EXISTS `t_review`;
CREATE TABLE `t_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `rating` tinyint NOT NULL COMMENT '评分（1-5星）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评价内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评价图片（JSON数组）',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '商家回复',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-否, 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product`(`product_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_review
-- ----------------------------
INSERT INTO `t_review` VALUES (1, 2, 1, 1, 5, '小金毛非常健康活泼，到家第三天就学会坐下了，太聪明了！店家服务态度也很好。', NULL, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_review` VALUES (2, 2, 1, 17, 5, '狗粮颗粒大小合适，狗狗很爱吃，没有出现肠胃不适的情况。', NULL, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_review` VALUES (3, 3, 2, 6, 5, '蓝猫品相很好，眼睛又大又圆，特别粘人，走到哪跟到哪，太可爱了！', NULL, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_review` VALUES (4, 3, 2, 20, 4, '猫粮质量不错，就是颗粒稍微有点大，小猫吃起来有点费劲。', NULL, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_review` VALUES (5, 4, 3, 2, 5, '柯基真的太可爱了，短腿翘臀，在小区里回头率超高！', NULL, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_review` VALUES (6, 4, 3, 24, 5, '飞盘质量很好，软胶材质不伤狗的牙齿和牙龈，强烈推荐。', NULL, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_review` VALUES (7, 2, 4, 25, 4, '猫窝大小合适，面料柔软，猫猫很喜欢钻进去睡觉，就是夏天有点热。', NULL, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_review` VALUES (8, 3, 5, 29, 5, '沐浴露香味很好闻，洗完毛特别蓬松，留香时间也挺长的。', NULL, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);

-- ----------------------------
-- Table structure for t_store
-- ----------------------------
DROP TABLE IF EXISTS `t_store`;
CREATE TABLE `t_store`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '店铺名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '店铺简介',
  `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '店铺图片',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '联系电话',
  `business_hours` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '营业时间',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '区/县',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `longitude` decimal(10, 6) NULL DEFAULT 0.000000 COMMENT '经度',
  `latitude` decimal(10, 6) NULL DEFAULT 0.000000 COMMENT '纬度',
  `rating` decimal(2, 1) NULL DEFAULT 5.0 COMMENT '店铺评分',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-营业中, 1-休息中',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-否, 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_city`(`city` ASC) USING BTREE,
  INDEX `idx_location`(`longitude` ASC, `latitude` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '宠物商店表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_store
-- ----------------------------
INSERT INTO `t_store` VALUES (1, '宠爱有家', '十年老店，专注品质宠物，所有宠物均经过健康检疫', 'https://picsum.photos/seed/store1/400/300', '0592-1234567', '09:00-21:00', '福建省', '厦门市', '集美区', '理工路600号', 118.097000, 24.613000, 4.8, 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_store` VALUES (2, '萌宠乐园', '品种齐全，售后无忧，提供宠物寄养和美容服务', 'https://picsum.photos/seed/store2/400/300', '0592-2345678', '10:00-22:00', '福建省', '厦门市', '思明区', '思明南路100号', 118.082000, 24.445000, 4.6, 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_store` VALUES (3, '汪星人宠物', '专业犬类繁育基地，提供冠军血统幼犬，终身健康保障', 'https://picsum.photos/seed/store3/400/300', '0591-3456789', '08:30-20:00', '福建省', '福州市', '鼓楼区', '五四路200号', 119.306000, 26.075000, 4.9, 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_store` VALUES (4, '喵星球', '猫咪主题宠物店，品种纯正，性格温顺，适合家庭饲养', 'https://picsum.photos/seed/store4/400/300', '0592-4567890', '10:00-20:00', '福建省', '厦门市', '湖里区', '金山路88号', 118.144000, 24.506000, 4.7, 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_store` VALUES (5, '爱宠天地', '综合型宠物超市，商品种类丰富，价格实惠', 'https://picsum.photos/seed/store5/400/300', '0595-5678901', '09:00-21:30', '福建省', '泉州市', '丰泽区', '田安南路300号', 118.613000, 24.874000, 4.5, 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '昵称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '邮箱',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '头像URL',
  `member_level` tinyint NULL DEFAULT 0 COMMENT '会员等级：0-普通, 1-银卡, 2-金卡, 3-钻石',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'user' COMMENT '角色：user-用户, admin-管理员',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-正常, 1-禁用',
  `github_id` bigint NULL DEFAULT NULL COMMENT 'GitHub用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-否, 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `idx_github_id`(`github_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (2, 'admin', '$2a$10$0TkO4LbyOh0qHk15J0oY2uywVYGOdjpD07HwVf6DCDt3bZXzsNwF.', '系统管理员', '13800000000', 'admin@petshop.cn', '', 3, 'admin', 0, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_user` VALUES (3, 'zhangsan', '$2a$10$0TkO4LbyOh0qHk15J0oY2uywVYGOdjpD07HwVf6DCDt3bZXzsNwF.', '张三', '13800000001', 'zhangsan@qq.com', '', 0, 'user', 0, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_user` VALUES (4, 'lisi', '$2a$10$0TkO4LbyOh0qHk15J0oY2uywVYGOdjpD07HwVf6DCDt3bZXzsNwF.', '李四', '13800000002', 'lisi@qq.com', '', 1, 'user', 0, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_user` VALUES (5, 'wangwu', '$2a$10$0TkO4LbyOh0qHk15J0oY2uywVYGOdjpD07HwVf6DCDt3bZXzsNwF.', '王五', '13800000003', 'wangwu@qq.com', '', 2, 'user', 0, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_user` VALUES (6, 'zhaoliu', '$2a$10$0TkO4LbyOh0qHk15J0oY2uywVYGOdjpD07HwVf6DCDt3bZXzsNwF.', '赵六', '13800000004', 'zhaoliu@qq.com', '', 0, 'user', 0, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_user` VALUES (7, 'linhy1122', '$2a$10$kb4DAcwl4Ujnm885gRXcB.odyDNPq/ync.U08mUp9Lcj02c0AaAW6', 'linhy1122', '', '1768013886@qq.com', 'https://avatars.githubusercontent.com/u/146430198?v=4', 0, 'user', 0, 146430198, '2026-06-23 22:17:56', '2026-06-23 22:17:56', 0);
INSERT INTO `t_user` VALUES (8, 'xiaoming', '$2a$10$cXiQqc9lzWPwU6vQgiJ1yOVicSpP0lqXZCV8Sx1sDFI6qzyVZqCT.', '用户ce07d9a4', '', '', '', 0, 'user', 0, NULL, '2026-06-24 08:26:35', '2026-06-24 08:26:35', 0);

-- ----------------------------
-- Table structure for t_video
-- ----------------------------
DROP TABLE IF EXISTS `t_video`;
CREATE TABLE `t_video`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '视频标题',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '视频描述',
  `cover` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '封面图URL',
  `video_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '视频URL',
  `product_id` bigint NULL DEFAULT NULL COMMENT '关联商品ID',
  `category_id` bigint NULL DEFAULT NULL COMMENT '宠物分类ID',
  `duration` int NULL DEFAULT 0 COMMENT '视频时长（秒）',
  `view_count` int NULL DEFAULT 0 COMMENT '播放量',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-审核中, 1-已发布, 2-已下架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-否, 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category_id` ASC) USING BTREE,
  INDEX `idx_product`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '视频表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_video
-- ----------------------------
INSERT INTO `t_video` VALUES (1, '金毛幼犬的日常训练', '记录小金毛从到家到学会坐下、握手、趴下的完整训练过程', '/uploads/ace1c29a-5cc1-498a-9225-542afa1a5a99.jpg', '/uploads/db8c6591-08e4-445f-83e0-dc33a0c7c3f9.mp4', NULL, 1, 180, 1555, 290, 1, '2026-06-22 16:24:59', '2026-06-24 18:10:13', 0);
INSERT INTO `t_video` VALUES (2, '英短蓝猫卖萌日常', '我家英短蓝猫的可爱瞬间合集，看完心都化了', 'https://picsum.photos/seed/v2/400/225', 'https://example.com/video2.mp4', NULL, 2, 120, 3203, 567, 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_video` VALUES (3, '如何正确给狗狗洗澡', '宠物美容师教你正确的洗澡步骤，新手必看', 'https://picsum.photos/seed/v3/400/225', 'https://example.com/video3.mp4', NULL, 1, 300, 892, 134, 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_video` VALUES (4, '猫咪最爱的5款玩具测评', '亲测5款网红猫咪玩具，帮你选出主子最爱的那款', 'https://picsum.photos/seed/v4/400/225', 'https://example.com/video4.mp4', NULL, 2, 240, 2150, 432, 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_video` VALUES (5, '布偶猫从小到大的变化', '记录布偶猫1个月到1岁的颜值蜕变，从小仙女到大仙女', 'https://picsum.photos/seed/v5/400/225', 'https://example.com/video5.mp4', NULL, 2, 150, 5602, 1023, 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_video` VALUES (6, '新手养狗必买的10件物品', '养狗前一定要准备的东西，避免踩坑', 'https://picsum.photos/seed/v6/400/225', 'https://example.com/video6.mp4', NULL, 1, 420, 1780, 345, 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_video` VALUES (7, '龙猫的喂养与护理', '龙猫饲养入门指南，从饮食到环境全方位讲解', 'https://picsum.photos/seed/v7/400/225', 'https://example.com/video7.mp4', NULL, 3, 260, 671, 98, 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_video` VALUES (8, '虎皮鹦鹉上手训练', '两周让你的虎皮鹦鹉从怕人到主动上手', 'https://picsum.photos/seed/v8/400/225', 'https://example.com/video8.mp4', NULL, 5, 200, 451, 76, 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);

SET FOREIGN_KEY_CHECKS = 1;
