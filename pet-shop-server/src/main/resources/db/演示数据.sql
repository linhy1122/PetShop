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

 Date: 07/07/2026 14:59:41
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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收货地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_address
-- ----------------------------
INSERT INTO `t_address` VALUES (1, 2, '张三', '13800000001', '福建省', '厦门市', '集美区', '理工路600号厦门理工学院', 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_address` VALUES (2, 3, '李四', '13800000002', '福建省', '厦门市', '思明区', '思明南路100号', 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_address` VALUES (3, 4, '王五', '13800000003', '福建省', '福州市', '鼓楼区', '五四路200号', 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_address` VALUES (4, 2, '张三', '13900202220', '福建省', '厦门市', '海沧区', '1号', 0, '2026-07-03 08:38:28', '2026-07-03 08:38:28', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '购物车表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_cart
-- ----------------------------
INSERT INTO `t_cart` VALUES (1, 2, 15, 12, 1, '2026-06-22 16:46:12', '2026-06-23 14:20:06', 1);
INSERT INTO `t_cart` VALUES (2, 2, 6, 7, 1, '2026-06-23 14:19:50', '2026-06-23 14:20:02', 1);
INSERT INTO `t_cart` VALUES (3, 7, 27, 3, 1, '2026-06-24 09:09:51', '2026-06-24 09:09:51', 0);
INSERT INTO `t_cart` VALUES (4, 2, 27, 3, 1, '2026-06-24 11:18:07', '2026-06-29 08:47:32', 1);
INSERT INTO `t_cart` VALUES (5, 2, 38, 1, 1, '2026-06-24 17:09:20', '2026-06-29 08:47:32', 1);
INSERT INTO `t_cart` VALUES (6, 2, 21, 1, 1, '2026-06-29 08:48:44', '2026-06-29 08:48:50', 1);
INSERT INTO `t_cart` VALUES (7, 7, 7, 1, 0, '2026-06-29 09:05:03', '2026-06-29 09:05:03', 0);
INSERT INTO `t_cart` VALUES (8, 2, 22, 1, 1, '2026-06-29 09:20:06', '2026-06-29 09:23:02', 1);
INSERT INTO `t_cart` VALUES (9, 2, 21, 1, 1, '2026-06-29 09:23:07', '2026-06-29 09:23:23', 1);
INSERT INTO `t_cart` VALUES (10, 2, 21, 1, 1, '2026-06-29 09:26:46', '2026-06-29 10:22:40', 1);
INSERT INTO `t_cart` VALUES (11, 2, 21, 1, 1, '2026-06-29 10:22:48', '2026-06-29 10:22:52', 1);
INSERT INTO `t_cart` VALUES (12, 2, 22, 1, 1, '2026-06-29 10:29:57', '2026-06-29 10:30:02', 1);
INSERT INTO `t_cart` VALUES (13, 2, 27, 1, 1, '2026-06-29 10:54:09', '2026-06-29 10:54:13', 1);
INSERT INTO `t_cart` VALUES (14, 2, 27, 1, 1, '2026-06-29 10:54:28', '2026-06-29 10:54:33', 1);
INSERT INTO `t_cart` VALUES (15, 2, 27, 1, 1, '2026-06-29 11:06:42', '2026-06-29 11:06:45', 1);
INSERT INTO `t_cart` VALUES (16, 2, 10, 1, 1, '2026-06-29 15:36:37', '2026-06-29 15:36:42', 1);
INSERT INTO `t_cart` VALUES (17, 2, 27, 1, 1, '2026-06-30 15:47:26', '2026-06-30 15:47:35', 1);
INSERT INTO `t_cart` VALUES (18, 2, 27, 1, 1, '2026-06-30 15:54:24', '2026-07-03 09:39:43', 1);
INSERT INTO `t_cart` VALUES (19, 2, 6, 1, 1, '2026-06-30 15:54:31', '2026-07-03 09:39:43', 1);
INSERT INTO `t_cart` VALUES (20, 2, 17, 1, 1, '2026-07-02 10:54:52', '2026-07-02 10:55:05', 1);
INSERT INTO `t_cart` VALUES (21, 2, 38, 1, 1, '2026-07-02 11:03:30', '2026-07-03 09:39:09', 1);
INSERT INTO `t_cart` VALUES (22, 9, 35, 1, 0, '2026-07-02 14:56:44', '2026-07-02 14:56:44', 0);
INSERT INTO `t_cart` VALUES (23, 9, 22, 1, 1, '2026-07-02 14:56:45', '2026-07-02 14:56:45', 0);
INSERT INTO `t_cart` VALUES (24, 9, 26, 1, 1, '2026-07-02 14:56:46', '2026-07-02 14:56:46', 0);
INSERT INTO `t_cart` VALUES (25, 10, 27, 1, 1, '2026-07-02 15:47:01', '2026-07-02 15:47:01', 0);
INSERT INTO `t_cart` VALUES (26, 2, 35, 1, 1, '2026-07-03 09:13:35', '2026-07-03 09:39:12', 1);
INSERT INTO `t_cart` VALUES (27, 2, 8, 1, 1, '2026-07-03 09:38:44', '2026-07-03 09:39:12', 1);
INSERT INTO `t_cart` VALUES (28, 7, 21, 1, 1, '2026-07-03 10:49:17', '2026-07-03 10:49:17', 0);
INSERT INTO `t_cart` VALUES (29, 2, 24, 3, 1, '2026-07-03 14:38:15', '2026-07-03 14:38:23', 1);
INSERT INTO `t_cart` VALUES (30, 2, 21, 1, 0, '2026-07-03 15:31:23', '2026-07-03 15:31:56', 1);
INSERT INTO `t_cart` VALUES (31, 2, 21, 1, 1, '2026-07-03 15:32:25', '2026-07-03 15:32:29', 1);
INSERT INTO `t_cart` VALUES (32, 2, 27, 1, 1, '2026-07-06 11:16:41', '2026-07-06 11:25:13', 1);
INSERT INTO `t_cart` VALUES (33, 2, 21, 1, 1, '2026-07-06 11:16:43', '2026-07-06 11:25:13', 1);
INSERT INTO `t_cart` VALUES (34, 2, 38, 1, 1, '2026-07-06 11:16:57', '2026-07-06 11:25:11', 1);
INSERT INTO `t_cart` VALUES (35, 2, 10, 1, 1, '2026-07-06 11:17:09', '2026-07-06 11:25:05', 1);
INSERT INTO `t_cart` VALUES (36, 2, 21, 1, 1, '2026-07-06 11:46:10', '2026-07-07 11:31:19', 1);
INSERT INTO `t_cart` VALUES (37, 2, 27, 1, 1, '2026-07-06 11:46:11', '2026-07-07 11:31:19', 1);
INSERT INTO `t_cart` VALUES (38, 2, 38, 1, 1, '2026-07-06 11:46:44', '2026-07-07 11:31:17', 1);
INSERT INTO `t_cart` VALUES (39, 2, 38, 1, 1, '2026-07-07 14:55:24', '2026-07-07 14:55:24', 0);
INSERT INTO `t_cart` VALUES (40, 2, 27, 1, 1, '2026-07-07 14:55:32', '2026-07-07 14:55:32', 0);
INSERT INTO `t_cart` VALUES (41, 2, 35, 1, 1, '2026-07-07 14:55:34', '2026-07-07 14:55:34', 0);
INSERT INTO `t_cart` VALUES (42, 2, 37, 1, 1, '2026-07-07 14:55:42', '2026-07-07 14:55:42', 0);
INSERT INTO `t_cart` VALUES (43, 2, 23, 2, 1, '2026-07-07 14:55:50', '2026-07-07 14:55:50', 0);
INSERT INTO `t_cart` VALUES (44, 2, 24, 1, 1, '2026-07-07 14:55:56', '2026-07-07 14:55:56', 0);
INSERT INTO `t_cart` VALUES (45, 2, 17, 1, 1, '2026-07-07 14:55:57', '2026-07-07 14:55:57', 0);
INSERT INTO `t_cart` VALUES (46, 11, 32, 1, 1, '2026-07-07 14:58:02', '2026-07-07 14:58:02', 0);
INSERT INTO `t_cart` VALUES (47, 11, 13, 1, 1, '2026-07-07 14:58:03', '2026-07-07 14:58:03', 0);
INSERT INTO `t_cart` VALUES (48, 11, 14, 1, 1, '2026-07-07 14:58:05', '2026-07-07 14:58:05', 0);
INSERT INTO `t_cart` VALUES (49, 11, 20, 1, 1, '2026-07-07 14:58:06', '2026-07-07 14:58:06', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品分类表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息通知表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES (1, '20250601001', 2, 1, 1958.00, 1958.00, 'alipay', '2025-06-01 10:30:00', 4, '请尽快发货', '', '', '', NULL, '2025-06-03 14:00:00', '2025-06-05 09:00:00', '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (2, '20250601002', 3, 2, 1768.00, 1768.00, 'wechat', '2025-06-01 11:00:00', 4, '', '', '', '', NULL, '2025-06-04 10:00:00', '2025-06-06 15:00:00', '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (3, '20250601003', 4, 3, 2522.00, 2522.00, 'alipay', '2025-06-02 09:00:00', 4, '送朋友的礼物', '', '', '', NULL, '2025-06-05 16:00:00', '2025-06-07 11:00:00', '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (4, '20250602001', 2, 1, 68.00, 68.00, 'alipay', '2025-06-02 14:00:00', 4, '', '', '', '', NULL, '2025-06-05 18:00:00', '2025-06-06 10:00:00', '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (5, '20250603001', 3, 2, 39.00, 39.00, 'wechat', '2025-06-03 08:00:00', 4, '', '', '', '', NULL, '2025-06-06 12:00:00', '2025-06-07 08:00:00', '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (6, '20250628001', 2, 1, 2500.00, 2500.00, '', NULL, -1, '柯基犬订单', '超时未支付自动取消', '', '', NULL, NULL, NULL, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, 'SYSTEM', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (7, '20250628002', 3, 2, 1600.00, 1600.00, 'alipay', '2025-06-28 14:00:00', 2, '英短蓝猫', '', '圆通速递', '1111', '2026-06-29 15:48:55', NULL, NULL, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (8, '20250628003', 4, 3, 168.00, 168.00, 'wechat', '2025-06-28 16:00:00', 2, '猫粮急用', '', '顺丰速运', 'SF1234567890', '2025-06-28 18:00:00', NULL, NULL, '2026-06-29 11:12:36', '2026-06-29 11:12:36', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (9, '2071498075666345984', 2, 1, 200.00, 200.00, 'alipay', '2026-06-29 15:36:44', 4, '', '', '顺丰速运', '1111111121212', '2026-06-29 15:37:41', '2026-07-02 10:54:03', '2026-07-02 10:54:15', '2026-06-29 15:36:42', '2026-06-29 15:36:42', 0, '', '用户申请退单', '', NULL, NULL, '必须买', 2, '2026-07-02 10:54:15');
INSERT INTO `t_order` VALUES (10, '2071863201405808640', 2, 1, 18.00, 18.00, 'alipay', '2026-06-30 15:47:39', 1, '', '', '', '', NULL, NULL, NULL, '2026-06-30 15:47:35', '2026-06-30 15:47:35', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (11, '2072514365105348608', 2, 1, 89.00, 89.00, 'alipay', '2026-07-02 10:55:06', 4, '', '', '中通快递', '22323232', '2026-07-02 10:55:16', '2026-07-02 10:55:21', '2026-07-02 10:55:30', '2026-07-02 10:55:05', '2026-07-02 10:55:05', 0, '', '', '', NULL, NULL, '', NULL, '2026-07-02 10:55:30');
INSERT INTO `t_order` VALUES (12, '2072857656913022976', 2, 1, 1229.00, 1229.00, '', NULL, -1, '', '不要了', '', '', NULL, NULL, NULL, '2026-07-03 09:39:12', '2026-07-03 09:39:12', 0, 'USER', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (13, '2072857785657184256', 2, 1, 1618.00, 1618.00, 'alipay', '2026-07-03 09:39:47', 3, '', '', '中通快递', '112211221', '2026-07-03 09:40:02', '2026-07-03 09:40:14', NULL, '2026-07-03 09:39:43', '2026-07-03 09:39:43', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (14, '2072932950311129088', 2, 1, 87.00, 87.00, 'alipay', '2026-07-03 14:38:25', 3, '', '', '韵达快递', '454', '2026-07-03 14:38:40', '2026-07-03 14:38:46', NULL, '2026-07-03 14:38:23', '2026-07-03 14:38:23', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (15, '2072946564904480768', 2, 1, 25.00, 21.25, 'alipay', '2026-07-03 15:32:31', 1, '', '', '', '', NULL, NULL, NULL, '2026-07-03 15:32:29', '2026-07-03 15:32:29', 0, '', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (16, '2073971499219214336', 2, 1, 43.00, 36.55, '', NULL, -1, '', '超时未支付自动取消', '', '', NULL, NULL, NULL, '2026-07-06 11:25:13', '2026-07-06 11:25:13', 0, 'SYSTEM', '', '', NULL, NULL, '', NULL, NULL);
INSERT INTO `t_order` VALUES (17, '2074335423739396096', 2, 1, 43.00, 36.55, '', NULL, -1, '', '超时未支付自动取消', '', '', NULL, NULL, NULL, '2026-07-07 11:31:19', '2026-07-07 11:31:19', 0, 'SYSTEM', '', '', NULL, NULL, '', NULL, NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单项表' ROW_FORMAT = DYNAMIC;

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
INSERT INTO `t_order_item` VALUES (21, 9, 10, '荷兰垂耳兔', '/uploads/86d42483-fb89-4b84-8916-aa1d0f073c97.png', 1, 200.00, 200.00, '2026-06-29 15:36:42', '2026-06-29 15:36:42', 0);
INSERT INTO `t_order_item` VALUES (22, 10, 27, '猫咪逗猫棒套装', '/uploads/f73f1629-4526-4a13-a229-d00c9445de69.png', 1, 18.00, 18.00, '2026-06-30 15:47:35', '2026-06-30 15:47:35', 0);
INSERT INTO `t_order_item` VALUES (23, 11, 17, '麦富迪牛肉双拼狗粮2kg', '/uploads/0acd09c5-1925-4b73-ac84-1ad98e957278.png', 1, 89.00, 89.00, '2026-07-02 10:55:05', '2026-07-02 10:55:05', 0);
INSERT INTO `t_order_item` VALUES (24, 12, 8, '美短虎斑', '/uploads/58538f37-c0c5-43fa-82c9-22ca1ebe0ce7.png', 1, 1200.00, 1200.00, '2026-07-03 09:39:12', '2026-07-03 09:39:12', 0);
INSERT INTO `t_order_item` VALUES (25, 12, 35, '豆腐猫砂6L', '/uploads/35477b0c-9dd5-49ea-b300-dbaabcd2763c.png', 1, 29.00, 29.00, '2026-07-03 09:39:12', '2026-07-03 09:39:12', 0);
INSERT INTO `t_order_item` VALUES (26, 13, 6, '英短蓝猫', '/uploads/db9420d7-7439-4e5f-9923-589c81c50b9e.png', 1, 1600.00, 1600.00, '2026-07-03 09:39:43', '2026-07-03 09:39:43', 0);
INSERT INTO `t_order_item` VALUES (27, 13, 27, '猫咪逗猫棒套装', '/uploads/f73f1629-4526-4a13-a229-d00c9445de69.png', 1, 18.00, 18.00, '2026-07-03 09:39:43', '2026-07-03 09:39:43', 0);
INSERT INTO `t_order_item` VALUES (28, 14, 24, '耐咬橡胶磨牙球', '/uploads/daccb850-ac9d-48fb-a6fd-6045e3dc5e8a.png', 3, 29.00, 87.00, '2026-07-03 14:38:23', '2026-07-03 14:38:23', 0);
INSERT INTO `t_order_item` VALUES (29, 15, 21, '鸡肉绕钙奶棒100g', '/uploads/7e810e5e-bfa5-4b0e-9d3e-e24d2bc15d35.png', 1, 25.00, 25.00, '2026-07-03 15:32:29', '2026-07-03 15:32:29', 0);
INSERT INTO `t_order_item` VALUES (30, 16, 21, '鸡肉绕钙奶棒100g', '/uploads/7e810e5e-bfa5-4b0e-9d3e-e24d2bc15d35.png', 1, 25.00, 25.00, '2026-07-06 11:25:13', '2026-07-06 11:25:13', 0);
INSERT INTO `t_order_item` VALUES (31, 16, 27, '猫咪逗猫棒套装', '/uploads/f73f1629-4526-4a13-a229-d00c9445de69.png', 1, 18.00, 18.00, '2026-07-06 11:25:13', '2026-07-06 11:25:13', 0);
INSERT INTO `t_order_item` VALUES (32, 17, 27, '猫咪逗猫棒套装', '/uploads/f73f1629-4526-4a13-a229-d00c9445de69.png', 1, 18.00, 18.00, '2026-07-07 11:31:19', '2026-07-07 11:31:19', 0);
INSERT INTO `t_order_item` VALUES (33, 17, 21, '鸡肉绕钙奶棒100g', '/uploads/7e810e5e-bfa5-4b0e-9d3e-e24d2bc15d35.png', 1, 25.00, 25.00, '2026-07-07 11:31:19', '2026-07-07 11:31:19', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单操作日志表' ROW_FORMAT = DYNAMIC;

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
INSERT INTO `t_order_log` VALUES (13, 6, 'SYSTEM', 0, -1, '系统自动取消（超时未支付）', '2026-06-29 15:21:31');
INSERT INTO `t_order_log` VALUES (14, 9, '用户', NULL, 0, '提交订单', '2026-06-29 15:36:42');
INSERT INTO `t_order_log` VALUES (15, 9, '用户', 0, 1, '支付成功，方式：alipay', '2026-06-29 15:36:44');
INSERT INTO `t_order_log` VALUES (16, 9, '管理员', 1, 2, '发货：顺丰速运 1111111121212', '2026-06-29 15:37:41');
INSERT INTO `t_order_log` VALUES (17, 7, '管理员', 1, 2, '发货：圆通速递 1111', '2026-06-29 15:48:54');
INSERT INTO `t_order_log` VALUES (18, 10, '用户', NULL, 0, '提交订单', '2026-06-30 15:47:35');
INSERT INTO `t_order_log` VALUES (19, 10, '用户', 0, 1, '支付成功，方式：alipay', '2026-06-30 15:47:38');
INSERT INTO `t_order_log` VALUES (20, 9, '用户', 2, -2, '申请退单：用户申请退单', '2026-06-30 15:48:02');
INSERT INTO `t_order_log` VALUES (21, 9, '管理员', -2, 2, '退单审核拒绝：必须买', '2026-07-02 10:52:02');
INSERT INTO `t_order_log` VALUES (22, 9, '用户', 2, 3, '确认收货', '2026-07-02 10:54:03');
INSERT INTO `t_order_log` VALUES (23, 9, '用户', 3, 4, '评价完成，订单已完成', '2026-07-02 10:54:15');
INSERT INTO `t_order_log` VALUES (24, 11, '用户', NULL, 0, '提交订单', '2026-07-02 10:55:04');
INSERT INTO `t_order_log` VALUES (25, 11, '用户', 0, 1, '支付成功，方式：alipay', '2026-07-02 10:55:05');
INSERT INTO `t_order_log` VALUES (26, 11, '管理员', 1, 2, '发货：中通快递 22323232', '2026-07-02 10:55:16');
INSERT INTO `t_order_log` VALUES (27, 11, '用户', 2, 3, '确认收货', '2026-07-02 10:55:20');
INSERT INTO `t_order_log` VALUES (28, 11, '用户', 3, 4, '评价完成，订单已完成', '2026-07-02 10:55:30');
INSERT INTO `t_order_log` VALUES (29, 12, '用户', NULL, 0, '提交订单', '2026-07-03 09:39:11');
INSERT INTO `t_order_log` VALUES (30, 12, 'USER', 0, -1, '不要了', '2026-07-03 09:39:28');
INSERT INTO `t_order_log` VALUES (31, 13, '用户', NULL, 0, '提交订单', '2026-07-03 09:39:42');
INSERT INTO `t_order_log` VALUES (32, 13, '用户', 0, 1, '支付成功，方式：alipay', '2026-07-03 09:39:47');
INSERT INTO `t_order_log` VALUES (33, 13, '管理员', 1, 2, '发货：中通快递 112211221', '2026-07-03 09:40:01');
INSERT INTO `t_order_log` VALUES (34, 13, '用户', 2, 3, '确认收货', '2026-07-03 09:40:13');
INSERT INTO `t_order_log` VALUES (35, 14, '用户', NULL, 0, '提交订单', '2026-07-03 14:38:23');
INSERT INTO `t_order_log` VALUES (36, 14, '用户', 0, 1, '支付成功，方式：alipay', '2026-07-03 14:38:25');
INSERT INTO `t_order_log` VALUES (37, 14, '管理员', 1, 2, '发货：韵达快递 454', '2026-07-03 14:38:39');
INSERT INTO `t_order_log` VALUES (38, 14, '用户', 2, 3, '确认收货', '2026-07-03 14:38:46');
INSERT INTO `t_order_log` VALUES (39, 15, '用户', NULL, 0, '提交订单', '2026-07-03 15:32:29');
INSERT INTO `t_order_log` VALUES (40, 15, '用户', 0, 1, '支付成功，方式：alipay', '2026-07-03 15:32:31');
INSERT INTO `t_order_log` VALUES (41, 16, '用户', NULL, 0, '提交订单', '2026-07-06 11:25:12');
INSERT INTO `t_order_log` VALUES (42, 16, 'SYSTEM', 0, -1, '系统自动取消（超时未支付）', '2026-07-06 11:55:25');
INSERT INTO `t_order_log` VALUES (43, 17, '用户', NULL, 0, '提交订单', '2026-07-07 11:31:18');
INSERT INTO `t_order_log` VALUES (44, 17, 'SYSTEM', 0, -1, '系统自动取消（超时未支付）', '2026-07-07 12:01:44');

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
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_product
-- ----------------------------
INSERT INTO `t_product` VALUES (1, '纯种金毛幼犬', '血统纯正金毛寻回犬，性格温顺，适合家庭陪伴，已接种两针疫苗', '', '/uploads/0c266130-38fc-416c-8940-f6566f80afc4.png', '[]', 1, 1, 1, 1800.00, 1, 12, 1, '金毛寻回犬', '3个月', 1, '2026-06-22 16:24:59', '2026-06-29 10:47:48', 0);
INSERT INTO `t_product` VALUES (2, '柯基犬幼崽', '双色柯基，短腿翘臀，活泼可爱，已驱虫已疫苗', NULL, 'https://picsum.photos/seed/corgi/400/300', NULL, 1, 3, 1, 2500.00, 1, 8, 1, '彭布罗克柯基', '2个月', 2, '2026-06-22 16:24:59', '2026-06-29 10:29:02', 1);
INSERT INTO `t_product` VALUES (3, '泰迪贵宾犬', '玩具泰迪，体型小巧，不掉毛，适合公寓饲养', '', '/uploads/aafc234f-297f-461b-94ce-9c085186677f.png', '[]', 1, 1, 1, 1500.00, 1, 15, 1, '贵宾犬', '4个月', 2, '2026-06-22 16:24:59', '2026-06-29 10:47:42', 0);
INSERT INTO `t_product` VALUES (4, '哈士奇幼犬', '蓝眼三把火，品相极佳，精力充沛，适合活跃家庭', '', '/uploads/d83f9b6b-afba-4d44-9f3a-f8687a7bbe27.png', '[]', 1, 3, 1, 2200.00, 1, 6, 1, '西伯利亚哈士奇', '3个月', 1, '2026-06-22 16:24:59', '2026-06-29 10:49:13', 0);
INSERT INTO `t_product` VALUES (5, '拉布拉多幼犬', '奶油色拉布拉多，智商高易训练，导盲犬首选品种', NULL, 'https://picsum.photos/seed/lab/400/300', NULL, 1, 1, 1, 2000.00, 1, 10, 1, '拉布拉多寻回犬', '3个月', 1, '2026-06-22 16:24:59', '2026-06-29 10:29:05', 1);
INSERT INTO `t_product` VALUES (6, '英短蓝猫', '英国短毛猫，圆脸大眼，性格安静温顺，品相一流', '', '/uploads/db9420d7-7439-4e5f-9923-589c81c50b9e.png', '[]', 2, 4, 1, 1600.00, 0, 20, 1, '英国短毛猫', '4个月', 2, '2026-06-22 16:24:59', '2026-07-06 11:16:53', 0);
INSERT INTO `t_product` VALUES (7, '布偶猫', '双色布偶，仙女颜值，性格粘人像狗狗，适合陪伴', '', '/uploads/79aa1c90-9d41-4251-9804-3069ef0225dc.png', '[]', 2, 4, 1, 3500.00, 1, 5, 1, '布偶猫', '5个月', 1, '2026-06-22 16:24:59', '2026-06-29 10:49:19', 0);
INSERT INTO `t_product` VALUES (8, '美短虎斑', '美国短毛猫银虎斑，体格健壮，花纹清晰美观', '', '/uploads/58538f37-c0c5-43fa-82c9-22ca1ebe0ce7.png', '[]', 2, 2, 1, 1200.00, 1, 9, 1, '美国短毛猫', '3个月', 1, '2026-06-22 16:24:59', '2026-06-29 10:48:00', 0);
INSERT INTO `t_product` VALUES (9, '暹罗猫', '挖煤工暹罗，蓝眼重点色，聪明活泼爱撒娇', NULL, 'https://picsum.photos/seed/siamese/400/300', NULL, 2, 2, 1, 1000.00, 1, 7, 1, '暹罗猫', '4个月', 2, '2026-06-22 16:24:59', '2026-06-29 10:29:42', 1);
INSERT INTO `t_product` VALUES (10, '荷兰垂耳兔', '萌萌垂耳兔，温顺可爱，适合小朋友饲养', '', '/uploads/86d42483-fb89-4b84-8916-aa1d0f073c97.png', '[]', 3, 2, 1, 200.00, 0, 25, 0, '荷兰垂耳兔', '2个月', 2, '2026-06-22 16:24:59', '2026-07-06 11:17:16', 0);
INSERT INTO `t_product` VALUES (11, '龙猫', '标灰色龙猫，绒毛超级柔软，夜间活跃白天安静', '', '/uploads/bb06b37b-8d5f-4689-a036-f7f085f846c6.png', '[]', 3, 5, 1, 800.00, 1, 4, 1, '龙猫', '6个月', 1, '2026-06-22 16:24:59', '2026-06-29 10:49:25', 0);
INSERT INTO `t_product` VALUES (12, '锦鲤', '红白锦鲤，色泽鲜艳，寓意吉祥，适合庭院鱼池', '', '/uploads/10508527-dba8-4b2b-a361-45fa1f70bf6f.png', '[]', 4, 5, 1, 300.00, 1, 11, 1, '红白锦鲤', '1岁', 0, '2026-06-22 16:24:59', '2026-06-29 10:47:54', 0);
INSERT INTO `t_product` VALUES (13, '泰国斗鱼', '半月斗鱼，颜色绚丽，好养耐活，适合桌面鱼缸', '', '/uploads/c5f23df1-3130-41d5-b131-2715bde76f81.png', '[]', 4, 2, 1, 50.00, 1, 30, 1, '泰国斗鱼', '成鱼', 1, '2026-06-22 16:24:59', '2026-06-29 10:47:16', 0);
INSERT INTO `t_product` VALUES (14, '虎皮鹦鹉', '蓝黄虎皮鹦鹉，活泼好动，可训练上手，适合新手饲养', '', '/uploads/0bd4332f-7544-4ad4-86be-7e8d0fea7d3d.png', '[]', 5, 5, 1, 80.00, 1, 18, 1, '虎皮鹦鹉', '3个月', 0, '2026-06-22 16:24:59', '2026-06-29 10:47:35', 0);
INSERT INTO `t_product` VALUES (15, '皇家小型犬成犬粮2kg', '法国皇家狗粮，精准营养配方，适口性佳', '', '/uploads/19844e72-9d5b-42d0-85b0-7ce58591bcb8.png', '[]', 6, 1, 2, 158.00, 188, 168, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:41:56', 0);
INSERT INTO `t_product` VALUES (16, '比瑞吉天然全价狗粮1.5kg', '天然食材，无谷低敏，小颗粒易咀嚼', '', '/uploads/4e7a4f60-cde2-4ff2-a670-f32d991c065d.png', '[]', 6, 3, 2, 128.00, 150, 89, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:42:40', 0);
INSERT INTO `t_product` VALUES (17, '麦富迪牛肉双拼狗粮2kg', '鲜牛肉+冻干双拼，高肉含量，适口性极佳', '', '/uploads/0acd09c5-1925-4b73-ac84-1ad98e957278.png', '[\"/uploads/bf9b489d-98f8-4a1a-bc0b-b2fc7f5b1d49.jpg\"]', 6, 2, 2, 89.00, 299, 235, 1, '', '', 0, '2026-06-22 16:24:59', '2026-07-06 11:23:51', 0);
INSERT INTO `t_product` VALUES (18, '皇家室内成猫粮2kg', '针对室内猫咪配方，帮助化毛，促进消化', '', '/uploads/12eb68df-6b9b-4ad4-a428-bce9b57cbad5.png', '[]', 7, 4, 2, 168.00, 180, 145, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:42:10', 0);
INSERT INTO `t_product` VALUES (19, '渴望六种鱼猫粮1.8kg', '加拿大进口，6种深海鱼，85%含肉量', '', '/uploads/74b1d967-1d1a-4f76-917d-0885f0448b44.png', '[]', 7, 4, 2, 320.00, 80, 67, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:47:09', 0);
INSERT INTO `t_product` VALUES (20, '网易严选全价猫粮1.8kg', '网易自营品牌，70%鲜肉含量，性价比之选', '', '/uploads/c1fd6351-296b-47bd-b7f9-a973b5096419.png', '[]', 7, 2, 2, 79.00, 250, 312, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:24:08', 0);
INSERT INTO `t_product` VALUES (21, '鸡肉绕钙奶棒100g', '天然鸡肉制成，磨牙洁齿，补充钙质', '', '/uploads/7e810e5e-bfa5-4b0e-9d3e-e24d2bc15d35.png', '[]', 8, 1, 2, 25.00, 499, 453, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:22:00', 0);
INSERT INTO `t_product` VALUES (22, '猫咪猫薄荷饼干80g', '天然猫薄荷+鸡肉配方，猫咪最爱，帮助消化', '', '/uploads/8561aa69-0b7f-4899-8c68-dcc01e73e950.png', '[]', 8, 4, 2, 19.00, 400, 380, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:23:22', 0);
INSERT INTO `t_product` VALUES (23, '冻干三文鱼块50g', '纯肉冻干技术，锁住营养和鲜味，猫狗通用', '', '/uploads/0d5cd1c0-505d-4e01-b473-dfeb3e215767.png', '[]', 8, 2, 2, 35.00, 350, 290, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:24:25', 0);
INSERT INTO `t_product` VALUES (24, '耐咬橡胶磨牙球', '天然橡胶材质，内置发声器，耐咬耐玩', '', '/uploads/daccb850-ac9d-48fb-a6fd-6045e3dc5e8a.png', '[]', 9, 1, 2, 29.00, 297, 270, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:24:34', 0);
INSERT INTO `t_product` VALUES (25, '电动仿真鱼', '触碰感应自动摇摆，内含猫薄荷，猫咪疯狂追逐', NULL, 'https://picsum.photos/seed/toy2/400/300', NULL, 9, 4, 2, 39.00, 200, 189, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:30:01', 1);
INSERT INTO `t_product` VALUES (26, '狗狗飞盘', '软胶材质不伤牙龈，户外互动必备', '', '/uploads/27e8ec70-bbf7-43b9-b85f-9b4d6b7f7de3.png', '[]', 9, 3, 2, 22.00, 400, 356, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:23:29', 0);
INSERT INTO `t_product` VALUES (27, '猫咪逗猫棒套装', '3件套组合装，羽毛+铃铛+激光笔，主子玩不腻', '', '/uploads/f73f1629-4526-4a13-a229-d00c9445de69.png', '[]', 9, 4, 2, 18.00, 495, 485, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:21:47', 0);
INSERT INTO `t_product` VALUES (28, '四季通用狗窝M号', '可拆洗，加厚PP棉填充，柔软舒适保暖', '', '/uploads/6ac85216-717c-4836-b911-9247b66349e7.png', '[]', 10, 1, 2, 68.00, 120, 98, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:42:25', 0);
INSERT INTO `t_product` VALUES (29, '封闭式猫窝', '半封闭设计给猫咪安全感，内附软垫可拆洗', '', '/uploads/7f6c1124-ea87-407a-b5fc-ee74c342be3e.png', '[]', 10, 4, 2, 79.00, 100, 87, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:42:46', 0);
INSERT INTO `t_product` VALUES (30, '宠物凉席垫子L号', '冰丝凉席面料，夏季降温神器，大狗也能用', '', '/uploads/fcb07557-51f0-4b6e-be30-8531f756d107.png', '[]', 10, 2, 2, 45.00, 180, 145, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:42:17', 0);
INSERT INTO `t_product` VALUES (31, '狗狗纯棉T恤M号', '纯棉透气，卡通印花，可机洗不掉色', '', '/uploads/ffbc760b-bf33-49f1-b3ca-cdbcfa7e4f49.png', '[]', 11, 2, 2, 35.00, 200, 156, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:42:02', 0);
INSERT INTO `t_product` VALUES (32, '宠物雨衣', '防水速干面料，带反光条，雨天遛狗必备', '', '/uploads/ccd7d83c-c264-4544-b28b-ea802ebbc333.png', '[]', 11, 1, 2, 55.00, 148, 91, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:42:34', 0);
INSERT INTO `t_product` VALUES (33, '宠物专用沐浴露500ml', '温和配方不刺激，留香持久，毛发蓬松顺滑', '', '/uploads/72b6fae1-e9db-4f88-9011-1f1b6c304d89.png', '[]', 12, 2, 2, 39.00, 350, 312, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:24:16', 0);
INSERT INTO `t_product` VALUES (34, '猫砂盆全封闭式', '防外溅设计，活性炭过滤除臭，抽屉式易清理', '', '/uploads/8901fe7a-85da-4fec-a212-b4d9e8d7ed9d.png', '[]', 12, 4, 2, 89.00, 90, 78, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:46:59', 0);
INSERT INTO `t_product` VALUES (35, '豆腐猫砂6L', '天然豆腐渣制成，可冲马桶，结团快吸臭强', '', '/uploads/35477b0c-9dd5-49ea-b300-dbaabcd2763c.png', '[]', 12, 4, 2, 29.00, 400, 390, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:22:22', 0);
INSERT INTO `t_product` VALUES (36, '宠物驱虫药体内外一体', '广谱驱虫，一月一次，安全有效适用于猫狗', '', '/uploads/ca1a450d-377a-46e9-aafd-744fbea6501f.png', '[]', 13, 1, 2, 68.00, 300, 256, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:24:42', 0);
INSERT INTO `t_product` VALUES (37, '宠物益生菌粉30包', '调理肠胃，改善软便拉稀，提高免疫力', '', '/uploads/29c7ab6e-af8a-4b8a-8141-0192dbc3fbfa.png', '[]', 13, 2, 2, 58.00, 250, 198, 1, '', '', 0, '2026-06-22 16:24:59', '2026-06-29 10:41:50', 0);
INSERT INTO `t_product` VALUES (38, '小狗', '这是🐷', '', '/uploads/2e734bba-c81a-4abc-a2eb-31a4c75a6a92.jpg', '[\"/uploads/b2c45085-4a48-47bd-b7b5-08c8d5c104ec.png\",\"/uploads/192e41cd-483c-4dcf-a75a-01bc877a6c9c.jpg\",\"/uploads/cf017d06-991c-437f-979f-a532d3c9914f.jpg\",\"/uploads/0a082ac5-8a33-4327-8cda-bf41d3af061c.jpg\",\"/uploads/8a221ff2-a8c9-4be3-8bf2-0990e4504b8c.jpg\"]', 2, 4, 1, 100.00, 0, 0, 0, '', '3个月', 2, '2026-06-24 11:49:04', '2026-07-07 14:56:17', 0);
INSERT INTO `t_product` VALUES (39, '宇宙第一的狗粮', '', '', '/uploads/ab7f766c-cddd-452f-875f-5ac29cb3c898.png', '[]', 6, 1, 2, 2888.00, 0, 0, 1, '', '', 0, '2026-06-24 17:16:31', '2026-06-29 10:29:49', 1);
INSERT INTO `t_product` VALUES (40, '狗', '', '', '/uploads/1855d325-2542-4cd6-ba18-58db15b36f68.webp', '[\"blob:http://localhost:3000/3230ad07-603e-4f5b-8d95-e91a21f3cc67\",\"/uploads/85a415ad-88b0-402f-940a-a2789b9af33d.jpg\",\"blob:http://localhost:3000/604f6e09-1301-42e6-9844-fd31cae479a1\"]', 1, 1, 1, 1.00, 1, 0, 1, '', '', 1, '2026-07-03 09:42:24', '2026-07-03 09:43:06', 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评价表' ROW_FORMAT = DYNAMIC;

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
INSERT INTO `t_review` VALUES (9, 2, 9, 10, 5, '好吃', '[]', NULL, '2026-07-02 10:54:15', '2026-07-02 10:54:15', 0);
INSERT INTO `t_review` VALUES (10, 2, 11, 17, 5, '非常好吃', '[\"/uploads/79763cc1-f6c6-4984-866a-8066074661d7.jpg\"]', NULL, '2026-07-02 10:55:30', '2026-07-02 10:55:30', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '宠物商店表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_store
-- ----------------------------
INSERT INTO `t_store` VALUES (1, '宠爱有家', '十年老店，专注品质宠物，所有宠物均经过健康检疫', 'https://picsum.photos/seed/store1/400/300', '0592-1234567', '09:00-21:00', '福建省', '厦门市', '集美区', '理工路660号', 118.097000, 24.613000, 4.8, 0, '2026-06-22 16:24:59', '2026-06-29 15:51:46', 0);
INSERT INTO `t_store` VALUES (2, '萌宠乐园', '品种齐全，售后无忧，提供宠物寄养和美容服务', 'https://picsum.photos/seed/store2/400/300', '0592-2345678', '10:00-22:00', '福建省', '厦门市', '思明区', '思明南路100号', 118.082000, 24.445000, 4.6, 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_store` VALUES (3, '汪星人宠物', '专业犬类繁育基地，提供冠军血统幼犬，终身健康保障', 'https://picsum.photos/seed/store3/400/300', '0591-3456789', '08:30-20:00', '福建省', '福州市', '鼓楼区', '五四路200号', 119.306000, 26.075000, 4.9, 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_store` VALUES (4, '喵星球', '猫咪主题宠物店，品种纯正，性格温顺，适合家庭饲养', 'https://picsum.photos/seed/store4/400/300', '0592-4567890', '10:00-20:00', '福建省', '厦门市', '湖里区', '金山路88号', 118.144000, 24.506000, 4.7, 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_store` VALUES (5, '爱宠天地', '综合型宠物超市，商品种类丰富，价格实惠', 'https://picsum.photos/seed/store5/400/300', '0595-5678901', '09:00-21:30', '福建省', '泉州市', '丰泽区', '田安南路300号', 118.613000, 24.874000, 4.5, 0, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_store` VALUES (6, '店1', '', '', '18966666666', '', '', '厦门', '', '理工路333号', 2.000000, 2.000000, 5.0, 0, '2026-07-03 09:44:11', '2026-07-03 09:44:11', 0);

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
  `openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '微信小程序openid',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `idx_github_id`(`github_id` ASC) USING BTREE,
  UNIQUE INDEX `idx_openid`(`openid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (2, 'admin', '$2a$10$0TkO4LbyOh0qHk15J0oY2uywVYGOdjpD07HwVf6DCDt3bZXzsNwF.', '系统管理员', '13800000000', 'admin@petshop.cn', '', 3, 'admin', 0, NULL, '2026-06-22 16:24:59', '2026-07-03 15:47:15', 0, NULL);
INSERT INTO `t_user` VALUES (3, 'zhangsan', '$2a$10$0TkO4LbyOh0qHk15J0oY2uywVYGOdjpD07HwVf6DCDt3bZXzsNwF.', '张三', '13800000001', 'zhangsan@qq.com', '', 0, 'user', 0, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0, NULL);
INSERT INTO `t_user` VALUES (4, 'lisi', '$2a$10$0TkO4LbyOh0qHk15J0oY2uywVYGOdjpD07HwVf6DCDt3bZXzsNwF.', '李四', '13800000002', 'lisi@qq.com', '', 1, 'user', 0, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0, NULL);
INSERT INTO `t_user` VALUES (5, 'wangwu', '$2a$10$0TkO4LbyOh0qHk15J0oY2uywVYGOdjpD07HwVf6DCDt3bZXzsNwF.', '王五', '13800000003', 'wangwu@qq.com', '', 2, 'user', 0, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0, NULL);
INSERT INTO `t_user` VALUES (6, 'zhaoliu', '$2a$10$0TkO4LbyOh0qHk15J0oY2uywVYGOdjpD07HwVf6DCDt3bZXzsNwF.', '赵六', '13800000004', 'zhaoliu@qq.com', '', 0, 'user', 0, NULL, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0, NULL);
INSERT INTO `t_user` VALUES (7, 'linhy1122', '$2a$10$kb4DAcwl4Ujnm885gRXcB.odyDNPq/ync.U08mUp9Lcj02c0AaAW6', 'linhy1122', '', '1768013886@qq.com', 'https://avatars.githubusercontent.com/u/146430198?v=4', 0, 'user', 0, 146430198, '2026-06-23 22:17:56', '2026-06-23 22:17:56', 0, NULL);
INSERT INTO `t_user` VALUES (8, 'xiaoming', '$2a$10$cXiQqc9lzWPwU6vQgiJ1yOVicSpP0lqXZCV8Sx1sDFI6qzyVZqCT.', '用户ce07d9a4', '', '', '', 0, 'user', 0, NULL, '2026-06-24 08:26:35', '2026-06-30 08:30:55', 0, NULL);
INSERT INTO `t_user` VALUES (10, 'wx_oVKlK3YbTzWB', '$2a$10$1Jv8RhP8cKgBmcbIWZVxAuWMB2lty4mEATgjP6eDxhJuae/1s1U.e', '微信用户5R5dOc', '', '', '', 0, 'user', 0, NULL, '2026-07-02 15:39:22', '2026-07-02 15:39:22', 0, 'oVKlK3YbTzWB45y6_7oUcb5R5dOc');
INSERT INTO `t_user` VALUES (11, 'wx_oVKlK3fK3U1m', '$2a$10$SMcPfVpFx95z6rvBplmi1.xeiBoCG83PuCJ3dgGgZpQtXxW2IXPnS', '微信用户1XpjVg', '', '', '/uploads/b90fd9ae-be2e-4ef3-a63f-cde8a5d7e6b3.jpeg', 0, 'user', 0, NULL, '2026-07-02 15:50:26', '2026-07-02 15:50:26', 0, 'oVKlK3fK3U1mPYeVPeYgHe1XpjVg');

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
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '视频表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_video
-- ----------------------------
INSERT INTO `t_video` VALUES (1, '金毛幼犬的日常训练', '记录小金毛从到家到学会坐下、握手、趴下的完整训练过程', '/uploads/ace1c29a-5cc1-498a-9225-542afa1a5a99.jpg', '/uploads/db8c6591-08e4-445f-83e0-dc33a0c7c3f9.mp4', NULL, 1, 180, 1564, 294, 1, '2026-06-22 16:24:59', '2026-06-24 18:10:13', 0);
INSERT INTO `t_video` VALUES (2, '耄猫卖萌日常', '我家猫的可爱瞬间合集，看完心都化了', '/uploads/f219836a-9175-4a2d-a936-94d5b1623f29.jpg', '/uploads/10729c01-e63e-4b03-b687-4819ec65b1a6.mp4', 22, 2, 120, 3213, 580, 1, '2026-06-22 16:24:59', '2026-07-03 09:07:09', 0);
INSERT INTO `t_video` VALUES (3, '如何正确给狗狗洗澡', '宠物美容师教你正确的洗澡步骤，新手必看', '/uploads/fc4e34e4-c3d2-4ee8-bf10-efb4bdccfb17.jpg', '/uploads/c1c32147-3129-4da2-a2c1-698acfb786d5.mp4', NULL, 1, 300, 893, 134, 1, '2026-06-22 16:24:59', '2026-07-03 15:41:50', 0);
INSERT INTO `t_video` VALUES (4, '猫咪最爱的5款玩具测评', '亲测5款网红猫咪玩具，帮你选出主子最爱的那款', '/uploads/9f938990-ce6f-4fc9-bc80-824d37af24ff.jpg', '/uploads/9e72f61e-72b6-47e3-8624-b75278e44c8a.mp4', NULL, 2, 240, 2152, 432, 1, '2026-06-22 16:24:59', '2026-07-03 15:42:43', 0);
INSERT INTO `t_video` VALUES (5, '布偶猫从小到大的变化', '记录布偶猫1个月到1岁的颜值蜕变，从小仙女到大仙女', 'https://picsum.photos/seed/v5/400/225', 'https://example.com/video5.mp4', NULL, 2, 150, 5602, 1023, 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_video` VALUES (6, '新手养狗必买的10件物品', '养狗前一定要准备的东西，避免踩坑', 'https://picsum.photos/seed/v6/400/225', 'https://example.com/video6.mp4', NULL, 1, 420, 1780, 345, 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_video` VALUES (7, '龙猫的喂养与护理', '龙猫饲养入门指南，从饮食到环境全方位讲解', 'https://picsum.photos/seed/v7/400/225', 'https://example.com/video7.mp4', NULL, 3, 260, 671, 98, 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_video` VALUES (8, '虎皮鹦鹉上手训练', '两周让你的虎皮鹦鹉从怕人到主动上手', 'https://picsum.photos/seed/v8/400/225', 'https://example.com/video8.mp4', NULL, 5, 200, 451, 76, 1, '2026-06-22 16:24:59', '2026-06-22 16:24:59', 0);
INSERT INTO `t_video` VALUES (9, '小鸭', '咕咕嘎嘎', '/uploads/6e1f8c41-7e51-4b79-b9d4-123c31dca348.jpg', '/uploads/dca5fb8a-25e0-4ea6-b78d-981918ffaf77.mp4', NULL, 5, 0, 1, 0, 1, '2026-07-03 09:08:54', '2026-07-03 15:40:57', 0);
INSERT INTO `t_video` VALUES (10, '猫猫', '猫咪', '/uploads/5e5ded0f-7f96-4593-a96a-9d7485d978ee.jpg', '/uploads/efb53002-a20a-4998-b5f9-b6c746fe6d0d.mp4', 27, 2, 0, 2, 1, 1, '2026-07-03 09:09:54', '2026-07-03 09:09:54', 0);
INSERT INTO `t_video` VALUES (11, '树枝猫', '', '/uploads/14f5169f-66a3-450b-bea3-4909c16fe4d7.jpg', '/uploads/41090c6b-e7d0-42ea-ba43-8e95740d7960.mp4', NULL, 2, 0, 9, 7, 1, '2026-07-03 09:12:23', '2026-07-03 09:12:23', 0);

SET FOREIGN_KEY_CHECKS = 1;
