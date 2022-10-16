-- 创建数据库SQL,需要一并创建数据库就解开下面3行脚本
-- drop database if exists `db_autumn`;
-- CREATE DATABASE `db_autumn`;
-- use `db_autumn`;
--

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

 Date: 16/10/2022 21:03:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
                            `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT ' 主题',
                            `username` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作用户名',
                            `method_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '方法名',
                            `visit_date` datetime NOT NULL COMMENT '访问时间',
                            `execution_time` int NULL DEFAULT NULL COMMENT '操作时长',
                            `ip` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '访问ip',
                            `url` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '访问url',
                            `params` varchar(600) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '访问参数',
                            `os_and_browser` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作系统及浏览器信息',
                            `result` varchar(600) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行结果',
                            `type` smallint NULL DEFAULT NULL COMMENT '日志类型(1:登录2:登录3:其他)',
                            `exception_msg` varchar(600) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '异常信息'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_log
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
INSERT INTO `sys_permission` VALUES ('1425384412992856065', '1425381271220101122', '角色管理', '/system/role', 'system/role/RoleList', 'fa fa-user-secret', 2, 1, 1, 1, 1, 1, '2022-01-05 00:00:00', NULL, '2022-01-20 16:03:45', '1425004256210038785', 1, 'per_role', '角色管理');
INSERT INTO `sys_permission` VALUES ('1425384413584252930', '1425381271220101122', '菜单管理', '/system/permission', 'system/permission/PermissionList', 'fa fa-bars', 3, 1, 1, 1, 1, 1, '2022-01-05 00:00:00', NULL, '2022-01-20 17:04:23', '1425004256210038785', 1, 'per_menu', '菜单管理');
INSERT INTO `sys_permission` VALUES ('1461990613021151233', '0', '系统监控', '', NULL, 'fa fa-tv', 100, 1, 1, 1, 1, 0, '2021-11-20 17:31:38', '1425004256210038785', '2022-10-16 19:21:01', '1425004256210038785', 1, 'systemMonitor', '系统监控');
INSERT INTO `sys_permission` VALUES ('1462047178541559809', '1425384413584252930', '新增菜单', '', NULL, NULL, 2, NULL, 1, 1, 1, 2, '2021-11-20 21:16:26', '1425004256210038785', '2022-10-16 19:04:56', '1425004256210038785', 1, 'system:menu:add', '新增菜单');
INSERT INTO `sys_permission` VALUES ('1463297492196085761', '1461990613021151233', 'driud监控', '/dataView/druid/index.html', 'monitoring/Durid', 'fa fa-database', 2, 1, 1, 1, 1, 1, '2021-11-24 00:00:00', '1425004256210038785', '2022-01-28 16:49:29', '1425004256210038785', 1, 'duridMonitoring', 'driud监控');
INSERT INTO `sys_permission` VALUES ('1486984444607004673', '1461990613021151233', '操作日志', 'monitor/log', 'monitor/log/LogList', 'fa fa-camera', 1, 1, 1, 1, 1, 1, '2022-01-28 00:00:00', '1425004256210038785', '2022-10-16 19:20:48', '1425004256210038785', 1, 'systemMonitor:sysLog', NULL);
INSERT INTO `sys_permission` VALUES ('1581601424831889410', '1425384413584252930', '修改菜单', NULL, NULL, NULL, 3, NULL, 1, 1, 1, 2, '2022-10-16 19:02:19', '1425004256210038785', '2022-10-16 19:05:09', '1425004256210038785', 1, 'system:menu:update', NULL);
INSERT INTO `sys_permission` VALUES ('1581601696417267713', '1425384413584252930', '删除菜单', NULL, NULL, NULL, 4, NULL, 1, 1, 1, 2, '2022-10-16 19:03:24', '1425004256210038785', '2022-10-16 19:05:16', '1425004256210038785', 1, 'system:menu:delete', NULL);
INSERT INTO `sys_permission` VALUES ('1581602029910573057', '1425384413584252930', '菜单查询', NULL, NULL, NULL, 1, NULL, 1, 1, 1, 2, '2022-10-16 19:04:43', '1425004256210038785', '2022-10-16 19:07:38', '1425004256210038785', 1, 'system:menu:query', NULL);
INSERT INTO `sys_permission` VALUES ('1581602500524064770', '1425384255047946241', '用户查询', NULL, NULL, NULL, 1, NULL, 1, 1, 1, 2, '2022-10-16 19:06:36', '1425004256210038785', '2022-10-16 19:07:21', '1425004256210038785', 1, 'system:user:query', NULL);
INSERT INTO `sys_permission` VALUES ('1581602868477771778', '1425384255047946241', '新增用户', NULL, NULL, NULL, 2, NULL, 1, 1, 1, 2, '2022-10-16 19:08:03', '1425004256210038785', NULL, NULL, 1, 'system:user:add', NULL);
INSERT INTO `sys_permission` VALUES ('1581602962446958593', '1425384255047946241', '修改用户', NULL, NULL, NULL, 3, NULL, 1, 1, 1, 2, '2022-10-16 19:08:26', '1425004256210038785', NULL, NULL, 1, 'system:user:update', NULL);
INSERT INTO `sys_permission` VALUES ('1581603052624494594', '1425384255047946241', '删除用户', NULL, NULL, NULL, 4, NULL, 1, 1, 1, 2, '2022-10-16 19:08:47', '1425004256210038785', NULL, NULL, 1, 'system:user:delete', NULL);
INSERT INTO `sys_permission` VALUES ('1581603287866228737', '1425384412992856065', '角色查询', NULL, NULL, NULL, 1, NULL, 1, 1, 1, 2, '2022-10-16 19:09:43', '1425004256210038785', NULL, NULL, 1, 'system:role:query', NULL);
INSERT INTO `sys_permission` VALUES ('1581603401271820290', '1425384412992856065', '角色添加', NULL, NULL, NULL, 2, NULL, 1, 1, 1, 2, '2022-10-16 19:10:10', '1425004256210038785', NULL, NULL, 1, 'system:role:add', NULL);
INSERT INTO `sys_permission` VALUES ('1581603490417557506', '1425384412992856065', '角色修改', NULL, NULL, NULL, 3, NULL, 1, 1, 1, 2, '2022-10-16 19:10:32', '1425004256210038785', NULL, NULL, 1, 'system:role:update', NULL);
INSERT INTO `sys_permission` VALUES ('1581603638262579201', '1425384412992856065', '角色删除', NULL, NULL, NULL, 4, NULL, 1, 1, 1, 2, '2022-10-16 19:11:07', '1425004256210038785', NULL, NULL, 1, 'system:role:delete', NULL);
INSERT INTO `sys_permission` VALUES ('1581607067961737218', '1486984444607004673', '日志查看', NULL, NULL, NULL, 1, NULL, 1, 1, 1, 2, '2022-10-16 19:24:44', '1425004256210038785', NULL, NULL, 1, 'systemMonitor:sysLog:query', NULL);
INSERT INTO `sys_permission` VALUES ('1581607834781171714', '1486984444607004673', '日志清空', NULL, NULL, NULL, 1, NULL, 1, 1, 1, 2, '2022-10-16 19:27:47', '1425004256210038785', NULL, NULL, 1, 'systemMonitor:sysLog:clear', NULL);
INSERT INTO `sys_permission` VALUES ('1581621978532102146', '1425384255047946241', '用户状态修改', NULL, NULL, NULL, 5, NULL, 1, 1, 1, 2, '2022-10-16 20:23:59', '1425004256210038785', NULL, NULL, 1, 'system:user:changeStatus', NULL);

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
INSERT INTO `sys_role` VALUES ('1484008391340990466', 'razor', '剃刀党', 1, NULL, '2022-01-20 11:42:27', '1425004256210038785', NULL, NULL, 1, NULL, 15);
INSERT INTO `sys_role` VALUES ('1484069473107582977', 'security staff', '保安', 1, NULL, '2022-01-20 15:45:10', '1425004256210038785', NULL, NULL, 1, NULL, 12);

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
INSERT INTO `sys_role_permission` VALUES ('1', '1425384413584252930', '2022-01-19 19:58:10');
INSERT INTO `sys_role_permission` VALUES ('1', '1425381271220101122', '2022-01-19 19:58:10');
INSERT INTO `sys_role_permission` VALUES ('1', '1462047178541559809', '2022-01-19 19:58:10');
INSERT INTO `sys_role_permission` VALUES ('1484008013073489921', '1425384413584252930', '2022-01-20 11:47:12');
INSERT INTO `sys_role_permission` VALUES ('1484008013073489921', '1425381271220101122', '2022-01-20 11:47:12');
INSERT INTO `sys_role_permission` VALUES ('1484008013073489921', '1462047178541559809', '2022-01-20 11:47:12');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1486984444607004673', '2022-01-28 16:48:13');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581601424831889410', '2022-10-16 19:02:19');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581601696417267713', '2022-10-16 19:03:24');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581602029910573057', '2022-10-16 19:04:43');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581602500524064770', '2022-10-16 19:06:36');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581602868477771778', '2022-10-16 19:08:03');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581602962446958593', '2022-10-16 19:08:26');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581603052624494594', '2022-10-16 19:08:47');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581603287866228737', '2022-10-16 19:09:43');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581603401271820290', '2022-10-16 19:10:10');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581603490417557506', '2022-10-16 19:10:32');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581603638262579201', '2022-10-16 19:11:07');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581607067961737218', '2022-10-16 19:24:45');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581607834781171714', '2022-10-16 19:27:47');
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234', '1581621978532102146', '2022-10-16 20:23:59');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1461990613021151233', '2022-10-16 20:48:51');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1425381271220101122', '2022-10-16 20:48:51');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1425384255047946241', '2022-10-16 20:48:51');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1425384412992856065', '2022-10-16 20:48:51');
INSERT INTO `sys_role_permission` VALUES ('1479338174086205442', '1486984444607004673', '2022-10-16 20:48:51');

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
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', 'jack', '$2a$10$WJNCkw.EEwjJHKiHWUbZoe.XUqrxES4b9m4rWDmaHAVhHMQqGO9DS', ' 小晨', 0, '1998-06-15', '', 1, 'http://r67xhbta9.hn-bkt.clouddn.com/20220127081538_1.jpg', '', '2021-09-14 00:00:00', NULL, '2022-10-16 20:49:41', '1425004256210038785', 1, 1, 1);
INSERT INTO `sys_user` VALUES ('1425004256210038785', 'tom', '$2a$10$rvfn7AvmOxnF3yqYkRjChejNVItVbxg8uvpzWUGRG9qWAWEZPsK3m', '小杨', 1, '1770-01-02', '18270917877', 1, 'http://r67xhbta9.hn-bkt.clouddn.com/20220521171950_1425004256210038785.jpg', 'tom@163.com', '2021-08-10 00:00:00', NULL, '2022-05-17 00:44:01', '1425004256210038785', 1, NULL, 1);
INSERT INTO `sys_user` VALUES ('1483764958705852417', 'www', '$2a$10$pqsXe26VDomSb/P9YkjjieIHzdPEHds0fLq5b9a0FG1qIWRMtRx.m', '2222', 1, '2004-06-25', '15825652541', 1, 'http://r67xhbta9.hn-bkt.clouddn.com/20220127081625_1483764958705852417.jpg', '222@qq.com', '2022-01-19 00:00:00', '1425004256210038785', '2022-01-20 11:29:55', '1425004256210038785', 1, 1, 1);
INSERT INTO `sys_user` VALUES ('1484059335881662465', '尊敬的弗拉基米尔伯爵二世', '$2a$10$/RziMD8yXb95fvZXo1kUT.tnj4XMdIWDysVmY3cvt2DAepHgm47Yi', '尊敬的弗拉基米尔伯爵', 0, '2022-01-04', NULL, 1, NULL, NULL, '2022-01-20 00:00:00', '1425004256210038785', '2022-05-03 01:55:02', '1425004256210038785', 1, 1, 1);
INSERT INTO `sys_user` VALUES ('1485569489789837313', 'aaa', '$2a$10$O1nAXEJp419a2jdFX0gWB.3Do/Pr5YLbXzRwN6vQJpDVBnTCDSJxa', 'aaa', 1, NULL, NULL, 1, NULL, NULL, '2022-01-24 19:05:42', '1425004256210038785', NULL, NULL, 1, 1, 1);
INSERT INTO `sys_user` VALUES ('1486508175301906434', 'alice', '$2a$10$PbkGcKwOaHnryGtpHpAMAumcpBlrqN6nglKNQuy.hJhNURq3Mo1IC', '爱丽丝', 0, NULL, NULL, 1, NULL, NULL, '2022-01-27 09:15:42', '1425004256210038785', NULL, NULL, 1, 1, 1);
INSERT INTO `sys_user` VALUES ('1486508328414973954', 'dina', '$2a$10$SzCKiqWSCUzO914TO8XsEeha/H72S7iwoUjhc8rIcc1.T7nlPRCw6', '黛娜', 0, NULL, NULL, 1, NULL, NULL, '2022-01-27 09:16:18', '1425004256210038785', NULL, NULL, 1, 1, 1);
INSERT INTO `sys_user` VALUES ('1486508482861830146', 'jane', '$2a$10$CM4EuA0DIs4x4J12RozmbOentUH/DVD4Tx5b83iQeoNTUeAmY/YHu', '简123', 0, NULL, NULL, 1, NULL, NULL, '2022-01-27 00:00:00', '1425004256210038785', '2022-03-01 20:03:31', '1425004256210038785', 1, 1, 1);
INSERT INTO `sys_user` VALUES ('1486508911700054018', 'anna', '$2a$10$xkMqm4fgT8XIoEpl/nn/SOfVsJwyVM8GhdSGi7jcExJon.lfK1JPO', '安娜', 1, '2022-05-15', NULL, 1, NULL, NULL, '2022-01-27 00:00:00', '1425004256210038785', '2022-05-15 20:33:44', '1425004256210038785', 1, 1, 1);
INSERT INTO `sys_user` VALUES ('1486508971322085378', 'bob', '$2a$10$R67uE/okZw5.6s7kPI46ZOWLrLnMZwsu/maCsNDEBthbCtJ7j8H8m', '鲍勃', 1, '2022-05-15', NULL, 1, 'http://r67xhbta9.hn-bkt.clouddn.com/20220127093137_1486508971322085378.jpg', NULL, '2022-01-27 00:00:00', '1425004256210038785', '2022-05-15 20:32:42', '1425004256210038785', 1, 1, 1);
INSERT INTO `sys_user` VALUES ('1486509025365692417', 'lucy', '$2a$10$5M0Ez872DiGr0Jzz9.Uj8OYUR7HrenJQMme/d/80hWIFdl1vM8JPG', '露西', 0, '2022-05-15', NULL, 1, NULL, NULL, '2022-01-27 00:00:00', '1425004256210038785', '2022-05-15 20:32:21', '1425004256210038785', 1, 1, 1);

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
INSERT INTO `sys_user_role` VALUES ('1486508175301906434', '1', '2022-01-27 09:15:42');
INSERT INTO `sys_user_role` VALUES ('1486508328414973954', '1484008292372193281', '2022-01-27 09:16:18');
INSERT INTO `sys_user_role` VALUES ('1484059335881662465', '1484008391340990466', '2022-05-03 01:55:02');
INSERT INTO `sys_user_role` VALUES ('1425004256210038785', '1425011630752735234', '2022-05-15 20:50:07');
INSERT INTO `sys_user_role` VALUES ('1425004256210038785', '1484008391340990466', '2022-05-15 20:50:07');
INSERT INTO `sys_user_role` VALUES ('1425004256210038785', '1484008120518975489', '2022-05-15 20:50:07');
INSERT INTO `sys_user_role` VALUES ('1425004256210038785', '1484008013073489921', '2022-05-15 20:50:07');
INSERT INTO `sys_user_role` VALUES ('1425004256210038785', '1484008292372193281', '2022-05-15 20:50:07');
INSERT INTO `sys_user_role` VALUES ('1', '1', '2022-10-16 20:49:41');
INSERT INTO `sys_user_role` VALUES ('1', '1479338174086205442', '2022-10-16 20:49:41');

SET FOREIGN_KEY_CHECKS = 1;
