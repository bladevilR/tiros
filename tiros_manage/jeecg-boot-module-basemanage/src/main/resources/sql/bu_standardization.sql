-- ============================================================================
-- 标准化管理模块数据库脚本
-- 作者: Claude AI
-- 日期: 2026-01-28
-- ============================================================================

-- 1. 工序标准表
CREATE TABLE bu_process_standard (
    id VARCHAR2(36) NOT NULL,
    standard_code VARCHAR2(50) NOT NULL UNIQUE,
    standard_name VARCHAR2(200) NOT NULL,
    standard_category VARCHAR2(100),
    applicable_process VARCHAR2(500),
    standard_content CLOB,
    version VARCHAR2(20),
    effective_date DATE,
    review_cycle NUMBER(3),
    next_review_date DATE,
    issuing_dept VARCHAR2(100),
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_process_standard PRIMARY KEY (id)
);

COMMENT ON TABLE bu_process_standard IS '工序标准表';

CREATE INDEX idx_process_std_code ON bu_process_standard(standard_code);
CREATE INDEX idx_process_std_category ON bu_process_standard(standard_category);
CREATE INDEX idx_next_review ON bu_process_standard(next_review_date);

-- 2. 质量标准表
CREATE TABLE bu_quality_standard (
    id VARCHAR2(36) NOT NULL,
    standard_code VARCHAR2(50) NOT NULL UNIQUE,
    standard_name VARCHAR2(200) NOT NULL,
    standard_type VARCHAR2(100),
    inspection_items CLOB,
    acceptance_criteria CLOB,
    test_method VARCHAR2(500),
    reference_standard VARCHAR2(200),
    version VARCHAR2(20),
    effective_date DATE,
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_quality_standard PRIMARY KEY (id)
);

COMMENT ON TABLE bu_quality_standard IS '质量标准表';

CREATE INDEX idx_quality_std_code ON bu_quality_standard(standard_code);
CREATE INDEX idx_quality_std_type ON bu_quality_standard(standard_type);

-- 3. 安全标准表
CREATE TABLE bu_safety_standard (
    id VARCHAR2(36) NOT NULL,
    standard_code VARCHAR2(50) NOT NULL UNIQUE,
    standard_name VARCHAR2(200) NOT NULL,
    safety_category VARCHAR2(100),
    safety_requirements CLOB,
    prohibited_actions CLOB,
    mandatory_ppe CLOB,
    emergency_procedures CLOB,
    version VARCHAR2(20),
    effective_date DATE,
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_safety_standard PRIMARY KEY (id)
);

COMMENT ON TABLE bu_safety_standard IS '安全标准表';

CREATE INDEX idx_safety_std_code ON bu_safety_standard(standard_code);
CREATE INDEX idx_safety_category ON bu_safety_standard(safety_category);

-- 4. 标准化检查清单表
CREATE TABLE bu_standardization_checklist (
    id VARCHAR2(36) NOT NULL,
    checklist_code VARCHAR2(50) NOT NULL UNIQUE,
    checklist_name VARCHAR2(200) NOT NULL,
    checklist_type VARCHAR2(100),
    check_items CLOB,
    scoring_method VARCHAR2(100),
    applicable_scope VARCHAR2(500),
    check_frequency VARCHAR2(100),
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_std_checklist PRIMARY KEY (id)
);

COMMENT ON TABLE bu_standardization_checklist IS '标准化检查清单表';

CREATE INDEX idx_checklist_code ON bu_standardization_checklist(checklist_code);
CREATE INDEX idx_checklist_type ON bu_standardization_checklist(checklist_type);

-- 5. 标准化符合性记录表
CREATE TABLE bu_standardization_compliance (
    id VARCHAR2(36) NOT NULL,
    compliance_code VARCHAR2(50) NOT NULL UNIQUE,
    checklist_id VARCHAR2(36),
    check_date DATE NOT NULL,
    checker VARCHAR2(50),
    check_results CLOB,
    total_score NUMBER(5,2),
    pass_status NUMBER(1),
    non_compliances CLOB,
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
    CONSTRAINT pk_std_compliance PRIMARY KEY (id),
    CONSTRAINT fk_compliance_checklist FOREIGN KEY (checklist_id) REFERENCES bu_standardization_checklist(id)
);

COMMENT ON TABLE bu_standardization_compliance IS '标准化符合性记录表';

CREATE INDEX idx_compliance_code ON bu_standardization_compliance(compliance_code);
CREATE INDEX idx_compliance_checklist ON bu_standardization_compliance(checklist_id);
CREATE INDEX idx_check_date ON bu_standardization_compliance(check_date);
CREATE INDEX idx_pass_status ON bu_standardization_compliance(pass_status);

COMMIT;

-- 初始化示例数据
INSERT INTO bu_process_standard (id, standard_code, standard_name, standard_category, effective_date, status, create_time, create_by)
VALUES (SYS_GUID(), 'STD-PROC-001', '跨接电缆安装标准工序', '电气系统', SYSDATE, 1, SYSDATE, 'system');

INSERT INTO bu_quality_standard (id, standard_code, standard_name, standard_type, version, effective_date, status, create_time, create_by)
VALUES (SYS_GUID(), 'STD-QUAL-001', '跨接电缆质量检验标准', '电气产品', '1.0', SYSDATE, 1, SYSDATE, 'system');

INSERT INTO bu_safety_standard (id, standard_code, standard_name, safety_category, version, effective_date, status, create_time, create_by)
VALUES (SYS_GUID(), 'STD-SAFE-001', '电气作业安全标准', '电气安全', '1.0', SYSDATE, 1, SYSDATE, 'system');

COMMIT;

SELECT COUNT(*) AS process_std_count FROM bu_process_standard WHERE del_flag = 0;
SELECT COUNT(*) AS quality_std_count FROM bu_quality_standard WHERE del_flag = 0;
SELECT COUNT(*) AS safety_std_count FROM bu_safety_standard WHERE del_flag = 0;
SELECT COUNT(*) AS checklist_count FROM bu_standardization_checklist WHERE del_flag = 0;
SELECT COUNT(*) AS compliance_count FROM bu_standardization_compliance WHERE del_flag = 0;
