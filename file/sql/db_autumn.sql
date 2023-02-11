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

 Date: 15/01/2023 17:47:07
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_record
-- ----------------------------

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
                            `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT ' 主题',
                            `username` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作用户名',
                            `method_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '方法名',
                            `visit_date` datetime NOT NULL COMMENT '访问时间',
                            `execution_time` bigint NULL DEFAULT NULL COMMENT '操作时长',
                            `ip` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '访问ip',
                            `url` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '访问url',
                            `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '真实地址',
                            `params` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '访问参数',
                            `os` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作系统',
                            `result` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '执行结果',
                            `type` tinyint NULL DEFAULT NULL COMMENT '日志类型(1:登录2:退出3:其他)',
                            `exception_msg` varchar(1500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '异常信息',
                            `browser` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '浏览器'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

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
                                   `type` tinyint NULL DEFAULT NULL COMMENT '登录方式(0：账号密码，1：短信)',
                                   `ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ip',
                                   `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实地址',
                                   `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器',
                                   `os` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作系统',
                                   `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '信息',
                                   `login_time` datetime NULL DEFAULT NULL COMMENT '登录时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 387 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
                                   `code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限编码',
                                   `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '前端路由对象name值(已用title字段替代)',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES ('1425381271220101122', '0', '系统管理', '', NULL, 'fa fa-gear', 3, NULL, 1, 1, 1, 0, '2021-08-11 17:20:45', NULL, '2022-05-03 02:24:54', '1425004256210038785', 1, 'system', '系统管理');
INSERT INTO `sys_permission` VALUES ('1425384255047946241', '1425381271220101122', '用户管理', '/system/user', 'system/user/UserList', 'fa fa-user', 1, 1, 1, 1, 1, 1, '2021-08-11 00:00:00', NULL, '2022-10-16 19:00:58', '1425004256210038785', 1, 'system:user', '用户管理');
INSERT INTO `sys_permission` VALUES ('1425384412992856065', '1425381271220101122', '角色管理', '/system/role', 'system/role/RoleList', 'fa fa-user-secret', 2, 1, 1, 1, 1, 1, '2022-01-05 00:00:00', NULL, '2022-10-20 23:36:59', '1425004256210038785', 1, 'system:role', '角色管理');
INSERT INTO `sys_permission` VALUES ('1425384413584252930', '1425381271220101122', '菜单管理', '/system/permission', 'system/permission/PermissionList', 'el-icon-menu', 3, 1, 1, 1, 1, 1, '2022-01-05 00:00:00', NULL, '2022-12-31 22:06:25', '1425004256210038785', 1, 'system:menu', '菜单管理');
INSERT INTO `sys_permission` VALUES ('1461990613021151233', '0', '系统监控', '', NULL, 'fa fa-tv', 100, 1, 1, 1, 1, 0, '2021-11-20 17:31:38', '1425004256210038785', '2022-10-16 19:21:01', '1425004256210038785', 1, 'systemMonitor', '系统监控');
INSERT INTO `sys_permission` VALUES ('1462047178541559809', '1425384413584252930', '新增菜单', '', NULL, NULL, 2, NULL, 1, 1, 1, 2, '2021-11-20 21:16:26', '1425004256210038785', '2022-10-16 19:04:56', '1425004256210038785', 1, 'system:menu:add', '新增菜单');
INSERT INTO `sys_permission` VALUES ('1463297492196085761', '1461990613021151233', 'driud监控', 'monitor/druid', 'monitor/druid/index', 'fa fa-database', 4, 1, 1, 1, 1, 1, '2021-11-24 00:00:00', '1425004256210038785', '2022-11-06 19:44:37', '1425004256210038785', 1, 'duridMonitoring', 'driud监控');
INSERT INTO `sys_permission` VALUES ('1486984444607004673', '1461990613021151233', '操作日志', 'monitor/log', 'monitor/log/LogList', 'fa fa-camera', 2, 1, 1, 1, 1, 1, '2022-01-28 00:00:00', '1425004256210038785', '2022-11-06 19:44:26', '1425004256210038785', 1, 'systemMonitor:sysLog', NULL);
INSERT INTO `sys_permission` VALUES ('1581601424831889410', '1425384413584252930', '修改菜单', NULL, NULL, NULL, 3, NULL, 1, 1, 1, 2, '2022-10-16 19:02:19', '1425004256210038785', '2022-10-16 19:05:09', '1425004256210038785', 1, 'system:menu:update', NULL);
INSERT INTO `sys_permission` VALUES ('1581601696417267713', '1425384413584252930', '删除菜单', NULL, NULL, NULL, 4, NULL, 1, 1, 1, 2, '2022-10-16 19:03:24', '1425004256210038785', '2022-10-16 19:05:16', '1425004256210038785', 1, 'system:menu:delete', NULL);
INSERT INTO `sys_permission` VALUES ('1581602868477771778', '1425384255047946241', '新增用户', NULL, NULL, NULL, 2, NULL, 1, 1, 1, 2, '2022-10-16 19:08:03', '1425004256210038785', NULL, NULL, 1, 'system:user:add', NULL);
INSERT INTO `sys_permission` VALUES ('1581602962446958593', '1425384255047946241', '修改用户', NULL, NULL, NULL, 3, NULL, 1, 1, 1, 2, '2022-10-16 19:08:26', '1425004256210038785', NULL, NULL, 1, 'system:user:update', NULL);
INSERT INTO `sys_permission` VALUES ('1581603052624494594', '1425384255047946241', '删除用户', NULL, NULL, NULL, 4, NULL, 1, 1, 1, 2, '2022-10-16 19:08:47', '1425004256210038785', NULL, NULL, 1, 'system:user:delete', NULL);
INSERT INTO `sys_permission` VALUES ('1581603401271820290', '1425384412992856065', '角色添加', NULL, NULL, NULL, 2, NULL, 1, 1, 1, 2, '2022-10-16 19:10:10', '1425004256210038785', NULL, NULL, 1, 'system:role:add', NULL);
INSERT INTO `sys_permission` VALUES ('1581603490417557506', '1425384412992856065', '角色修改', NULL, NULL, NULL, 3, NULL, 1, 1, 1, 2, '2022-10-16 19:10:32', '1425004256210038785', NULL, NULL, 1, 'system:role:update', NULL);
INSERT INTO `sys_permission` VALUES ('1581603638262579201', '1425384412992856065', '角色删除', NULL, NULL, NULL, 4, NULL, 1, 1, 1, 2, '2022-10-16 19:11:07', '1425004256210038785', NULL, NULL, 1, 'system:role:delete', NULL);
INSERT INTO `sys_permission` VALUES ('1581607834781171714', '1486984444607004673', '清空操作日志表', NULL, NULL, NULL, 1, NULL, 1, 1, 1, 2, '2022-10-16 19:27:47', '1425004256210038785', '2022-11-06 20:32:02', '1425004256210038785', 1, 'systemMonitor:sysLog:clear', NULL);
INSERT INTO `sys_permission` VALUES ('1581621978532102146', '1425384255047946241', '用户状态修改', NULL, NULL, NULL, 5, NULL, 1, 1, 1, 2, '2022-10-16 20:23:59', '1425004256210038785', NULL, NULL, 1, 'system:user:changeStatus', NULL);
INSERT INTO `sys_permission` VALUES ('1582756101220888578', '1461990613021151233', '在线用户', 'monitor/online', 'monitor/online/OnlineList', 'fa fa-group', 3, 1, 1, 1, 1, 1, '2022-10-19 00:00:00', '1425004256210038785', '2022-11-06 19:44:15', '1425004256210038785', 1, 'onlineUser', NULL);
INSERT INTO `sys_permission` VALUES ('1583049737108144129', '1582756101220888578', '强退在线用户', NULL, NULL, NULL, 1, NULL, 1, 1, 1, 2, '2022-10-20 18:57:24', '1', '2022-10-20 23:40:06', '1425004256210038785', 1, 'onlineUser:kick', NULL);
INSERT INTO `sys_permission` VALUES ('1586279773806186498', '1461990613021151233', '服务监控', 'monitor/server', 'monitor/server/index', 'fa fa-heartbeat', 5, 1, 1, 1, 1, 1, '2022-10-29 00:00:00', '1425004256210038785', '2022-12-20 22:11:11', '1425004256210038785', 1, 'systemMonitor:server', NULL);
INSERT INTO `sys_permission` VALUES ('1589222073322692610', '1461990613021151233', '登录日志', 'monitor/loginInfor', 'monitor/loginInfor/index', 'fa fa-sign-in', 1, 1, 1, 1, 1, 1, '2022-11-06 00:00:00', '1425004256210038785', '2022-11-06 19:57:24', '1425004256210038785', 1, 'systemMonitor:sysLoginInfor', NULL);
INSERT INTO `sys_permission` VALUES ('1589233921480712193', '1589222073322692610', '清空登录日志表', NULL, NULL, NULL, 1, NULL, 1, 1, 1, 2, '2022-11-06 20:31:08', '1425004256210038785', '2022-11-06 20:31:48', '1425004256210038785', 1, 'systemMonitor:sysLoginInfor:truncate', NULL);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
                             `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
                             `code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色码',
                             `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色名称',
                             `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用0：禁用1：启用',
                             `remark` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                             `gmt_create` datetime NULL DEFAULT NULL,
                             `create_by` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `gmt_modified` datetime NULL DEFAULT NULL,
                             `modified_by` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `del_flag` tinyint(1) NULL DEFAULT 1,
                             `version` bigint NULL DEFAULT NULL,
                             `sort` int NOT NULL COMMENT '排序',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', 'user', '用户', 1, NULL, '2022-01-05 00:00:00', NULL, NULL, NULL, 1, NULL, 4);
INSERT INTO `sys_role` VALUES ('1425011630752735234', 'super_admin', '超级管理员', 1, 'superAdmin！！！', '2022-01-05 00:00:00', NULL, '2022-01-11 08:58:46', '1425004256210038785', 1, NULL, 1);
INSERT INTO `sys_role` VALUES ('1479338174086205442', 'admin', '管理员', 1, NULL, '2022-01-07 00:00:00', '1425004256210038785', '2022-05-03 02:24:25', '1425004256210038785', 1, NULL, 2);
INSERT INTO `sys_role` VALUES ('1484006551870251010', 'test', '测试', 1, NULL, '2022-01-20 00:00:00', '1425004256210038785', '2022-01-20 16:56:05', '1425004256210038785', 1, NULL, 4);
INSERT INTO `sys_role` VALUES ('1484006672313884673', 'guest', '游客', 1, NULL, '2022-01-04 00:00:00', '1425004256210038785', '2022-01-20 16:56:13', '1425004256210038785', 1, NULL, 4);
INSERT INTO `sys_role` VALUES ('1484007600303644673', 'ghost', '无业游民', 0, '？', '2022-01-20 00:00:00', '1425004256210038785', '2022-01-20 17:06:13', '1425004256210038785', 1, NULL, 10);
INSERT INTO `sys_role` VALUES ('1484007915195211778', 'geek', '极客', 1, NULL, '2022-01-20 11:40:33', '1425004256210038785', NULL, NULL, 1, NULL, 11);
INSERT INTO `sys_role` VALUES ('1484008013073489921', 'BountyHunter', '赏金猎人', 1, NULL, '2022-01-20 11:40:57', '1425004256210038785', NULL, NULL, 1, NULL, 12);
INSERT INTO `sys_role` VALUES ('1484008120518975489', 'gunner', '枪手', 1, NULL, '2022-01-20 11:41:22', '1425004256210038785', NULL, NULL, 1, NULL, 13);
INSERT INTO `sys_role` VALUES ('1484008292372193281', 'priate', '海盗', 1, NULL, '2022-01-20 11:42:03', '1425004256210038785', NULL, NULL, 1, NULL, 14);
INSERT INTO `sys_role` VALUES ('1484069473107582977', 'security staff', '保安', 1, NULL, '2022-01-20 15:45:10', '1425004256210038785', NULL, NULL, 1, NULL, 12);
INSERT INTO `sys_role` VALUES ('1584907057962328066', 'gitee', 'gitee用户', 1, 'gitee用户访问默认角色', '2022-10-25 00:00:00', '7853024', '2022-10-25 21:58:15', '7853024', 1, NULL, 1);
INSERT INTO `sys_role` VALUES ('1601532409578823682', 'starGuardian', '星之守护者', 1, NULL, '2022-12-10 00:00:00', '1425004256210038785', '2022-12-10 19:01:38', '1425004256210038785', 1, NULL, 5);

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
                                        `role_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                        `per_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                        `gmt_create` datetime NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

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
INSERT INTO `sys_role_permission` VALUES ('1484008013073489921', '1425384413584252930', '2022-01-20 11:47:12');
INSERT INTO `sys_role_permission` VALUES ('1484008013073489921', '1425381271220101122', '2022-01-20 11:47:12');
INSERT INTO `sys_role_permission` VALUES ('1484008013073489921', '1462047178541559809', '2022-01-20 11:47:12');
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
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1461990613021151233', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1425381271220101122', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581607834781171714', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1586279773806186498', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1425384255047946241', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581602868477771778', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1425384412992856065', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581603052624494594', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1582756101220888578', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1425384413584252930', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581601696417267713', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581601424831889410', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1583049737108144129', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581602962446958593', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1463297492196085761', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581603638262579201', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1462047178541559809', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581621978532102146', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1486984444607004673', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581603401271820290', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1584907057962328066', '1581603490417557506', '2022-10-30 15:33:37');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1589222073322692610', '2022-11-06 19:44:03');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1589233921480712193', '2022-11-06 20:31:08');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1425384413584252930', '2022-11-06 21:43:31');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1461990613021151233', '2022-11-06 21:43:31');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1425381271220101122', '2022-11-06 21:43:31');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1589222073322692610', '2022-11-06 21:43:31');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1463297492196085761', '2022-11-06 21:43:31');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1425384255047946241', '2022-11-06 21:43:31');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1425384412992856065', '2022-11-06 21:43:31');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1486984444607004673', '2022-11-06 21:43:31');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1582756101220888578', '2022-11-06 21:43:31');
INSERT INTO `sys_role_permission` VALUES ('1601532409578823682', '1425381271220101122', '2022-12-10 19:03:12');
INSERT INTO `sys_role_permission` VALUES ('1601532409578823682', '1581602962446958593', '2022-12-10 19:03:12');
INSERT INTO `sys_role_permission` VALUES ('1601532409578823682', '1425384255047946241', '2022-12-10 19:03:12');
INSERT INTO `sys_role_permission` VALUES ('1601532409578823682', '1581602868477771778', '2022-12-10 19:03:12');
INSERT INTO `sys_role_permission` VALUES ('1601532409578823682', '1581621978532102146', '2022-12-10 19:03:12');

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
                             `source` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '登录方式(0：系统用户)',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `login_name_unique`(`login_name` ASC) USING BTREE,
                             INDEX `idx_name`(`login_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', 'bob', '$2a$10$CG9zi0YGQq8LEG2ekMPp6.y5eGdv3M6XLaqd.SVjv6J1BYbHmcIsC', '鲍勃', 0, '1998-06-15', '18270917877', 1, '/profile\\avatar\\a4f0400a-6a6c-4f82-9b48-deafbd5496f7.jpg', '', '2021-09-14 00:00:00', NULL, '2023-01-15 17:41:05', '1425004256210038785', 1, 1, 1, '0');
