-- 创建、使用数据库
CREATE DATABASE `lc_predictor`;
USE `lc_predictor`;

-- 创建 LC 用户表
DROP TABLE IF EXISTS `lc_user`;
CREATE TABLE `lc_user`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `data_region` VARCHAR(100) NOT NULL COMMENT '数据区域: CN/US',
    `username`    VARCHAR(255) NOT NULL COMMENT '用户名',
    `nickname`    VARCHAR(255) NOT NULL COMMENT '昵称',
    `avatar`      VARCHAR(255) NOT NULL COMMENT '头像',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_data_region_username` (`data_region`, `username`)
) ENGINE INNODB
  DEFAULT CHARSET UTF8MB4 COMMENT 'LC 用户表';

-- 创建 LC 竞赛表
DROP TABLE IF EXISTS `lc_contest`;
CREATE TABLE `lc_contest`
(
    `id`           BIGINT   NOT NULL AUTO_INCREMENT COMMENT 'id',
    `contest_id`   INT      NOT NULL COMMENT '竞赛编号: 周赛场次 * 2 + 1, 双周赛场次 * 2',
    `start_time`   DATETIME NOT NULL COMMENT '开始时间',
    `predict_time` DATETIME          DEFAULT NULL COMMENT '预测时间',
    `deleted`      TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_contest_id` (`contest_id`)
) ENGINE INNODB
  DEFAULT CHARSET UTF8MB4 COMMENT 'LC 竞赛表';

-- 创建 LC 预测表
DROP TABLE IF EXISTS `lc_predict`;
CREATE TABLE `lc_predict`
(
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `contest_id`     INT          NOT NULL COMMENT '竞赛编号: 周赛场次 * 2 + 1, 双周赛场次 * 2',
    `data_region`    VARCHAR(100) NOT NULL COMMENT '数据区域: CN/US',
    `username`       VARCHAR(255) NOT NULL COMMENT '用户名',
    `ranking`        INT          NOT NULL COMMENT '排名',
    `attended_count` INT                   DEFAULT NULL COMMENT '参赛次数',
    `old_rating`     DOUBLE                DEFAULT NULL COMMENT '当前评分',
    `new_rating`     DOUBLE                DEFAULT NULL COMMENT '预测评分',
    `deleted`        TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_contest_id_data_region_username` (`contest_id`, `data_region`, `username`),
    UNIQUE KEY `uk_contest_id_ranking` (`contest_id`, `ranking`)
) ENGINE INNODB
  DEFAULT CHARSET UTF8MB4 COMMENT 'LC 预测表';

-- 创建 LC 关注表
DROP TABLE IF EXISTS `lc_follow`;
CREATE TABLE `lc_follow`
(
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `source_data_region` VARCHAR(100) NOT NULL COMMENT '数据区域: CN/US',
    `source_username`    VARCHAR(255) NOT NULL COMMENT '用户名',
    `target_data_region` VARCHAR(100) NOT NULL COMMENT '数据区域: CN/US',
    `target_username`    VARCHAR(255) NOT NULL COMMENT '用户名',
    `deleted`            TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    INDEX `idx_source_data_region_source_username` (`source_data_region`, `source_username`)
) ENGINE INNODB
  DEFAULT CHARSET UTF8MB4 COMMENT 'LC 关注表';
