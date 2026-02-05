-- ============================================================================
-- 工具仪器管理模块数据库脚本
-- 作者: Claude AI
-- 日期: 2026-01-28
-- ============================================================================

-- 1. 工具主记录表
CREATE TABLE bu_tool_master (
    id VARCHAR2(36) NOT NULL,
    tool_code VARCHAR2(50) NOT NULL UNIQUE,
    tool_name VARCHAR2(200) NOT NULL,
    tool_category VARCHAR2(100),
    tool_model VARCHAR2(100),
    manufacturer VARCHAR2(200),
    specification VARCHAR2(200),
    serial_no VARCHAR2(100),
    measurement_range VARCHAR2(100),
    accuracy_level VARCHAR2(50),
    purchase_date DATE,
    warranty_period NUMBER(3),
    asset_no VARCHAR2(100),
    current_status VARCHAR2(20) DEFAULT '在库',
    current_location VARCHAR2(200),
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_tool_master PRIMARY KEY (id)
);

COMMENT ON TABLE bu_tool_master IS '工具仪器主记录表';
COMMENT ON COLUMN bu_tool_master.id IS '主键ID';
COMMENT ON COLUMN bu_tool_master.tool_code IS '工具编号';
COMMENT ON COLUMN bu_tool_master.tool_name IS '工具名称';
COMMENT ON COLUMN bu_tool_master.tool_category IS '工具类别';
COMMENT ON COLUMN bu_tool_master.measurement_range IS '测量范围';
COMMENT ON COLUMN bu_tool_master.accuracy_level IS '准确度等级';
COMMENT ON COLUMN bu_tool_master.current_status IS '当前状态：在库/借出/维修/报废';

CREATE INDEX idx_tool_code ON bu_tool_master(tool_code);
CREATE INDEX idx_tool_category ON bu_tool_master(tool_category);
CREATE INDEX idx_tool_status ON bu_tool_master(current_status);

-- 2. 工具标定表
CREATE TABLE bu_tool_calibration (
    id VARCHAR2(36) NOT NULL,
    tool_id VARCHAR2(36) NOT NULL,
    calibration_code VARCHAR2(50) NOT NULL,
    calibration_date DATE NOT NULL,
    calibrator VARCHAR2(50),
    calibration_agency VARCHAR2(200),
    certificate_no VARCHAR2(100),
    test_results CLOB,
    pass_status NUMBER(1),
    next_calibration_date DATE,
    attachment_url VARCHAR2(1000),
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_tool_calibration PRIMARY KEY (id),
    CONSTRAINT fk_calibration_tool FOREIGN KEY (tool_id) REFERENCES bu_tool_master(id)
);

CREATE INDEX idx_calibration_tool ON bu_tool_calibration(tool_id);
CREATE INDEX idx_calibration_date ON bu_tool_calibration(calibration_date);
CREATE INDEX idx_next_calib_date ON bu_tool_calibration(next_calibration_date);

-- 3. 工具维护表
CREATE TABLE bu_tool_maintenance (
    id VARCHAR2(36) NOT NULL,
    tool_id VARCHAR2(36) NOT NULL,
    maintenance_code VARCHAR2(50) NOT NULL,
    maintenance_type VARCHAR2(50),
    maintenance_date DATE NOT NULL,
    maintenance_person VARCHAR2(50),
    maintenance_desc CLOB,
    parts_replaced CLOB,
    maintenance_cost NUMBER(10,2),
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_tool_maintenance PRIMARY KEY (id),
    CONSTRAINT fk_maintenance_tool FOREIGN KEY (tool_id) REFERENCES bu_tool_master(id)
);

CREATE INDEX idx_maintenance_tool ON bu_tool_maintenance(tool_id);
CREATE INDEX idx_maintenance_date ON bu_tool_maintenance(maintenance_date);

-- 4. 工具分配表
CREATE TABLE bu_tool_allocation (
    id VARCHAR2(36) NOT NULL,
    tool_id VARCHAR2(36) NOT NULL,
    allocation_code VARCHAR2(50) NOT NULL,
    borrower VARCHAR2(50),
    borrower_dept VARCHAR2(100),
    borrow_date DATE,
    expected_return_date DATE,
    actual_return_date DATE,
    purpose VARCHAR2(500),
    work_order_no VARCHAR2(50),
    return_condition VARCHAR2(100),
    allocation_status VARCHAR2(20) DEFAULT '借出中',
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_tool_allocation PRIMARY KEY (id),
    CONSTRAINT fk_allocation_tool FOREIGN KEY (tool_id) REFERENCES bu_tool_master(id)
);

CREATE INDEX idx_allocation_tool ON bu_tool_allocation(tool_id);
CREATE INDEX idx_allocation_borrower ON bu_tool_allocation(borrower);
CREATE INDEX idx_allocation_status ON bu_tool_allocation(allocation_status);

-- 5. 仪器读数规则表
CREATE TABLE bu_instrument_reading_rule (
    id VARCHAR2(36) NOT NULL,
    tool_id VARCHAR2(36),
    rule_code VARCHAR2(50) NOT NULL,
    rule_name VARCHAR2(200),
    reading_method CLOB,
    unit VARCHAR2(50),
    decimal_places NUMBER(1),
    reading_guidance CLOB,
    error_margin VARCHAR2(100),
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_reading_rule PRIMARY KEY (id),
    CONSTRAINT fk_reading_tool FOREIGN KEY (tool_id) REFERENCES bu_tool_master(id)
);

CREATE INDEX idx_reading_rule_code ON bu_instrument_reading_rule(rule_code);
CREATE INDEX idx_reading_rule_tool ON bu_instrument_reading_rule(tool_id);

COMMIT;

-- 初始化示例数据
INSERT INTO bu_tool_master (id, tool_code, tool_name, tool_category, tool_model, specification, current_status, status, create_time, create_by)
VALUES (SYS_GUID(), 'TOOL-001', '扭力扳手', '测量工具', 'TW-10-50', '(10-50)Nm', '在库', 1, SYSDATE, 'system');

INSERT INTO bu_tool_master (id, tool_code, tool_name, tool_category, tool_model, specification, current_status, status, create_time, create_by)
VALUES (SYS_GUID(), 'TOOL-002', '钢卷尺', '测量工具', 'JCQ-3', '3m', '在库', 1, SYSDATE, 'system');

COMMIT;

SELECT COUNT(*) AS tool_count FROM bu_tool_master WHERE del_flag = 0;
