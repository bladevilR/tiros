-- ============================================================================
-- 防护管理模块数据库脚本
-- 作者: Claude AI
-- 日期: 2026-01-28
-- ============================================================================

-- 1. PPE类型表
CREATE TABLE bu_ppe_type (
    id VARCHAR2(36) NOT NULL,
    ppe_code VARCHAR2(50) NOT NULL UNIQUE,
    ppe_name VARCHAR2(200) NOT NULL,
    ppe_category VARCHAR2(100),
    specification VARCHAR2(200),
    standard VARCHAR2(100),
    protection_level VARCHAR2(50),
    service_life NUMBER(3),
    replacement_criteria CLOB,
    maintenance_guidance CLOB,
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_ppe_type PRIMARY KEY (id)
);

COMMENT ON TABLE bu_ppe_type IS 'PPE个人防护装备类型表';

CREATE INDEX idx_ppe_code ON bu_ppe_type(ppe_code);
CREATE INDEX idx_ppe_category ON bu_ppe_type(ppe_category);

-- 2. PPE分配表
CREATE TABLE bu_ppe_allocation (
    id VARCHAR2(36) NOT NULL,
    ppe_type_id VARCHAR2(36) NOT NULL,
    allocation_code VARCHAR2(50) NOT NULL,
    recipient VARCHAR2(50),
    recipient_dept VARCHAR2(100),
    allocation_date DATE NOT NULL,
    quantity NUMBER(5),
    serial_nos CLOB,
    expected_replacement_date DATE,
    usage_training_completed NUMBER(1),
    return_date DATE,
    return_condition VARCHAR2(100),
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_ppe_allocation PRIMARY KEY (id),
    CONSTRAINT fk_allocation_ppe FOREIGN KEY (ppe_type_id) REFERENCES bu_ppe_type(id)
);

CREATE INDEX idx_ppe_alloc_code ON bu_ppe_allocation(allocation_code);
CREATE INDEX idx_ppe_recipient ON bu_ppe_allocation(recipient);

-- 3. 防护区域表
CREATE TABLE bu_protection_area (
    id VARCHAR2(36) NOT NULL,
    area_code VARCHAR2(50) NOT NULL UNIQUE,
    area_name VARCHAR2(200) NOT NULL,
    area_type VARCHAR2(100),
    hazard_level VARCHAR2(20),
    required_ppe CLOB,
    warning_signs CLOB,
    emergency_equipment VARCHAR2(500),
    responsible_person VARCHAR2(50),
    inspection_frequency VARCHAR2(50),
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_protection_area PRIMARY KEY (id)
);

CREATE INDEX idx_area_code ON bu_protection_area(area_code);
CREATE INDEX idx_hazard_level ON bu_protection_area(hazard_level);

-- 4. 防护措施表
CREATE TABLE bu_protection_measure (
    id VARCHAR2(36) NOT NULL,
    area_id VARCHAR2(36),
    measure_code VARCHAR2(50) NOT NULL,
    measure_name VARCHAR2(200),
    measure_type VARCHAR2(100),
    measure_desc CLOB,
    implementation_date DATE,
    responsible_person VARCHAR2(50),
    verification_photos VARCHAR2(1000),
    effectiveness_check VARCHAR2(200),
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_protection_measure PRIMARY KEY (id),
    CONSTRAINT fk_measure_area FOREIGN KEY (area_id) REFERENCES bu_protection_area(id)
);

CREATE INDEX idx_prot_measure_code ON bu_protection_measure(measure_code);
CREATE INDEX idx_prot_area ON bu_protection_measure(area_id);

-- 5. 防护审计表
CREATE TABLE bu_protection_audit (
    id VARCHAR2(36) NOT NULL,
    audit_code VARCHAR2(50) NOT NULL UNIQUE,
    audit_date DATE NOT NULL,
    auditor VARCHAR2(50),
    audit_scope VARCHAR2(500),
    ppe_usage_check CLOB,
    area_protection_check CLOB,
    compliance_issues CLOB,
    corrective_actions CLOB,
    follow_up_date DATE,
    follow_up_result VARCHAR2(500),
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_protection_audit PRIMARY KEY (id)
);

CREATE INDEX idx_audit_code ON bu_protection_audit(audit_code);
CREATE INDEX idx_audit_date ON bu_protection_audit(audit_date);

COMMIT;

SELECT COUNT(*) AS ppe_type_count FROM bu_ppe_type WHERE del_flag = 0;
