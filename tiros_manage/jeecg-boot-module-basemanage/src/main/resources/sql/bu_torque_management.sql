-- ============================================================================
-- 紧固力矩管理模块数据库脚本
-- 作者: Claude AI
-- 日期: 2026-01-28
-- 说明: 用于车辆架大修信息管理系统的紧固力矩管理功能
-- ============================================================================

-- 1. 力矩规格表
CREATE TABLE bu_torque_specification (
    id VARCHAR2(36) NOT NULL,
    torque_code VARCHAR2(50) NOT NULL,
    torque_name VARCHAR2(200) NOT NULL,
    process_id VARCHAR2(36),
    fastener_type VARCHAR2(100),
    nominal_torque NUMBER(10,2),
    min_torque NUMBER(10,2),
    max_torque NUMBER(10,2),
    unit VARCHAR2(20) DEFAULT 'Nm',
    tool_id VARCHAR2(36),
    operator_guidance CLOB,
    warning_msg CLOB,
    status NUMBER(1) DEFAULT 0,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_torque_spec PRIMARY KEY (id)
);

COMMENT ON TABLE bu_torque_specification IS '紧固力矩规格表';
COMMENT ON COLUMN bu_torque_specification.id IS '主键ID';
COMMENT ON COLUMN bu_torque_specification.torque_code IS '力矩规格编号';
COMMENT ON COLUMN bu_torque_specification.torque_name IS '力矩规格名称';
COMMENT ON COLUMN bu_torque_specification.process_id IS '关联标准工序ID';
COMMENT ON COLUMN bu_torque_specification.fastener_type IS '紧固件类型';
COMMENT ON COLUMN bu_torque_specification.nominal_torque IS '标准力矩值';
COMMENT ON COLUMN bu_torque_specification.min_torque IS '最小力矩值';
COMMENT ON COLUMN bu_torque_specification.max_torque IS '最大力矩值';
COMMENT ON COLUMN bu_torque_specification.unit IS '单位';
COMMENT ON COLUMN bu_torque_specification.tool_id IS '所需工具ID';
COMMENT ON COLUMN bu_torque_specification.operator_guidance IS '操作指导';
COMMENT ON COLUMN bu_torque_specification.warning_msg IS '警告信息';
COMMENT ON COLUMN bu_torque_specification.status IS '状态：0-草稿，1-已发布，2-已作废';
COMMENT ON COLUMN bu_torque_specification.remark IS '备注';

CREATE INDEX idx_torque_spec_code ON bu_torque_specification(torque_code);
CREATE INDEX idx_torque_spec_process ON bu_torque_specification(process_id);
CREATE INDEX idx_torque_spec_status ON bu_torque_specification(status);
CREATE INDEX idx_torque_spec_ctime ON bu_torque_specification(create_time);

-- 2. 力矩检查点表
CREATE TABLE bu_torque_checkpoint (
    id VARCHAR2(36) NOT NULL,
    checkpoint_code VARCHAR2(50) NOT NULL,
    checkpoint_name VARCHAR2(200) NOT NULL,
    torque_spec_id VARCHAR2(36),
    process_id VARCHAR2(36),
    checkpoint_order NUMBER(10),
    location_desc VARCHAR2(500),
    position_diagram CLOB,
    check_frequency VARCHAR2(100),
    is_critical NUMBER(1) DEFAULT 0,
    accept_criteria CLOB,
    quality_level VARCHAR2(50),
    status NUMBER(1) DEFAULT 0,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_torque_checkpoint PRIMARY KEY (id)
);

