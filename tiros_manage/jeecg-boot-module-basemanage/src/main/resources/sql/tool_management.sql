-- 工具仪器管理模块 SQL脚本

-- 1. 工具主记录表
CREATE TABLE bu_tool_master (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    tool_code VARCHAR(64) NOT NULL UNIQUE COMMENT '工具编码',
    tool_name VARCHAR(256) NOT NULL COMMENT '工具名称',
    tool_category VARCHAR(64) COMMENT '工具分类(扭力扳手/钢卷尺/套筒等)',
    tool_model VARCHAR(128) COMMENT '工具型号',
    manufacturer VARCHAR(256) COMMENT '制造商',
    specification VARCHAR(256) COMMENT '规格',
    serial_no VARCHAR(128) COMMENT '序列号',
    measurement_range VARCHAR(256) COMMENT '测量范围',
    accuracy_level VARCHAR(64) COMMENT '准确度等级',
    purchase_date DATE COMMENT '购买日期',
    warranty_period INT COMMENT '保修期(月)',
    asset_no VARCHAR(128) COMMENT '资产编号',
    current_status INT DEFAULT 0 COMMENT '当前状态(0-在库 1-借出 2-维修 3-报废)',
    current_location VARCHAR(256) COMMENT '当前位置',
    status INT DEFAULT 0 COMMENT '状态(0-启用 1-禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(50) COMMENT '更新人',
    remark TEXT COMMENT '备注',
    INDEX idx_tool_code (tool_code),
    INDEX idx_tool_category (tool_category),
    INDEX idx_current_status (current_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工具主记录表';

-- 2. 工具标定表
CREATE TABLE bu_tool_calibration (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    tool_id VARCHAR(36) NOT NULL COMMENT '工具ID',
    calibration_code VARCHAR(64) NOT NULL UNIQUE COMMENT '标定编码',
    calibration_date DATE NOT NULL COMMENT '标定日期',
    calibrator VARCHAR(128) COMMENT '标定人',
    calibration_agency VARCHAR(256) COMMENT '标定机构',
    certificate_no VARCHAR(128) COMMENT '证书编号',
    test_results LONGTEXT COMMENT '测试结果(JSON)',
    pass_status INT COMMENT '是否通过(0-未通过 1-通过)',
    next_calibration_date DATE COMMENT '下次标定日期',
    attachment_url VARCHAR(512) COMMENT '附件URL',
    status INT DEFAULT 0 COMMENT '状态(0-启用 1-禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(50) COMMENT '更新人',
    remark TEXT COMMENT '备注',
    FOREIGN KEY (tool_id) REFERENCES bu_tool_master(id),
    INDEX idx_tool_id (tool_id),
    INDEX idx_calibration_code (calibration_code),
    INDEX idx_calibration_date (calibration_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工具标定表';

-- 3. 工具维护表
CREATE TABLE bu_tool_maintenance (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    tool_id VARCHAR(36) NOT NULL COMMENT '工具ID',
    maintenance_code VARCHAR(64) NOT NULL UNIQUE COMMENT '维护编码',
    maintenance_type INT COMMENT '维护类型(0-保养 1-维修)',
    maintenance_date DATE NOT NULL COMMENT '维护日期',
    maintenance_person VARCHAR(128) COMMENT '维护人员',
    maintenance_desc TEXT COMMENT '维护描述',
    parts_replaced LONGTEXT COMMENT '更换零件(JSON)',
    maintenance_cost DECIMAL(10,2) COMMENT '维护费用',
    status INT DEFAULT 0 COMMENT '状态(0-启用 1-禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(50) COMMENT '更新人',
    remark TEXT COMMENT '备注',
    FOREIGN KEY (tool_id) REFERENCES bu_tool_master(id),
    INDEX idx_tool_id (tool_id),
    INDEX idx_maintenance_code (maintenance_code),
    INDEX idx_maintenance_date (maintenance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工具维护表';

-- 4. 工具分配表
CREATE TABLE bu_tool_allocation (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    tool_id VARCHAR(36) NOT NULL COMMENT '工具ID',
    allocation_code VARCHAR(64) NOT NULL UNIQUE COMMENT '分配编码',
    borrower VARCHAR(128) NOT NULL COMMENT '借用人',
    borrower_dept VARCHAR(256) COMMENT '借用部门',
    borrow_date DATETIME NOT NULL COMMENT '借用日期',
    expected_return_date DATETIME COMMENT '预期归还日期',
    actual_return_date DATETIME COMMENT '实际归还日期',
    purpose TEXT COMMENT '用途',
    work_order_no VARCHAR(128) COMMENT '工单编号',
    return_condition INT COMMENT '归还状态(0-完好 1-轻微磨损 2-需维修)',
    allocation_status INT DEFAULT 0 COMMENT '分配状态(0-借出中 1-已归还)',
    status INT DEFAULT 0 COMMENT '状态(0-启用 1-禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(50) COMMENT '更新人',
    remark TEXT COMMENT '备注',
    FOREIGN KEY (tool_id) REFERENCES bu_tool_master(id),
    INDEX idx_tool_id (tool_id),
    INDEX idx_allocation_code (allocation_code),
    INDEX idx_borrower (borrower),
    INDEX idx_allocation_status (allocation_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工具分配表';

-- 5. 仪器读数规则表
CREATE TABLE bu_instrument_reading_rule (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    tool_id VARCHAR(36) NOT NULL COMMENT '工具ID',
    rule_code VARCHAR(64) NOT NULL UNIQUE COMMENT '规则编码',
    rule_name VARCHAR(256) NOT NULL COMMENT '规则名称',
    reading_method TEXT COMMENT '读数方法',
    unit VARCHAR(64) COMMENT '单位',
    decimal_places INT COMMENT '小数位数',
    reading_guidance TEXT COMMENT '读数指导',
    error_margin VARCHAR(256) COMMENT '误差范围',
    status INT DEFAULT 0 COMMENT '状态(0-启用 1-禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(50) COMMENT '更新人',
    remark TEXT COMMENT '备注',
    FOREIGN KEY (tool_id) REFERENCES bu_tool_master(id),
    INDEX idx_tool_id (tool_id),
    INDEX idx_rule_code (rule_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仪器读数规则表';
