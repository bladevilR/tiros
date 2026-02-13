-- ============================================================
-- TIROS 新模块建表合并脚本（Oracle）
-- 包含：定额BOM、标准工序、生产通知单、质量可视化、作业指导书、菜单配置
-- 执行前请确认表不存在，避免重复创建
-- ============================================================

-- ************************************************************
-- 一、定额BOM管理表
-- ************************************************************
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
    CONSTRAINT pk_quota_bom PRIMARY KEY (id)
);

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

CREATE INDEX idx_bom_code ON bu_quota_bom(bom_code);
CREATE INDEX idx_bom_train_type ON bu_quota_bom(train_type);
CREATE INDEX idx_bom_line ON bu_quota_bom(line);

-- ************************************************************
-- 二、标准工序管理表
-- ************************************************************
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
    CONSTRAINT pk_standard_process PRIMARY KEY (id)
);

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

CREATE INDEX idx_process_no ON bu_standard_process(process_no);
CREATE INDEX idx_process_name ON bu_standard_process(process_name);
CREATE INDEX idx_process_type ON bu_standard_process(process_type);

-- ************************************************************
-- 三、生产通知单管理表
-- ************************************************************
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
COMMENT ON COLUMN bu_production_notice.del_flag IS '删除标志：0-未删除 1-已删除';

CREATE INDEX idx_notice_no ON bu_production_notice(notice_no);
CREATE INDEX idx_notice_status ON bu_production_notice(status);
CREATE INDEX idx_notice_create_time ON bu_production_notice(create_time);

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

CREATE INDEX idx_notice_order_rel_notice ON bu_production_notice_order_rel(notice_id);
CREATE INDEX idx_notice_order_rel_order ON bu_production_notice_order_rel(order_id);
CREATE INDEX idx_notice_order_rel_train ON bu_production_notice_order_rel(train_no);

-- ************************************************************
-- 四、质量可视化表
-- ************************************************************
CREATE TABLE bu_quality_visual (
    id VARCHAR2(36) NOT NULL,
    plan_id VARCHAR2(36),
    train_no VARCHAR2(50),
    train_type VARCHAR2(50),
    project_name VARCHAR2(200),
    process_flow CLOB,
    quality_points CLOB,
    completed_processes NUMBER(10) DEFAULT 0,
    total_processes NUMBER(10) DEFAULT 0,
    progress VARCHAR2(50) DEFAULT '0%',
    quality_issues NUMBER(10) DEFAULT 0,
    open_issues NUMBER(10) DEFAULT 0,
    quality_level VARCHAR2(20),
    remark VARCHAR2(500),
    create_by VARCHAR2(50),
    create_time DATE DEFAULT SYSDATE,
    update_by VARCHAR2(50),
    update_time DATE DEFAULT SYSDATE,
    del_flag NUMBER(1) DEFAULT 0,
    CONSTRAINT pk_quality_visual PRIMARY KEY (id)
);

COMMENT ON TABLE bu_quality_visual IS '质量可视化表';
COMMENT ON COLUMN bu_quality_visual.id IS '主键ID';
COMMENT ON COLUMN bu_quality_visual.plan_id IS '关联列计划ID';
COMMENT ON COLUMN bu_quality_visual.train_no IS '车号';
COMMENT ON COLUMN bu_quality_visual.train_type IS '车型';
COMMENT ON COLUMN bu_quality_visual.project_name IS '项目名称';
COMMENT ON COLUMN bu_quality_visual.process_flow IS '工序流程数据JSON';
COMMENT ON COLUMN bu_quality_visual.quality_points IS '质量检查点数据JSON';
COMMENT ON COLUMN bu_quality_visual.quality_level IS '质量等级：A-优秀 B-良好 C-合格 D-不合格';
COMMENT ON COLUMN bu_quality_visual.del_flag IS '删除标志：0-未删除 1-已删除';

CREATE INDEX idx_quality_plan_id ON bu_quality_visual(plan_id);
CREATE INDEX idx_quality_train_no ON bu_quality_visual(train_no);
CREATE INDEX idx_quality_create_time ON bu_quality_visual(create_time);

-- ************************************************************
-- 五、作业指导书模块（4张表）
-- ************************************************************