COMMENT ON TABLE bu_torque_checkpoint IS '紧固力矩检查点表';
COMMENT ON COLUMN bu_torque_checkpoint.id IS '主键ID';
COMMENT ON COLUMN bu_torque_checkpoint.checkpoint_code IS '检查点编号';
COMMENT ON COLUMN bu_torque_checkpoint.checkpoint_name IS '检查点名称';
COMMENT ON COLUMN bu_torque_checkpoint.torque_spec_id IS '关联力矩规格ID';
COMMENT ON COLUMN bu_torque_checkpoint.process_id IS '关联标准工序ID';
COMMENT ON COLUMN bu_torque_checkpoint.checkpoint_order IS '检查顺序';
COMMENT ON COLUMN bu_torque_checkpoint.location_desc IS '位置描述';
COMMENT ON COLUMN bu_torque_checkpoint.position_diagram IS '位置示意图(JSON)';
COMMENT ON COLUMN bu_torque_checkpoint.check_frequency IS '检查频率';
COMMENT ON COLUMN bu_torque_checkpoint.is_critical IS '是否关键点：0-否，1-是';
COMMENT ON COLUMN bu_torque_checkpoint.accept_criteria IS '验收标准(JSON)';
COMMENT ON COLUMN bu_torque_checkpoint.quality_level IS '质量等级';
COMMENT ON COLUMN bu_torque_checkpoint.status IS '状态：0-草稿，1-已发布，2-已作废';
COMMENT ON COLUMN bu_torque_checkpoint.remark IS '备注';

CREATE INDEX idx_checkpoint_code ON bu_torque_checkpoint(checkpoint_code);
CREATE INDEX idx_checkpoint_spec ON bu_torque_checkpoint(torque_spec_id);
CREATE INDEX idx_checkpoint_process ON bu_torque_checkpoint(process_id);
CREATE INDEX idx_checkpoint_order ON bu_torque_checkpoint(checkpoint_order);

-- 3. 扭力工具标定记录表
CREATE TABLE bu_torque_calibration (
    id VARCHAR2(36) NOT NULL,
    calibration_code VARCHAR2(50) NOT NULL,
    tool_id VARCHAR2(36) NOT NULL,
    tool_name VARCHAR2(200),
    tool_model VARCHAR2(100),
    calibration_date DATE,
    calibrator VARCHAR2(50),
    calibration_agency VARCHAR2(200),
    certificate_no VARCHAR2(100),
    torque_range VARCHAR2(100),
    accuracy_level VARCHAR2(50),
    test_points CLOB,
    test_results CLOB,
    calibration_result VARCHAR2(20),
    pass_status NUMBER(1),
    next_calibration_date DATE,
    attachment_url VARCHAR2(1000),
    status NUMBER(1) DEFAULT 0,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_torque_calibration PRIMARY KEY (id)
);

COMMENT ON TABLE bu_torque_calibration IS '扭力工具标定记录表';
COMMENT ON COLUMN bu_torque_calibration.id IS '主键ID';
COMMENT ON COLUMN bu_torque_calibration.calibration_code IS '标定记录编号';
COMMENT ON COLUMN bu_torque_calibration.tool_id IS '工具ID';
COMMENT ON COLUMN bu_torque_calibration.tool_name IS '工具名称';
COMMENT ON COLUMN bu_torque_calibration.tool_model IS '工具型号';
COMMENT ON COLUMN bu_torque_calibration.calibration_date IS '标定日期';
COMMENT ON COLUMN bu_torque_calibration.calibrator IS '标定人员';
COMMENT ON COLUMN bu_torque_calibration.calibration_agency IS '标定机构';
COMMENT ON COLUMN bu_torque_calibration.certificate_no IS '证书编号';
COMMENT ON COLUMN bu_torque_calibration.torque_range IS '力矩范围';
COMMENT ON COLUMN bu_torque_calibration.accuracy_level IS '准确度等级';
COMMENT ON COLUMN bu_torque_calibration.test_points IS '测试点(JSON)';
COMMENT ON COLUMN bu_torque_calibration.test_results IS '测试结果(JSON)';
COMMENT ON COLUMN bu_torque_calibration.calibration_result IS '标定结论';
COMMENT ON COLUMN bu_torque_calibration.pass_status IS '是否合格：0-不合格，1-合格';
COMMENT ON COLUMN bu_torque_calibration.next_calibration_date IS '下次标定日期';
COMMENT ON COLUMN bu_torque_calibration.attachment_url IS '附件路径';
COMMENT ON COLUMN bu_torque_calibration.status IS '状态：0-草稿，1-已归档';
COMMENT ON COLUMN bu_torque_calibration.remark IS '备注';

