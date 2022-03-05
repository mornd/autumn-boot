-- 创建数据库SQL,需要一并创建数据库就解开下面3行脚本
-- drop database if exists `db_autumn`;
-- CREATE DATABASE `db_autumn`;
-- use `db_autumn`;
--

-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: db_autumn
-- ------------------------------------------------------
-- Server version	8.0.19

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `persistent_logins`
--

DROP TABLE IF EXISTS `persistent_logins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `persistent_logins`
--

LOCK TABLES `persistent_logins` WRITE;
/*!40000 ALTER TABLE `persistent_logins` DISABLE KEYS */;
/*!40000 ALTER TABLE `persistent_logins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_log`
--

DROP TABLE IF EXISTS `sys_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_log` (
  `title` varchar(100) NOT NULL COMMENT ' 主题',
  `username` varchar(100) DEFAULT NULL COMMENT '操作用户名',
  `method_name` varchar(100) DEFAULT NULL COMMENT '方法名',
  `visit_date` datetime NOT NULL COMMENT '访问时间',
  `execution_time` int DEFAULT NULL COMMENT '操作时长',
  `ip` varchar(100) DEFAULT NULL COMMENT '访问ip',
  `url` varchar(100) DEFAULT NULL COMMENT '访问url',
  `params` varchar(600) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '访问参数',
  `os_and_browser` varchar(100) DEFAULT NULL COMMENT '操作系统及浏览器信息',
  `result` varchar(600) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '执行结果',
  `type` smallint DEFAULT NULL COMMENT '日志类型(1:登录2:登录3:其他)',
  `exception_msg` varchar(600) DEFAULT NULL COMMENT '异常信息'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_log`
--

