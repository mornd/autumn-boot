-- 创建数据库SQL,需要一并创建数据库就解开下面3行脚本
-- drop database if exists `db_autumn`;
-- CREATE DATABASE `db_autumn`;
-- use `db_autumn`;

/*
 Navicat Premium Data Transfer

 Source Server         : local_3306
 Source Server Type    : MySQL
 Source Server Version : 80019
 Source Host           : localhost:3306
 Source Schema         : db_autumn

 Target Server Type    : MySQL
 Target Server Version : 80019
 File Encoding         : 65001

 Date: 03/03/2023 23:45:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat_message
-- ----------------------------

-- ----------------------------
-- Table structure for chat_record
-- ----------------------------
DROP TABLE IF EXISTS `chat_record`;
CREATE TABLE `chat_record`  (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `from_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发件人',
                                `to_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收件人',
                                `create_time` datetime NOT NULL COMMENT '时间',
                                `from_read` tinyint NULL DEFAULT 0 COMMENT '发件人是否读取( 0：未读1：已读)',
                                `to_read` tinyint NULL DEFAULT 0 COMMENT '收件人是否读取',
                                `from_deleted` tinyint NULL DEFAULT 0 COMMENT '发件人是否删除该条消息(0：未删1：已删)',
                                `to_deleted` tinyint NULL DEFAULT 0 COMMENT '收件人是否删除该条消息',
                                `message_id` bigint NOT NULL COMMENT '消息id',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `idx_from`(`from_key` ASC) USING BTREE,
                                INDEX `idx_to`(`to_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat_record
-- ----------------------------

-- ----------------------------
-- Table structure for mail_log
-- ----------------------------
DROP TABLE IF EXISTS `mail_log`;
CREATE TABLE `mail_log`  (
                             `msg_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息id',
                             `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接收者id',
                             `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态(0：消息投递中，1：投递成功，2：发送到交换机失败，3：发送到队列失败，4：消费消息时发生异常失败)',
                             `exchange` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交换机',
                             `routing_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由键',
                             `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误消息',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`msg_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮件发送队列记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mail_log
-- ----------------------------

-- ----------------------------
-- Table structure for oa_process
-- ----------------------------
DROP TABLE IF EXISTS `oa_process`;
CREATE TABLE `oa_process`  (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                               `process_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '审批code',
                               `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
                               `user_id` varbinary(255) NOT NULL COMMENT '用户id',
                               `status` tinyint NULL DEFAULT NULL COMMENT '状态(0:默认1:审批中2:审批通过-1:驳回)',
                               `process_type_id` bigint NULL DEFAULT NULL COMMENT '审批类型id',
                               `process_template_id` bigint NULL DEFAULT NULL COMMENT '审批模板id',
                               `form_values` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '表单值',
                               `process_instance_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '流程实例id',
                               `current_auditor_id` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '当前审批人id(逗号分隔)',
                               `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                               `create_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                               `create_time` datetime NULL DEFAULT NULL,
                               `update_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                               `update_time` datetime NULL DEFAULT NULL,
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审批流程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oa_process
-- ----------------------------

-- ----------------------------
-- Table structure for oa_process_record
-- ----------------------------
DROP TABLE IF EXISTS `oa_process_record`;
CREATE TABLE `oa_process_record`  (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `process_id` bigint NOT NULL COMMENT '流程id',
                                      `status` tinyint NULL DEFAULT NULL COMMENT '状态',
                                      `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                                      `operate_user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人id',
                                      `operate_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人真实姓名',
                                      `create_time` datetime NULL DEFAULT NULL,
                                      `create_id` varbinary(255) NULL DEFAULT NULL,
                                      `update_time` datetime NULL DEFAULT NULL,
                                      `update_id` varbinary(255) NULL DEFAULT NULL,
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oa_process_record
-- ----------------------------

-- ----------------------------
-- Table structure for oa_process_template
-- ----------------------------
DROP TABLE IF EXISTS `oa_process_template`;
CREATE TABLE `oa_process_template`  (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                        `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板名称',
                                        `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标路径',
                                        `process_type_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '流程类型id',
                                        `form_props` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '动态表单属性',
                                        `form_options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '动态表单选项',
                                        `process_definition_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '流程定义key',
                                        `process_definition_file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '流程定义文件路径',
                                        `process_model_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '流程定义模型id',
                                        `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                                        `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态(0:未发布1: 已发布)',
                                        `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                        `create_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                        `update_time` datetime NULL DEFAULT NULL,
                                        `update_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '流程模板表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oa_process_template
-- ----------------------------

-- ----------------------------
-- Table structure for oa_process_type
-- ----------------------------
DROP TABLE IF EXISTS `oa_process_type`;
CREATE TABLE `oa_process_type`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT ' 类型名称',
                                    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                                    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `create_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                    `update_time` datetime NULL DEFAULT NULL,
                                    `update_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '流程类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oa_process_type
-- ----------------------------
INSERT INTO `oa_process_type` VALUES (20, '出勤', '', '2023-03-26 00:00:00', '1425004256210038785', '2023-03-26 00:00:00', '1425004256210038785');
INSERT INTO `oa_process_type` VALUES (21, '人事', '人事类型', '2023-03-26 00:00:00', '1425004256210038785', '2023-04-16 13:51:18', '1425004256210038785');
INSERT INTO `oa_process_type` VALUES (22, '财务', '', '2023-03-26 00:00:00', '1425004256210038785', '2023-03-27 00:00:00', '1425004256210038785');
INSERT INTO `oa_process_type` VALUES (27, '测试', '', '2023-04-05 15:29:07', '1425004256210038785', NULL, NULL);

-- ----------------------------
-- Table structure for oa_wechat_menu
-- ----------------------------
DROP TABLE IF EXISTS `oa_wechat_menu`;
CREATE TABLE `oa_wechat_menu`  (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `parent_id` bigint NOT NULL COMMENT '上级id',
                                   `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
                                   `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单类型',
                                   `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '网页链接，用户点击菜单可打开链接',
                                   `menu_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单key值，用于消息接口推送',
                                   `sort` tinyint NULL DEFAULT NULL COMMENT '排序',
                                   `create_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                   `create_time` datetime NULL DEFAULT NULL,
                                   `update_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                   `update_time` datetime NULL DEFAULT NULL,
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oa_wechat_menu
-- ----------------------------
INSERT INTO `oa_wechat_menu` VALUES (1, 0, '审批列表', NULL, NULL, NULL, 1, NULL, '2023-04-06 20:11:34', '1425004256210038785', '2023-04-08 17:44:23');
INSERT INTO `oa_wechat_menu` VALUES (2, 0, '审批中心', 'view', '/', NULL, 2, NULL, '2023-04-06 20:11:34', '1425004256210038785', '2023-04-08 17:44:27');
INSERT INTO `oa_wechat_menu` VALUES (3, 0, '我的', '', NULL, NULL, 3, NULL, '2023-04-06 20:11:34', '1425004256210038785', '2023-04-08 17:44:34');
INSERT INTO `oa_wechat_menu` VALUES (4, 1, '待处理', 'view', '/list/0', NULL, 1, NULL, '2023-04-06 20:11:34', NULL, NULL);
INSERT INTO `oa_wechat_menu` VALUES (5, 1, '已处理', 'view', '/list/1', NULL, 2, NULL, '2023-04-06 20:11:34', NULL, NULL);
INSERT INTO `oa_wechat_menu` VALUES (6, 1, '已发起', 'view', '/list/2', NULL, 3, NULL, '2023-04-06 20:11:34', NULL, NULL);
INSERT INTO `oa_wechat_menu` VALUES (7, 3, '基本信息', 'view', '/user', NULL, 1, NULL, '2023-04-06 20:11:34', NULL, NULL);
INSERT INTO `oa_wechat_menu` VALUES (8, 3, '关于我', 'view', '/about', NULL, 2, NULL, '2023-04-06 20:11:34', '1425004256210038785', '2023-04-12 21:35:01');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
                            `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT ' 主题',
                            `username` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录名',
                            `real_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
                            `method_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '方法名',
                            `visit_date` datetime NOT NULL COMMENT '访问时间',
                            `execution_time` bigint NULL DEFAULT NULL COMMENT '操作时长',
                            `type` tinyint NULL DEFAULT NULL COMMENT '日志类型(1:查询2:新增3:修改4:删除5:其他6:下载7:上传8:清空9:发布10:审批11:同步12:授权)',
                            `params` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '访问参数',
                            `status` tinyint NULL DEFAULT NULL COMMENT '状态(0:访问成功1:访问失败)',
                            `ip` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '访问ip',
                            `url` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '访问url',
                            `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '真实地址',
                            `os` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作系统',
                            `browser` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '浏览器',
                            `result` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '执行结果',
                            `exception_msg` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '异常信息'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `user_id` varbinary(255) NULL DEFAULT NULL COMMENT '用户id',
                                   `login_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户登录名',
                                   `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态(0：成功，1：失败) ',
                                   `type` tinyint NULL DEFAULT NULL COMMENT '登录方式(0：账号密码1：短信2：gitee3：微信)',
                                   `ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ip',
                                   `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实地址',
                                   `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器',
                                   `os` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作系统',
                                   `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '信息',
                                   `login_time` datetime NULL DEFAULT NULL COMMENT '登录时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
                                   `id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
                                   `parent_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '父id',
                                   `title` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单标题',
                                   `code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限编码',
                                   `path` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '前端路由对象path值',
                                   `component` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '前端路由对象component组件路径(无需加’/‘)',
                                   `icon` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
                                   `sort` double NOT NULL COMMENT '菜单排序',
                                   `keep_alive` tinyint(1) NULL DEFAULT NULL COMMENT '是否保持激活(0：否 1：是)',
                                   `require_auth` tinyint(1) NULL DEFAULT 1 COMMENT '是否要求权限(0:否 1:是)',
                                   `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用(1：启用0：禁用)',
                                   `hidden` tinyint(1) NULL DEFAULT 1 COMMENT '是否隐藏菜单(0：隐藏，1：显示)',
                                   `menu_type` tinyint(1) NULL DEFAULT NULL COMMENT '菜单类型(0:一级菜单;1:子菜单;2:按钮权限)',
                                   `gmt_create` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   `create_by` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                   `gmt_modified` datetime NULL DEFAULT NULL COMMENT '修改时间',
                                   `modified_by` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
                                   `del_flag` tinyint(1) NULL DEFAULT 1 COMMENT '逻辑删除',
                                   `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '前端路由对象name值(已用title字段替代)',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES ('1425381271220101122', '0', '系统管理', 'system', '', NULL, 'fa fa-gear', 1, NULL, 1, 1, 1, 0, '2021-08-11 17:20:45', NULL, '2023-03-22 21:45:01', '1425004256210038785', 1, '系统管理');
INSERT INTO `sys_permission` VALUES ('1425384255047946241', '1425381271220101122', '用户管理', 'system:user', '/system/user', 'system/user/UserList', 'fa fa-user', 1, 1, 1, 1, 1, 1, '2021-08-11 00:00:00', NULL, '2022-10-16 19:00:58', '1425004256210038785', 1, '用户管理');
INSERT INTO `sys_permission` VALUES ('1425384412992856065', '1425381271220101122', '角色管理', 'system:role', '/system/role', 'system/role/RoleList', 'fa fa-user-secret', 2, 1, 1, 1, 1, 1, '2022-01-05 00:00:00', NULL, '2022-10-20 23:36:59', '1425004256210038785', 1, '角色管理');
INSERT INTO `sys_permission` VALUES ('1425384413584252930', '1425381271220101122', '菜单管理', 'system:menu', '/system/permission', 'system/permission/PermissionList', 'el-icon-menu', 3, 1, 1, 1, 1, 1, '2022-01-05 00:00:00', NULL, '2022-12-31 22:06:25', '1425004256210038785', 1, '菜单管理');
INSERT INTO `sys_permission` VALUES ('1461990613021151233', '0', '系统监控', 'systemMonitor', '', NULL, 'fa fa-tv', 2, 1, 1, 1, 1, 0, '2021-11-20 17:31:38', '1425004256210038785', '2023-03-25 22:47:20', '1425004256210038785', 1, '系统监控');
INSERT INTO `sys_permission` VALUES ('1462047178541559809', '1425384413584252930', '新增菜单', 'system:menu:add', '', NULL, NULL, 2, NULL, 1, 1, 1, 2, '2021-11-20 21:16:26', '1425004256210038785', '2022-10-16 19:04:56', '1425004256210038785', 1, '新增菜单');
INSERT INTO `sys_permission` VALUES ('1463297492196085761', '1461990613021151233', 'driud监控', 'duridMonitoring', 'monitor/druid', 'monitor/druid/index', 'fa fa-database', 4, 1, 1, 1, 1, 1, '2021-11-24 00:00:00', '1425004256210038785', '2022-11-06 19:44:37', '1425004256210038785', 1, 'driud监控');
INSERT INTO `sys_permission` VALUES ('1486984444607004673', '1461990613021151233', '操作日志', 'systemMonitor:sysLog', 'monitor/log', 'monitor/log/LogList', 'fa fa-camera', 1, 1, 1, 1, 1, 1, '2022-01-28 00:00:00', '1425004256210038785', '2023-03-31 21:54:14', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581601424831889410', '1425384413584252930', '修改菜单', 'system:menu:update', NULL, NULL, NULL, 3, NULL, 1, 1, 1, 2, '2022-10-16 19:02:19', '1425004256210038785', '2022-10-16 19:05:09', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581601696417267713', '1425384413584252930', '删除菜单', 'system:menu:delete', NULL, NULL, NULL, 4, NULL, 1, 1, 1, 2, '2022-10-16 19:03:24', '1425004256210038785', '2022-10-16 19:05:16', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581602868477771778', '1425384255047946241', '新增用户', 'system:user:add', NULL, NULL, NULL, 2, NULL, 1, 1, 1, 2, '2022-10-16 19:08:03', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581602962446958593', '1425384255047946241', '修改用户', 'system:user:update', NULL, NULL, NULL, 3, NULL, 1, 1, 1, 2, '2022-10-16 19:08:26', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581603052624494594', '1425384255047946241', '删除用户', 'system:user:delete', NULL, NULL, NULL, 4, NULL, 1, 1, 1, 2, '2022-10-16 19:08:47', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581603401271820290', '1425384412992856065', '角色添加', 'system:role:add', NULL, NULL, NULL, 2, NULL, 1, 1, 1, 2, '2022-10-16 19:10:10', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581603490417557506', '1425384412992856065', '角色修改', 'system:role:update', NULL, NULL, NULL, 3, NULL, 1, 1, 1, 2, '2022-10-16 19:10:32', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581603638262579201', '1425384412992856065', '角色删除', 'system:role:delete', NULL, NULL, NULL, 4, NULL, 1, 1, 1, 2, '2022-10-16 19:11:07', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581607834781171714', '1486984444607004673', '清空操作日志表', 'systemMonitor:sysLog:clear', NULL, NULL, NULL, 1, NULL, 1, 1, 1, 2, '2022-10-16 19:27:47', '1425004256210038785', '2022-11-06 20:32:02', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581621978532102146', '1425384255047946241', '用户状态修改', 'system:user:changeStatus', NULL, NULL, NULL, 5, NULL, 1, 1, 1, 2, '2022-10-16 20:23:59', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1582756101220888578', '1461990613021151233', '在线用户', 'onlineUser', 'monitor/online', 'monitor/online/OnlineList', 'fa fa-group', 3, 1, 1, 1, 1, 1, '2022-10-19 00:00:00', '1425004256210038785', '2022-11-06 19:44:15', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1583049737108144129', '1582756101220888578', '强退在线用户', 'onlineUser:kick', NULL, NULL, NULL, 1, NULL, 1, 1, 1, 2, '2022-10-20 18:57:24', '1', '2022-10-20 23:40:06', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1586279773806186498', '1461990613021151233', '服务监控', 'systemMonitor:server', 'monitor/server', 'monitor/server/index', 'fa fa-heartbeat', 5, 1, 1, 1, 1, 1, '2022-10-29 00:00:00', '1425004256210038785', '2022-12-20 22:11:11', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1589222073322692610', '1461990613021151233', '登录日志', 'systemMonitor:sysLoginInfor', 'monitor/loginInfor', 'monitor/loginInfor/index', 'fa fa-sign-in', 1.1, 1, 1, 1, 1, 1, '2022-11-06 00:00:00', '1425004256210038785', '2023-03-31 21:54:46', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1589233921480712193', '1589222073322692610', '清空登录日志表', 'systemMonitor:sysLoginInfor:truncate', NULL, NULL, NULL, 1, NULL, 1, 1, 1, 2, '2022-11-06 20:31:08', '1425004256210038785', '2022-11-06 20:31:48', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1637813004071940097', '0', '统计分析', 'statistics', NULL, NULL, 'fa fa-pie-chart', 20, NULL, 1, 1, 1, 0, '2023-03-20 21:47:04', '1425004256210038785', '2023-03-20 21:52:19', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1637813578733531137', '1637813004071940097', '图表模板', 'statistics:chart', '/statistics/chart', '/statistics/chart', 'fa fa-area-chart', 5, 1, 1, 1, 1, 1, '2023-03-20 00:00:00', '1425004256210038785', '2023-03-22 20:53:03', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1639639714044121089', '0', '审批设置', 'processSettings', NULL, NULL, 'fa fa-hand-pointer-o', 3, NULL, 1, 1, 1, 0, '2023-03-25 22:45:45', '1425004256210038785', '2023-03-25 22:49:40', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1639639987563073538', '0', '审批管理', 'processManagement', NULL, NULL, 'fa fa-sitemap', 4, NULL, 1, 1, 1, 0, '2023-03-25 22:46:51', '1425004256210038785', '2023-03-25 22:49:55', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1639640297413087233', '1639639714044121089', '审批类型', 'processType', 'processSettings/processType', 'processSettings/processType', 'fa fa-list-ul', 1, 1, 1, 1, 1, 1, '2023-03-25 00:00:00', '1425004256210038785', '2023-03-25 22:49:29', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1639919258760118274', '1639639714044121089', '审批模板', 'processTemplate', 'processSettings/processTemplate', 'processSettings/processTemplate', 'fa fa-paste', 2, 1, 1, 1, 1, 1, '2023-03-26 17:16:34', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1641455943276900354', '1639639987563073538', '审批列表', 'processList', 'processManagement/processList', 'processManagement/processList', 'fa fa-list', 1, 1, 1, 1, 1, 1, '2023-03-30 23:02:48', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1643982576965693441', '1639639714044121089', '公众号菜单', 'prcesswechatmenu', '/processSettings/wechatmenu', '/processSettings/wechatmenu', 'fa fa-wechat', 3, 1, 1, 1, 1, 1, '2023-04-06 22:22:45', '1425004256210038785', NULL, NULL, 1, NULL);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
                             `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
                             `code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色码',
                             `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色名称',
                             `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用0：禁用1：启用',
                             `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                             `gmt_create` datetime NULL DEFAULT NULL,
                             `create_by` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `gmt_modified` datetime NULL DEFAULT NULL,
                             `modified_by` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `del_flag` tinyint(1) NULL DEFAULT 1,
                             `version` bigint NULL DEFAULT NULL,
                             `sort` int NOT NULL COMMENT '排序',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', 'user', '用户', 1, NULL, '2022-01-05 00:00:00', NULL, NULL, NULL, 1, NULL, 4);
INSERT INTO `sys_role` VALUES ('1425011630752735234', 'super_admin', '超级管理员', 1, 'superAdmin！！！', '2022-01-05 00:00:00', NULL, '2022-01-11 08:58:46', '1425004256210038785', 1, NULL, 1);
INSERT INTO `sys_role` VALUES ('1479338174086205442', 'admin', '管理员', 1, NULL, '2022-01-07 00:00:00', '1425004256210038785', '2022-05-03 02:24:25', '1425004256210038785', 1, NULL, 2);
INSERT INTO `sys_role` VALUES ('1484006672313884673', 'guest', '游客', 1, NULL, '2022-01-04 00:00:00', '1425004256210038785', '2022-01-20 16:56:13', '1425004256210038785', 1, NULL, 4);
INSERT INTO `sys_role` VALUES ('1584907057962328066', 'gitee', 'gitee用户', 1, 'gitee用户访问默认角色', '2022-10-25 00:00:00', '7853024', '2023-03-25 13:31:55', '1425004256210038785', 1, NULL, 5);
INSERT INTO `sys_role` VALUES ('1642066476367622146', '测试', 'test', 1, NULL, '2023-04-01 15:28:51', '1638915764242632705', NULL, NULL, 1, NULL, 5);

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
                                        `role_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                        `per_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                        `gmt_create` datetime NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1425384413584252930', '2022-01-13 08:13:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1461990613021151233', '2022-01-13 08:13:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1425381271220101122', '2022-01-13 08:13:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1463297492196085761', '2022-01-13 08:13:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1425384255047946241', '2022-01-13 08:13:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1462047178541559809', '2022-01-13 08:13:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1425384412992856065', '2022-01-13 08:13:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1486984444607004673', '2022-01-28 16:48:13');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581601424831889410', '2022-10-16 19:02:19');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581601696417267713', '2022-10-16 19:03:24');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581602868477771778', '2022-10-16 19:08:03');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581602962446958593', '2022-10-16 19:08:26');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581603052624494594', '2022-10-16 19:08:47');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581603401271820290', '2022-10-16 19:10:10');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581603490417557506', '2022-10-16 19:10:32');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581603638262579201', '2022-10-16 19:11:07');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581607834781171714', '2022-10-16 19:27:47');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581621978532102146', '2022-10-16 20:23:59');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1582756101220888578', '2022-10-19 23:30:35');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1583049737108144129', '2022-10-20 18:57:24');
INSERT INTO `sys_role_permission` VALUES ('1', '1425381271220101122', '2022-10-20 23:52:19');
INSERT INTO `sys_role_permission` VALUES ('1', '1425384255047946241', '2022-10-20 23:52:19');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1425384413584252930', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1461990613021151233', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1425381271220101122', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1463297492196085761', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1425384255047946241', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1425384412992856065', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1486984444607004673', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1582756101220888578', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1586278812291354625', '2022-10-29 16:48:35');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1586279773806186498', '2022-10-29 16:52:24');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1589222073322692610', '2022-11-06 19:44:03');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1589233921480712193', '2022-11-06 20:31:08');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1637813004071940097', '2023-03-20 21:47:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1637813578733531137', '2023-03-20 21:49:21');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1461990613021151233', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1425381271220101122', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1589222073322692610', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1425384255047946241', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581602868477771778', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1425384412992856065', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1582756101220888578', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1637813578733531137', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1462047178541559809', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581603401271820290', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1589233921480712193', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581607834781171714', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1586279773806186498', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581603052624494594', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1425384413584252930', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581601696417267713', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581601424831889410', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1583049737108144129', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581602962446958593', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1463297492196085761', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581603638262579201', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1637813004071940097', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581621978532102146', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1486984444607004673', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581603490417557506', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1639639714044121089', '2023-03-25 22:45:46');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1639639987563073538', '2023-03-25 22:46:51');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1639640297413087233', '2023-03-25 22:48:05');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1639919258760118274', '2023-03-26 17:16:34');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1641455943276900354', '2023-03-30 23:02:48');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1643982576965693441', '2023-04-06 22:22:45');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1461990613021151233', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1643982576965693441', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1425381271220101122', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1589222073322692610', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1425384255047946241', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581602868477771778', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1425384412992856065', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1582756101220888578', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1637813578733531137', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1462047178541559809', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1641455943276900354', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581603401271820290', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1589233921480712193', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581607834781171714', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1639640297413087233', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1586279773806186498', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581603052624494594', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1425384413584252930', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581601696417267713', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581601424831889410', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1583049737108144129', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581602962446958593', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1463297492196085761', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581603638262579201', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1639639714044121089', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1637813004071940097', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581621978532102146', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1486984444607004673', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1639919258760118274', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1639639987563073538', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581603490417557506', '2023-04-13 01:11:14');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
                             `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id',
                             `login_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录名称',
                             `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
                             `real_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
                             `gender` tinyint(1) NULL DEFAULT NULL COMMENT '性别0：女1：男',
                             `birthday` date NULL DEFAULT NULL COMMENT '生日',
                             `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话',
                             `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态0：禁用1：正常',
                             `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
                             `email` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
                             `gmt_create` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `create_by` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                             `gmt_modified` datetime NULL DEFAULT NULL COMMENT '修改时间',
                             `modified_by` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
                             `del_flag` tinyint(1) NULL DEFAULT 1 COMMENT '逻辑删除0：删除1：正常',
                             `version` bigint NULL DEFAULT 1 COMMENT '版本号',
                             `account_locked` tinyint NULL DEFAULT 1 COMMENT '账号是否被锁定0：锁定1：正常',
                             `open_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信openid',
                             `source` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '登录方式(0：系统用户)',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `login_name_unique`(`login_name` ASC) USING BTREE,
                             INDEX `idx_name`(`login_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1425004256210038785', 'tom', '$2a$10$YNXMehner5tmZlC4adQA3.aAelN6/9QRxM6IOK5KR/9kMKCWChkUK', '超级管理员', 0, '2000-04-01', NULL, 1, '/profile/avatar/93dc8a10-0c32-494d-9ef4-f1c458e03965.jpg', NULL, '2021-08-10 00:00:00', NULL, '2023-04-10 21:32:57', '1425004256210038785', 1, NULL, 1, NULL, '0');
INSERT INTO `sys_user` VALUES ('1646198846768410625', 'alice', '$2a$10$fy3O2x5R4SD/Sa2xepkrCuWy2sfA0WBRvGoeIuZG4915wxc6wFvHm', '爱丽丝', 0, '2000-01-01', NULL, 1, '/profile/avatar/96070403-e628-4b27-a395-cd5794e2b709.jpg', NULL, '2023-04-13 00:00:00', '1425004256210038785', '2023-04-13 01:09:39', '1425004256210038785', 1, 1, 1, NULL, '0');
INSERT INTO `sys_user` VALUES ('1646198878523486209', 'bob', '$2a$10$tKHwRxpHQ8CQcvtg.vlbQ.i4wYSP5lJu8UHhL0RhERnyiosECxtf2', '鲍勃', 1, NULL, NULL, 1, NULL, NULL, '2023-04-13 00:00:00', '1425004256210038785', '2023-04-15 19:58:19', '1425004256210038785', 1, 1, 1, NULL, '0');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
                                  `user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                  `role_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                  `gmt_create` datetime NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('1646198846768410625', '1479338174086205442', '2023-04-13 01:09:39');
INSERT INTO `sys_user_role` VALUES ('1425004256210038785', '1425011630752735234', '2023-04-13 08:41:47');
INSERT INTO `sys_user_role` VALUES ('1425004256210038785', '1484006672313884673', '2023-04-13 08:41:47');
INSERT INTO `sys_user_role` VALUES ('1425004256210038785', '1584907057962328066', '2023-04-13 08:41:47');
INSERT INTO `sys_user_role` VALUES ('1646496062062063618', '1479338174086205442', '2023-04-13 20:50:26');
INSERT INTO `sys_user_role` VALUES ('1646496172292567041', '1479338174086205442', '2023-04-13 20:50:53');
INSERT INTO `sys_user_role` VALUES ('1646198878523486209', '1479338174086205442', '2023-04-15 19:58:20');

SET FOREIGN_KEY_CHECKS = 1;
-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat_message
-- ----------------------------

-- ----------------------------
-- Table structure for chat_record
-- ----------------------------
DROP TABLE IF EXISTS `chat_record`;
CREATE TABLE `chat_record`  (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `from_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发件人',
                                `to_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收件人',
                                `create_time` datetime NOT NULL COMMENT '时间',
                                `from_read` tinyint NULL DEFAULT 0 COMMENT '发件人是否读取( 0：未读1：已读)',
                                `to_read` tinyint NULL DEFAULT 0 COMMENT '收件人是否读取',
                                `from_deleted` tinyint NULL DEFAULT 0 COMMENT '发件人是否删除该条消息(0：未删1：已删)',
                                `to_deleted` tinyint NULL DEFAULT 0 COMMENT '收件人是否删除该条消息',
                                `message_id` bigint NOT NULL COMMENT '消息id',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `idx_from`(`from_key` ASC) USING BTREE,
                                INDEX `idx_to`(`to_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat_record
-- ----------------------------

-- ----------------------------
-- Table structure for mail_log
-- ----------------------------
DROP TABLE IF EXISTS `mail_log`;
CREATE TABLE `mail_log`  (
                             `msg_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息id',
                             `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接收者id',
                             `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态(0：消息投递中，1：投递成功，2：发送到交换机失败，3：发送到队列失败，4：消费消息时发生异常失败)',
                             `exchange` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交换机',
                             `routing_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由键',
                             `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误消息',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`msg_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮件发送队列记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mail_log
-- ----------------------------

-- ----------------------------
-- Table structure for oa_process
-- ----------------------------
DROP TABLE IF EXISTS `oa_process`;
CREATE TABLE `oa_process`  (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                               `process_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '审批code',
                               `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
                               `user_id` varbinary(255) NOT NULL COMMENT '用户id',
                               `status` tinyint NULL DEFAULT NULL COMMENT '状态(0:默认1:审批中2:审批通过-1:驳回)',
                               `process_type_id` bigint NULL DEFAULT NULL COMMENT '审批类型id',
                               `process_template_id` bigint NULL DEFAULT NULL COMMENT '审批模板id',
                               `form_values` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '表单值',
                               `process_instance_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '流程实例id',
                               `current_auditor_id` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '当前审批人id(逗号分隔)',
                               `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                               `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝理由',
                               `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间（流程结束时间）',
                               `create_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                               `create_time` datetime NULL DEFAULT NULL,
                               `update_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                               `update_time` datetime NULL DEFAULT NULL,
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审批流程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oa_process
-- ----------------------------

-- ----------------------------
-- Table structure for oa_process_record
-- ----------------------------
DROP TABLE IF EXISTS `oa_process_record`;
CREATE TABLE `oa_process_record`  (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `process_id` bigint NOT NULL COMMENT '流程id',
                                      `status` tinyint NULL DEFAULT NULL COMMENT '状态',
                                      `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                                      `operate_user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人id',
                                      `operate_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人真实姓名',
                                      `create_time` datetime NULL DEFAULT NULL,
                                      `create_id` varbinary(255) NULL DEFAULT NULL,
                                      `update_time` datetime NULL DEFAULT NULL,
                                      `update_id` varbinary(255) NULL DEFAULT NULL,
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oa_process_record
-- ----------------------------

-- ----------------------------
-- Table structure for oa_process_template
-- ----------------------------
DROP TABLE IF EXISTS `oa_process_template`;
CREATE TABLE `oa_process_template`  (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                        `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板名称',
                                        `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标路径',
                                        `process_type_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '流程类型id',
                                        `form_props` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '动态表单属性',
                                        `form_options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '动态表单选项',
                                        `process_definition_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '流程定义key',
                                        `process_definition_file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '流程定义文件路径',
                                        `process_model_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '流程定义模型id',
                                        `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                                        `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态(0:未发布1: 已发布)',
                                        `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                        `create_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                        `update_time` datetime NULL DEFAULT NULL,
                                        `update_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '流程模板表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oa_process_template
-- ----------------------------

-- ----------------------------
-- Table structure for oa_process_type
-- ----------------------------
DROP TABLE IF EXISTS `oa_process_type`;
CREATE TABLE `oa_process_type`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT ' 类型名称',
                                    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                                    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `create_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                    `update_time` datetime NULL DEFAULT NULL,
                                    `update_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '流程类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oa_process_type
-- ----------------------------
INSERT INTO `oa_process_type` VALUES (20, '出勤', '', '2023-03-26 00:00:00', '1425004256210038785', '2023-03-26 00:00:00', '1425004256210038785');
INSERT INTO `oa_process_type` VALUES (21, '人事', '人事类型', '2023-03-26 00:00:00', '1425004256210038785', '2023-04-16 13:51:18', '1425004256210038785');
INSERT INTO `oa_process_type` VALUES (22, '财务', '', '2023-03-26 00:00:00', '1425004256210038785', '2023-03-27 00:00:00', '1425004256210038785');
INSERT INTO `oa_process_type` VALUES (27, '测试', '', '2023-04-05 15:29:07', '1425004256210038785', NULL, NULL);

-- ----------------------------
-- Table structure for oa_wechat_menu
-- ----------------------------
DROP TABLE IF EXISTS `oa_wechat_menu`;
CREATE TABLE `oa_wechat_menu`  (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `parent_id` bigint NOT NULL COMMENT '上级id',
                                   `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
                                   `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单类型',
                                   `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '网页链接，用户点击菜单可打开链接',
                                   `menu_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单key值，用于消息接口推送',
                                   `sort` tinyint NULL DEFAULT NULL COMMENT '排序',
                                   `create_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                   `create_time` datetime NULL DEFAULT NULL,
                                   `update_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                   `update_time` datetime NULL DEFAULT NULL,
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oa_wechat_menu
-- ----------------------------
INSERT INTO `oa_wechat_menu` VALUES (1, 0, '审批列表', NULL, NULL, NULL, 1, NULL, '2023-04-06 20:11:34', '1425004256210038785', '2023-04-08 17:44:23');
INSERT INTO `oa_wechat_menu` VALUES (2, 0, '审批中心', 'view', '/', NULL, 2, NULL, '2023-04-06 20:11:34', '1425004256210038785', '2023-04-08 17:44:27');
INSERT INTO `oa_wechat_menu` VALUES (3, 0, '我的', '', NULL, NULL, 3, NULL, '2023-04-06 20:11:34', '1425004256210038785', '2023-04-08 17:44:34');
INSERT INTO `oa_wechat_menu` VALUES (4, 1, '待处理', 'view', '/list/0', NULL, 1, NULL, '2023-04-06 20:11:34', NULL, NULL);
INSERT INTO `oa_wechat_menu` VALUES (5, 1, '已处理', 'view', '/list/1', NULL, 2, NULL, '2023-04-06 20:11:34', NULL, NULL);
INSERT INTO `oa_wechat_menu` VALUES (6, 1, '已发起', 'view', '/list/2', NULL, 3, NULL, '2023-04-06 20:11:34', NULL, NULL);
INSERT INTO `oa_wechat_menu` VALUES (7, 3, '基本信息', 'view', '/user', NULL, 1, NULL, '2023-04-06 20:11:34', NULL, NULL);
INSERT INTO `oa_wechat_menu` VALUES (8, 3, '关于我', 'view', '/about', NULL, 2, NULL, '2023-04-06 20:11:34', '1425004256210038785', '2023-04-12 21:35:01');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
                            `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT ' 主题',
                            `username` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录名',
                            `real_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
                            `method_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '方法名',
                            `visit_date` datetime NOT NULL COMMENT '访问时间',
                            `execution_time` bigint NULL DEFAULT NULL COMMENT '操作时长',
                            `type` tinyint NULL DEFAULT NULL COMMENT '日志类型(1:查询2:新增3:修改4:删除5:其他6:下载7:上传8:清空9:发布10:审批11:同步12:授权)',
                            `params` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '访问参数',
                            `status` tinyint NULL DEFAULT NULL COMMENT '状态(0:访问成功1:访问失败)',
                            `ip` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '访问ip',
                            `url` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '访问url',
                            `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '真实地址',
                            `os` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作系统',
                            `browser` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '浏览器',
                            `result` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '执行结果',
                            `exception_msg` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '异常信息'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `user_id` varbinary(255) NULL DEFAULT NULL COMMENT '用户id',
                                   `login_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户登录名',
                                   `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态(0：成功，1：失败) ',
                                   `type` tinyint NULL DEFAULT NULL COMMENT '登录方式(0：账号密码，1：短信2：gitee3：微信)',
                                   `ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ip',
                                   `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实地址',
                                   `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器',
                                   `os` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作系统',
                                   `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '信息',
                                   `login_time` datetime NULL DEFAULT NULL COMMENT '登录时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
                                   `id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
                                   `parent_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '父id',
                                   `title` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单标题',
                                   `code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限编码',
                                   `path` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '前端路由对象path值',
                                   `component` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '前端路由对象component组件路径(无需加’/‘)',
                                   `icon` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
                                   `sort` double NOT NULL COMMENT '菜单排序',
                                   `keep_alive` tinyint(1) NULL DEFAULT NULL COMMENT '是否保持激活(0：否 1：是)',
                                   `require_auth` tinyint(1) NULL DEFAULT 1 COMMENT '是否要求权限(0:否 1:是)',
                                   `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用(1：启用0：禁用)',
                                   `hidden` tinyint(1) NULL DEFAULT 1 COMMENT '是否隐藏菜单(0：隐藏，1：显示)',
                                   `menu_type` tinyint(1) NULL DEFAULT NULL COMMENT '菜单类型(0:一级菜单;1:子菜单;2:按钮权限)',
                                   `gmt_create` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   `create_by` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                   `gmt_modified` datetime NULL DEFAULT NULL COMMENT '修改时间',
                                   `modified_by` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
                                   `del_flag` tinyint(1) NULL DEFAULT 1 COMMENT '逻辑删除',
                                   `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '前端路由对象name值(已用title字段替代)',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES ('1425381271220101122', '0', '系统管理', 'system', '', NULL, 'fa fa-gear', 1, NULL, 1, 1, 1, 0, '2021-08-11 17:20:45', NULL, '2023-03-22 21:45:01', '1425004256210038785', 1, '系统管理');
INSERT INTO `sys_permission` VALUES ('1425384255047946241', '1425381271220101122', '用户管理', 'system:user', '/system/user', 'system/user/UserList', 'fa fa-user', 1, 1, 1, 1, 1, 1, '2021-08-11 00:00:00', NULL, '2022-10-16 19:00:58', '1425004256210038785', 1, '用户管理');
INSERT INTO `sys_permission` VALUES ('1425384412992856065', '1425381271220101122', '角色管理', 'system:role', '/system/role', 'system/role/RoleList', 'fa fa-user-secret', 2, 1, 1, 1, 1, 1, '2022-01-05 00:00:00', NULL, '2022-10-20 23:36:59', '1425004256210038785', 1, '角色管理');
INSERT INTO `sys_permission` VALUES ('1425384413584252930', '1425381271220101122', '菜单管理', 'system:menu', '/system/permission', 'system/permission/PermissionList', 'el-icon-menu', 3, 1, 1, 1, 1, 1, '2022-01-05 00:00:00', NULL, '2022-12-31 22:06:25', '1425004256210038785', 1, '菜单管理');
INSERT INTO `sys_permission` VALUES ('1461990613021151233', '0', '系统监控', 'systemMonitor', '', NULL, 'fa fa-tv', 2, 1, 1, 1, 1, 0, '2021-11-20 17:31:38', '1425004256210038785', '2023-03-25 22:47:20', '1425004256210038785', 1, '系统监控');
INSERT INTO `sys_permission` VALUES ('1462047178541559809', '1425384413584252930', '新增菜单', 'system:menu:add', '', NULL, NULL, 2, NULL, 1, 1, 1, 2, '2021-11-20 21:16:26', '1425004256210038785', '2022-10-16 19:04:56', '1425004256210038785', 1, '新增菜单');
INSERT INTO `sys_permission` VALUES ('1463297492196085761', '1461990613021151233', 'driud监控', 'duridMonitoring', 'monitor/druid', 'monitor/druid/index', 'fa fa-database', 4, 1, 1, 1, 1, 1, '2021-11-24 00:00:00', '1425004256210038785', '2022-11-06 19:44:37', '1425004256210038785', 1, 'driud监控');
INSERT INTO `sys_permission` VALUES ('1486984444607004673', '1461990613021151233', '操作日志', 'systemMonitor:sysLog', 'monitor/log', 'monitor/log/LogList', 'fa fa-camera', 1, 1, 1, 1, 1, 1, '2022-01-28 00:00:00', '1425004256210038785', '2023-03-31 21:54:14', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581601424831889410', '1425384413584252930', '修改菜单', 'system:menu:update', NULL, NULL, NULL, 3, NULL, 1, 1, 1, 2, '2022-10-16 19:02:19', '1425004256210038785', '2022-10-16 19:05:09', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581601696417267713', '1425384413584252930', '删除菜单', 'system:menu:delete', NULL, NULL, NULL, 4, NULL, 1, 1, 1, 2, '2022-10-16 19:03:24', '1425004256210038785', '2022-10-16 19:05:16', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581602868477771778', '1425384255047946241', '新增用户', 'system:user:add', NULL, NULL, NULL, 2, NULL, 1, 1, 1, 2, '2022-10-16 19:08:03', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581602962446958593', '1425384255047946241', '修改用户', 'system:user:update', NULL, NULL, NULL, 3, NULL, 1, 1, 1, 2, '2022-10-16 19:08:26', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581603052624494594', '1425384255047946241', '删除用户', 'system:user:delete', NULL, NULL, NULL, 4, NULL, 1, 1, 1, 2, '2022-10-16 19:08:47', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581603401271820290', '1425384412992856065', '角色添加', 'system:role:add', NULL, NULL, NULL, 2, NULL, 1, 1, 1, 2, '2022-10-16 19:10:10', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581603490417557506', '1425384412992856065', '角色修改', 'system:role:update', NULL, NULL, NULL, 3, NULL, 1, 1, 1, 2, '2022-10-16 19:10:32', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581603638262579201', '1425384412992856065', '角色删除', 'system:role:delete', NULL, NULL, NULL, 4, NULL, 1, 1, 1, 2, '2022-10-16 19:11:07', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581607834781171714', '1486984444607004673', '清空操作日志表', 'systemMonitor:sysLog:clear', NULL, NULL, NULL, 1, NULL, 1, 1, 1, 2, '2022-10-16 19:27:47', '1425004256210038785', '2022-11-06 20:32:02', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1581621978532102146', '1425384255047946241', '用户状态修改', 'system:user:changeStatus', NULL, NULL, NULL, 5, NULL, 1, 1, 1, 2, '2022-10-16 20:23:59', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1582756101220888578', '1461990613021151233', '在线用户', 'onlineUser', 'monitor/online', 'monitor/online/OnlineList', 'fa fa-group', 3, 1, 1, 1, 1, 1, '2022-10-19 00:00:00', '1425004256210038785', '2022-11-06 19:44:15', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1583049737108144129', '1582756101220888578', '强退在线用户', 'onlineUser:kick', NULL, NULL, NULL, 1, NULL, 1, 1, 1, 2, '2022-10-20 18:57:24', '1', '2022-10-20 23:40:06', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1586279773806186498', '1461990613021151233', '服务监控', 'systemMonitor:server', 'monitor/server', 'monitor/server/index', 'fa fa-heartbeat', 5, 1, 1, 1, 1, 1, '2022-10-29 00:00:00', '1425004256210038785', '2022-12-20 22:11:11', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1589222073322692610', '1461990613021151233', '登录日志', 'systemMonitor:sysLoginInfor', 'monitor/loginInfor', 'monitor/loginInfor/index', 'fa fa-sign-in', 1.1, 1, 1, 1, 1, 1, '2022-11-06 00:00:00', '1425004256210038785', '2023-03-31 21:54:46', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1589233921480712193', '1589222073322692610', '清空登录日志表', 'systemMonitor:sysLoginInfor:truncate', NULL, NULL, NULL, 1, NULL, 1, 1, 1, 2, '2022-11-06 20:31:08', '1425004256210038785', '2022-11-06 20:31:48', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1637813004071940097', '0', '统计分析', 'statistics', NULL, NULL, 'fa fa-pie-chart', 20, NULL, 1, 1, 1, 0, '2023-03-20 21:47:04', '1425004256210038785', '2023-03-20 21:52:19', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1637813578733531137', '1637813004071940097', '图表模板', 'statistics:chart', '/statistics/chart', '/statistics/chart', 'fa fa-area-chart', 5, 1, 1, 1, 1, 1, '2023-03-20 00:00:00', '1425004256210038785', '2023-03-22 20:53:03', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1639639714044121089', '0', '审批设置', 'processSettings', NULL, NULL, 'fa fa-hand-pointer-o', 3, NULL, 1, 1, 1, 0, '2023-03-25 22:45:45', '1425004256210038785', '2023-03-25 22:49:40', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1639639987563073538', '0', '审批管理', 'processManagement', NULL, NULL, 'fa fa-sitemap', 4, NULL, 1, 1, 1, 0, '2023-03-25 22:46:51', '1425004256210038785', '2023-03-25 22:49:55', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1639640297413087233', '1639639714044121089', '审批类型', 'processType', 'processSettings/processType', 'processSettings/processType', 'fa fa-list-ul', 1, 1, 1, 1, 1, 1, '2023-03-25 00:00:00', '1425004256210038785', '2023-03-25 22:49:29', '1425004256210038785', 1, NULL);
INSERT INTO `sys_permission` VALUES ('1639919258760118274', '1639639714044121089', '审批模板', 'processTemplate', 'processSettings/processTemplate', 'processSettings/processTemplate', 'fa fa-paste', 2, 1, 1, 1, 1, 1, '2023-03-26 17:16:34', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1641455943276900354', '1639639987563073538', '审批列表', 'processList', 'processManagement/processList', 'processManagement/processList', 'fa fa-list', 1, 1, 1, 1, 1, 1, '2023-03-30 23:02:48', '1425004256210038785', NULL, NULL, 1, NULL);
INSERT INTO `sys_permission` VALUES ('1643982576965693441', '1639639714044121089', '公众号菜单', 'prcesswechatmenu', '/processSettings/wechatmenu', '/processSettings/wechatmenu', 'fa fa-wechat', 3, 1, 1, 1, 1, 1, '2023-04-06 22:22:45', '1425004256210038785', NULL, NULL, 1, NULL);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
                             `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
                             `code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色码',
                             `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色名称',
                             `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用0：禁用1：启用',
                             `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                             `gmt_create` datetime NULL DEFAULT NULL,
                             `create_by` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `gmt_modified` datetime NULL DEFAULT NULL,
                             `modified_by` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `del_flag` tinyint(1) NULL DEFAULT 1,
                             `version` bigint NULL DEFAULT NULL,
                             `sort` int NOT NULL COMMENT '排序',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', 'user', '用户', 1, NULL, '2022-01-05 00:00:00', NULL, NULL, NULL, 1, NULL, 4);
INSERT INTO `sys_role` VALUES ('1425011630752735234', 'super_admin', '超级管理员', 1, 'superAdmin！！！', '2022-01-05 00:00:00', NULL, '2022-01-11 08:58:46', '1425004256210038785', 1, NULL, 1);
INSERT INTO `sys_role` VALUES ('1479338174086205442', 'admin', '管理员', 1, NULL, '2022-01-07 00:00:00', '1425004256210038785', '2022-05-03 02:24:25', '1425004256210038785', 1, NULL, 2);
INSERT INTO `sys_role` VALUES ('1484006672313884673', 'guest', '游客', 1, NULL, '2022-01-04 00:00:00', '1425004256210038785', '2022-01-20 16:56:13', '1425004256210038785', 1, NULL, 4);
INSERT INTO `sys_role` VALUES ('1584907057962328066', 'gitee', 'gitee用户', 1, 'gitee用户访问默认角色', '2022-10-25 00:00:00', '7853024', '2023-03-25 13:31:55', '1425004256210038785', 1, NULL, 5);
INSERT INTO `sys_role` VALUES ('1642066476367622146', '测试', 'test', 1, NULL, '2023-04-01 15:28:51', '1638915764242632705', NULL, NULL, 1, NULL, 5);

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
                                        `role_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                        `per_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                        `gmt_create` datetime NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1425384413584252930', '2022-01-13 08:13:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1461990613021151233', '2022-01-13 08:13:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1425381271220101122', '2022-01-13 08:13:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1463297492196085761', '2022-01-13 08:13:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1425384255047946241', '2022-01-13 08:13:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1462047178541559809', '2022-01-13 08:13:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1425384412992856065', '2022-01-13 08:13:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1486984444607004673', '2022-01-28 16:48:13');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581601424831889410', '2022-10-16 19:02:19');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581601696417267713', '2022-10-16 19:03:24');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581602868477771778', '2022-10-16 19:08:03');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581602962446958593', '2022-10-16 19:08:26');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581603052624494594', '2022-10-16 19:08:47');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581603401271820290', '2022-10-16 19:10:10');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581603490417557506', '2022-10-16 19:10:32');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581603638262579201', '2022-10-16 19:11:07');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581607834781171714', '2022-10-16 19:27:47');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581621978532102146', '2022-10-16 20:23:59');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1582756101220888578', '2022-10-19 23:30:35');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1583049737108144129', '2022-10-20 18:57:24');
INSERT INTO `sys_role_permission` VALUES ('1', '1425381271220101122', '2022-10-20 23:52:19');
INSERT INTO `sys_role_permission` VALUES ('1', '1425384255047946241', '2022-10-20 23:52:19');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1425384413584252930', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1461990613021151233', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1425381271220101122', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1463297492196085761', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1425384255047946241', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1425384412992856065', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1486984444607004673', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1484006672313884673', '1582756101220888578', '2022-10-25 21:58:01');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1586278812291354625', '2022-10-29 16:48:35');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1586279773806186498', '2022-10-29 16:52:24');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1589222073322692610', '2022-11-06 19:44:03');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1589233921480712193', '2022-11-06 20:31:08');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1637813004071940097', '2023-03-20 21:47:04');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1637813578733531137', '2023-03-20 21:49:21');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1461990613021151233', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1425381271220101122', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1589222073322692610', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1425384255047946241', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581602868477771778', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1425384412992856065', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1582756101220888578', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1637813578733531137', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1462047178541559809', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581603401271820290', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1589233921480712193', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581607834781171714', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1586279773806186498', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581603052624494594', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1425384413584252930', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581601696417267713', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581601424831889410', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1583049737108144129', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581602962446958593', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1463297492196085761', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581603638262579201', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1637813004071940097', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581621978532102146', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1486984444607004673', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581603490417557506', '2023-03-22 23:14:48');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1639639714044121089', '2023-03-25 22:45:46');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1639639987563073538', '2023-03-25 22:46:51');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1639640297413087233', '2023-03-25 22:48:05');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1639919258760118274', '2023-03-26 17:16:34');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1641455943276900354', '2023-03-30 23:02:48');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1643982576965693441', '2023-04-06 22:22:45');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1461990613021151233', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1643982576965693441', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1425381271220101122', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1589222073322692610', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1425384255047946241', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581602868477771778', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1425384412992856065', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1582756101220888578', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1637813578733531137', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1462047178541559809', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1641455943276900354', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581603401271820290', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1589233921480712193', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581607834781171714', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1639640297413087233', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1586279773806186498', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581603052624494594', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1425384413584252930', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581601696417267713', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581601424831889410', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1583049737108144129', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581602962446958593', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1463297492196085761', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581603638262579201', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1639639714044121089', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1637813004071940097', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581621978532102146', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1486984444607004673', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1639919258760118274', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1639639987563073538', '2023-04-13 01:11:14');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1581603490417557506', '2023-04-13 01:11:14');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
                             `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id',
                             `login_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录名称',
                             `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
                             `real_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
                             `gender` tinyint(1) NULL DEFAULT NULL COMMENT '性别0：女1：男',
                             `birthday` date NULL DEFAULT NULL COMMENT '生日',
                             `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话',
                             `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态0：禁用1：正常',
                             `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
                             `email` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
                             `gmt_create` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `create_by` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                             `gmt_modified` datetime NULL DEFAULT NULL COMMENT '修改时间',
                             `modified_by` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
                             `del_flag` tinyint(1) NULL DEFAULT 1 COMMENT '逻辑删除0：删除1：正常',
                             `version` bigint NULL DEFAULT 1 COMMENT '版本号',
                             `account_locked` tinyint NULL DEFAULT 1 COMMENT '账号是否被锁定0：锁定1：正常',
                             `open_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信openid',
                             `source` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '登录方式(0：系统用户)',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `login_name_unique`(`login_name` ASC) USING BTREE,
                             INDEX `idx_name`(`login_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1425004256210038785', 'tom', '$2a$10$YNXMehner5tmZlC4adQA3.aAelN6/9QRxM6IOK5KR/9kMKCWChkUK', '超级管理员', 0, '2000-04-01', NULL, 1, '/profile/avatar/93dc8a10-0c32-494d-9ef4-f1c458e03965.jpg', NULL, '2021-08-10 00:00:00', NULL, '2023-04-10 21:32:57', '1425004256210038785', 1, NULL, 1, NULL, '0');
INSERT INTO `sys_user` VALUES ('1646198846768410625', 'alice', '$2a$10$fy3O2x5R4SD/Sa2xepkrCuWy2sfA0WBRvGoeIuZG4915wxc6wFvHm', '爱丽丝', 0, '2000-01-01', NULL, 1, '/profile/avatar/96070403-e628-4b27-a395-cd5794e2b709.jpg', NULL, '2023-04-13 00:00:00', '1425004256210038785', '2023-04-13 01:09:39', '1425004256210038785', 1, 1, 1, NULL, '0');
INSERT INTO `sys_user` VALUES ('1646198878523486209', 'bob', '$2a$10$tKHwRxpHQ8CQcvtg.vlbQ.i4wYSP5lJu8UHhL0RhERnyiosECxtf2', '鲍勃', 1, NULL, NULL, 1, NULL, NULL, '2023-04-13 00:00:00', '1425004256210038785', '2023-04-15 19:58:19', '1425004256210038785', 1, 1, 1, NULL, '0');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
                                  `user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                  `role_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                  `gmt_create` datetime NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('1646198846768410625', '1479338174086205442', '2023-04-13 01:09:39');
INSERT INTO `sys_user_role` VALUES ('1425004256210038785', '1425011630752735234', '2023-04-13 08:41:47');
INSERT INTO `sys_user_role` VALUES ('1425004256210038785', '1484006672313884673', '2023-04-13 08:41:47');
INSERT INTO `sys_user_role` VALUES ('1425004256210038785', '1584907057962328066', '2023-04-13 08:41:47');
INSERT INTO `sys_user_role` VALUES ('1646496062062063618', '1479338174086205442', '2023-04-13 20:50:26');
INSERT INTO `sys_user_role` VALUES ('1646496172292567041', '1479338174086205442', '2023-04-13 20:50:53');
INSERT INTO `sys_user_role` VALUES ('1646198878523486209', '1479338174086205442', '2023-04-15 19:58:20');

SET FOREIGN_KEY_CHECKS = 1;
