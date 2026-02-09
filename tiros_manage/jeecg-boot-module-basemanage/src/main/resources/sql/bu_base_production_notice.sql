-- 生产通知单管理表
CREATE TABLE bu_production_notice (
    id VARCHAR2(36) NOT NULL,
    notice_no VARCHAR2(50) NOT NULL UNIQUE,
    title VARCHAR2(200) NOT NULL,
    notice_type VARCHAR2(50),
    status VARCHAR2(50) DEFAULT '0',
    train_type VARCHAR2(50),
    line VARCHAR2(100),
    scope VARCHAR2(500),
    total_trains NUMBER(10) DEFAULT 0,
    completed_trains NUMBER(10) DEFAULT 0,
    progress VARCHAR2(50) DEFAULT '0%',
    content CLOB,
    related_doc_ids VARCHAR2(2000),
    related_form_ids VARCHAR2(2000),
    creator VARCHAR2(50),
    checker VARCHAR2(50),
    approver VARCHAR2(50),
    publish_time DATE,
    remark VARCHAR2(500),
    create_by VARCHAR2(50),
    create_time DATE DEFAULT SYSDATE,
    update_by VARCHAR2(50),
    update_time DATE DEFAULT SYSDATE,
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_bu_production_notice PRIMARY KEY (id)
);

-- 添加表注释
COMMENT ON TABLE bu_production_notice IS '生产通知单管理表';
COMMENT ON COLUMN bu_production_notice.id IS '主键ID';
COMMENT ON COLUMN bu_production_notice.notice_no IS '通知单号';
COMMENT ON COLUMN bu_production_notice.title IS '通知单标题';
COMMENT ON COLUMN bu_production_notice.notice_type IS '通知类型：1-技术类 2-变更类';
COMMENT ON COLUMN bu_production_notice.status IS '状态：0-草稿 1-审核中 2-已发布 3-已关闭';
COMMENT ON COLUMN bu_production_notice.train_type IS '适用车型';
COMMENT ON COLUMN bu_production_notice.line IS '适用线别';
COMMENT ON COLUMN bu_production_notice.scope IS '作业范围描述';
COMMENT ON COLUMN bu_production_notice.total_trains IS '涉及列车总数';
COMMENT ON COLUMN bu_production_notice.completed_trains IS '已完成列车数';
COMMENT ON COLUMN bu_production_notice.progress IS '完成进度百分比';
COMMENT ON COLUMN bu_production_notice.content IS '通知内容';
COMMENT ON COLUMN bu_production_notice.related_doc_ids IS '关联工艺文件ID';
COMMENT ON COLUMN bu_production_notice.related_form_ids IS '关联作业记录表ID';
COMMENT ON COLUMN bu_production_notice.creator IS '编制人';
COMMENT ON COLUMN bu_production_notice.checker IS '核对人';
COMMENT ON COLUMN bu_production_notice.approver IS '审批人';
COMMENT ON COLUMN bu_production_notice.publish_time IS '发布时间';
COMMENT ON COLUMN bu_production_notice.remark IS '备注';
COMMENT ON COLUMN bu_production_notice.create_by IS '创建人';
COMMENT ON COLUMN bu_production_notice.create_time IS '创建时间';
COMMENT ON COLUMN bu_production_notice.update_by IS '更新人';
COMMENT ON COLUMN bu_production_notice.update_time IS '更新时间';
COMMENT ON COLUMN bu_production_notice.del_flag IS '删除标志：0-未删除 1-已删除';

-- 创建索引
CREATE INDEX idx_notice_no ON bu_production_notice(notice_no);
CREATE INDEX idx_status ON bu_production_notice(status);
CREATE INDEX idx_create_time ON bu_production_notice(create_time);

-- 生产通知单与工单关联表
CREATE TABLE bu_production_notice_order_rel (
    id VARCHAR2(36) NOT NULL,
    notice_id VARCHAR2(36) NOT NULL,
    order_id VARCHAR2(36) NOT NULL,
    order_code VARCHAR2(50),
    train_no VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    create_time DATE DEFAULT SYSDATE,
    update_time DATE DEFAULT SYSDATE,
    CONSTRAINT pk_bu_production_notice_order_rel PRIMARY KEY (id)
);

COMMENT ON TABLE bu_production_notice_order_rel IS '生产通知单与工单关联表';
COMMENT ON COLUMN bu_production_notice_order_rel.id IS '主键ID';
COMMENT ON COLUMN bu_production_notice_order_rel.notice_id IS '生产通知单ID';
COMMENT ON COLUMN bu_production_notice_order_rel.order_id IS '工单ID';
COMMENT ON COLUMN bu_production_notice_order_rel.order_code IS '工单编号';
COMMENT ON COLUMN bu_production_notice_order_rel.train_no IS '车号';
COMMENT ON COLUMN bu_production_notice_order_rel.del_flag IS '删除标记：0-未删除 1-已删除';
COMMENT ON COLUMN bu_production_notice_order_rel.create_time IS '创建时间';
COMMENT ON COLUMN bu_production_notice_order_rel.update_time IS '更新时间';

CREATE INDEX idx_notice_order_rel_notice ON bu_production_notice_order_rel(notice_id);
CREATE INDEX idx_notice_order_rel_order ON bu_production_notice_order_rel(order_id);
CREATE INDEX idx_notice_order_rel_train ON bu_production_notice_order_rel(train_no);
