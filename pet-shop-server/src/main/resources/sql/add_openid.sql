-- 为 t_user 表添加微信小程序 openid 字段
ALTER TABLE t_user ADD COLUMN openid VARCHAR(100) DEFAULT NULL COMMENT '微信小程序openid';
ALTER TABLE t_user ADD UNIQUE INDEX idx_openid (openid);
