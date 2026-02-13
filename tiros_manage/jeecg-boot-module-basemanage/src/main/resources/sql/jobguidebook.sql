-- ============================================================
-- 作业指导书模块 Oracle DDL
-- ============================================================

-- 1. 作业指导书主表
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
COMMENT ON COLUMN bu_job_guide_book.reviewer_id IS '审核人ID';
COMMENT ON COLUMN bu_job_guide_book.reviewer_name IS '审核人姓名';
COMMENT ON COLUMN bu_job_guide_book.review_comment IS '审核意见';
COMMENT ON COLUMN bu_job_guide_book.review_time IS '审核时间';
COMMENT ON COLUMN bu_job_guide_book.approver_id IS '审批人ID';
COMMENT ON COLUMN bu_job_guide_book.approver_name IS '审批人姓名';
COMMENT ON COLUMN bu_job_guide_book.approve_comment IS '审批意见';
COMMENT ON COLUMN bu_job_guide_book.approve_time IS '审批时间';

CREATE INDEX idx_jgb_file_no ON bu_job_guide_book(file_no);
CREATE INDEX idx_jgb_status ON bu_job_guide_book(status);
CREATE INDEX idx_jgb_line_id ON bu_job_guide_book(line_id);
CREATE INDEX idx_jgb_train_type ON bu_job_guide_book(train_type_id);
CREATE INDEX idx_jgb_repair_prog ON bu_job_guide_book(repair_program_id);
CREATE INDEX idx_jgb_create_time ON bu_job_guide_book(create_time);

-- 2. 作业指导书明细(步骤)表
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

-- 3. 作业指导书步骤物料表
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

-- 4. 作业指导书步骤工器具表
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
