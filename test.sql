/*
Navicat MySQL Data Transfer

Source Server         : locahost
Source Server Version : 50539
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50539
File Encoding         : 65001

Date: 2017-11-14 13:00:18
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `i_order`
-- ----------------------------
DROP TABLE IF EXISTS `i_order`;
CREATE TABLE `i_order` (
  `tel` varchar(36) NOT NULL,
  `packet_id` varchar(36) NOT NULL,
  PRIMARY KEY (`tel`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of i_order
-- ----------------------------

-- ----------------------------
-- Table structure for `i_packet`
-- ----------------------------
DROP TABLE IF EXISTS `i_packet`;
CREATE TABLE `i_packet` (
  `id` varchar(36) NOT NULL,
  `name` varchar(36) DEFAULT NULL,
  `value` int(8) DEFAULT NULL,
  `tel` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of i_packet
-- ----------------------------

-- ----------------------------
-- Procedure structure for `bind`
-- ----------------------------
DROP PROCEDURE IF EXISTS `bind`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `bind`(IN i_id varchar(36), IN i_tel varchar(30), OUT o_result int)
BEGIN
DECLARE c int;
DECLARE c2 int;
select count(0) into c from i_order where tel = i_tel ;
if (c = 0)  then 
  insert into i_order(tel, packet_id) values(i_tel, i_id);
  set o_result = 1;
else
 set o_result = -1;
end if;
END
;;
DELIMITER ;
