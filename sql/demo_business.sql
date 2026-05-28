/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80409 (8.4.9)
 Source Host           : localhost:3306
 Source Schema         : demo_business

 Target Server Type    : MySQL
 Target Server Version : 80409 (8.4.9)
 File Encoding         : 65001

 Date: 28/05/2026 22:21:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_acceptance
-- ----------------------------
DROP TABLE IF EXISTS `t_acceptance`;
CREATE TABLE `t_acceptance`  (
  `id` bigint NOT NULL COMMENT '主键',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '采购订单号',
  `supplier_id` bigint NULL DEFAULT NULL COMMENT '供应商ID',
  `supplier_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '供应商名称(冗余)',
  `arrival_date` date NULL DEFAULT NULL COMMENT '到货日期',
  `inspector_id` bigint NULL DEFAULT NULL COMMENT '验收人ID',
  `inspector_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '验收人姓名',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注/验收记录文本',
  `summary_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI 摘要 JSON',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_supplier`(`supplier_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '验收单主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_acceptance
-- ----------------------------
INSERT INTO `t_acceptance` VALUES (1001, 'PO20260520-001', 1, '北京源辉天然气设备有限公司', '2026-05-20', 2, 'Alice', 'PENDING', '调压器到货5台，外观检查待开展', NULL, '2026-05-28 03:29:08', '2026-05-28 03:29:08');
INSERT INTO `t_acceptance` VALUES (1002, 'PO20260518-002', 2, '上海远景管道工程有限公司', '2026-05-18', 2, 'Alice', 'PASSED', '阀门到货已开箱，3台铭牌磨损待复核', '{\"deviceSummary\":\"球阀10台、截止阀6台，共16台阀门到货验收\",\"inspectionOverview\":\"对外观、铭牌、规格、数量、功能等进行检查，球阀全部合格，截止阀有3台铭牌字迹磨损需让步接收\",\"issues\":\"3台截止阀铭牌字迹磨损，已要求供应商补发铭牌贴\",\"conclusion\":\"CONCESSION\",\"conclusionText\":\"让步接收\"}', '2026-05-28 03:29:08', '2026-05-28 03:29:08');
INSERT INTO `t_acceptance` VALUES (1003, 'PO20260510-003', 3, '深圳启明流体控制有限公司', '2026-05-10', 3, 'Bob', 'PASSED', '流量计批次验收完成，全部合格', NULL, '2026-05-28 03:29:08', '2026-05-28 03:29:08');
INSERT INTO `t_acceptance` VALUES (2059867584973344770, '456', NULL, '456546', '2026-05-16', 1, '456456', 'PENDING', 'hrthsrhrhrthr', NULL, '2026-05-28 13:21:17', '2026-05-28 13:21:17');
INSERT INTO `t_acceptance` VALUES (2059870618184945665, '54645', NULL, '6456', '2026-05-06', 1, 'y45y54y', 'RECTIFIED', 'hrthrsh', '{\"deviceSummary\":\"456型设备2台\",\"inspectionOverview\":\"外观/铭牌/规格/数量/功能等检查点，其中1项合格，1项让步接收\",\"issues\":\"无\",\"conclusion\":\"CONCESSION\",\"conclusionText\":\"让步接收\"}', '2026-05-28 13:33:21', '2026-05-28 13:33:21');
INSERT INTO `t_acceptance` VALUES (2059900484972294146, 'HHHHHHHHHHH', 2, '上海远景管道工程有限公司', '2026-05-20', 1, '默认', 'PENDING', '哥哥哥哥哥哥人', NULL, '2026-05-28 15:32:01', '2026-05-28 15:32:01');

-- ----------------------------
-- Table structure for t_acceptance_item
-- ----------------------------
DROP TABLE IF EXISTS `t_acceptance_item`;
CREATE TABLE `t_acceptance_item`  (
  `id` bigint NOT NULL COMMENT '主键',
  `acceptance_id` bigint NOT NULL COMMENT '验收单ID',
  `device_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备编码',
  `device_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备名称',
  `device_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备类型',
  `spec` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规格型号',
  `unit` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位',
  `qty` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '数量',
  `result` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'QUALIFIED' COMMENT '验收结果',
  `defect_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '缺陷描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_acceptance`(`acceptance_id` ASC) USING BTREE,
  INDEX `idx_device_type`(`device_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '验收单明细行' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_acceptance_item
-- ----------------------------
INSERT INTO `t_acceptance_item` VALUES (2001, 1001, 'REG-001', '智能调压器', '调压器', 'DN50 0.4MPa', '台', 5.00, 'QUALIFIED', '', '2026-05-28 03:29:08', '2026-05-28 03:29:08');
INSERT INTO `t_acceptance_item` VALUES (2002, 1002, 'VAL-001', '球阀', '阀门', 'DN100 PN16', '台', 10.00, 'QUALIFIED', '', '2026-05-28 03:29:08', '2026-05-28 03:29:08');
INSERT INTO `t_acceptance_item` VALUES (2003, 1002, 'VAL-002', '截止阀', '阀门', 'DN80 PN16', '台', 6.00, 'CONCESSION', '3台铭牌字迹磨损，已要求供应商补发铭牌贴', '2026-05-28 03:29:08', '2026-05-28 03:29:08');
INSERT INTO `t_acceptance_item` VALUES (2004, 1003, 'FLO-001', '超声波流量计', '流量计', 'DN150', '台', 4.00, 'QUALIFIED', '', '2026-05-28 03:29:08', '2026-05-28 03:29:08');
INSERT INTO `t_acceptance_item` VALUES (2005, 1003, 'FLO-002', '涡轮流量计', '流量计', 'DN80', '台', 8.00, 'QUALIFIED', '', '2026-05-28 03:29:08', '2026-05-28 03:29:08');
INSERT INTO `t_acceptance_item` VALUES (2059867584985927682, 2059867584973344770, 'h', 't54', '45', '45', '台', 1.00, 'QUALIFIED', '', '2026-05-28 13:21:17', '2026-05-28 13:21:17');
INSERT INTO `t_acceptance_item` VALUES (2059898609564758018, 2059870618184945665, '344', '456', '', '456', '台', 1.00, 'QUALIFIED', '', '2026-05-28 15:24:34', '2026-05-28 15:24:34');
INSERT INTO `t_acceptance_item` VALUES (2059898609564758019, 2059870618184945665, '43', '456', '', '456', '台', 1.00, 'CONCESSION', '', '2026-05-28 15:24:34', '2026-05-28 15:24:34');
INSERT INTO `t_acceptance_item` VALUES (2059900484972294147, 2059900484972294146, '4', '34', '', '34', '台', 1.00, 'QUALIFIED', '', '2026-05-28 15:32:01', '2026-05-28 15:32:01');

-- ----------------------------
-- Table structure for t_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `t_operation_log`;
CREATE TABLE `t_operation_log`  (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人姓名',
  `op_time` datetime NOT NULL COMMENT '操作时间',
  `op_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
  `target_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标对象类型',
  `target_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标对象ID',
  `summary` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '摘要描述',
  `before_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '变更前 JSON',
  `after_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '变更后 JSON',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_op_time`(`op_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_operation_log
-- ----------------------------
INSERT INTO `t_operation_log` VALUES (2059867585048842241, 1, 'admin', '2026-05-28 13:21:17', 'CREATE', 'ACCEPTANCE', '2059867584973344770', '创建验收单 456', NULL, NULL, '2026-05-28 13:21:17', '2026-05-28 13:21:17');
INSERT INTO `t_operation_log` VALUES (2059870618184945668, 1, 'admin', '2026-05-28 13:33:21', 'CREATE', 'ACCEPTANCE', '2059870618184945665', '创建验收单 54645', NULL, NULL, '2026-05-28 13:33:21', '2026-05-28 13:33:21');
INSERT INTO `t_operation_log` VALUES (2059872258019397634, 1, 'admin', '2026-05-28 13:39:52', 'AI_SUMMARY', 'ACCEPTANCE', '1002', '生成 AI 验收结论：让步接收', NULL, NULL, '2026-05-28 13:39:52', '2026-05-28 13:39:52');
INSERT INTO `t_operation_log` VALUES (2059872273181806593, 1, 'admin', '2026-05-28 13:39:55', 'UPDATE', 'ACCEPTANCE', '1002', '保存 AI 摘要', NULL, NULL, '2026-05-28 13:39:55', '2026-05-28 13:39:55');
INSERT INTO `t_operation_log` VALUES (2059876548289073154, 1, 'admin', '2026-05-28 13:56:54', 'AI_SUMMARY', 'ACCEPTANCE', '1002', '生成 AI 验收结论：让步接收', NULL, NULL, '2026-05-28 13:56:54', '2026-05-28 13:56:54');
INSERT INTO `t_operation_log` VALUES (2059876759853961218, 1, 'admin', '2026-05-28 13:57:45', 'STATUS_CHANGE', 'ACCEPTANCE', '1002', '状态：验收中 → 整改中', 'IN_PROGRESS', 'RECTIFYING', '2026-05-28 13:57:45', '2026-05-28 13:57:45');
INSERT INTO `t_operation_log` VALUES (2059876778581528577, 1, 'admin', '2026-05-28 13:57:49', 'STATUS_CHANGE', 'ACCEPTANCE', '1002', '状态：整改中 → 整改完成', 'RECTIFYING', 'RECTIFIED', '2026-05-28 13:57:49', '2026-05-28 13:57:49');
INSERT INTO `t_operation_log` VALUES (2059876794016567298, 1, 'admin', '2026-05-28 13:57:53', 'STATUS_CHANGE', 'ACCEPTANCE', '1002', '状态：整改完成 → 验收通过', 'RECTIFIED', 'PASSED', '2026-05-28 13:57:53', '2026-05-28 13:57:53');
INSERT INTO `t_operation_log` VALUES (2059898482452180993, 1, 'admin', '2026-05-28 15:24:04', 'AI_SUMMARY', 'ACCEPTANCE', '2059870618184945665', '生成 AI 验收结论：让步接收', NULL, NULL, '2026-05-28 15:24:04', '2026-05-28 15:24:04');
INSERT INTO `t_operation_log` VALUES (2059898501079085060, 1, 'admin', '2026-05-28 15:24:08', 'UPDATE', 'ACCEPTANCE', '2059870618184945665', '更新验收单 54645', NULL, NULL, '2026-05-28 15:24:08', '2026-05-28 15:24:08');
INSERT INTO `t_operation_log` VALUES (2059898520817479682, 1, 'admin', '2026-05-28 15:24:13', 'STATUS_CHANGE', 'ACCEPTANCE', '2059870618184945665', '状态：待验收 → 验收中', 'PENDING', 'IN_PROGRESS', '2026-05-28 15:24:13', '2026-05-28 15:24:13');
INSERT INTO `t_operation_log` VALUES (2059898566015299586, 1, 'admin', '2026-05-28 15:24:24', 'UPDATE', 'ACCEPTANCE', '2059870618184945665', '更新验收单 54645', NULL, NULL, '2026-05-28 15:24:24', '2026-05-28 15:24:24');
INSERT INTO `t_operation_log` VALUES (2059898599586508803, 1, 'admin', '2026-05-28 15:24:32', 'STATUS_CHANGE', 'ACCEPTANCE', '2059870618184945665', '状态：验收中 → 整改中', 'IN_PROGRESS', 'RECTIFYING', '2026-05-28 15:24:32', '2026-05-28 15:24:32');
INSERT INTO `t_operation_log` VALUES (2059898609564758020, 1, 'admin', '2026-05-28 15:24:34', 'UPDATE', 'ACCEPTANCE', '2059870618184945665', '更新验收单 54645', NULL, NULL, '2026-05-28 15:24:34', '2026-05-28 15:24:34');
INSERT INTO `t_operation_log` VALUES (2059898619182297089, 1, 'admin', '2026-05-28 15:24:37', 'STATUS_CHANGE', 'ACCEPTANCE', '2059870618184945665', '状态：整改中 → 整改完成', 'RECTIFYING', 'RECTIFIED', '2026-05-28 15:24:37', '2026-05-28 15:24:37');
INSERT INTO `t_operation_log` VALUES (2059900484972294148, 1, 'admin', '2026-05-28 15:32:01', 'CREATE', 'ACCEPTANCE', '2059900484972294146', '创建验收单 HHHHHHHHHHH', NULL, NULL, '2026-05-28 15:32:01', '2026-05-28 15:32:01');

-- ----------------------------
-- Table structure for t_rectification
-- ----------------------------
DROP TABLE IF EXISTS `t_rectification`;
CREATE TABLE `t_rectification`  (
  `id` bigint NOT NULL COMMENT '主键',
  `acceptance_id` bigint NOT NULL COMMENT '验收单ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '整改内容',
  `owner` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '责任人',
  `deadline` date NULL DEFAULT NULL COMMENT '截止日期',
  `finished` tinyint NOT NULL DEFAULT 0 COMMENT '0=未完成 1=已完成',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_acceptance`(`acceptance_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '整改记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_rectification
-- ----------------------------
INSERT INTO `t_rectification` VALUES (2059876759853961217, 1002, 'gegeagerg', 'gergeg', '2026-05-14', 1, '2026-05-28 13:57:45', '2026-05-28 13:57:45');
INSERT INTO `t_rectification` VALUES (2059898599586508802, 2059870618184945665, 'gregerge', 'hhhhh', '2026-05-14', 1, '2026-05-28 15:24:32', '2026-05-28 15:24:32');

SET FOREIGN_KEY_CHECKS = 1;