INSERT INTO `sys_user` VALUES ('1425004256210038785', 'tom', '$2a$10$YNXMehner5tmZlC4adQA3.aAelN6/9QRxM6IOK5KR/9kMKCWChkUK', '小杨', 1, '1770-01-02', '18270917870', 1, '/profile\\avatar\\5c5b86f2-f028-4aa4-9ecb-3f05dc75de7e.jpg', 'tom@163.com', '2021-08-10 00:00:00', NULL, '2022-12-19 21:15:50', '1425004256210038785', 1, NULL, 1, '0');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
                                  `user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                  `role_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                  `gmt_create` datetime NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('1425004256210038785', '1425011630752735234', '2022-12-19 21:15:50');
INSERT INTO `sys_user_role` VALUES ('1425004256210038785', '1484008120518975489', '2022-12-19 21:15:50');
INSERT INTO `sys_user_role` VALUES ('1425004256210038785', '1484008013073489921', '2022-12-19 21:15:50');
INSERT INTO `sys_user_role` VALUES ('1425004256210038785', '1484008292372193281', '2022-12-19 21:15:50');
INSERT INTO `sys_user_role` VALUES ('1', '1', '2023-01-15 17:41:05');
INSERT INTO `sys_user_role` VALUES ('1', '1479338174086205442', '2023-01-15 17:41:05');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮件发送队列记录表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

