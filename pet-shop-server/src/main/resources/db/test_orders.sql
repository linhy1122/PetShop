-- =============================================
-- 订单测试数据
-- 确保已执行 schema.sql 和 data.sql
-- =============================================

USE petshop;

-- 清理旧数据
DELETE FROM t_order_item WHERE order_id IN (1,2,3,4,5,6,7,8);
DELETE FROM t_order WHERE id IN (1,2,3,4,5,6,7,8);

-- ==================== 订单 ====================
-- 用户ID: 2=张三, 3=李四, 4=王五
-- 地址ID: 1=张三地址, 2=李四地址, 3=王五地址

INSERT INTO t_order (id, order_no, user_id, address_id, total_amount, pay_amount, pay_method, pay_time, status, remark, receive_time, finish_time) VALUES
(1, '20250601001', 2, 1, 1958.00, 1958.00, 'alipay', '2025-06-01 10:30:00', 4, '请尽快发货', '2025-06-03 14:00:00', '2025-06-05 09:00:00'),
(2, '20250601002', 3, 2, 1768.00, 1768.00, 'wechat', '2025-06-01 11:00:00', 4, '', '2025-06-04 10:00:00', '2025-06-06 15:00:00'),
(3, '20250601003', 4, 3, 2522.00, 2522.00, 'alipay', '2025-06-02 09:00:00', 4, '送朋友的礼物', '2025-06-05 16:00:00', '2025-06-07 11:00:00'),
(4, '20250602001', 2, 1,  68.00,  68.00, 'alipay', '2025-06-02 14:00:00', 4, '', '2025-06-05 18:00:00', '2025-06-06 10:00:00'),
(5, '20250603001', 3, 2,  39.00,  39.00, 'wechat', '2025-06-03 08:00:00', 4, '', '2025-06-06 12:00:00', '2025-06-07 08:00:00'),

-- 状态0-待支付（用于测试支付）
(6, '20250628001', 2, 1, 2500.00, 2500.00, '',        NULL,                0, '柯基犬订单', NULL, NULL),

-- 状态1-已支付待发货（管理员可测试发货）
(7, '20250628002', 3, 2, 1600.00, 1600.00, 'alipay', '2025-06-28 14:00:00', 1, '英短蓝猫', NULL, NULL),

-- 状态2-已发货待收货（用户可测试确认收货）
(8, '20250628003', 4, 3,  168.00,  168.00, 'wechat', '2025-06-28 16:00:00', 2, '猫粮急用', NULL, NULL);

-- ==================== 订单项 ====================
-- 订单1: 金毛幼犬 + 狗粮
INSERT INTO t_order_item (order_id, product_id, product_name, product_image, quantity, price, subtotal) VALUES
(1, 1,  '纯种金毛幼犬',        '', 1, 1800.00, 1800.00),
(1, 17, '皇家小型犬成犬粮2kg', '', 1,  158.00,  158.00);

-- 订单2: 英短蓝猫 + 猫粮
INSERT INTO t_order_item (order_id, product_id, product_name, product_image, quantity, price, subtotal) VALUES
(2, 6,  '英短蓝猫',           '', 1, 1600.00, 1600.00),
(2, 20, '皇家室内成猫粮2kg',  '', 1,  168.00,  168.00);

-- 订单3: 柯基犬 + 飞盘
INSERT INTO t_order_item (order_id, product_id, product_name, product_image, quantity, price, subtotal) VALUES
(3, 2,  '柯基犬幼崽', '', 1, 2500.00, 2500.00),
(3, 24, '狗狗飞盘',   '', 1,   22.00,   22.00);

-- 订单4: 狗窝
INSERT INTO t_order_item (order_id, product_id, product_name, product_image, quantity, price, subtotal) VALUES
(4, 25, '四季通用狗窝M号', '', 1, 68.00, 68.00);

-- 订单5: 沐浴露
INSERT INTO t_order_item (order_id, product_id, product_name, product_image, quantity, price, subtotal) VALUES
(5, 29, '宠物专用沐浴露500ml', '', 1, 39.00, 39.00);

-- 订单6: 柯基犬（待支付）
INSERT INTO t_order_item (order_id, product_id, product_name, product_image, quantity, price, subtotal) VALUES
(6, 2, '柯基犬幼崽', '', 1, 2500.00, 2500.00);

-- 订单7: 英短蓝猫（待发货）
INSERT INTO t_order_item (order_id, product_id, product_name, product_image, quantity, price, subtotal) VALUES
(7, 6, '英短蓝猫', '', 1, 1600.00, 1600.00);

-- 订单8: 猫粮（待收货）
INSERT INTO t_order_item (order_id, product_id, product_name, product_image, quantity, price, subtotal) VALUES
(8, 20, '皇家室内成猫粮2kg', '', 1, 168.00, 168.00);

-- 订单8更新物流信息（模拟已发货）
UPDATE t_order SET logistics_company = '顺丰速运', logistics_no = 'SF1234567890', delivery_time = '2025-06-28 18:00:00' WHERE id = 8;
