-- 就诊通知表添加新字段
ALTER TABLE `jiuzhentongzhi` 
ADD COLUMN `songzhuangtai` INT DEFAULT 0 COMMENT '发送状态（0：待发送，1：已发送，2：发送失败，3：发送失败（已达到最大重试次数））',
ADD COLUMN `chongshicishu` INT DEFAULT 0 COMMENT '重试次数',
ADD COLUMN `zuihoufasongshijian` DATETIME DEFAULT NULL COMMENT '最后发送时间',
ADD COLUMN `tongzhileixing` INT DEFAULT 0 COMMENT '通知类型（0：预约成功提醒，1：就诊前1天提醒，2：就诊前1小时提醒）';

-- 创建通知发送记录表
DROP TABLE IF EXISTS `tongzhijilu`;
CREATE TABLE `tongzhijilu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tongzhiid` bigint(20) DEFAULT NULL COMMENT '通知ID',
  `zhanghao` varchar(200) DEFAULT NULL COMMENT '账号',
  `shouji` varchar(200) DEFAULT NULL COMMENT '手机',
  `tongzhineirong` longtext COMMENT '通知内容',
  `fasongjieguo` varchar(200) DEFAULT NULL COMMENT '发送结果',
  `cuowuxinxi` longtext COMMENT '错误信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知发送记录表';
