-- ============================================================
-- natural-demo 数据库初始化脚本
--   demo_user      用户管理
--   demo_supplier  供应商管理
--   demo_business  验收业务（验收单 + 明细 + 整改 + 操作日志）
-- 字符集 utf8mb4 / 时区 Asia/Shanghai
-- ============================================================

CREATE DATABASE IF NOT EXISTS demo_user
    DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;

USE demo_user;

DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id          BIGINT       NOT NULL                COMMENT '主键(雪花ID)',
    username    VARCHAR(64)  NOT NULL                COMMENT '登录名',
    password    VARCHAR(128) NULL                    COMMENT '密码(加密后)',
    nickname    VARCHAR(64)  NULL                    COMMENT '昵称',
    email       VARCHAR(128) NULL                    COMMENT '邮箱',
    phone       VARCHAR(32)  NULL                    COMMENT '手机号',
    avatar      VARCHAR(255) NULL                    COMMENT '头像',
    roles       VARCHAR(255) NULL                    COMMENT '角色，逗号分隔',
    status      TINYINT      NOT NULL DEFAULT 1      COMMENT '1=正常 0=禁用',
    create_time DATETIME     NOT NULL                COMMENT '创建时间',
    update_time DATETIME     NOT NULL                COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_phone (phone),
    KEY idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '用户表';

INSERT INTO t_user (id, username, nickname, email, phone, roles, status, create_time, update_time) VALUES
(1, 'admin',  '管理员', 'admin@example.com',  '13800000000', 'admin',     1, NOW(), NOW()),
(2, 'alice',  'Alice', 'alice@example.com',  '13800000001', 'inspector', 1, NOW(), NOW()),
(3, 'bob',    'Bob',   'bob@example.com',    '13800000002', 'inspector', 1, NOW(), NOW());

-- ============================================================

CREATE DATABASE IF NOT EXISTS demo_supplier
    DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;

USE demo_supplier;

DROP TABLE IF EXISTS t_supplier;
CREATE TABLE t_supplier (
    id           BIGINT        NOT NULL                COMMENT '主键(雪花ID)',
    code         VARCHAR(64)   NOT NULL                COMMENT '供应商编码',
    name         VARCHAR(128)  NOT NULL                COMMENT '供应商名称',
    contact      VARCHAR(64)   NULL                    COMMENT '联系人',
    phone        VARCHAR(32)   NULL                    COMMENT '联系电话',
    address      VARCHAR(255)  NULL                    COMMENT '地址',
    credit_score DECIMAL(5,2)  NULL                    COMMENT '信用分(0-100)',
    status       TINYINT       NOT NULL DEFAULT 1      COMMENT '1=合作中 0=停用',
    create_time  DATETIME      NOT NULL                COMMENT '创建时间',
    update_time  DATETIME      NOT NULL                COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code),
    KEY idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '供应商表';

INSERT INTO t_supplier (id, code, name, contact, phone, address, credit_score, status, create_time, update_time) VALUES
(1, 'SUP001', '北京源辉天然气设备有限公司', '王经理', '010-88880001', '北京市朝阳区', 92.50, 1, NOW(), NOW()),
(2, 'SUP002', '上海远景管道工程有限公司',   '李经理', '021-88880002', '上海市浦东新区', 85.00, 1, NOW(), NOW()),
(3, 'SUP003', '深圳启明流体控制有限公司',   '张经理', '0755-8888003', '深圳市南山区', 78.50, 1, NOW(), NOW());

-- ============================================================
-- demo_business：验收业务
-- 状态枚举：PENDING(待验收) / IN_PROGRESS(验收中) / PASSED(验收通过) / RETURNED(退货)
--          / RECTIFYING(整改中) / RECTIFIED(整改完成)
-- 明细验收结果：QUALIFIED(合格) / CONCESSION(让步接收) / RETURNED(退货)
-- ============================================================

CREATE DATABASE IF NOT EXISTS demo_business
    DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;

USE demo_business;

