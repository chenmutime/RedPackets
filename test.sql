/*
Navicat MySQL Data Transfer

Source Server         : locahost
Source Server Version : 50539
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50539
File Encoding         : 65001

Date: 2017-11-13 20:11:41
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
  `tel` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of packet
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
START TRANSACTION;
select count(0) into c from packet where tel = i_tel;
if (c = 0)  then 
   update packet set tel = i_tel where id = i_id;
  set o_result = 1;
  COMMIT;
else
 set o_result = -1;
end if;
END
;;
DELIMITER ;
