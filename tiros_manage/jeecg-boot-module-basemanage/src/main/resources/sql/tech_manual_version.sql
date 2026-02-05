-- 工艺电子手册版本表
CREATE TABLE bu_base_tech_manual_version (
    id VARCHAR2(36) PRIMARY KEY,
    manual_id VARCHAR2(36) NOT NULL,
    version_number VARCHAR2(20) NOT NULL,
    version_name VARCHAR2(200),
    version_content CLOB,
    status NUMBER(1) DEFAULT 0,
    publish_time DATE,
    remark VARCHAR2(500),
    create_time DATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50)
);

COMMENT ON TABLE bu_base_tech_manual_version IS '工艺电子手册版本';
COMMENT ON COLUMN bu_base_tech_manual_version.id IS '主键ID';
COMMENT ON COLUMN bu_base_tech_manual_version.manual_id IS '手册ID';
COMMENT ON COLUMN bu_base_tech_manual_version.version_number IS '版本号';
COMMENT ON COLUMN bu_base_tech_manual_version.version_name IS '版本名称';
COMMENT ON COLUMN bu_base_tech_manual_version.version_content IS '版本内容（JSON格式）';
COMMENT ON COLUMN bu_base_tech_manual_version.status IS '状态：0-草稿，1-已发布';
COMMENT ON COLUMN bu_base_tech_manual_version.publish_time IS '发布时间';
COMMENT ON COLUMN bu_base_tech_manual_version.remark IS '备注';

-- 创建索引
CREATE INDEX idx_tech_manual_version_manual_id ON bu_base_tech_manual_version(manual_id);
CREATE INDEX idx_tech_manual_version_status ON bu_base_tech_manual_version(status);
