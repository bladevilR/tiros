-- 工艺电子手册主表
CREATE TABLE bu_base_tech_manual (
    id VARCHAR2(36) PRIMARY KEY,
    manual_code VARCHAR2(50) NOT NULL,
    manual_name VARCHAR2(200) NOT NULL,
    current_version VARCHAR2(20),
    status NUMBER(1) DEFAULT 0,
    implement_date DATE,
    before_content CLOB,
    after_content CLOB,
    remark VARCHAR2(500),
    create_time DATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50)
);

COMMENT ON TABLE bu_base_tech_manual IS '工艺电子手册';
COMMENT ON COLUMN bu_base_tech_manual.id IS '主键ID';
COMMENT ON COLUMN bu_base_tech_manual.manual_code IS '手册编号';
COMMENT ON COLUMN bu_base_tech_manual.manual_name IS '手册名称';
COMMENT ON COLUMN bu_base_tech_manual.current_version IS '当前版本';
COMMENT ON COLUMN bu_base_tech_manual.status IS '状态：0-草稿，1-已发布，2-已作废';
COMMENT ON COLUMN bu_base_tech_manual.implement_date IS '实施日期';
COMMENT ON COLUMN bu_base_tech_manual.before_content IS '修改前内容';
COMMENT ON COLUMN bu_base_tech_manual.after_content IS '修改后内容';
COMMENT ON COLUMN bu_base_tech_manual.remark IS '备注';

-- 工艺电子手册模板表
CREATE TABLE bu_base_tech_manual_template (
    id VARCHAR2(36) PRIMARY KEY,
    template_name VARCHAR2(200) NOT NULL,
    template_desc VARCHAR2(500),
    template_content CLOB,
    status NUMBER(1) DEFAULT 1,
    sort_order NUMBER(10) DEFAULT 0,
    create_time DATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50)
);

COMMENT ON TABLE bu_base_tech_manual_template IS '工艺电子手册模板';
COMMENT ON COLUMN bu_base_tech_manual_template.id IS '主键ID';
COMMENT ON COLUMN bu_base_tech_manual_template.template_name IS '模板名称';
COMMENT ON COLUMN bu_base_tech_manual_template.template_desc IS '模板描述';
COMMENT ON COLUMN bu_base_tech_manual_template.template_content IS '模板内容（JSON格式）';
COMMENT ON COLUMN bu_base_tech_manual_template.status IS '状态：0-禁用，1-启用';
COMMENT ON COLUMN bu_base_tech_manual_template.sort_order IS '排序';

-- 工艺电子手册模块表
CREATE TABLE bu_base_tech_manual_module (
    id VARCHAR2(36) PRIMARY KEY,
    manual_id VARCHAR2(36) NOT NULL,
    module_name VARCHAR2(200) NOT NULL,
    module_type VARCHAR2(50),
    module_data CLOB,
    sort_order NUMBER(10) DEFAULT 0,
    create_time DATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50)
);

COMMENT ON TABLE bu_base_tech_manual_module IS '工艺电子手册模块';
COMMENT ON COLUMN bu_base_tech_manual_module.id IS '主键ID';
COMMENT ON COLUMN bu_base_tech_manual_module.manual_id IS '手册ID';
COMMENT ON COLUMN bu_base_tech_manual_module.module_name IS '模块名称';
COMMENT ON COLUMN bu_base_tech_manual_module.module_type IS '模块类型';
COMMENT ON COLUMN bu_base_tech_manual_module.module_data IS '模块数据（JSON格式）';
COMMENT ON COLUMN bu_base_tech_manual_module.sort_order IS '排序';

-- 创建索引
CREATE INDEX idx_tech_manual_code ON bu_base_tech_manual(manual_code);
CREATE INDEX idx_tech_manual_status ON bu_base_tech_manual(status);
CREATE INDEX idx_tech_manual_module_manual_id ON bu_base_tech_manual_module(manual_id);
CREATE INDEX idx_tech_manual_template_status ON bu_base_tech_manual_template(status);
