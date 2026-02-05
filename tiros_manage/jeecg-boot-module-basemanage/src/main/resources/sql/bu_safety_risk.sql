-- ============================================================================
-- 安全风险管理模块数据库脚本
-- 作者: Claude AI
-- 日期: 2026-01-28
-- ============================================================================

-- 1. 危险源登记表
CREATE TABLE bu_hazard_register (
    id VARCHAR2(36) NOT NULL,
    hazard_code VARCHAR2(50) NOT NULL UNIQUE,
    hazard_name VARCHAR2(200) NOT NULL,
    hazard_category VARCHAR2(100),
    activity_desc CLOB,
    potential_event CLOB,
    possible_consequence CLOB,
    severity_level NUMBER(1),
    occurrence_probability NUMBER(1),
    risk_level NUMBER(2),
    affected_personnel VARCHAR2(500),
    control_measures CLOB,
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_hazard_register PRIMARY KEY (id)
);

COMMENT ON TABLE bu_hazard_register IS '危险源登记表';

CREATE INDEX idx_hazard_code ON bu_hazard_register(hazard_code);
CREATE INDEX idx_hazard_category ON bu_hazard_register(hazard_category);
CREATE INDEX idx_risk_level ON bu_hazard_register(risk_level);

-- 2. 风险评估表
CREATE TABLE bu_risk_assessment (
    id VARCHAR2(36) NOT NULL,
    hazard_id VARCHAR2(36) NOT NULL,
    assessment_code VARCHAR2(50) NOT NULL,
    assessment_date DATE NOT NULL,
    assessor VARCHAR2(50),
    assessment_method VARCHAR2(100),
    assessment_criteria CLOB,
    initial_risk_level VARCHAR2(20),
    residual_risk_level VARCHAR2(20),
    control_effectiveness VARCHAR2(100),
    additional_measures CLOB,
    review_date DATE,
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_risk_assessment PRIMARY KEY (id),
    CONSTRAINT fk_assessment_hazard FOREIGN KEY (hazard_id) REFERENCES bu_hazard_register(id)
);

CREATE INDEX idx_assessment_hazard ON bu_risk_assessment(hazard_id);
CREATE INDEX idx_assessment_date ON bu_risk_assessment(assessment_date);

-- 3. 安全措施表
CREATE TABLE bu_safety_measure (
    id VARCHAR2(36) NOT NULL,
    hazard_id VARCHAR2(36) NOT NULL,
    measure_code VARCHAR2(50) NOT NULL,
    measure_name VARCHAR2(200),
    measure_type VARCHAR2(50),
    measure_desc CLOB,
    implementation_dept VARCHAR2(100),
    responsible_person VARCHAR2(50),
    implementation_date DATE,
    verification_method CLOB,
    verification_result VARCHAR2(100),
    effectiveness_evaluation VARCHAR2(500),
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_safety_measure PRIMARY KEY (id),
    CONSTRAINT fk_measure_hazard FOREIGN KEY (hazard_id) REFERENCES bu_hazard_register(id)
);

CREATE INDEX idx_measure_hazard ON bu_safety_measure(hazard_id);
CREATE INDEX idx_measure_code ON bu_safety_measure(measure_code);

-- 4. 安全事件表
CREATE TABLE bu_safety_incident (
    id VARCHAR2(36) NOT NULL,
    incident_code VARCHAR2(50) NOT NULL UNIQUE,
    incident_title VARCHAR2(200),
    incident_type VARCHAR2(50),
    incident_date DATE NOT NULL,
    incident_location VARCHAR2(200),
    incident_desc CLOB,
    related_hazard_id VARCHAR2(36),
    injured_person VARCHAR2(200),
    injury_degree VARCHAR2(100),
    immediate_cause CLOB,
    root_cause CLOB,
    corrective_action CLOB,
    preventive_action CLOB,
    lesson_learned CLOB,
    investigation_team VARCHAR2(500),
    investigation_date DATE,
    status VARCHAR2(20) DEFAULT '开放',
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_safety_incident PRIMARY KEY (id),
    CONSTRAINT fk_incident_hazard FOREIGN KEY (related_hazard_id) REFERENCES bu_hazard_register(id)
);

CREATE INDEX idx_incident_code ON bu_safety_incident(incident_code);
CREATE INDEX idx_incident_date ON bu_safety_incident(incident_date);
CREATE INDEX idx_incident_status ON bu_safety_incident(status);

-- 5. 安全培训记录表
CREATE TABLE bu_safety_training_record (
    id VARCHAR2(36) NOT NULL,
    training_code VARCHAR2(50) NOT NULL,
    training_topic VARCHAR2(200),
    training_type VARCHAR2(50),
    training_date DATE NOT NULL,
    trainer VARCHAR2(50),
    training_content CLOB,
    trainees CLOB,
    training_hours NUMBER(5,2),
    training_materials VARCHAR2(500),
    assessment_method VARCHAR2(100),
    assessment_results CLOB,
    pass_rate NUMBER(5,2),
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_training_record PRIMARY KEY (id)
);

CREATE INDEX idx_training_code ON bu_safety_training_record(training_code);
CREATE INDEX idx_training_date ON bu_safety_training_record(training_date);

COMMIT;

SELECT COUNT(*) AS hazard_count FROM bu_hazard_register WHERE del_flag = 0;
