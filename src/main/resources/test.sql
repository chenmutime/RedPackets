/*
Navicat MySQL Data Transfer

Source Server         : locahost
Source Server Version : 50539
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50539
File Encoding         : 65001

Date: 2017-11-08 15:33:36
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `packet`
-- ----------------------------
DROP TABLE IF EXISTS `packet`;
CREATE TABLE `packet` (
  `id` varchar(36) NOT NULL,
  `name` varchar(36) DEFAULT NULL,
  `value` int(8) DEFAULT NULL,
  `tel` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of packet
-- ----------------------------

-- ----------------------------
-- Procedure structure for `sim_pro`
-- ----------------------------
DROP PROCEDURE IF EXISTS `sim_pro`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sim_pro`(param int)
begin declare var char(10); if param=1 then set var='hmm'; else set var='zs'; end if; insert into user values(param,var); end
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for `sim_pro2`
-- ----------------------------
DROP PROCEDURE IF EXISTS `sim_pro2`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sim_pro2`(in param int, out param2 varchar(10))
begin declare var char(10); if param=1 then set var='hmm'; else set var='zs'; end if; insert into user values(param,var);select name from user limit 1 into param2; end
;;
DELIMITER ;
