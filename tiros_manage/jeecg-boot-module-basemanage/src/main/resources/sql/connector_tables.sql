-- ============================================
-- 连接器检查管理模块 - 数据库表创建脚本
-- ============================================

-- 1. 连接器类型表
CREATE TABLE bu_connector_type (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键',
    connector_code VARCHAR(64) NOT NULL UNIQUE COMMENT '连接器编码',
    connector_name VARCHAR(128) NOT NULL COMMENT '连接器名称',
    connector_category VARCHAR(32) COMMENT '分类（直式/弯式等）',
    manufacturer VARCHAR(64) COMMENT '制造商',
    spec_sheet_url VARCHAR(255) COMMENT '规格书路径',
    pin_count INT COMMENT '引脚数',
    rated_voltage VARCHAR(32) COMMENT '额定电压',
    rated_current VARCHAR(32) COMMENT '额定电流',
    contact_material VARCHAR(64) COMMENT '接触材料',
    operating_temperature VARCHAR(64) COMMENT '工作温度范围',
    lifecycle INT COMMENT '生命周期（小时）',
    replacement_standard VARCHAR(255) COMMENT '替换标准',
    error_prevention_table LONGTEXT COMMENT '防差错表（JSON）',
    positioning_config LONGTEXT COMMENT '定位点配置（JSON）',
    status INT DEFAULT 1 COMMENT '状态（1:启用 0:禁用）',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(36) COMMENT '创建人',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    update_by VARCHAR(36) COMMENT '修改人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='连接器类型表';

-- 2. 连接器检查规则表
CREATE TABLE bu_connector_inspection_rule (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键',
    connector_type_id VARCHAR(36) NOT NULL COMMENT '连接器类型ID',
    rule_code VARCHAR(64) NOT NULL COMMENT '规则编码',
    rule_name VARCHAR(128) NOT NULL COMMENT '规则名称',
    inspection_type VARCHAR(32) COMMENT '检查类型（外观/接触/绝缘/功能）',
    check_method VARCHAR(255) COMMENT '检查方法',
    acceptance_criteria VARCHAR(255) COMMENT '验收标准',
    check_interval INT COMMENT '检查间隔（小时）',
    required_tools LONGTEXT COMMENT '所需工具（JSON）',
    test_equipment LONGTEXT COMMENT '测试设备（JSON）',
    safety_precautions VARCHAR(500) COMMENT '安全注意事项',
    status INT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(36) COMMENT '创建人',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    update_by VARCHAR(36) COMMENT '修改人',
    FOREIGN KEY (connector_type_id) REFERENCES bu_connector_type(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='连接器检查规则表';

-- 3. 连接器实例表
CREATE TABLE bu_connector_instance (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键',
    instance_code VARCHAR(64) NOT NULL UNIQUE COMMENT '实例编码',
    connector_type_id VARCHAR(36) NOT NULL COMMENT '连接器类型ID',
    connector_name VARCHAR(128) COMMENT '连接器名称',
    installation_location VARCHAR(128) COMMENT '安装位置',
    train_no VARCHAR(32) COMMENT '车辆编号',
    carriage INT COMMENT '车厢号',
    work_position VARCHAR(32) COMMENT '工位（TC-MP/MP-M/M1-M2）',
    connector_pair VARCHAR(64) COMMENT '配对连接器',
    positioning_status VARCHAR(32) COMMENT '定位点状态（正常/异常）',
    pin_status VARCHAR(32) COMMENT '引脚状态（正常/磨损/腐蚀）',
    manufacture_date DATE COMMENT '制造日期',
    installation_date DATE COMMENT '安装日期',
    last_maintenance_date DATE COMMENT '最后维护日期',
    mileage DECIMAL(10,2) COMMENT '里程（千米）',
    status INT DEFAULT 1 COMMENT '状态（1:正常 0:停用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(36) COMMENT '创建人',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    update_by VARCHAR(36) COMMENT '修改人',
    FOREIGN KEY (connector_type_id) REFERENCES bu_connector_type(id),
    INDEX idx_train_no (train_no),
    INDEX idx_work_position (work_position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='连接器实例表';

-- 4. 连接器检查记录表
CREATE TABLE bu_connector_inspection_record (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键',
    record_code VARCHAR(64) NOT NULL UNIQUE COMMENT '记录编码',
    connector_instance_id VARCHAR(36) NOT NULL COMMENT '连接器实例ID',
    connector_type_id VARCHAR(36) NOT NULL COMMENT '连接器类型ID',
    rule_id VARCHAR(36) COMMENT '检查规则ID',
    inspection_date DATETIME COMMENT '检查日期',
    inspector VARCHAR(64) COMMENT '检查员',
    work_position VARCHAR(32) COMMENT '工位',
    visual_condition VARCHAR(255) COMMENT '外观状况',
    contact_condition VARCHAR(255) COMMENT '接触状况',
    insulation_result VARCHAR(32) COMMENT '绝缘结果（合格/不合格）',
    insulation_value DECIMAL(12,2) COMMENT '绝缘值（欧姆）',
    functionality_result VARCHAR(32) COMMENT '功能结果（合格/不合格）',
    pin_check_result LONGTEXT COMMENT '缩针检查结果（JSON）',
    positioning_check_result LONGTEXT COMMENT '定位点检查结果（JSON）',
    abnormalities VARCHAR(500) COMMENT '异常描述',
    photos_url LONGTEXT COMMENT '照片（JSON）',
    repair_required INT DEFAULT 0 COMMENT '是否需维修（1:是 0:否）',
    repair_records VARCHAR(500) COMMENT '维修记录',
    approval_status VARCHAR(32) DEFAULT '待审批' COMMENT '审批状态（待审批/已审批/已驳回）',
    approval_by VARCHAR(64) COMMENT '审批人',
    approval_time DATETIME COMMENT '审批时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(36) COMMENT '创建人',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    update_by VARCHAR(36) COMMENT '修改人',
    FOREIGN KEY (connector_instance_id) REFERENCES bu_connector_instance(id),
    FOREIGN KEY (connector_type_id) REFERENCES bu_connector_type(id),
    INDEX idx_inspection_date (inspection_date),
    INDEX idx_approval_status (approval_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='连接器检查记录表';

-- 5. 连接器维护记录表
CREATE TABLE bu_connector_maintenance (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键',
    connector_instance_id VARCHAR(36) NOT NULL COMMENT '连接器实例ID',
    maintenance_code VARCHAR(64) NOT NULL UNIQUE COMMENT '维护编码',
    maintenance_type VARCHAR(32) COMMENT '维护类型（清洁/更换/修复）',
    maintenance_date DATETIME COMMENT '维护日期',
    maintenance_person VARCHAR(64) COMMENT '维护人员',
    maintenance_desc VARCHAR(500) COMMENT '维护描述',
    parts_replaced LONGTEXT COMMENT '更换零件（JSON）',
    maintenance_cost DECIMAL(10,2) COMMENT '维护成本',
    before_photos LONGTEXT COMMENT '维护前照片（JSON）',
    after_photos LONGTEXT COMMENT '维护后照片（JSON）',
    verification_result VARCHAR(32) COMMENT '验证结果（合格/不合格）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(36) COMMENT '创建人',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    update_by VARCHAR(36) COMMENT '修改人',
    FOREIGN KEY (connector_instance_id) REFERENCES bu_connector_instance(id),
    INDEX idx_maintenance_date (maintenance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='连接器维护记录表';

-- 创建索引
CREATE INDEX idx_connector_code ON bu_connector_type(connector_code);
CREATE INDEX idx_instance_code ON bu_connector_instance(instance_code);
CREATE INDEX idx_record_code ON bu_connector_inspection_record(record_code);
CREATE INDEX idx_maintenance_code ON bu_connector_maintenance(maintenance_code);
