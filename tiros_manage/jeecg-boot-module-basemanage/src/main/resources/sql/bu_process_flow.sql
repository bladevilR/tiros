-- ============================================================================
-- 工序流程管理模块数据库脚本
-- 作者: Claude AI
-- 日期: 2026-01-28
-- 说明: 用于车辆架大修信息管理系统的工序流程编排和执行追踪功能
-- ============================================================================

-- 1. 工序流程定义表
CREATE TABLE bu_process_flow_definition (
    id VARCHAR2(36) NOT NULL,
    flow_code VARCHAR2(50) NOT NULL,
    flow_name VARCHAR2(200) NOT NULL,
    flow_type VARCHAR2(50),
    flow_category VARCHAR2(50),
    description CLOB,
    train_type VARCHAR2(100),
    applicable_scope VARCHAR2(500),
    flowchart_config CLOB,
    estimated_time NUMBER(10,2),
    difficulty_level NUMBER(1),
    version VARCHAR2(20),
    status NUMBER(1) DEFAULT 0,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_process_flow_def PRIMARY KEY (id)
);

COMMENT ON TABLE bu_process_flow_definition IS '工序流程定义表';
COMMENT ON COLUMN bu_process_flow_definition.id IS '主键ID';
COMMENT ON COLUMN bu_process_flow_definition.flow_code IS '流程编号';
COMMENT ON COLUMN bu_process_flow_definition.flow_name IS '流程名称';
COMMENT ON COLUMN bu_process_flow_definition.flow_type IS '流程类型：架修、大修、临修等';
COMMENT ON COLUMN bu_process_flow_definition.flow_category IS '流程分类：车体、转向架、电气等';
COMMENT ON COLUMN bu_process_flow_definition.description IS '流程描述';
COMMENT ON COLUMN bu_process_flow_definition.train_type IS '适用车型';
COMMENT ON COLUMN bu_process_flow_definition.applicable_scope IS '适用范围';
COMMENT ON COLUMN bu_process_flow_definition.flowchart_config IS '流程图配置(JSON)';
COMMENT ON COLUMN bu_process_flow_definition.estimated_time IS '预计工时(小时)';
COMMENT ON COLUMN bu_process_flow_definition.difficulty_level IS '难度等级：1-5';
COMMENT ON COLUMN bu_process_flow_definition.version IS '版本号';
COMMENT ON COLUMN bu_process_flow_definition.status IS '状态：0-草稿，1-已发布，2-已作废';
COMMENT ON COLUMN bu_process_flow_definition.remark IS '备注';

CREATE INDEX idx_flow_def_code ON bu_process_flow_definition(flow_code);
CREATE INDEX idx_flow_def_type ON bu_process_flow_definition(flow_type);
CREATE INDEX idx_flow_def_status ON bu_process_flow_definition(status);
CREATE INDEX idx_flow_def_ctime ON bu_process_flow_definition(create_time);

-- 2. 流程步骤表
CREATE TABLE bu_process_flow_step (
    id VARCHAR2(36) NOT NULL,
    flow_id VARCHAR2(36) NOT NULL,
    step_code VARCHAR2(50) NOT NULL,
    step_name VARCHAR2(200) NOT NULL,
    step_order NUMBER(10),
    step_type VARCHAR2(50),
    process_id VARCHAR2(36),
    quality_process_id VARCHAR2(36),
    standard_time NUMBER(10,2),
    is_parallel NUMBER(1) DEFAULT 0,
    is_critical NUMBER(1) DEFAULT 0,
    is_quality_control NUMBER(1) DEFAULT 0,
    parent_step_id VARCHAR2(36),
    prerequisite_steps VARCHAR2(1000),
    step_config CLOB,
    operation_guidance CLOB,
    safety_warning CLOB,
    required_skills VARCHAR2(500),
    required_tools CLOB,
    required_materials CLOB,
    quality_checklist CLOB,
    attachment_urls CLOB,
    status NUMBER(1) DEFAULT 0,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_process_flow_step PRIMARY KEY (id)
);