-- 5.1 作业指导书主表
CREATE TABLE bu_job_guide_book (
    id                  VARCHAR2(64)    PRIMARY KEY,
    file_no             VARCHAR2(100)   NOT NULL,
    file_name           VARCHAR2(255)   NOT NULL,
    file_ver            VARCHAR2(50)    NOT NULL,
    line_id             VARCHAR2(64),
    train_type_id       VARCHAR2(64),
    repair_program_id   VARCHAR2(64),
    project             VARCHAR2(200),
    exe_time            DATE,
    status              NUMBER(2)       DEFAULT 0,
    template_flag       NUMBER(1)       DEFAULT 0,
    content_html        CLOB,
    review_status       NUMBER(2)       DEFAULT 0,
    reviewer_id         VARCHAR2(64),
    reviewer_name       VARCHAR2(100),
    review_comment      VARCHAR2(1000),
    review_time         TIMESTAMP,
    approver_id         VARCHAR2(64),
    approver_name       VARCHAR2(100),
    approve_comment     VARCHAR2(1000),
    approve_time        TIMESTAMP,
    company_id          VARCHAR2(64),
    workshop_id         VARCHAR2(64),
    remark              VARCHAR2(500),
    create_by           VARCHAR2(64),
    create_time         TIMESTAMP       DEFAULT SYSTIMESTAMP,
    update_by           VARCHAR2(64),
    update_time         TIMESTAMP       DEFAULT SYSTIMESTAMP
);

COMMENT ON TABLE bu_job_guide_book IS '作业指导书主表';
COMMENT ON COLUMN bu_job_guide_book.file_no IS '文件编号';
COMMENT ON COLUMN bu_job_guide_book.file_name IS '文件名称';
COMMENT ON COLUMN bu_job_guide_book.file_ver IS '版本号';
COMMENT ON COLUMN bu_job_guide_book.line_id IS '所属线路ID';
COMMENT ON COLUMN bu_job_guide_book.train_type_id IS '车型ID';
COMMENT ON COLUMN bu_job_guide_book.repair_program_id IS '修程ID';
COMMENT ON COLUMN bu_job_guide_book.project IS '项目';
COMMENT ON COLUMN bu_job_guide_book.exe_time IS '实施日期';
COMMENT ON COLUMN bu_job_guide_book.status IS '状态：0-草稿 1-发布 2-审批中 3-审批通过 9-作废';
COMMENT ON COLUMN bu_job_guide_book.template_flag IS '是否模板：0-正式 1-模板';
COMMENT ON COLUMN bu_job_guide_book.content_html IS '正文HTML';
COMMENT ON COLUMN bu_job_guide_book.review_status IS '审核状态：0-草稿 1-待审核 2-审核通过 3-审核驳回';

CREATE INDEX idx_jgb_file_no ON bu_job_guide_book(file_no);
CREATE INDEX idx_jgb_status ON bu_job_guide_book(status);
CREATE INDEX idx_jgb_line_id ON bu_job_guide_book(line_id);
CREATE INDEX idx_jgb_train_type ON bu_job_guide_book(train_type_id);
CREATE INDEX idx_jgb_repair_prog ON bu_job_guide_book(repair_program_id);
CREATE INDEX idx_jgb_create_time ON bu_job_guide_book(create_time);

-- 5.2 作业指导书明细(步骤)表
CREATE TABLE bu_job_guide_book_detail (
    id              VARCHAR2(64)    PRIMARY KEY,
    book_id         VARCHAR2(64)    NOT NULL,
    step_num        NUMBER(5)       NOT NULL,
    step_title      VARCHAR2(255)   NOT NULL,
    step_content    CLOB,
    create_by       VARCHAR2(64),
    create_time     TIMESTAMP       DEFAULT SYSTIMESTAMP,
    update_by       VARCHAR2(64),
    update_time     TIMESTAMP       DEFAULT SYSTIMESTAMP
);

COMMENT ON TABLE bu_job_guide_book_detail IS '作业指导书明细(步骤)';
CREATE INDEX idx_jgb_detail_book ON bu_job_guide_book_detail(book_id);

-- 5.3 作业指导书步骤物料表
CREATE TABLE bu_job_guide_book_material (
    id                  VARCHAR2(64)    PRIMARY KEY,
    book_detail_id      VARCHAR2(64)    NOT NULL,
    material_type_id    VARCHAR2(64)    NOT NULL,
    amount              NUMBER(10,2),
    create_by           VARCHAR2(64),
    create_time         TIMESTAMP       DEFAULT SYSTIMESTAMP
);

COMMENT ON TABLE bu_job_guide_book_material IS '作业指导书步骤物料';
CREATE INDEX idx_jgb_mat_detail ON bu_job_guide_book_material(book_detail_id);