CREATE INDEX idx_calibration_code ON bu_torque_calibration(calibration_code);
CREATE INDEX idx_calibration_tool ON bu_torque_calibration(tool_id);
CREATE INDEX idx_calibration_date ON bu_torque_calibration(calibration_date);
CREATE INDEX idx_calibration_next ON bu_torque_calibration(next_calibration_date);

-- 4. 紧固力矩执行记录表
CREATE TABLE bu_torque_execution_record (
    id VARCHAR2(36) NOT NULL,
    record_code VARCHAR2(50) NOT NULL,
    checkpoint_id VARCHAR2(36),
    torque_spec_id VARCHAR2(36),
    process_id VARCHAR2(36),
    work_order_no VARCHAR2(50),
    train_no VARCHAR2(50),
    component_no VARCHAR2(100),
    execution_date DATE,
    executor VARCHAR2(50),
    tool_id VARCHAR2(36),
    tool_serial_no VARCHAR2(100),
    standard_torque NUMBER(10,2),
    actual_torque NUMBER(10,2),
    torque_unit VARCHAR2(20) DEFAULT 'Nm',
    is_qualified NUMBER(1),
    deviation NUMBER(10,2),
    deviation_rate NUMBER(10,2),
    mark_status VARCHAR2(20),
    re_tighten_count NUMBER(5) DEFAULT 0,
    re_tighten_reason CLOB,
    environment_temp NUMBER(10,2),
    environment_humidity NUMBER(10,2),
    photo_urls CLOB,
    inspector VARCHAR2(50),
    inspection_time DATE,
    inspection_result VARCHAR2(20),
    inspection_comment CLOB,
    status NUMBER(1) DEFAULT 0,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_torque_execution PRIMARY KEY (id)
);

COMMENT ON TABLE bu_torque_execution_record IS '紧固力矩执行记录表';
COMMENT ON COLUMN bu_torque_execution_record.id IS '主键ID';
COMMENT ON COLUMN bu_torque_execution_record.record_code IS '记录编号';
COMMENT ON COLUMN bu_torque_execution_record.checkpoint_id IS '检查点ID';
COMMENT ON COLUMN bu_torque_execution_record.torque_spec_id IS '力矩规格ID';
COMMENT ON COLUMN bu_torque_execution_record.process_id IS '工序ID';
COMMENT ON COLUMN bu_torque_execution_record.work_order_no IS '工单编号';
COMMENT ON COLUMN bu_torque_execution_record.train_no IS '车辆编号';
COMMENT ON COLUMN bu_torque_execution_record.component_no IS '部件编号';
COMMENT ON COLUMN bu_torque_execution_record.execution_date IS '执行日期';
COMMENT ON COLUMN bu_torque_execution_record.executor IS '执行人';
COMMENT ON COLUMN bu_torque_execution_record.tool_id IS '使用工具ID';
COMMENT ON COLUMN bu_torque_execution_record.tool_serial_no IS '工具序列号';
COMMENT ON COLUMN bu_torque_execution_record.standard_torque IS '标准力矩';
COMMENT ON COLUMN bu_torque_execution_record.actual_torque IS '实际力矩';
COMMENT ON COLUMN bu_torque_execution_record.torque_unit IS '力矩单位';
COMMENT ON COLUMN bu_torque_execution_record.is_qualified IS '是否合格：0-否，1-是';
COMMENT ON COLUMN bu_torque_execution_record.deviation IS '偏差值';
COMMENT ON COLUMN bu_torque_execution_record.deviation_rate IS '偏差率(%)';
COMMENT ON COLUMN bu_torque_execution_record.mark_status IS '防松标记状态';
COMMENT ON COLUMN bu_torque_execution_record.re_tighten_count IS '重新紧固次数';
COMMENT ON COLUMN bu_torque_execution_record.re_tighten_reason IS '重新紧固原因';
COMMENT ON COLUMN bu_torque_execution_record.environment_temp IS '环境温度(℃)';
COMMENT ON COLUMN bu_torque_execution_record.environment_humidity IS '环境湿度(%)';
COMMENT ON COLUMN bu_torque_execution_record.photo_urls IS '照片URLs(JSON)';
COMMENT ON COLUMN bu_torque_execution_record.inspector IS '检验人';
COMMENT ON COLUMN bu_torque_execution_record.inspection_time IS '检验时间';
COMMENT ON COLUMN bu_torque_execution_record.inspection_result IS '检验结果';
COMMENT ON COLUMN bu_torque_execution_record.inspection_comment IS '检验意见';
COMMENT ON COLUMN bu_torque_execution_record.status IS '状态：0-待检，1-已检';
COMMENT ON COLUMN bu_torque_execution_record.remark IS '备注';

