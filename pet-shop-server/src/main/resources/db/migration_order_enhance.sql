-- =============================================
-- 订单模块增强 数据库迁移
-- 执行前请确保已运行 schema.sql 建表
-- 注意：本脚本仅需执行一次，重复执行会报错
-- =============================================

USE petshop;

-- ==================== 订单表增加字段 ====================
-- 若已执行过，跳过 ALTER（列已存在时会报错，忽略即可）

ALTER TABLE t_order ADD COLUMN cancel_type     VARCHAR(20)  DEFAULT ''    COMMENT '取消类型：USER-用户, SYSTEM-系统, ADMIN-管理员';
ALTER TABLE t_order ADD COLUMN refund_reason   VARCHAR(500) DEFAULT ''    COMMENT '退款原因';
ALTER TABLE t_order ADD COLUMN refund_remark   VARCHAR(500) DEFAULT ''    COMMENT '退款说明';
ALTER TABLE t_order ADD COLUMN refund_money    DECIMAL(10,2) DEFAULT NULL COMMENT '退款金额';
ALTER TABLE t_order ADD COLUMN refund_time     DATETIME      DEFAULT NULL COMMENT '退款时间';
ALTER TABLE t_order ADD COLUMN audit_remark    VARCHAR(500) DEFAULT ''    COMMENT '审核意见';
ALTER TABLE t_order ADD COLUMN previous_status INT           DEFAULT NULL COMMENT '退单前的原状态（审核拒绝后恢复用）';
ALTER TABLE t_order ADD COLUMN comment_time    DATETIME      DEFAULT NULL COMMENT '评价时间';

-- ==================== 订单操作日志表 ====================
CREATE TABLE IF NOT EXISTS t_order_log (
    id            BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    order_id      BIGINT       NOT NULL                    COMMENT '订单ID',
    operator      VARCHAR(50)  NOT NULL                    COMMENT '操作人（用户名/系统）',
    from_status   INT          DEFAULT NULL                COMMENT '原状态',
    to_status     INT          NOT NULL                    COMMENT '新状态',
    remark        VARCHAR(500) DEFAULT ''                  COMMENT '备注',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单操作日志表';
