-- 安全风险管理模块 SQL脚本

-- 1. 危险源登记表
CREATE TABLE bu_hazard_register (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    hazard_code VARCHAR(64) NOT NULL UNIQUE COMMENT '危险源编码',
    hazard_name VARCHAR(256) NOT NULL COMMENT '危险源名称',
    hazard_category VARCHAR(64) COMMENT '危险源分类(机械/电气/化学/高处等)',
    activity_desc TEXT COMMENT '作业活动描述',
    potential_event TEXT COMMENT '潜在事件',
    possible_consequence TEXT COMMENT '可能后果',
    severity_level INT COMMENT '严重程度(1-5)',
    occurrence_probability INT COMMENT '发生概率(1-5)',
    risk_level INT COMMENT '风险等级(严重度×概率)',
    affected_personnel VARCHAR(512) COMMENT '受影响人员',
    control_measures LONGTEXT COMMENT '控制措施(JSON)',
    status INT DEFAULT 0 COMMENT '状态(0-启用 1-禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(50) COMMENT '更新人',
    remark TEXT COMMENT '备注',
    INDEX idx_hazard_code (hazard_code),
    INDEX idx_risk_level (risk_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='危险源登记表';

-- 2. 风险评估表
CREATE TABLE bu_risk_assessment (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    hazard_id VARCHAR(36) NOT NULL COMMENT '危险源ID',
    assessment_code VARCHAR(64) NOT NULL UNIQUE COMMENT '评估编码',
    assessment_date DATE NOT NULL COMMENT '评估日期',
    assessor VARCHAR(128) COMMENT '评估人',
    assessment_method VARCHAR(128) COMMENT '评估方法(LEC/矩阵法等)',
    assessment_criteria TEXT COMMENT '评估准则',
    initial_risk_level INT COMMENT '初始风险等级',
    residual_risk_level INT COMMENT '剩余风险等级',
    control_effectiveness VARCHAR(256) COMMENT '控制措施有效性',
    additional_measures TEXT COMMENT '附加措施',
    review_date DATE COMMENT '评审日期',
    status INT DEFAULT 0 COMMENT '状态(0-启用 1-禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(50) COMMENT '更新人',
    remark TEXT COMMENT '备注',
    FOREIGN KEY (hazard_id) REFERENCES bu_hazard_register(id),
    INDEX idx_assessment_code (assessment_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='风险评估表';

-- 3. 安全措施表
CREATE TABLE bu_safety_measure (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    hazard_id VARCHAR(36) NOT NULL COMMENT '危险源ID',
    measure_code VARCHAR(64) NOT NULL UNIQUE COMMENT '措施编码',
    measure_name VARCHAR(256) NOT NULL COMMENT '措施名称',
    measure_type VARCHAR(64) COMMENT '措施类型(工程/管理/PPE)',
    measure_desc TEXT COMMENT '措施描述',
    implementation_dept VARCHAR(256) COMMENT '实施部门',
    responsible_person VARCHAR(128) COMMENT '责任人',
    implementation_date DATE COMMENT '实施日期',
    verification_method VARCHAR(256) COMMENT '验证方法',
    verification_result TEXT COMMENT '验证结果',
    effectiveness_evaluation TEXT COMMENT '有效性评价',
    status INT DEFAULT 0 COMMENT '状态(0-启用 1-禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(50) COMMENT '更新人',
    remark TEXT COMMENT '备注',
    FOREIGN KEY (hazard_id) REFERENCES bu_hazard_register(id),
    INDEX idx_measure_code (measure_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='安全措施表';

-- 4. 安全事件表
CREATE TABLE bu_safety_incident (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    incident_code VARCHAR(64) NOT NULL UNIQUE COMMENT '事件编码',
    incident_title VARCHAR(256) NOT NULL COMMENT '事件标题',
    incident_type VARCHAR(64) COMMENT '事件类型(未遂/轻伤/重伤)',
    incident_date DATETIME NOT NULL COMMENT '事件日期',
    incident_location VARCHAR(256) COMMENT '事件地点',
    incident_desc TEXT COMMENT '事件描述',
    related_hazard_id VARCHAR(36) COMMENT '关联危险源ID',
    injured_person VARCHAR(128) COMMENT '受伤人员',
    injury_degree VARCHAR(128) COMMENT '受伤程度',
    immediate_cause TEXT COMMENT '直接原因',
    root_cause TEXT COMMENT '根本原因',
    corrective_action TEXT COMMENT '纠正措施',
    preventive_action TEXT COMMENT '预防措施',
    lesson_learned TEXT COMMENT '经验教训',
    investigation_team VARCHAR(512) COMMENT '调查组',
    investigation_date DATE COMMENT '调查日期',
    status INT DEFAULT 0 COMMENT '状态(0-启用 1-禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(50) COMMENT '更新人',
    remark TEXT COMMENT '备注',
    FOREIGN KEY (related_hazard_id) REFERENCES bu_hazard_register(id),
    INDEX idx_incident_code (incident_code),
    INDEX idx_incident_date (incident_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='安全事件表';

-- 5. 安全培训记录表
CREATE TABLE bu_safety_training_record (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    training_code VARCHAR(64) NOT NULL UNIQUE COMMENT '培训编码',
    training_topic VARCHAR(256) NOT NULL COMMENT '培训主题',
    training_type VARCHAR(64) COMMENT '培训类型(入职/专项/复训)',
    training_date DATE NOT NULL COMMENT '培训日期',
    trainer VARCHAR(128) COMMENT '培训师',
    training_content TEXT COMMENT '培训内容',
    trainees LONGTEXT COMMENT '受训人员(JSON)',
    training_hours DECIMAL(10,2) COMMENT '培训时长',
    training_materials VARCHAR(512) COMMENT '培训材料',
    assessment_method VARCHAR(256) COMMENT '考核方法',
    assessment_results LONGTEXT COMMENT '考核结果(JSON)',
    pass_rate DECIMAL(5,2) COMMENT '通过率',
    status INT DEFAULT 0 COMMENT '状态(0-启用 1-禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(50) COMMENT '更新人',
    remark TEXT COMMENT '备注',
    INDEX idx_training_code (training_code),
    INDEX idx_training_date (training_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='安全培训记录表';
