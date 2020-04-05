/*
Navicat MySQL Data Transfer

Source Server         : work
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : jeecg-boot

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2020-04-05 11:31:17
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for test_demo_car
-- ----------------------------
DROP TABLE IF EXISTS `test_demo_car`;
CREATE TABLE `test_demo_car` (
  `id` varchar(36) NOT NULL,
  `serial_number` int(11) DEFAULT NULL COMMENT '序号',
  `name` varchar(32) NOT NULL COMMENT '车名',
  `alias` varchar(32) NOT NULL COMMENT '别名',
  `type` varchar(16) NOT NULL COMMENT '车类型',
  `identification_code` varchar(16) NOT NULL COMMENT '识别码',
  `suggest_price` int(11) NOT NULL COMMENT '指导价',
  `logo_img` varchar(32) DEFAULT NULL COMMENT 'logo图',
  `type_img` varchar(32) DEFAULT NULL COMMENT '类型图',
  `link` varchar(128) DEFAULT NULL COMMENT '链接',
  `is_new` int(2) DEFAULT NULL COMMENT '是否新品，1新品，0非新品',
  `create_by` varchar(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(6) DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(20) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime(6) DEFAULT NULL COMMENT '更新日期',
  `pid` varchar(32) DEFAULT NULL COMMENT '父级节点',
  `has_child` varchar(3) DEFAULT NULL COMMENT '是否有子节点',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of test_demo_car
-- ----------------------------
INSERT INTO `test_demo_car` VALUES ('1245917782845038593', '77', '全新换代雷凌', '2019newlevin', '轿车', '004036', '0', '', null, null, null, 'admin', '2020-04-03 11:35:22.000000', null, null, '0', null);
INSERT INTO `test_demo_car` VALUES ('1246092837034000385', '2', '雷凌双擎E+', 'levinphev', '新能源', '004031', '232388', null, null, 'https://www.gac-toyota.com.cn/vehicles/levinphev', '1', 'admin', '2020-04-03 23:08:28.000000', null, null, '0', null);
INSERT INTO `test_demo_car` VALUES ('1246100228093902849', '3', 'C-HR', 'chr', 'SUV/MPV', '004027', '141800', 'temp/logo_2_1585928350154.png', 'temp/CHR-PCnew_1585928400833.png', 'https://www.gac-toyota.com.cn/vehicles/chr', '1', null, '2020-04-03 23:40:20.000000', null, null, '0', null);
INSERT INTO `test_demo_car` VALUES ('1246286497700855810', '1', '埃尔法', 'alphard', 'SUV/MPV', '\'004030', '772000', 'temp/logo_2_1585972794047.png', 'temp/yarisL1_1585972816690.png', 'https://www.gac-toyota.com.cn/vehicles/alphard', '1', 'admin', '2020-04-04 12:00:30.591000', 'admin', '2020-04-04 15:24:44.000000', '0', null);