COMMENT ON TABLE bu_process_flow_step IS '工序流程步骤表';
COMMENT ON COLUMN bu_process_flow_step.id IS '主键ID';
COMMENT ON COLUMN bu_process_flow_step.flow_id IS '流程定义ID';
COMMENT ON COLUMN bu_process_flow_step.step_code IS '步骤编号';
COMMENT ON COLUMN bu_process_flow_step.step_name IS '步骤名称';
COMMENT ON COLUMN bu_process_flow_step.step_order IS '步骤顺序';
COMMENT ON COLUMN bu_process_flow_step.step_type IS '步骤类型：准备、操作、检查、记录等';
COMMENT ON COLUMN bu_process_flow_step.process_id IS '关联标准工序ID';
COMMENT ON COLUMN bu_process_flow_step.quality_process_id IS '关联质量工序ID';
COMMENT ON COLUMN bu_process_flow_step.standard_time IS '标准工时(小时)';
COMMENT ON COLUMN bu_process_flow_step.is_parallel IS '是否并行步骤：0-否，1-是';
COMMENT ON COLUMN bu_process_flow_step.is_critical IS '是否关键步骤：0-否，1-是';
COMMENT ON COLUMN bu_process_flow_step.is_quality_control IS '是否质量控制点：0-否，1-是';
COMMENT ON COLUMN bu_process_flow_step.parent_step_id IS '父步骤ID（用于子步骤）';
COMMENT ON COLUMN bu_process_flow_step.prerequisite_steps IS '前置步骤ID列表(逗号分隔)';
COMMENT ON COLUMN bu_process_flow_step.step_config IS '步骤配置(JSON)';
COMMENT ON COLUMN bu_process_flow_step.operation_guidance IS '操作指导';
COMMENT ON COLUMN bu_process_flow_step.safety_warning IS '安全警示';
COMMENT ON COLUMN bu_process_flow_step.required_skills IS '所需技能';
COMMENT ON COLUMN bu_process_flow_step.required_tools IS '所需工具(JSON)';
COMMENT ON COLUMN bu_process_flow_step.required_materials IS '所需物料(JSON)';
COMMENT ON COLUMN bu_process_flow_step.quality_checklist IS '质量检查项(JSON)';
COMMENT ON COLUMN bu_process_flow_step.attachment_urls IS '附件URLs(JSON)';
COMMENT ON COLUMN bu_process_flow_step.status IS '状态：0-禁用，1-启用';
COMMENT ON COLUMN bu_process_flow_step.remark IS '备注';

CREATE INDEX idx_flow_step_flow ON bu_process_flow_step(flow_id);
CREATE INDEX idx_flow_step_code ON bu_process_flow_step(step_code);
CREATE INDEX idx_flow_step_order ON bu_process_flow_step(step_order);
CREATE INDEX idx_flow_step_process ON bu_process_flow_step(process_id);

-- 3. 流程转移规则表
CREATE TABLE bu_process_flow_transition (
    id VARCHAR2(36) NOT NULL,
    flow_id VARCHAR2(36) NOT NULL,
    transition_name VARCHAR2(200),
    from_step_id VARCHAR2(36) NOT NULL,
    to_step_id VARCHAR2(36) NOT NULL,
    transition_type VARCHAR2(50),
    condition_expression CLOB,
    condition_desc VARCHAR2(500),
    action_config CLOB,
    priority NUMBER(10),
    is_default NUMBER(1) DEFAULT 0,
    status NUMBER(1) DEFAULT 1,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_flow_transition PRIMARY KEY (id)
);

COMMENT ON TABLE bu_process_flow_transition IS '工序流程转移规则表';
COMMENT ON COLUMN bu_process_flow_transition.id IS '主键ID';
COMMENT ON COLUMN bu_process_flow_transition.flow_id IS '流程定义ID';
COMMENT ON COLUMN bu_process_flow_transition.transition_name IS '转移规则名称';
COMMENT ON COLUMN bu_process_flow_transition.from_step_id IS '起始步骤ID';
COMMENT ON COLUMN bu_process_flow_transition.to_step_id IS '目标步骤ID';
COMMENT ON COLUMN bu_process_flow_transition.transition_type IS '转移类型：正常、异常、分支等';
COMMENT ON COLUMN bu_process_flow_transition.condition_expression IS '条件表达式';
COMMENT ON COLUMN bu_process_flow_transition.condition_desc IS '条件描述';
COMMENT ON COLUMN bu_process_flow_transition.action_config IS '动作配置(JSON)';
COMMENT ON COLUMN bu_process_flow_transition.priority IS '优先级';
COMMENT ON COLUMN bu_process_flow_transition.is_default IS '是否默认转移：0-否，1-是';
COMMENT ON COLUMN bu_process_flow_transition.status IS '状态：0-禁用，1-启用';
COMMENT ON COLUMN bu_process_flow_transition.remark IS '备注';

CREATE INDEX idx_transition_flow ON bu_process_flow_transition(flow_id);
CREATE INDEX idx_transition_from ON bu_process_flow_transition(from_step_id);
CREATE INDEX idx_transition_to ON bu_process_flow_transition(to_step_id);

