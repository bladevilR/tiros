-- 生产通知单管理表
CREATE TABLE bu_base_production_notice (
    id VARCHAR2(36) NOT NULL,
    notice_code VARCHAR2(50) NOT NULL,
    notice_title VARCHAR2(200) NOT NULL,
    notice_type VARCHAR2(50),
    work_order_no VARCHAR2(50),
    content CLOB,
    template_id VARCHAR2(36),
    approval_status NUMBER(1) DEFAULT 0,
    reviewer VARCHAR2(50),
    review_time DATE,
    review_comment VARCHAR2(1000),
    remark VARCHAR2(500),
    create_time DATE,
    create_by VARCHAR2(50),
    update_time DATE,
    update_by VARCHAR2(50),
    CONSTRAINT pk_bu_base_production_notice PRIMARY KEY (id)
);

-- 添加表注释
COMMENT ON TABLE bu_base_production_notice IS '生产通知单管理表';

-- 添加字段注释
COMMENT ON COLUMN bu_base_production_notice.id IS '主键ID';
COMMENT ON COLUMN bu_base_production_notice.notice_code IS '通知单编号';
COMMENT ON COLUMN bu_base_production_notice.notice_title IS '通知单标题';
COMMENT ON COLUMN bu_base_production_notice.notice_type IS '通知类型';
COMMENT ON COLUMN bu_base_production_notice.work_order_no IS '关联工单号';
COMMENT ON COLUMN bu_base_production_notice.content IS '通知内容';
COMMENT ON COLUMN bu_base_production_notice.template_id IS '模板ID';
COMMENT ON COLUMN bu_base_production_notice.approval_status IS '审批状态(0-草稿,1-待审核,2-已通过,3-已驳回)';
COMMENT ON COLUMN bu_base_production_notice.reviewer IS '审核人';
COMMENT ON COLUMN bu_base_production_notice.review_time IS '审核时间';
COMMENT ON COLUMN bu_base_production_notice.review_comment IS '审核意见';
COMMENT ON COLUMN bu_base_production_notice.remark IS '备注';
COMMENT ON COLUMN bu_base_production_notice.create_time IS '创建时间';
COMMENT ON COLUMN bu_base_production_notice.create_by IS '创建人';
COMMENT ON COLUMN bu_base_production_notice.update_time IS '更新时间';
COMMENT ON COLUMN bu_base_production_notice.update_by IS '更新人';

-- 创建索引
CREATE INDEX idx_notice_code ON bu_base_production_notice(notice_code);
CREATE INDEX idx_approval_status ON bu_base_production_notice(approval_status);
CREATE INDEX idx_work_order_no ON bu_base_production_notice(work_order_no);
CREATE INDEX idx_create_time ON bu_base_production_notice(create_time);