CREATE INDEX idx_exec_record_code ON bu_torque_execution_record(record_code);
CREATE INDEX idx_exec_checkpoint ON bu_torque_execution_record(checkpoint_id);
CREATE INDEX idx_exec_spec ON bu_torque_execution_record(torque_spec_id);
CREATE INDEX idx_exec_work_order ON bu_torque_execution_record(work_order_no);
CREATE INDEX idx_exec_train ON bu_torque_execution_record(train_no);
CREATE INDEX idx_exec_date ON bu_torque_execution_record(execution_date);
CREATE INDEX idx_exec_executor ON bu_torque_execution_record(executor);

-- 初始化示例数据
-- 示例1: 跨接线夹块螺栓力矩规格
INSERT INTO bu_torque_specification (
    id, torque_code, torque_name, fastener_type,
    nominal_torque, min_torque, max_torque, unit,
    operator_guidance, status, create_time, create_by
) VALUES (
    SYS_GUID(), 'TQ-001', '跨接线夹块螺栓紧固', '夹块螺栓',
    10.00, 9.50, 10.50, 'Nm',
    '使用扭力扳手紧固，紧固后涂打防松标记', 1, SYSDATE, 'system'
);

-- 示例2: 矩形连接器螺栓力矩规格
INSERT INTO bu_torque_specification (
    id, torque_code, torque_name, fastener_type,
    nominal_torque, min_torque, max_torque, unit,
    operator_guidance, status, create_time, create_by
) VALUES (
    SYS_GUID(), 'TQ-002', '矩形连接器螺栓紧固', '矩形连接器螺栓',
    7.10, 6.80, 7.40, 'Nm',
    '使用扭力扳手紧固，确保连接器完全对位', 1, SYSDATE, 'system'
);

-- 示例3: 信号跨接线连接器力矩规格
INSERT INTO bu_torque_specification (
    id, torque_code, torque_name, fastener_type,
    nominal_torque, min_torque, max_torque, unit,
    operator_guidance, warning_msg, status, create_time, create_by
) VALUES (
    SYS_GUID(), 'TQ-003', '信号跨接线连接器紧固', '信号连接器螺栓',
    4.00, 3.80, 4.20, 'Nm',
    '使用扭力扳手紧固，严格控制力矩值',
    '【注意】此力矩值已在Rev2版本中从原值修改为4Nm，请严格按照新标准执行',
    1, SYSDATE, 'system'
);

-- 示例4: 车体间等势线螺栓力矩规格
INSERT INTO bu_torque_specification (
    id, torque_code, torque_name, fastener_type,
    nominal_torque, min_torque, max_torque, unit,
    operator_guidance, status, create_time, create_by
) VALUES (
    SYS_GUID(), 'TQ-004', '车体间等势线螺栓紧固', '等势线螺栓',
    31.00, 30.00, 32.00, 'Nm',
    '使用扭力扳手紧固，确保接地良好', 1, SYSDATE, 'system'
);

COMMIT;

-- 查询验证
SELECT COUNT(*) AS torque_spec_count FROM bu_torque_specification WHERE del_flag = 0;
SELECT COUNT(*) AS checkpoint_count FROM bu_torque_checkpoint WHERE del_flag = 0;
SELECT COUNT(*) AS calibration_count FROM bu_torque_calibration WHERE del_flag = 0;
SELECT COUNT(*) AS execution_count FROM bu_torque_execution_record WHERE del_flag = 0;