-- 5.4 作业指导书步骤工器具表
CREATE TABLE bu_job_guide_book_tools (
    id                  VARCHAR2(64)    PRIMARY KEY,
    book_detail_id      VARCHAR2(64)    NOT NULL,
    tool_type_id        VARCHAR2(64)    NOT NULL,
    amount              NUMBER(10,2),
    create_by           VARCHAR2(64),
    create_time         TIMESTAMP       DEFAULT SYSTIMESTAMP
);

COMMENT ON TABLE bu_job_guide_book_tools IS '作业指导书步骤工器具';
CREATE INDEX idx_jgb_tool_detail ON bu_job_guide_book_tools(book_detail_id);

-- ************************************************************
-- 六、菜单配置（SYS_PERMISSION）
-- 注意：如果菜单已存在请跳过对应INSERT，避免主键冲突
-- ************************************************************

-- 6.1 父菜单：TIROS基础管理
INSERT INTO SYS_PERMISSION VALUES (
  'tiros_basic_manage_parent', NULL, 'TIROS基础管理',
  '/tiros/craft', 'layouts/RouteView', NULL, NULL,
  0, NULL, NULL, 100, 0, 'tool', 1, 0, 0, 0,
  'TIROS基础数据管理', 'admin', SYSDATE, 'admin', SYSDATE,
  0, 0, '1', 0
);

-- 6.2 生产通知单
INSERT INTO SYS_PERMISSION VALUES (
  'tiros_production_notice', 'tiros_basic_manage_parent', '生产通知单',
  '/tiros/craft/production-notice', 'tiros/basic/ProductionNotice', 'ProductionNotice', NULL,
  1, NULL, '1', 1, 0, 'file-text', 1, 1, 1, 0,
  '生产通知单管理', 'admin', SYSDATE, 'admin', SYSDATE,
  0, 0, '1', 0
);

-- 6.3 质量可视化
INSERT INTO SYS_PERMISSION VALUES (
  'tiros_quality_process', 'tiros_basic_manage_parent', '质量可视化',
  '/tiros/craft/quality-process', 'tiros/basic/QualityProcess', 'QualityProcess', NULL,
  1, NULL, '1', 2, 0, 'bar-chart', 1, 1, 1, 0,
  '质量管理可视化', 'admin', SYSDATE, 'admin', SYSDATE,
  0, 0, '1', 0
);

-- 6.4 定额BOM
INSERT INTO SYS_PERMISSION VALUES (
  'tiros_quota_bom', 'tiros_basic_manage_parent', '定额BOM',
  '/tiros/craft/quota-bom', 'tiros/basic/QuotaBom', 'QuotaBom', NULL,
  1, NULL, '1', 3, 0, 'database', 1, 1, 1, 0,
  '定额BOM管理', 'admin', SYSDATE, 'admin', SYSDATE,
  0, 0, '1', 0
);

-- 6.5 标准工序
INSERT INTO SYS_PERMISSION VALUES (
  'tiros_standard_process', 'tiros_basic_manage_parent', '标准工序',
  '/tiros/craft/standard-process', 'tiros/basic/StandardProcess', 'StandardProcess', NULL,
  1, NULL, '1', 4, 0, 'ordered-list', 1, 1, 1, 0,
  '标准工序管理', 'admin', SYSDATE, 'admin', SYSDATE,
  0, 0, '1', 0
);

-- 6.6 工艺电子手册
INSERT INTO SYS_PERMISSION VALUES (
  'tiros_tech_manual', 'tiros_basic_manage_parent', '工艺电子手册',
  '/tiros/craft/tech-manual', 'tiros/basic/TechManual', 'TechManual', NULL,
  1, NULL, '1', 5, 0, 'book', 1, 1, 1, 0,
  '工艺电子手册管理', 'admin', SYSDATE, 'admin', SYSDATE,
  0, 0, '1', 0
);

-- 6.7 作业指导书
INSERT INTO SYS_PERMISSION VALUES (
  'tiros_job_guide_book', 'tiros_basic_manage_parent', '作业指导书',
  '/tiros/craft/job-guide-book', 'tiros/basic/JobGuideBook', 'JobGuideBook', NULL,
  1, NULL, '1', 6, 0, 'file-done', 1, 1, 1, 0,
  '作业指导书管理', 'admin', SYSDATE, 'admin', SYSDATE,
  0, 0, '1', 0
);

COMMIT;
