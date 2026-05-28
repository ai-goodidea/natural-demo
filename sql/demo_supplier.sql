/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80409 (8.4.9)
 Source Host           : localhost:3306
 Source Schema         : demo_supplier

 Target Server Type    : MySQL
 Target Server Version : 80409 (8.4.9)
 File Encoding         : 65001

 Date: 28/05/2026 22:21:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_supplier
-- ----------------------------
DROP TABLE IF EXISTS `t_supplier`;
CREATE TABLE `t_supplier`  (
  `id` bigint NOT NULL COMMENT '主键(雪花ID)',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '供应商编码',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '供应商名称',
  `contact` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `credit_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '信用分(0-100)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '1=合作中 0=停用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '供应商表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_supplier
-- ----------------------------
INSERT INTO `t_supplier` VALUES (1, 'SUP001', '北京源辉天然气设备有限公司', '王经理', '010-88880001', '北京市朝阳区', 92.50, 1, '2026-05-28 03:29:08', '2026-05-28 03:29:08');
INSERT INTO `t_supplier` VALUES (2, 'SUP002', '上海远景管道工程有限公司', '李经理', '021-88880002', '上海市浦东新区', 85.00, 1, '2026-05-28 03:29:08', '2026-05-28 03:29:08');
INSERT INTO `t_supplier` VALUES (3, 'SUP003', '深圳启明流体控制有限公司', '张经理', '0755-8888003', '深圳市南山区', 78.50, 1, '2026-05-28 03:29:08', '2026-05-28 03:29:08');
INSERT INTO `t_supplier` VALUES (2059871720947154945, 'ger', '345', '345', '345', '345', 100.00, 1, '2026-05-28 13:37:43', '2026-05-28 13:37:43');

SET FOREIGN_KEY_CHECKS = 1;
