-- 标准工序管理表
CREATE TABLE bu_base_standard_process (
    id VARCHAR2(36) NOT NULL,
    process_code VARCHAR2(50) NOT NULL,
    process_name VARCHAR2(200) NOT NULL,
    process_type VARCHAR2(50),
    description VARCHAR2(2000),
    tightening_sequence CLOB,
    tightening_diagram CLOB,
    standard_time NUMBER(10,2),
    tools CLOB,
    materials CLOB,
    quality_points CLOB,
    status NUMBER(1) DEFAULT 0,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    CONSTRAINT pk_standard_process PRIMARY KEY (id)
);

-- 添加注释
COMMENT ON TABLE bu_base_standard_process IS '标准工序管理表';
COMMENT ON COLUMN bu_base_standard_process.id IS '主键ID';
COMMENT ON COLUMN bu_base_standard_process.process_code IS '工序编号';
COMMENT ON COLUMN bu_base_standard_process.process_name IS '工序名称';
COMMENT ON COLUMN bu_base_standard_process.process_type IS '工序类型';
COMMENT ON COLUMN bu_base_standard_process.description IS '工序描述';
COMMENT ON COLUMN bu_base_standard_process.tightening_sequence IS '紧固顺序配置(JSON格式)';
COMMENT ON COLUMN bu_base_standard_process.tightening_diagram IS '紧固示意图(图片路径或JSON)';
COMMENT ON COLUMN bu_base_standard_process.standard_time IS '标准工时(小时)';
COMMENT ON COLUMN bu_base_standard_process.tools IS '所需工具(JSON格式)';
COMMENT ON COLUMN bu_base_standard_process.materials IS '所需物料(JSON格式)';
COMMENT ON COLUMN bu_base_standard_process.quality_points IS '质量检查点(JSON格式)';
COMMENT ON COLUMN bu_base_standard_process.status IS '状态：0-草稿，1-已发布，2-已作废';
COMMENT ON COLUMN bu_base_standard_process.remark IS '备注';
COMMENT ON COLUMN bu_base_standard_process.create_time IS '创建时间';
COMMENT ON COLUMN bu_base_standard_process.create_by IS '创建人';
COMMENT ON COLUMN bu_base_standard_process.update_time IS '更新时间';
COMMENT ON COLUMN bu_base_standard_process.update_by IS '更新人';

-- 创建索引
CREATE INDEX idx_process_code ON bu_base_standard_process(process_code);
CREATE INDEX idx_process_name ON bu_base_standard_process(process_name);
CREATE INDEX idx_status ON bu_base_standard_process(status);