-- 4. 流程执行实例表
CREATE TABLE bu_process_flow_execution (
    id VARCHAR2(36) NOT NULL,
    execution_code VARCHAR2(50) NOT NULL,
    flow_id VARCHAR2(36) NOT NULL,
    flow_name VARCHAR2(200),
    work_order_no VARCHAR2(50),
    train_no VARCHAR2(50),
    component_no VARCHAR2(100),
    executor VARCHAR2(50),
    executor_team VARCHAR2(100),
    start_time DATE,
    end_time DATE,
    actual_duration NUMBER(10,2),
    current_step_id VARCHAR2(36),
    current_step_name VARCHAR2(200),
    completion_rate NUMBER(10,2),
    quality_score NUMBER(10,2),
    execution_status VARCHAR2(20),
    pause_reason CLOB,
    pause_time DATE,
    resume_time DATE,
    execution_notes CLOB,
    status NUMBER(1) DEFAULT 0,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_flow_execution PRIMARY KEY (id)
);

COMMENT ON TABLE bu_process_flow_execution IS '工序流程执行实例表';
COMMENT ON COLUMN bu_process_flow_execution.id IS '主键ID';
COMMENT ON COLUMN bu_process_flow_execution.execution_code IS '执行记录编号';
COMMENT ON COLUMN bu_process_flow_execution.flow_id IS '流程定义ID';
COMMENT ON COLUMN bu_process_flow_execution.flow_name IS '流程名称';
COMMENT ON COLUMN bu_process_flow_execution.work_order_no IS '工单编号';
COMMENT ON COLUMN bu_process_flow_execution.train_no IS '车辆编号';
COMMENT ON COLUMN bu_process_flow_execution.component_no IS '部件编号';
COMMENT ON COLUMN bu_process_flow_execution.executor IS '执行人';
COMMENT ON COLUMN bu_process_flow_execution.executor_team IS '执行班组';
COMMENT ON COLUMN bu_process_flow_execution.start_time IS '开始时间';
COMMENT ON COLUMN bu_process_flow_execution.end_time IS '结束时间';
COMMENT ON COLUMN bu_process_flow_execution.actual_duration IS '实际工时(小时)';
COMMENT ON COLUMN bu_process_flow_execution.current_step_id IS '当前步骤ID';
COMMENT ON COLUMN bu_process_flow_execution.current_step_name IS '当前步骤名称';
COMMENT ON COLUMN bu_process_flow_execution.completion_rate IS '完成率(%)';
COMMENT ON COLUMN bu_process_flow_execution.quality_score IS '质量评分';
COMMENT ON COLUMN bu_process_flow_execution.execution_status IS '执行状态：未开始、进行中、已暂停、已完成、已取消';
COMMENT ON COLUMN bu_process_flow_execution.pause_reason IS '暂停原因';
COMMENT ON COLUMN bu_process_flow_execution.pause_time IS '暂停时间';
COMMENT ON COLUMN bu_process_flow_execution.resume_time IS '恢复时间';
COMMENT ON COLUMN bu_process_flow_execution.execution_notes IS '执行备注';
COMMENT ON COLUMN bu_process_flow_execution.status IS '状态：0-待审核，1-已审核';
COMMENT ON COLUMN bu_process_flow_execution.remark IS '备注';

CREATE INDEX idx_execution_code ON bu_process_flow_execution(execution_code);
CREATE INDEX idx_execution_flow ON bu_process_flow_execution(flow_id);
CREATE INDEX idx_execution_work_order ON bu_process_flow_execution(work_order_no);
CREATE INDEX idx_execution_train ON bu_process_flow_execution(train_no);
CREATE INDEX idx_execution_status ON bu_process_flow_execution(execution_status);
CREATE INDEX idx_execution_start ON bu_process_flow_execution(start_time);

-- 5. 流程执行步骤记录表
CREATE TABLE bu_process_flow_execution_step (
    id VARCHAR2(36) NOT NULL,
    execution_id VARCHAR2(36) NOT NULL,
    step_id VARCHAR2(36) NOT NULL,
    step_code VARCHAR2(50),
    step_name VARCHAR2(200),
    step_order NUMBER(10),
    executor VARCHAR2(50),
    start_time DATE,
    end_time DATE,
    actual_duration NUMBER(10,2),
    step_status VARCHAR2(20),
    result_data CLOB,
    quality_check_result VARCHAR2(20),
    quality_issues CLOB,
    photos_urls CLOB,
    videos_urls CLOB,
    signatures CLOB,
    inspector VARCHAR2(50),
    inspection_time DATE,
    inspection_result VARCHAR2(20),
    inspection_comment CLOB,
    deviation_reason CLOB,
    corrective_action CLOB,
    rework_count NUMBER(5) DEFAULT 0,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_execution_step PRIMARY KEY (id)
);