LOCK TABLES `sys_log` WRITE;
/*!40000 ALTER TABLE `sys_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_permission`
--

DROP TABLE IF EXISTS `sys_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_permission` (
  `id` varchar(40) NOT NULL COMMENT '主键',
  `parent_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '父id',
  `title` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单标题',
  `path` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '前端路由对象path值',
  `component` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '前端路由对象component组件路径(无需加’/‘)',
  `icon` varchar(50) DEFAULT NULL COMMENT '菜单图标',
  `sort` double NOT NULL COMMENT '菜单排序',
  `keep_alive` tinyint(1) DEFAULT NULL COMMENT '是否保持激活(0：否 1：是)',
  `require_auth` tinyint(1) DEFAULT '1' COMMENT '是否要求权限(0:否 1:是)',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用(1：启用0：禁用)',
  `hidden` tinyint(1) DEFAULT '1' COMMENT '是否隐藏菜单(0：隐藏，1：显示)',
  `menu_type` tinyint(1) DEFAULT NULL COMMENT '菜单类型(0:一级菜单;1:子菜单;2:按钮权限)',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(40) DEFAULT NULL COMMENT '创建人',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  `modified_by` varchar(40) DEFAULT NULL COMMENT '修改人',
  `del_flag` tinyint(1) DEFAULT '1' COMMENT '逻辑删除',
  `code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '权限编码',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '前端路由对象name值(已用title字段替代)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_permission`
--

LOCK TABLES `sys_permission` WRITE;
/*!40000 ALTER TABLE `sys_permission` DISABLE KEYS */;
INSERT INTO `sys_permission` VALUES ('1425381271220101122','0','系统管理','',NULL,'fa fa-gear',3,NULL,1,1,1,0,'2021-08-11 17:20:45',NULL,'2022-01-19 20:04:47','1425004256210038785',1,'system','系统管理'),('1425384255047946241','1425381271220101122','用户管理','/system/user','system/user/UserList','fa fa-user',1,1,1,1,1,1,'2021-08-11 00:00:00',NULL,'2022-01-20 17:04:09','1425004256210038785',1,'per_user','用户管理'),('1425384412992856065','1425381271220101122','角色管理','/system/role','system/role/RoleList','fa fa-user-secret',2,1,1,1,1,1,'2022-01-05 00:00:00',NULL,'2022-01-20 16:03:45','1425004256210038785',1,'per_role','角色管理'),('1425384413584252930','1425381271220101122','菜单管理','/system/permission','system/permission/PermissionList','fa fa-bars',3,1,1,1,1,1,'2022-01-05 00:00:00',NULL,'2022-01-20 17:04:23','1425004256210038785',1,'per_menu','菜单管理'),('1461990613021151233','0','系统监控','',NULL,'fa fa-tv',100,1,1,1,1,0,'2021-11-20 17:31:38','1425004256210038785','2022-01-28 16:46:44','1425004256210038785',1,'SystemMonitor','系统监控'),('1462047178541559809','1425384413584252930','新增菜单','',NULL,NULL,1,NULL,1,0,1,2,'2021-11-20 21:16:26','1425004256210038785','2021-12-31 12:54:09','1425004256210038785',1,'permission:insert','新增菜单'),('1463297492196085761','1461990613021151233','driud监控','/dataView/druid/index.html','monitoring/Durid','fa fa-database',2,1,1,1,1,1,'2021-11-24 00:00:00','1425004256210038785','2022-01-28 16:49:29','1425004256210038785',1,'duridMonitoring','driud监控'),('1481059726834106370','0','测试',NULL,NULL,'fa fa-apple',1,NULL,1,1,1,0,'2022-01-12 08:25:30','1425004256210038785','2022-01-12 08:37:44','1425004256210038785',1,'test',NULL),('1484091994427174914','1481059726834106370','测试2',NULL,NULL,'fa fa-codepen',2,NULL,1,1,1,0,'2022-01-20 17:14:39','1425004256210038785',NULL,NULL,1,'test2',NULL),('1484092161368862722','1481059726834106370','测试3','test','test','fa fa-cloud',2,1,1,1,1,1,'2022-01-20 00:00:00','1425004256210038785','2022-01-20 17:15:26','1425004256210038785',1,'test3',NULL),('1484114216445358081','1481059726834106370','测试1','test1','test1','fa fa-upload',1,1,1,1,1,1,'2022-01-20 18:42:57','1425004256210038785',NULL,NULL,1,'test1',NULL),('1486984444607004673','1461990613021151233','操作日志','monitor/log','monitor/log/LogList','fa fa-camera',1,1,1,1,1,1,'2022-01-28 00:00:00','1425004256210038785','2022-01-28 16:50:36','1425004256210038785',1,'sysLog',NULL);
/*!40000 ALTER TABLE `sys_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
  `code` varchar(20) DEFAULT NULL COMMENT '角色码',
  `name` varchar(255) DEFAULT NULL COMMENT '角色名称',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用0：禁用1：启用',
  `remark` varchar(50) DEFAULT NULL COMMENT '备注',
  `gmt_create` datetime DEFAULT NULL,
  `create_by` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  `modified_by` varchar(40) DEFAULT NULL,
  `del_flag` tinyint(1) DEFAULT '1',
  `version` bigint DEFAULT NULL,
  `sort` int NOT NULL COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES ('1','user','用户',1,NULL,'2022-01-05 00:00:00',NULL,NULL,NULL,1,NULL,4),('1425011630752735234','super_admin','超级管理员',1,'superAdmin！！！','2022-01-05 00:00:00',NULL,'2022-01-11 08:58:46','1425004256210038785',1,NULL,1),('1479338174086205442','admin','管理员',1,NULL,'2022-01-07 00:00:00','1425004256210038785','2022-01-19 20:04:42','1425004256210038785',1,NULL,2),('1484006551870251010','test','测试',1,NULL,'2022-01-20 00:00:00','1425004256210038785','2022-01-20 16:56:05','1425004256210038785',1,NULL,4),('1484006672313884673','guest','游客',1,NULL,'2022-01-04 00:00:00','1425004256210038785','2022-01-20 16:56:13','1425004256210038785',1,NULL,4),('1484007600303644673','ghost','无业游民',0,'？','2022-01-20 00:00:00','1425004256210038785','2022-01-20 17:06:13','1425004256210038785',1,NULL,10),('1484007915195211778','geek','极客',1,NULL,'2022-01-20 11:40:33','1425004256210038785',NULL,NULL,1,NULL,11),('1484008013073489921','BountyHunter','赏金猎人',1,NULL,'2022-01-20 11:40:57','1425004256210038785',NULL,NULL,1,NULL,12),('1484008120518975489','gunner','枪手',1,NULL,'2022-01-20 11:41:22','1425004256210038785',NULL,NULL,1,NULL,13),('1484008292372193281','priate','海盗',1,NULL,'2022-01-20 11:42:03','1425004256210038785',NULL,NULL,1,NULL,14),('1484008391340990466','razor','剃刀党',1,NULL,'2022-01-20 11:42:27','1425004256210038785',NULL,NULL,1,NULL,15),('1484069473107582977','security staff','保安',1,NULL,'2022-01-20 15:45:10','1425004256210038785',NULL,NULL,1,NULL,12),('1484090043366977538','clear','清洁人员',1,NULL,'2022-01-20 17:06:54','1425004256210038785',NULL,NULL,1,NULL,12);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_permission`
--

DROP TABLE IF EXISTS `sys_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_permission` (
  `role_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `per_id` varchar(40) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_permission`
--

LOCK TABLES `sys_role_permission` WRITE;
/*!40000 ALTER TABLE `sys_role_permission` DISABLE KEYS */;
INSERT INTO `sys_role_permission` VALUES ('1425011630752735234','1425384413584252930','2022-01-13 08:13:04'),('1425011630752735234','1461990613021151233','2022-01-13 08:13:04'),('1425011630752735234','1425381271220101122','2022-01-13 08:13:04'),('1425011630752735234','1463297492196085761','2022-01-13 08:13:04'),('1425011630752735234','1425384255047946241','2022-01-13 08:13:04'),('1425011630752735234','1462047178541559809','2022-01-13 08:13:04'),('1425011630752735234','1425384412992856065','2022-01-13 08:13:04'),('1425011630752735234','1481059726834106370','2022-01-13 08:13:04'),('1','1425384413584252930','2022-01-19 19:58:10'),('1','1425381271220101122','2022-01-19 19:58:10'),('1','1462047178541559809','2022-01-19 19:58:10'),('1484008013073489921','1425384413584252930','2022-01-20 11:47:12'),('1484008013073489921','1425381271220101122','2022-01-20 11:47:12'),('1484008013073489921','1462047178541559809','2022-01-20 11:47:12'),('1425011630752735234','1484091994427174914','2022-01-20 17:14:39'),('1425011630752735234','1484092161368862722','2022-01-20 17:15:19'),('1425011630752735234','1484114216445358081','2022-01-20 18:42:57'),('1425011630752735234','1486984444607004673','2022-01-28 16:48:13');
/*!40000 ALTER TABLE `sys_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id',
  `login_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录名称',
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `real_name` varchar(20) DEFAULT NULL COMMENT '真实姓名',
  `gender` tinyint(1) DEFAULT NULL COMMENT '性别0：女1：男',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `phone` varchar(11) DEFAULT NULL COMMENT '电话',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态0：禁用1：正常',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `email` varchar(20) DEFAULT NULL COMMENT '邮箱',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(20) DEFAULT NULL COMMENT '创建人',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  `modified_by` varchar(20) DEFAULT NULL COMMENT '修改人',
  `del_flag` tinyint(1) DEFAULT '1' COMMENT '逻辑删除0：删除1：正常',
  `version` bigint DEFAULT '1' COMMENT '版本号',
  `account_locked` tinyint DEFAULT '1' COMMENT '账号是否被锁定0：锁定1：正常',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES ('1','jack','$2a$10$WJNCkw.EEwjJHKiHWUbZoe.XUqrxES4b9m4rWDmaHAVhHMQqGO9DS','小晨',1,'1998-06-15','',1,'http://r67xhbta9.hn-bkt.clouddn.com/20220127081538_1.jpg',NULL,'2021-09-14 00:00:00',NULL,'2022-01-24 08:23:10','1425004256210038785',1,1,1),('1425004256210038785','tom','$2a$10$ZNqS1AT8Lw0J68u60lMGrONRbpFJ/O8qiBrbWPi40b.uLM3HC4f3G','小杨',1,'1770-01-01','18270917877',1,'http://r67xhbta9.hn-bkt.clouddn.com/20220305165734_1425004256210038785.jpg','tom@163.com','2021-08-10 00:00:00',NULL,'2022-03-05 16:58:02','1425004256210038785',1,NULL,1),('1483764958705852417','www','$2a$10$pqsXe26VDomSb/P9YkjjieIHzdPEHds0fLq5b9a0FG1qIWRMtRx.m','2222',1,'2004-06-25','15825652541',1,'http://r67xhbta9.hn-bkt.clouddn.com/20220127081625_1483764958705852417.jpg','222@qq.com','2022-01-19 00:00:00','1425004256210038785','2022-01-20 11:29:55','1425004256210038785',1,1,1),('1484059335881662465','尊敬的弗拉基米尔伯爵二世','$2a$10$/RziMD8yXb95fvZXo1kUT.tnj4XMdIWDysVmY3cvt2DAepHgm47Yi','尊敬的弗拉基米尔伯爵',0,'2022-01-04',NULL,1,NULL,NULL,'2022-01-20 00:00:00','1425004256210038785','2022-01-27 09:14:28','1425004256210038785',1,1,1),('1485569489789837313','aaa','$2a$10$O1nAXEJp419a2jdFX0gWB.3Do/Pr5YLbXzRwN6vQJpDVBnTCDSJxa','aaa',1,NULL,NULL,1,NULL,NULL,'2022-01-24 19:05:42','1425004256210038785',NULL,NULL,1,1,1),('1486508175301906434','alice','$2a$10$PbkGcKwOaHnryGtpHpAMAumcpBlrqN6nglKNQuy.hJhNURq3Mo1IC','爱丽丝',0,NULL,NULL,1,NULL,NULL,'2022-01-27 09:15:42','1425004256210038785',NULL,NULL,1,1,1),('1486508328414973954','dina','$2a$10$SzCKiqWSCUzO914TO8XsEeha/H72S7iwoUjhc8rIcc1.T7nlPRCw6','黛娜',0,NULL,NULL,1,NULL,NULL,'2022-01-27 09:16:18','1425004256210038785',NULL,NULL,1,1,1),('1486508482861830146','jane','$2a$10$CM4EuA0DIs4x4J12RozmbOentUH/DVD4Tx5b83iQeoNTUeAmY/YHu','简123',0,NULL,NULL,1,NULL,NULL,'2022-01-27 00:00:00','1425004256210038785','2022-03-01 20:03:31','1425004256210038785',1,1,1),('1486508911700054018','anna','$2a$10$xkMqm4fgT8XIoEpl/nn/SOfVsJwyVM8GhdSGi7jcExJon.lfK1JPO','安娜',1,NULL,NULL,1,NULL,NULL,'2022-01-27 09:18:37','1425004256210038785',NULL,NULL,1,1,1),('1486508971322085378','bob','$2a$10$R67uE/okZw5.6s7kPI46ZOWLrLnMZwsu/maCsNDEBthbCtJ7j8H8m','鲍勃',1,NULL,NULL,1,'http://r67xhbta9.hn-bkt.clouddn.com/20220127093137_1486508971322085378.jpg',NULL,'2022-01-27 09:18:51','1425004256210038785',NULL,NULL,1,1,1),('1486509025365692417','lucy','$2a$10$5M0Ez872DiGr0Jzz9.Uj8OYUR7HrenJQMme/d/80hWIFdl1vM8JPG','露西',0,NULL,NULL,1,NULL,NULL,'2022-01-27 09:19:04','1425004256210038785',NULL,NULL,1,1,1);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `role_id` varchar(50) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES ('1','1484007915195211778','2022-01-24 08:23:10'),('1425004256210038785','1425011630752735234','2022-01-27 08:54:47'),('1425004256210038785','1484008391340990466','2022-01-27 08:54:47'),('1425004256210038785','1484008120518975489','2022-01-27 08:54:47'),('1425004256210038785','1484008013073489921','2022-01-27 08:54:47'),('1425004256210038785','1484008292372193281','2022-01-27 08:54:47'),('1484059335881662465','1484008391340990466','2022-01-27 09:14:28'),('1486508175301906434','1','2022-01-27 09:15:42'),('1486508328414973954','1484008292372193281','2022-01-27 09:16:18');
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'db_autumn'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-03-05 18:55:05
