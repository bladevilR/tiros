-- 定额BOM管理表
CREATE TABLE bu_base_quota_bom (
    id VARCHAR2(36) NOT NULL,
    bom_code VARCHAR2(50) NOT NULL,
    bom_name VARCHAR2(200) NOT NULL,
    train_type VARCHAR2(50),
    component_code VARCHAR2(50),
    component_name VARCHAR2(200),
    parent_id VARCHAR2(36),
    bom_level NUMBER(2),
    part_number VARCHAR2(50),
    part_name VARCHAR2(200),
    specification VARCHAR2(200),
    quantity NUMBER(10,2),
    unit VARCHAR2(20),
    maintenance_requirement VARCHAR2(1000),
    explosion_diagram CLOB,
    detail_config CLOB,
    status NUMBER(1) DEFAULT 0,
    remark VARCHAR2(500),
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    CONSTRAINT pk_quota_bom PRIMARY KEY (id)
);

-- 添加注释
COMMENT ON TABLE bu_base_quota_bom IS '定额BOM管理表';
COMMENT ON COLUMN bu_base_quota_bom.id IS '主键ID';
COMMENT ON COLUMN bu_base_quota_bom.bom_code IS 'BOM编号';
COMMENT ON COLUMN bu_base_quota_bom.bom_name IS 'BOM名称';
COMMENT ON COLUMN bu_base_quota_bom.train_type IS '车辆类型';
COMMENT ON COLUMN bu_base_quota_bom.component_code IS '部件编号';
COMMENT ON COLUMN bu_base_quota_bom.component_name IS '部件名称';
COMMENT ON COLUMN bu_base_quota_bom.parent_id IS '父级ID';
COMMENT ON COLUMN bu_base_quota_bom.bom_level IS '层级';
COMMENT ON COLUMN bu_base_quota_bom.part_number IS '零件编号';
COMMENT ON COLUMN bu_base_quota_bom.part_name IS '零件名称';
COMMENT ON COLUMN bu_base_quota_bom.specification IS '规格型号';
COMMENT ON COLUMN bu_base_quota_bom.quantity IS '数量';
COMMENT ON COLUMN bu_base_quota_bom.unit IS '单位';
COMMENT ON COLUMN bu_base_quota_bom.maintenance_requirement IS '维修要求';
COMMENT ON COLUMN bu_base_quota_bom.explosion_diagram IS '爆炸图路径';
COMMENT ON COLUMN bu_base_quota_bom.detail_config IS '明细表配置(JSON)';
COMMENT ON COLUMN bu_base_quota_bom.status IS '状态：0-草稿，1-已发布，2-已作废';
COMMENT ON COLUMN bu_base_quota_bom.remark IS '备注';
COMMENT ON COLUMN bu_base_quota_bom.create_time IS '创建时间';
COMMENT ON COLUMN bu_base_quota_bom.create_by IS '创建人';
COMMENT ON COLUMN bu_base_quota_bom.update_time IS '更新时间';
COMMENT ON COLUMN bu_base_quota_bom.update_by IS '更新人';

-- 创建索引
CREATE INDEX idx_bom_code ON bu_base_quota_bom(bom_code);
CREATE INDEX idx_train_type ON bu_base_quota_bom(train_type);
CREATE INDEX idx_parent_id ON bu_base_quota_bom(parent_id);
CREATE INDEX idx_status ON bu_base_quota_bom(status);
