/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50521
Source Host           : localhost:3306
Source Database       : verse-db

Target Server Type    : MYSQL
Target Server Version : 50521
File Encoding         : 65001

Date: 2012-04-13 15:17:11
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `actors`
-- ----------------------------
DROP TABLE IF EXISTS `actors`;
CREATE TABLE `actors` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `owner` varchar(45) COLLATE latin1_german1_ci DEFAULT NULL,
  `name` varchar(35) COLLATE latin1_german1_ci NOT NULL,
  `blueprint` mediumint(8) NOT NULL,
  `hero` tinyint(1) DEFAULT NULL,
  `exp` bigint(20) DEFAULT NULL,
  `x` mediumint(9) DEFAULT NULL,
  `y` mediumint(9) DEFAULT NULL,
  `heading` mediumint(9) DEFAULT NULL,
  `curHp` mediumint(8) DEFAULT NULL,
  `curShield` mediumint(8) DEFAULT NULL,
  `kills` int(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=latin1 COLLATE=latin1_german1_ci ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of actors
-- ----------------------------
INSERT INTO `actors` VALUES ('1', 'hosh', 'superkato', '1', '1', '0', '1009', '1009', '0', '23', '4', '0');
INSERT INTO `actors` VALUES ('12', 'hosh', 'Millenium Falcon', '1', '1', '0', '1000', '1090', '0', '5', '5', '0');
INSERT INTO `actors` VALUES ('14', '', 'Slave 1', '2', '0', '0', '1200', '1000', '0', '5', '5', '0');
INSERT INTO `actors` VALUES ('15', '', 'Slave 2', '2', '0', '0', '1200', '1200', '0', '5', '5', '0');
INSERT INTO `actors` VALUES ('16', '', 'Slave 3', '2', '0', '0', '1000', '1200', '0', '5', '5', '0');
INSERT INTO `actors` VALUES ('17', '', 'Tramiel ', '2', '1', '0', '2000', '2000', '0', '5', '5', '0');
INSERT INTO `actors` VALUES ('19', 'emmel', 'Millenium Falcon', '1', '1', '0', '1000', '1090', '0', '5', '5', '0');
INSERT INTO `actors` VALUES ('20', 'tarkin', 'Millenium Falcon', '1', '1', '0', '1000', '1090', '0', '5', '5', '0');
INSERT INTO `actors` VALUES ('21', 'android', 'Millenium Falcon', '1', '1', '0', '1000', '1090', '0', '5', '5', '0');

-- ----------------------------
-- Table structure for `blueprints`
-- ----------------------------
DROP TABLE IF EXISTS `blueprints`;
CREATE TABLE `blueprints` (
  `id` smallint(5) NOT NULL AUTO_INCREMENT,
  `type_name` varchar(200) COLLATE latin1_german1_ci DEFAULT NULL,
  `type_id` smallint(5) DEFAULT NULL,
  `model_id` smallint(5) DEFAULT NULL,
  `scale` mediumint(8) DEFAULT NULL,
  `extension_slots` smallint(3) DEFAULT NULL,
  `aggro` smallint(4) DEFAULT NULL,
  `color_r` decimal(4,3) DEFAULT NULL,
  `color_g` decimal(4,3) DEFAULT NULL,
  `color_b` decimal(4,3) DEFAULT NULL,
  `color_a` decimal(4,3) DEFAULT NULL,
  `collision_radius` decimal(6,2) DEFAULT NULL,
  `attack_range` smallint(4) DEFAULT NULL,
  `hp` mediumint(8) DEFAULT NULL,
  `shield` mediumint(8) DEFAULT NULL,
  `speed` smallint(3) DEFAULT NULL,
  `rotation_speed` smallint(3) DEFAULT NULL,
  `attack` smallint(3) DEFAULT NULL,
  `defense` smallint(3) DEFAULT NULL,
  `fuel_tank` mediumint(8) DEFAULT NULL,
  `cargo_space` mediumint(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COLLATE=latin1_german1_ci ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of blueprints
-- ----------------------------
INSERT INTO `blueprints` VALUES ('1', 'YT-1300', '1', '1', '1', '1', '10', '1.000', '0.000', '0.000', '1.000', '16.00', '10', '10', '10', '20', '50', '5', '5', '20', '10');
INSERT INTO `blueprints` VALUES ('2', 'C64', '2', '2', '1', '0', '20', '1.000', '0.500', '0.500', '1.000', '16.00', '10', '10', '12', '15', '40', '5', '4', '20', '2');

-- ----------------------------
-- Table structure for `characters`
-- ----------------------------
DROP TABLE IF EXISTS `characters`;
CREATE TABLE `characters` (
  `account_name` varchar(45) COLLATE latin1_german1_ci DEFAULT NULL,
  `charId` int(10) NOT NULL,
  `char_name` varchar(35) COLLATE latin1_german1_ci NOT NULL,
  `exp` bigint(20) DEFAULT NULL,
  `level` tinyint(3) DEFAULT NULL,
  `collision_radius` decimal(6,2) DEFAULT NULL,
  `x` mediumint(9) DEFAULT NULL,
  `y` mediumint(9) DEFAULT NULL,
  `heading` mediumint(9) DEFAULT NULL,
  `maxHp` mediumint(8) DEFAULT NULL,
  `curHp` mediumint(8) DEFAULT NULL,
  `class_id` tinyint(3) DEFAULT NULL,
  `maxShield` mediumint(8) DEFAULT NULL,
  `curShield` mediumint(8) DEFAULT NULL,
  PRIMARY KEY (`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_german1_ci;

-- ----------------------------
-- Records of characters
-- ----------------------------
INSERT INTO `characters` VALUES ('hosh', '1', 'hosh', '0', '0', '16.00', '1000', '1000', '0', '10', '10', '0', '10', '10');
INSERT INTO `characters` VALUES ('emmel', '2', 'emmel', '0', '0', '16.00', '1050', '1000', '0', '10', '10', '0', '10', '10');
INSERT INTO `characters` VALUES ('tarkin', '3', 'tarkin', '0', '0', '16.00', '1000', '1050', '0', '10', '10', '0', '10', '10');
INSERT INTO `characters` VALUES ('android', '4', 'android', '0', '0', '16.00', '1050', '1050', '0', '10', '10', '0', '10', '10');

-- ----------------------------
-- Table structure for `engines`
-- ----------------------------
DROP TABLE IF EXISTS `engines`;
CREATE TABLE `engines` (
  `ext_id` binary(16) NOT NULL,
  `name` varchar(200) COLLATE latin1_german1_ci NOT NULL,
  `grade` tinyint(4) NOT NULL,
  `mass` mediumint(8) NOT NULL,
  `thrust` mediumint(8) NOT NULL,
  `energy_consumption` mediumint(8) NOT NULL,
  `condition` decimal(6,2) NOT NULL,
  PRIMARY KEY (`ext_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_german1_ci;

-- ----------------------------
-- Records of engines
-- ----------------------------

-- ----------------------------
-- Table structure for `extensions`
-- ----------------------------
DROP TABLE IF EXISTS `extensions`;
CREATE TABLE `extensions` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `type` varchar(45) COLLATE latin1_german1_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_german1_ci;

-- ----------------------------
-- Records of extensions
-- ----------------------------

-- ----------------------------
-- Table structure for `owners`
-- ----------------------------
DROP TABLE IF EXISTS `owners`;
CREATE TABLE `owners` (
  `login` varchar(45) COLLATE latin1_german1_ci NOT NULL,
  `password` varchar(45) COLLATE latin1_german1_ci DEFAULT NULL,
  `lastactive` decimal(20,0) DEFAULT NULL,
  `accessLevel` tinyint(4) NOT NULL,
  `lastIP` char(15) COLLATE latin1_german1_ci DEFAULT NULL,
  `lastActor` int(10) DEFAULT NULL,
  PRIMARY KEY (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_german1_ci;

-- ----------------------------
-- Records of owners
-- ----------------------------
INSERT INTO `owners` VALUES ('android', '109', null, '1', null, null);
INSERT INTO `owners` VALUES ('emmel', '109', null, '1', null, null);
INSERT INTO `owners` VALUES ('hosh', '109', null, '1', null, '12');
INSERT INTO `owners` VALUES ('tarkin', '109', null, '1', null, null);
