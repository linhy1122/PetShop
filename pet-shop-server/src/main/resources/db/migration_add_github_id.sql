-- =============================================
-- GitHub OAuth 登录 - 数据库迁移
-- 为 t_user 表添加 github_id 字段
-- =============================================

ALTER TABLE t_user
    ADD COLUMN github_id BIGINT DEFAULT NULL COMMENT 'GitHub用户ID' AFTER status;

CREATE UNIQUE INDEX idx_github_id ON t_user (github_id);
