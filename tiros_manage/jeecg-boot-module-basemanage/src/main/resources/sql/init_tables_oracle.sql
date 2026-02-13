-- ============================================================
-- TIROS 自动建表脚本（Oracle）
-- 由 DatabaseTableInitializer 启动时自动执行
-- 用 -- @BLOCK@ 分隔每张表，用 -- TABLE: 标记表名
-- ============================================================

-- @BLOCK@
-- TABLE: BU_QUOTA_BOM
CREATE TABLE bu_quota_bom (
    id VARCHAR2(36) NOT NULL,
    bom_code VARCHAR2(50) NOT NULL,
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
COMMENT ON TABLE bu_quota_bom IS '定额BOM管理表';
CREATE INDEX idx_bu_quota_bom_code ON bu_quota_bom(bom_code);
CREATE INDEX idx_bu_quota_train_type ON bu_quota_bom(train_type);
CREATE INDEX idx_bu_quota_line ON bu_quota_bom(line);

-- @BLOCK@
-- TABLE: BU_STANDARD_PROCESS
CREATE TABLE bu_standard_process (
    id VARCHAR2(36) NOT NULL,
    process_no VARCHAR2(50) NOT NULL,
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
COMMENT ON TABLE bu_standard_process IS '标准工序管理表';
CREATE INDEX idx_bu_std_process_no ON bu_standard_process(process_no);
CREATE INDEX idx_bu_std_process_name ON bu_standard_process(process_name);
CREATE INDEX idx_bu_std_process_type ON bu_standard_process(process_type);

-- @BLOCK@
-- TABLE: BU_PRODUCTION_NOTICE
CREATE TABLE bu_production_notice (
    id VARCHAR2(36) NOT NULL,
    notice_no VARCHAR2(50) NOT NULL,
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
CREATE INDEX idx_notice_no ON bu_production_notice(notice_no);
CREATE INDEX idx_notice_status ON bu_production_notice(status);
CREATE INDEX idx_notice_create_time ON bu_production_notice(create_time);

-- @BLOCK@
-- TABLE: BU_PRODUCTION_NOTICE_ORDER_REL
CREATE TABLE bu_production_notice_order_rel (
    id VARCHAR2(36) NOT NULL,
    notice_id VARCHAR2(36) NOT NULL,
    order_id VARCHAR2(36) NOT NULL,
    order_code VARCHAR2(50),
    train_no VARCHAR2(50),
    del_flag NUMBER(1) DEFAULT 0,
    create_time DATE DEFAULT SYSDATE,
    update_time DATE DEFAULT SYSDATE,
    CONSTRAINT pk_bu_pn_order_rel PRIMARY KEY (id)
);
COMMENT ON TABLE bu_production_notice_order_rel IS '生产通知单与工单关联表';
CREATE INDEX idx_pn_rel_notice ON bu_production_notice_order_rel(notice_id);
CREATE INDEX idx_pn_rel_order ON bu_production_notice_order_rel(order_id);

-- @BLOCK@
-- TABLE: BU_QUALITY_VISUAL
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
CREATE INDEX idx_quality_plan_id ON bu_quality_visual(plan_id);
CREATE INDEX idx_quality_train_no ON bu_quality_visual(train_no);

-- @BLOCK@
-- TABLE: BU_JOB_GUIDE_BOOK
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
CREATE INDEX idx_jgb_file_no ON bu_job_guide_book(file_no);
CREATE INDEX idx_jgb_status ON bu_job_guide_book(status);
CREATE INDEX idx_jgb_line_id ON bu_job_guide_book(line_id);
CREATE INDEX idx_jgb_train_type ON bu_job_guide_book(train_type_id);
CREATE INDEX idx_jgb_repair_prog ON bu_job_guide_book(repair_program_id);

-- @BLOCK@
-- TABLE: BU_JOB_GUIDE_BOOK_DETAIL
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
COMMENT ON TABLE bu_job_guide_book_detail IS '作业指导书明细';
CREATE INDEX idx_jgb_detail_book ON bu_job_guide_book_detail(book_id);

-- @BLOCK@
-- TABLE: BU_JOB_GUIDE_BOOK_MATERIAL
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

-- @BLOCK@
-- TABLE: BU_JOB_GUIDE_BOOK_TOOLS
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

-- @BLOCK@
-- TABLE: SYS_PERMISSION_TIROS_MENU
INSERT INTO SYS_PERMISSION (ID,PARENT_ID,NAME,URL,COMPONENT,COMPONENT_NAME,REDIRECT,MENU_TYPE,PERMS,PERMS_TYPE,SORT_NO,ALWAYS_SHOW,ICON,IS_ROUTE,IS_LEAF,KEEP_ALIVE,HIDDEN,DESCRIPTION,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME,DEL_FLAG,RULE_FLAG,STATUS,INTERNAL_OR_EXTERNAL) SELECT 'tiros_basic_manage_parent',NULL,'TIROS基础管理','/tiros/craft','layouts/RouteView',NULL,NULL,0,NULL,NULL,100,0,'tool',1,0,0,0,'TIROS基础数据管理','admin',SYSDATE,'admin',SYSDATE,0,0,'1',0 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM SYS_PERMISSION WHERE ID='tiros_basic_manage_parent');
INSERT INTO SYS_PERMISSION (ID,PARENT_ID,NAME,URL,COMPONENT,COMPONENT_NAME,REDIRECT,MENU_TYPE,PERMS,PERMS_TYPE,SORT_NO,ALWAYS_SHOW,ICON,IS_ROUTE,IS_LEAF,KEEP_ALIVE,HIDDEN,DESCRIPTION,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME,DEL_FLAG,RULE_FLAG,STATUS,INTERNAL_OR_EXTERNAL) SELECT 'tiros_production_notice','tiros_basic_manage_parent','生产通知单','/tiros/craft/production-notice','tiros/basic/ProductionNotice','ProductionNotice',NULL,1,NULL,'1',1,0,'file-text',1,1,1,0,'生产通知单管理','admin',SYSDATE,'admin',SYSDATE,0,0,'1',0 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM SYS_PERMISSION WHERE ID='tiros_production_notice');
INSERT INTO SYS_PERMISSION (ID,PARENT_ID,NAME,URL,COMPONENT,COMPONENT_NAME,REDIRECT,MENU_TYPE,PERMS,PERMS_TYPE,SORT_NO,ALWAYS_SHOW,ICON,IS_ROUTE,IS_LEAF,KEEP_ALIVE,HIDDEN,DESCRIPTION,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME,DEL_FLAG,RULE_FLAG,STATUS,INTERNAL_OR_EXTERNAL) SELECT 'tiros_quality_process','tiros_basic_manage_parent','质量可视化','/tiros/craft/quality-process','tiros/basic/QualityProcess','QualityProcess',NULL,1,NULL,'1',2,0,'bar-chart',1,1,1,0,'质量管理可视化','admin',SYSDATE,'admin',SYSDATE,0,0,'1',0 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM SYS_PERMISSION WHERE ID='tiros_quality_process');
INSERT INTO SYS_PERMISSION (ID,PARENT_ID,NAME,URL,COMPONENT,COMPONENT_NAME,REDIRECT,MENU_TYPE,PERMS,PERMS_TYPE,SORT_NO,ALWAYS_SHOW,ICON,IS_ROUTE,IS_LEAF,KEEP_ALIVE,HIDDEN,DESCRIPTION,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME,DEL_FLAG,RULE_FLAG,STATUS,INTERNAL_OR_EXTERNAL) SELECT 'tiros_quota_bom','tiros_basic_manage_parent','定额BOM','/tiros/craft/quota-bom','tiros/basic/QuotaBom','QuotaBom',NULL,1,NULL,'1',3,0,'database',1,1,1,0,'定额BOM管理','admin',SYSDATE,'admin',SYSDATE,0,0,'1',0 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM SYS_PERMISSION WHERE ID='tiros_quota_bom');
INSERT INTO SYS_PERMISSION (ID,PARENT_ID,NAME,URL,COMPONENT,COMPONENT_NAME,REDIRECT,MENU_TYPE,PERMS,PERMS_TYPE,SORT_NO,ALWAYS_SHOW,ICON,IS_ROUTE,IS_LEAF,KEEP_ALIVE,HIDDEN,DESCRIPTION,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME,DEL_FLAG,RULE_FLAG,STATUS,INTERNAL_OR_EXTERNAL) SELECT 'tiros_standard_process','tiros_basic_manage_parent','标准工序','/tiros/craft/standard-process','tiros/basic/StandardProcess','StandardProcess',NULL,1,NULL,'1',4,0,'ordered-list',1,1,1,0,'标准工序管理','admin',SYSDATE,'admin',SYSDATE,0,0,'1',0 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM SYS_PERMISSION WHERE ID='tiros_standard_process');
INSERT INTO SYS_PERMISSION (ID,PARENT_ID,NAME,URL,COMPONENT,COMPONENT_NAME,REDIRECT,MENU_TYPE,PERMS,PERMS_TYPE,SORT_NO,ALWAYS_SHOW,ICON,IS_ROUTE,IS_LEAF,KEEP_ALIVE,HIDDEN,DESCRIPTION,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME,DEL_FLAG,RULE_FLAG,STATUS,INTERNAL_OR_EXTERNAL) SELECT 'tiros_tech_manual','tiros_basic_manage_parent','工艺电子手册','/tiros/craft/tech-manual','tiros/basic/TechManual','TechManual',NULL,1,NULL,'1',5,0,'book',1,1,1,0,'工艺电子手册管理','admin',SYSDATE,'admin',SYSDATE,0,0,'1',0 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM SYS_PERMISSION WHERE ID='tiros_tech_manual');
INSERT INTO SYS_PERMISSION (ID,PARENT_ID,NAME,URL,COMPONENT,COMPONENT_NAME,REDIRECT,MENU_TYPE,PERMS,PERMS_TYPE,SORT_NO,ALWAYS_SHOW,ICON,IS_ROUTE,IS_LEAF,KEEP_ALIVE,HIDDEN,DESCRIPTION,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME,DEL_FLAG,RULE_FLAG,STATUS,INTERNAL_OR_EXTERNAL) SELECT 'tiros_job_guide_book','tiros_basic_manage_parent','作业指导书','/tiros/craft/job-guide-book','tiros/basic/JobGuideBook','JobGuideBook',NULL,1,NULL,'1',6,0,'file-done',1,1,1,0,'作业指导书管理','admin',SYSDATE,'admin',SYSDATE,0,0,'1',0 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM SYS_PERMISSION WHERE ID='tiros_job_guide_book');
