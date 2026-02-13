-- 定额BOM管理表
CREATE TABLE bu_quota_bom (
    id VARCHAR2(36) NOT NULL,
    bom_code VARCHAR2(50) NOT NULL UNIQUE,
    bom_name VARCHAR2(200) NOT NULL,
    train_type VARCHAR2(50),
    line VARCHAR2(100),
    position VARCHAR2(100),
    system VARCHAR2(100),
    module_level2 VARCHAR2(100),
    module_level3 VARCHAR2(100),
    part_details CLOB,
    explosion_diagram VARCHAR2(500),
    material_links CLOB,
    remark VARCHAR2(500),
    create_by VARCHAR2(50),
    create_time DATE DEFAULT SYSDATE,
    update_by VARCHAR2(50),
    update_time DATE DEFAULT SYSDATE,
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_bu_quota_bom PRIMARY KEY (id)
);

-- 添加注释
COMMENT ON TABLE bu_quota_bom IS '定额BOM管理表';
COMMENT ON COLUMN bu_quota_bom.id IS '主键ID';
COMMENT ON COLUMN bu_quota_bom.bom_code IS 'BOM编码';
COMMENT ON COLUMN bu_quota_bom.bom_name IS 'BOM名称';
COMMENT ON COLUMN bu_quota_bom.train_type IS '适用车型';
COMMENT ON COLUMN bu_quota_bom.line IS '线别';
COMMENT ON COLUMN bu_quota_bom.position IS '位置';
COMMENT ON COLUMN bu_quota_bom.system IS '所属系统';
COMMENT ON COLUMN bu_quota_bom.module_level2 IS '二级模块';
COMMENT ON COLUMN bu_quota_bom.module_level3 IS '三级模块';
COMMENT ON COLUMN bu_quota_bom.part_details IS '部件明细JSON数据';
COMMENT ON COLUMN bu_quota_bom.explosion_diagram IS '爆炸图URL';
COMMENT ON COLUMN bu_quota_bom.material_links IS '关联物资JSON数据';
COMMENT ON COLUMN bu_quota_bom.remark IS '备注';
COMMENT ON COLUMN bu_quota_bom.create_by IS '创建人';
COMMENT ON COLUMN bu_quota_bom.create_time IS '创建时间';
COMMENT ON COLUMN bu_quota_bom.update_by IS '更新人';
COMMENT ON COLUMN bu_quota_bom.update_time IS '更新时间';
COMMENT ON COLUMN bu_quota_bom.del_flag IS '删除标志：0-未删除 1-已删除';

-- 创建索引
CREATE INDEX idx_bu_quota_bom_code ON bu_quota_bom(bom_code);
CREATE INDEX idx_bu_quota_train_type ON bu_quota_bom(train_type);
CREATE INDEX idx_bu_quota_line ON bu_quota_bom(line);