COMMENT ON TABLE bu_process_flow_execution_step IS '工序流程执行步骤记录表';
COMMENT ON COLUMN bu_process_flow_execution_step.id IS '主键ID';
COMMENT ON COLUMN bu_process_flow_execution_step.execution_id IS '流程执行实例ID';
COMMENT ON COLUMN bu_process_flow_execution_step.step_id IS '步骤定义ID';
COMMENT ON COLUMN bu_process_flow_execution_step.step_code IS '步骤编号';
COMMENT ON COLUMN bu_process_flow_execution_step.step_name IS '步骤名称';
COMMENT ON COLUMN bu_process_flow_execution_step.step_order IS '步骤顺序';
COMMENT ON COLUMN bu_process_flow_execution_step.executor IS '执行人';
COMMENT ON COLUMN bu_process_flow_execution_step.start_time IS '开始时间';
COMMENT ON COLUMN bu_process_flow_execution_step.end_time IS '结束时间';
COMMENT ON COLUMN bu_process_flow_execution_step.actual_duration IS '实际工时(小时)';
COMMENT ON COLUMN bu_process_flow_execution_step.step_status IS '步骤状态：未开始、进行中、已完成、已跳过、失败';
COMMENT ON COLUMN bu_process_flow_execution_step.result_data IS '结果数据(JSON)';
COMMENT ON COLUMN bu_process_flow_execution_step.quality_check_result IS '质量检查结果：合格、不合格';
COMMENT ON COLUMN bu_process_flow_execution_step.quality_issues IS '质量问题描述';
COMMENT ON COLUMN bu_process_flow_execution_step.photos_urls IS '照片URLs(JSON)';
COMMENT ON COLUMN bu_process_flow_execution_step.videos_urls IS '视频URLs(JSON)';
COMMENT ON COLUMN bu_process_flow_execution_step.signatures IS '签名数据(JSON)';
COMMENT ON COLUMN bu_process_flow_execution_step.inspector IS '检验人';
COMMENT ON COLUMN bu_process_flow_execution_step.inspection_time IS '检验时间';
COMMENT ON COLUMN bu_process_flow_execution_step.inspection_result IS '检验结果';
COMMENT ON COLUMN bu_process_flow_execution_step.inspection_comment IS '检验意见';
COMMENT ON COLUMN bu_process_flow_execution_step.deviation_reason IS '偏��原因';
COMMENT ON COLUMN bu_process_flow_execution_step.corrective_action IS '纠正措施';
COMMENT ON COLUMN bu_process_flow_execution_step.rework_count IS '返工次数';
COMMENT ON COLUMN bu_process_flow_execution_step.remark IS '备注';

CREATE INDEX idx_exec_step_exec ON bu_process_flow_execution_step(execution_id);
CREATE INDEX idx_exec_step_step ON bu_process_flow_execution_step(step_id);
CREATE INDEX idx_exec_step_executor ON bu_process_flow_execution_step(executor);
CREATE INDEX idx_exec_step_status ON bu_process_flow_execution_step(step_status);
CREATE INDEX idx_exec_step_start ON bu_process_flow_execution_step(start_time);

-- 初始化示例数据
-- 示例1: 跨接电缆安装流程定义
INSERT INTO bu_process_flow_definition (
    id, flow_code, flow_name, flow_type, flow_category,
    description, train_type, estimated_time, difficulty_level,
    status, create_time, create_by
) VALUES (
    SYS_GUID(), 'FLOW-001', '跨接电缆安装标准流程', '架修', '电气系统',
    '跨接电缆安装的标准作业流程，包括连接器检查、电缆安装、质量检查等步骤',
    '苏州地铁8号线', 8.0, 3, 1, SYSDATE, 'system'
);

-- 示例2: 车体转向架连接大修流程
INSERT INTO bu_process_flow_definition (
    id, flow_code, flow_name, flow_type, flow_category,
    description, train_type, estimated_time, difficulty_level,
    status, create_time, create_by
) VALUES (
    SYS_GUID(), 'FLOW-002', '车体转向架连接大修流程', '大修', '转向架',
    '电客车车体转向架连接大修的标准作业流程',
    '苏州1号线', 16.0, 4, 1, SYSDATE, 'system'
);

COMMIT;

-- 查询验证
SELECT COUNT(*) AS flow_def_count FROM bu_process_flow_definition WHERE del_flag = 0;
SELECT COUNT(*) AS flow_step_count FROM bu_process_flow_step WHERE del_flag = 0;
SELECT COUNT(*) AS transition_count FROM bu_process_flow_transition WHERE del_flag = 0;
SELECT COUNT(*) AS execution_count FROM bu_process_flow_execution WHERE del_flag = 0;
SELECT COUNT(*) AS exec_step_count FROM bu_process_flow_execution_step WHERE del_flag = 0;
