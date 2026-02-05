-- 质量管理可视化表
CREATE TABLE bu_base_quality_process (
    id VARCHAR2(36) NOT NULL,
    process_code VARCHAR2(50) NOT NULL,
    process_name VARCHAR2(200) NOT NULL,
    train_no VARCHAR2(50),
    process_type NUMBER(1),
    process_steps CLOB,
    quality_checkpoints CLOB,
    flowchart_config CLOB,
    status NUMBER(1) DEFAULT 0,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    CONSTRAINT pk_quality_process PRIMARY KEY (id)
);

-- 添加注释
COMMENT ON TABLE bu_base_quality_process IS '质量管理可视化表';
COMMENT ON COLUMN bu_base_quality_process.id IS '主键ID';
COMMENT ON COLUMN bu_base_quality_process.process_code IS '流程编号';
COMMENT ON COLUMN bu_base_quality_process.process_name IS '流程名称';
COMMENT ON COLUMN bu_base_quality_process.train_no IS '车辆编号';
COMMENT ON COLUMN bu_base_quality_process.process_type IS '工序类型：1-大修，2-架修';
COMMENT ON COLUMN bu_base_quality_process.process_steps IS '工序步骤(JSON格式)';
COMMENT ON COLUMN bu_base_quality_process.quality_checkpoints IS '质量检查点(JSON格式)';
COMMENT ON COLUMN bu_base_quality_process.flowchart_config IS '流程图配置(JSON格式)';
COMMENT ON COLUMN bu_base_quality_process.status IS '状态：0-未开始，1-进行中，2-已完成';
COMMENT ON COLUMN bu_base_quality_process.remark IS '备注';
COMMENT ON COLUMN bu_base_quality_process.create_time IS '创建时间';
COMMENT ON COLUMN bu_base_quality_process.create_by IS '创建人';
COMMENT ON COLUMN bu_base_quality_process.update_time IS '更新时间';
COMMENT ON COLUMN bu_base_quality_process.update_by IS '更新人';

-- 创建索引
CREATE INDEX idx_quality_process_code ON bu_base_quality_process(process_code);
CREATE INDEX idx_quality_train_no ON bu_base_quality_process(train_no);
CREATE INDEX idx_quality_status ON bu_base_quality_process(status);