DROP TABLE IF EXISTS t_acceptance;
CREATE TABLE t_acceptance (
    id              BIGINT        NOT NULL                COMMENT '主键',
    order_no        VARCHAR(64)   NOT NULL                COMMENT '采购订单号',
    supplier_id     BIGINT        NULL                    COMMENT '供应商ID',
    supplier_name   VARCHAR(128)  NULL                    COMMENT '供应商名称(冗余)',
    arrival_date    DATE          NULL                    COMMENT '到货日期',
    inspector_id    BIGINT        NULL                    COMMENT '验收人ID',
    inspector_name  VARCHAR(64)   NULL                    COMMENT '验收人姓名',
    status          VARCHAR(32)   NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    remark          VARCHAR(500)  NULL                    COMMENT '备注/验收记录文本',
    summary_json    TEXT          NULL                    COMMENT 'AI 摘要 JSON',
    create_time     DATETIME      NOT NULL                COMMENT '创建时间',
    update_time     DATETIME      NOT NULL                COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_order_no (order_no),
    KEY idx_supplier (supplier_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '验收单主表';

DROP TABLE IF EXISTS t_acceptance_item;
CREATE TABLE t_acceptance_item (
    id              BIGINT        NOT NULL                COMMENT '主键',
    acceptance_id   BIGINT        NOT NULL                COMMENT '验收单ID',
    device_code     VARCHAR(64)   NOT NULL                COMMENT '设备编码',
    device_name     VARCHAR(128)  NOT NULL                COMMENT '设备名称',
    device_type     VARCHAR(64)   NULL                    COMMENT '设备类型',
    spec            VARCHAR(128)  NULL                    COMMENT '规格型号',
    unit            VARCHAR(32)   NULL                    COMMENT '单位',
    qty             DECIMAL(12,2) NOT NULL DEFAULT 0      COMMENT '数量',
    result          VARCHAR(32)   NOT NULL DEFAULT 'QUALIFIED' COMMENT '验收结果',
    defect_desc     VARCHAR(500)  NULL                    COMMENT '缺陷描述',
    create_time     DATETIME      NOT NULL                COMMENT '创建时间',
    update_time     DATETIME      NOT NULL                COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_acceptance (acceptance_id),
    KEY idx_device_type (device_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '验收单明细行';

DROP TABLE IF EXISTS t_rectification;
CREATE TABLE t_rectification (
    id              BIGINT        NOT NULL                COMMENT '主键',
    acceptance_id   BIGINT        NOT NULL                COMMENT '验收单ID',
    content         VARCHAR(500)  NOT NULL                COMMENT '整改内容',
    owner           VARCHAR(64)   NULL                    COMMENT '责任人',
    deadline        DATE          NULL                    COMMENT '截止日期',
    finished        TINYINT       NOT NULL DEFAULT 0      COMMENT '0=未完成 1=已完成',
    create_time     DATETIME      NOT NULL                COMMENT '创建时间',
    update_time     DATETIME      NOT NULL                COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_acceptance (acceptance_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '整改记录';

DROP TABLE IF EXISTS t_operation_log;
CREATE TABLE t_operation_log (
    id              BIGINT        NOT NULL                COMMENT '主键',
    user_id         BIGINT        NULL                    COMMENT '操作人ID',
    user_name       VARCHAR(64)   NULL                    COMMENT '操作人姓名',
    op_time         DATETIME      NOT NULL                COMMENT '操作时间',
    op_type         VARCHAR(32)   NOT NULL                COMMENT '操作类型',
    target_type     VARCHAR(32)   NULL                    COMMENT '目标对象类型',
    target_id       VARCHAR(64)   NULL                    COMMENT '目标对象ID',
    summary         VARCHAR(255)  NULL                    COMMENT '摘要描述',
    before_json     TEXT          NULL                    COMMENT '变更前 JSON',
    after_json      TEXT          NULL                    COMMENT '变更后 JSON',
    create_time     DATETIME      NOT NULL                COMMENT '创建时间',
    update_time     DATETIME      NOT NULL                COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_target (target_type, target_id),
    KEY idx_op_time (op_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '操作日志';

-- ----------------------------------------------------------------
-- 种子数据：3 张验收单，覆盖不同状态
-- ----------------------------------------------------------------
INSERT INTO t_acceptance (id, order_no, supplier_id, supplier_name, arrival_date, inspector_id, inspector_name, status, remark, create_time, update_time) VALUES
(1001, 'PO20260520-001', 1, '北京源辉天然气设备有限公司', '2026-05-20', 2, 'Alice', 'PENDING',     '调压器到货5台，外观检查待开展', NOW(), NOW()),
(1002, 'PO20260518-002', 2, '上海远景管道工程有限公司',   '2026-05-18', 2, 'Alice', 'IN_PROGRESS', '阀门到货已开箱，3台铭牌磨损待复核',     NOW(), NOW()),
(1003, 'PO20260510-003', 3, '深圳启明流体控制有限公司',   '2026-05-10', 3, 'Bob',   'PASSED',      '流量计批次验收完成，全部合格',         NOW(), NOW());

INSERT INTO t_acceptance_item (id, acceptance_id, device_code, device_name, device_type, spec, unit, qty, result, defect_desc, create_time, update_time) VALUES
(2001, 1001, 'REG-001', '智能调压器', '调压器', 'DN50 0.4MPa', '台', 5, 'QUALIFIED', '', NOW(), NOW()),
(2002, 1002, 'VAL-001', '球阀',       '阀门',   'DN100 PN16',  '台', 10,'QUALIFIED','', NOW(), NOW()),
(2003, 1002, 'VAL-002', '截止阀',     '阀门',   'DN80 PN16',   '台', 6, 'CONCESSION','3台铭牌字迹磨损，已要求供应商补发铭牌贴',NOW(), NOW()),
(2004, 1003, 'FLO-001', '超声波流量计','流量计','DN150',       '台', 4, 'QUALIFIED','',NOW(), NOW()),
(2005, 1003, 'FLO-002', '涡轮流量计', '流量计', 'DN80',        '台', 8, 'QUALIFIED','',NOW(), NOW());
