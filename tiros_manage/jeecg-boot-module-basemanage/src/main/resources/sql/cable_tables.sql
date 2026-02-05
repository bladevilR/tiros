-- ============================================
-- 跨接电缆安装管理模块 - 数据库表创建脚本
-- ============================================

-- 1. 电缆规格表
CREATE TABLE bu_cable_specification (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键',
    cable_code VARCHAR(64) NOT NULL UNIQUE COMMENT '电缆编码',
    cable_name VARCHAR(128) NOT NULL COMMENT '电缆名称',
    cable_type VARCHAR(32) COMMENT '电缆类型（车间跳线/信号跨接线等）',
    manufacturer VARCHAR(64) COMMENT '制造商',
    cable_length DECIMAL(8,2) COMMENT '长度（米）',
    cable_diameter DECIMAL(8,2) COMMENT '直径（mm）',
    rated_voltage VARCHAR(32) COMMENT '额定电压',
    rated_current VARCHAR(32) COMMENT '额定电流',
    cable_structure VARCHAR(128) COMMENT '电缆结构',
    connector_a_code VARCHAR(64) COMMENT 'A端连接器编号',
    connector_b_code VARCHAR(64) COMMENT 'B端连接器编号',
    installation_diagram VARCHAR(255) COMMENT '安装示意图',
    spec_sheet_url VARCHAR(255) COMMENT '规格书路径',
    status INT DEFAULT 1 COMMENT '状态（1:启用 0:禁用）',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(36) COMMENT '创建人',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    update_by VARCHAR(36) COMMENT '修改人',
    INDEX idx_cable_code (cable_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电缆规格表';

-- 2. 电缆安装规则表
CREATE TABLE bu_cable_installation_rule (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键',
    cable_spec_id VARCHAR(36) NOT NULL COMMENT '电缆规格ID',
    rule_code VARCHAR(64) NOT NULL COMMENT '规则编码',
    rule_name VARCHAR(128) NOT NULL COMMENT '规则名称',
    work_position VARCHAR(32) COMMENT '工位（TC-MP/MP-M/M1-M2）',
    installation_sequence INT COMMENT '安装序',
    connector_pairing LONGTEXT COMMENT '连接器配对（JSON）',
    positioning_requirement VARCHAR(255) COMMENT '定位点要求',
    locking_requirement VARCHAR(255) COMMENT '锁紧要求',
    min_ground_clearance INT COMMENT '最低离地高度（mm）',
    torque_spec_id VARCHAR(36) COMMENT '关联力矩规格ID',
    wave_tube_bundling VARCHAR(255) COMMENT '波纹管捆扎要求',
    protection_requirement VARCHAR(255) COMMENT '防护要求',
    status INT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(36) COMMENT '创建人',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    update_by VARCHAR(36) COMMENT '修改人',
    FOREIGN KEY (cable_spec_id) REFERENCES bu_cable_specification(id),
    INDEX idx_work_position (work_position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电缆安装规则表';

-- 3. 电��路由路径表
CREATE TABLE bu_cable_routing_path (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键',
    cable_spec_id VARCHAR(36) NOT NULL COMMENT '电缆规格ID',
    path_code VARCHAR(64) NOT NULL UNIQUE COMMENT '路径编码',
    path_name VARCHAR(128) NOT NULL COMMENT '路径名称',
    start_position VARCHAR(128) COMMENT '起始位置',
    end_position VARCHAR(128) COMMENT '结束位置',
    intermediate_points LONGTEXT COMMENT '中间点（JSON）',
    path_length DECIMAL(8,2) COMMENT '路径长度',
    max_bend_radius DECIMAL(8,2) COMMENT '最大弯曲半径',
    support_points LONGTEXT COMMENT '支撑点（JSON）',
    protection_measures VARCHAR(255) COMMENT '防护措施',
    path_diagram VARCHAR(255) COMMENT '路径示意图',
    status INT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(36) COMMENT '创建人',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    update_by VARCHAR(36) COMMENT '修改人',
    FOREIGN KEY (cable_spec_id) REFERENCES bu_cable_specification(id),
    INDEX idx_path_code (path_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电缆路由路径表';

-- 4. 电缆安装记录表
CREATE TABLE bu_cable_installation_record (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键',
    record_code VARCHAR(64) NOT NULL UNIQUE COMMENT '记录编码',
    cable_spec_id VARCHAR(36) NOT NULL COMMENT '电缆规格ID',
    work_position VARCHAR(32) COMMENT '工位',
    work_order_no VARCHAR(64) COMMENT '工单号',
    train_no VARCHAR(32) COMMENT '车辆编号',
    installation_date DATETIME COMMENT '安装日期',
    installer VARCHAR(64) COMMENT '安装人员',
    connector_a_instance_id VARCHAR(36) COMMENT 'A端连接器实例ID',
    connector_b_instance_id VARCHAR(36) COMMENT 'B端连接器实例ID',
    connector_matching_check VARCHAR(32) COMMENT '连接器匹配检查结���（合格/不合格）',
    positioning_match_check VARCHAR(32) COMMENT '定位点匹配检查（合格/不合格）',
    insertion_status VARCHAR(32) COMMENT '插接到位状态（到位/未到位）',
    locking_status VARCHAR(32) COMMENT '锁紧状态（已锁紧/未锁紧）',
    ground_clearance_actual INT COMMENT '实际离地高度（mm）',
    torque_record_id VARCHAR(36) COMMENT '关联力矩记录ID',
    wave_tube_bundling_photos LONGTEXT COMMENT '波纹管捆扎照片（JSON）',
    protection_photos LONGTEXT COMMENT '防护照片（JSON）',
    inspector VARCHAR(64) COMMENT '检验员',
    inspection_time DATETIME COMMENT '检验时间',
    inspection_result VARCHAR(32) COMMENT '检验结果（合格/不合格）',
    quality_issues VARCHAR(500) COMMENT '质量问题',
    status INT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(36) COMMENT '创建人',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    update_by VARCHAR(36) COMMENT '修改人',
    FOREIGN KEY (cable_spec_id) REFERENCES bu_cable_specification(id),
    INDEX idx_record_code (record_code),
    INDEX idx_installation_date (installation_date),
    INDEX idx_inspection_result (inspection_result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电缆安装记录表';

-- 5. 电缆测试记录表
CREATE TABLE bu_cable_testing_record (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键',
    cable_installation_id VARCHAR(36) NOT NULL COMMENT '电缆安装记录ID',
    test_code VARCHAR(64) NOT NULL UNIQUE COMMENT '测试编码',
    test_type VARCHAR(32) COMMENT '测试类型（通断/绝缘/耐压）',
    test_date DATETIME COMMENT '测试日期',
    tester VARCHAR(64) COMMENT '测试员',
    test_equipment VARCHAR(128) COMMENT '测试设备',
    test_standard VARCHAR(255) COMMENT '测试标准',
    test_parameters LONGTEXT COMMENT '测试参数（JSON）',
    test_results LONGTEXT COMMENT '测试结果（JSON）',
    pass_status VARCHAR(32) COMMENT '通过状态（合格/不合格）',
    abnormalities VARCHAR(500) COMMENT '异常情况',
    corrective_action VARCHAR(500) COMMENT '纠正措施',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(36) COMMENT '创建人',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    update_by VARCHAR(36) COMMENT '修改人',
    FOREIGN KEY (cable_installation_id) REFERENCES bu_cable_installation_record(id),
    INDEX idx_test_code (test_code),
    INDEX idx_test_date (test_date),
    INDEX idx_pass_status (pass_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电缆测试记录表';

-- 创建索引
CREATE INDEX idx_cable_spec_id ON bu_cable_installation_rule(cable_spec_id);
CREATE INDEX idx_routing_cable_spec_id ON bu_cable_routing_path(cable_spec_id);
CREATE INDEX idx_installation_work_order ON bu_cable_installation_record(work_order_no);
