-- ============================================
-- 质量控制管理模块数据库表
-- 创建时间: 2026-01-28
-- 说明: 包含5张表，与QualityProcess协同实现质量执行管理
-- ============================================

-- 表1: 质量检查点表
DROP TABLE IF EXISTS bu_quality_check_point;
CREATE TABLE bu_quality_check_point (
    id VARCHAR2(36) PRIMARY KEY,
    checkpoint_code VARCHAR2(50) NOT NULL UNIQUE,
    checkpoint_name VARCHAR2(200) NOT NULL,
    process_id VARCHAR2(36),
    quality_process_id VARCHAR2(36),
    check_type VARCHAR2(50) COMMENT '检查类型：目测/仪表/尺寸测量/功能测试',
    accept_criteria CLOB COMMENT '验收准则(JSON格式)',
    checkpoint_order NUMBER(10) DEFAULT 0 COMMENT '检查点顺序',
    is_critical NUMBER(1) DEFAULT 0 COMMENT '是否关键点：0-否，1-是',
    standard_reference VARCHAR2(500) COMMENT '标准参考',
    control_method VARCHAR2(500) COMMENT '控制方法',
    status NUMBER(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    CONSTRAINT fk_checkpoint_quality_process FOREIGN KEY (quality_process_id)
        REFERENCES bu_base_quality_process(id) ON DELETE CASCADE
);

COMMENT ON TABLE bu_quality_check_point IS '质量检查点表';
COMMENT ON COLUMN bu_quality_check_point.checkpoint_code IS '检查点编号';
COMMENT ON COLUMN bu_quality_check_point.checkpoint_name IS '检查点名称';
COMMENT ON COLUMN bu_quality_check_point.process_id IS '工序ID';
COMMENT ON COLUMN bu_quality_check_point.quality_process_id IS '质量流程ID';
COMMENT ON COLUMN bu_quality_check_point.check_type IS '检查类型';
COMMENT ON COLUMN bu_quality_check_point.accept_criteria IS '验收准则JSON';
COMMENT ON COLUMN bu_quality_check_point.checkpoint_order IS '检查点顺序';
COMMENT ON COLUMN bu_quality_check_point.is_critical IS '是否关键点';

CREATE INDEX idx_checkpoint_code ON bu_quality_check_point(checkpoint_code);
CREATE INDEX idx_checkpoint_process ON bu_quality_check_point(quality_process_id);


-- 表2: 检查结果记录表
DROP TABLE IF EXISTS bu_quality_check_result;
CREATE TABLE bu_quality_check_result (
    id VARCHAR2(36) PRIMARY KEY,
    checkpoint_id VARCHAR2(36) NOT NULL,
    execution_record_id VARCHAR2(36) COMMENT '关联流程执行步骤ID',
    check_time DATE DEFAULT SYSDATE,
    check_person VARCHAR2(50),
    check_person_name VARCHAR2(100),
    result_status VARCHAR2(20) COMMENT '结果状态：合格/不合格/待复检',
    actual_value VARCHAR2(200) COMMENT '实际测量值',
    measure_unit VARCHAR2(50) COMMENT '测量单位',
    abnormality CLOB COMMENT '异常描述',
    defect_id VARCHAR2(36) COMMENT '关联缺陷ID',
    attachment_url VARCHAR2(500) COMMENT '附件路径',
    rework_required NUMBER(1) DEFAULT 0 COMMENT '是否需要返工：0-否，1-是',
    rework_time DATE COMMENT '返工时间',
    recheck_person VARCHAR2(50) COMMENT '复检人',
    recheck_time DATE COMMENT '复检时间',
    recheck_result VARCHAR2(20) COMMENT '复检结果',
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    CONSTRAINT fk_result_checkpoint FOREIGN KEY (checkpoint_id)
        REFERENCES bu_quality_check_point(id) ON DELETE CASCADE
);

COMMENT ON TABLE bu_quality_check_result IS '质量检查结果记录表';
COMMENT ON COLUMN bu_quality_check_result.checkpoint_id IS '检查点ID';
COMMENT ON COLUMN bu_quality_check_result.execution_record_id IS '流程执行步骤记录ID';
COMMENT ON COLUMN bu_quality_check_result.result_status IS '检查结果状态';
COMMENT ON COLUMN bu_quality_check_result.actual_value IS '实际测量值';
COMMENT ON COLUMN bu_quality_check_result.rework_required IS '是否需要返工';

CREATE INDEX idx_result_checkpoint ON bu_quality_check_result(checkpoint_id);
CREATE INDEX idx_result_execution ON bu_quality_check_result(execution_record_id);
CREATE INDEX idx_result_check_time ON bu_quality_check_result(check_time);


-- 表3: 缺陷管理表
DROP TABLE IF EXISTS bu_quality_defect;
CREATE TABLE bu_quality_defect (
    id VARCHAR2(36) PRIMARY KEY,
    defect_code VARCHAR2(50) NOT NULL UNIQUE,
    defect_name VARCHAR2(200) NOT NULL,
    checkpoint_id VARCHAR2(36),
    check_result_id VARCHAR2(36),
    severity_level VARCHAR2(20) COMMENT '严重程度：低/中/高/致命',
    defect_category VARCHAR2(100) COMMENT '缺陷类别',
    description CLOB COMMENT '缺陷描述',
    immediate_action CLOB COMMENT '立即采取行动',
    root_cause CLOB COMMENT '根本原因分析',
    corrective_action CLOB COMMENT '纠正措施',
    preventive_action CLOB COMMENT '预防措施',
    responsible_person VARCHAR2(50) COMMENT '责任人',
    responsible_person_name VARCHAR2(100),
    responsible_dept VARCHAR2(100) COMMENT '责任部门',
    found_date DATE DEFAULT SYSDATE COMMENT '发现日期',
    target_close_date DATE COMMENT '计划关闭日期',
    actual_close_date DATE COMMENT '实际关闭日期',
    verification_status VARCHAR2(20) COMMENT '验证状态：待验证/已验证/验证失败',
    verification_date DATE COMMENT '验证日期',
    verification_person VARCHAR2(50) COMMENT '验证人',
    verification_result CLOB COMMENT '验证结果',
    attachment_url VARCHAR2(500) COMMENT '附件路径',
    status VARCHAR2(20) DEFAULT '开放' COMMENT '状态：开放/进行中/已关闭/已取消',
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    CONSTRAINT fk_defect_checkpoint FOREIGN KEY (checkpoint_id)
        REFERENCES bu_quality_check_point(id) ON DELETE SET NULL
);

COMMENT ON TABLE bu_quality_defect IS '质量缺陷管理表';
COMMENT ON COLUMN bu_quality_defect.defect_code IS '缺陷编号';
COMMENT ON COLUMN bu_quality_defect.severity_level IS '严重程度';
COMMENT ON COLUMN bu_quality_defect.defect_category IS '缺陷类别';
COMMENT ON COLUMN bu_quality_defect.root_cause IS '根本原因';
COMMENT ON COLUMN bu_quality_defect.corrective_action IS '纠正措施';

CREATE INDEX idx_defect_code ON bu_quality_defect(defect_code);
CREATE INDEX idx_defect_status ON bu_quality_defect(status);
CREATE INDEX idx_defect_severity ON bu_quality_defect(severity_level);
CREATE INDEX idx_defect_responsible ON bu_quality_defect(responsible_person);


-- 表4: 不符合报告(NCR)表
DROP TABLE IF EXISTS bu_quality_ncr;
CREATE TABLE bu_quality_ncr (
    id VARCHAR2(36) PRIMARY KEY,
    ncr_code VARCHAR2(50) NOT NULL UNIQUE,
    ncr_title VARCHAR2(200) NOT NULL,
    defect_id VARCHAR2(36),
    report_person VARCHAR2(50) COMMENT '报告人',
    report_person_name VARCHAR2(100),
    report_time DATE DEFAULT SYSDATE COMMENT '报告时间',
    classification VARCHAR2(50) COMMENT '分类：设计问题/工艺问题/人员问题/设备问题/材料问题',
    severity_level VARCHAR2(20) COMMENT '严重程度：低/中/高/致命',
    description CLOB COMMENT '不符合描述',
    impact_analysis CLOB COMMENT '影响分析',
    immediate_action CLOB COMMENT '立即采取行动',
    response_deadline DATE COMMENT '响应期限',
    responsible_dept VARCHAR2(100) COMMENT '责任部门',
    responsible_person VARCHAR2(50) COMMENT '责任人',
    responsible_person_name VARCHAR2(100),
    corrective_plan CLOB COMMENT '纠正计划',
    preventive_plan CLOB COMMENT '预防计划',
    implementation_date DATE COMMENT '实施日期',
    implementation_person VARCHAR2(50) COMMENT '实施人',
    implementation_result CLOB COMMENT '实施结果',
    verification_date DATE COMMENT '验证日期',
    verification_person VARCHAR2(50) COMMENT '验证人',
    verification_result CLOB COMMENT '验证结果',
    approver VARCHAR2(50) COMMENT '审批人',
    approver_name VARCHAR2(100),
    approval_time DATE COMMENT '审批时间',
    approval_comment CLOB COMMENT '审批意见',
    approval_status VARCHAR2(20) COMMENT '审批状态：待审批/已批准/已驳回',
    attachment_url VARCHAR2(500) COMMENT '附件路径',
    status VARCHAR2(20) DEFAULT '开放' COMMENT '状态：开放/进行中/已关闭',
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    CONSTRAINT fk_ncr_defect FOREIGN KEY (defect_id)
        REFERENCES bu_quality_defect(id) ON DELETE CASCADE
);

COMMENT ON TABLE bu_quality_ncr IS '不符合报告NCR表';
COMMENT ON COLUMN bu_quality_ncr.ncr_code IS 'NCR编号';
COMMENT ON COLUMN bu_quality_ncr.ncr_title IS 'NCR标题';
COMMENT ON COLUMN bu_quality_ncr.classification IS 'NCR分类';
COMMENT ON COLUMN bu_quality_ncr.severity_level IS '严重程度';
COMMENT ON COLUMN bu_quality_ncr.approval_status IS '审批状态';

CREATE INDEX idx_ncr_code ON bu_quality_ncr(ncr_code);
CREATE INDEX idx_ncr_defect ON bu_quality_ncr(defect_id);
CREATE INDEX idx_ncr_status ON bu_quality_ncr(status);
CREATE INDEX idx_ncr_approval ON bu_quality_ncr(approval_status);


-- 表5: 质量趋势统计表
DROP TABLE IF EXISTS bu_quality_trend;
CREATE TABLE bu_quality_trend (
    id VARCHAR2(36) PRIMARY KEY,
    trend_code VARCHAR2(50) NOT NULL UNIQUE,
    checkpoint_id VARCHAR2(36),
    statistic_period VARCHAR2(50) COMMENT '统计周期：日/周/月/季度/年',
    period_start_date DATE COMMENT '周期开始日期',
    period_end_date DATE COMMENT '周期结束日期',
    total_checks NUMBER(10) DEFAULT 0 COMMENT '总检查次数',
    pass_count NUMBER(10) DEFAULT 0 COMMENT '合格次数',
    fail_count NUMBER(10) DEFAULT 0 COMMENT '不合格次数',
    pass_rate NUMBER(5, 2) COMMENT '合格率（百分比）',
    trend_value NUMBER(5, 2) COMMENT '趋势值（相对上期变化百分比）',
    defect_count NUMBER(10) DEFAULT 0 COMMENT '缺陷数量',
    ncr_count NUMBER(10) DEFAULT 0 COMMENT 'NCR数量',
    critical_defect_count NUMBER(10) DEFAULT 0 COMMENT '严重缺陷数量',
    analysis_notes CLOB COMMENT '分析备注',
    improvement_suggestions CLOB COMMENT '改进建议',
    status NUMBER(1) DEFAULT 1 COMMENT '状态：0-草稿，1-已发布',
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    CONSTRAINT fk_trend_checkpoint FOREIGN KEY (checkpoint_id)
        REFERENCES bu_quality_check_point(id) ON DELETE CASCADE
);

COMMENT ON TABLE bu_quality_trend IS '质量趋势统计分析表';
COMMENT ON COLUMN bu_quality_trend.trend_code IS '趋势编号';
COMMENT ON COLUMN bu_quality_trend.statistic_period IS '统计周期';
COMMENT ON COLUMN bu_quality_trend.total_checks IS '总检查次数';
COMMENT ON COLUMN bu_quality_trend.pass_rate IS '合格率';
COMMENT ON COLUMN bu_quality_trend.trend_value IS '趋势值';

CREATE INDEX idx_trend_code ON bu_quality_trend(trend_code);
CREATE INDEX idx_trend_checkpoint ON bu_quality_trend(checkpoint_id);
CREATE INDEX idx_trend_period ON bu_quality_trend(period_start_date, period_end_date);


-- 插入初始化数据（示例）
INSERT INTO bu_quality_check_point (id, checkpoint_code, checkpoint_name, check_type, accept_criteria, checkpoint_order, is_critical, standard_reference, control_method, status, create_time, create_by)
VALUES (SYS_GUID(), 'CP001', '车轮直径检测', '尺寸测量', '{"min": 840, "max": 860, "unit": "mm"}', 1, 1, 'TB/T 449-2016', '卡尺测量', 1, SYSDATE, 'admin');

INSERT INTO bu_quality_check_point (id, checkpoint_code, checkpoint_name, check_type, accept_criteria, checkpoint_order, is_critical, standard_reference, control_method, status, create_time, create_by)
VALUES (SYS_GUID(), 'CP002', '制动系统功能测试', '功能测试', '{"pressure": 800, "unit": "kPa", "testDuration": 120}', 2, 1, 'TB/T 3134-2013', '压力表测试', 1, SYSDATE, 'admin');

COMMIT;

-- 创建序列（用于自动生成编号）
CREATE SEQUENCE seq_checkpoint_code START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_defect_code START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_ncr_code START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_trend_code START WITH 1000 INCREMENT BY 1 NOCACHE;
