-- 标准工序管理表
CREATE TABLE bu_standard_process (
    id VARCHAR2(36) NOT NULL,
    process_no VARCHAR2(50) NOT NULL UNIQUE,
    process_name VARCHAR2(200) NOT NULL,
    process_type VARCHAR2(50),
    process_steps CLOB,
    standard_duration NUMBER(10) DEFAULT 0,
    process_images VARCHAR2(2000),
    fasten_sequence VARCHAR2(2000),
    fasten_diagram VARCHAR2(1000),
    train_type VARCHAR2(50),
    remark VARCHAR2(500),
    create_by VARCHAR2(50),
    create_time DATE DEFAULT SYSDATE,
    update_by VARCHAR2(50),
    update_time DATE DEFAULT SYSDATE,
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_bu_standard_process PRIMARY KEY (id)
);

-- 添加注释
COMMENT ON TABLE bu_standard_process IS '标准工序管理表';
COMMENT ON COLUMN bu_standard_process.id IS '主键ID';
COMMENT ON COLUMN bu_standard_process.process_no IS '工序编号';
COMMENT ON COLUMN bu_standard_process.process_name IS '工序名称';
COMMENT ON COLUMN bu_standard_process.process_type IS '工序类型：1-拆卸 2-检修 3-安装';
COMMENT ON COLUMN bu_standard_process.process_steps IS '工序步骤内容';
COMMENT ON COLUMN bu_standard_process.standard_duration IS '标准工时(分钟)';
COMMENT ON COLUMN bu_standard_process.process_images IS '工序图片，多个逗号分隔';
COMMENT ON COLUMN bu_standard_process.fasten_sequence IS '紧固顺序配置';
COMMENT ON COLUMN bu_standard_process.fasten_diagram IS '紧固示意图';
COMMENT ON COLUMN bu_standard_process.train_type IS '适用车型';
COMMENT ON COLUMN bu_standard_process.remark IS '备注';
COMMENT ON COLUMN bu_standard_process.create_by IS '创建人';
COMMENT ON COLUMN bu_standard_process.create_time IS '创建时间';
COMMENT ON COLUMN bu_standard_process.update_by IS '更新人';
COMMENT ON COLUMN bu_standard_process.update_time IS '更新时间';
COMMENT ON COLUMN bu_standard_process.del_flag IS '删除标志：0-未删除 1-已删除';

-- 创建索引
CREATE INDEX idx_bu_std_process_no ON bu_standard_process(process_no);
CREATE INDEX idx_bu_std_process_name ON bu_standard_process(process_name);
CREATE INDEX idx_bu_std_process_type ON bu_standard_process(process_type);
