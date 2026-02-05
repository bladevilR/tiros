-- 作业指导书管理模块SQL脚本

-- 1. 作业指导书主表
CREATE TABLE bu_job_guide_book (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键',
    book_code VARCHAR(100) NOT NULL UNIQUE COMMENT '指导书编号',
    book_name VARCHAR(255) NOT NULL COMMENT '指导书名称',
    process_id VARCHAR(64) COMMENT '关联标准工序ID',
    process_name VARCHAR(255) COMMENT '工序名称',
    target_staff_level VARCHAR(50) COMMENT '目标员工等级（新员工/普通/高级）',
    content_type VARCHAR(50) COMMENT '内容类型（简易/详细/对标）',
    estimated_time INT COMMENT '预计时长（分钟）',
    difficulty_level INT COMMENT '难度级别（1-5）',
    has_video TINYINT DEFAULT 0 COMMENT '是否有视频',
    has_images TINYINT DEFAULT 0 COMMENT '是否有图片',
    quality_checklist LONGTEXT COMMENT '质量检查项JSON',
    safety_warnings LONGTEXT COMMENT '安全警示JSON',
    version VARCHAR(50) DEFAULT '1.0' COMMENT '版本号',
    approval_status VARCHAR(50) DEFAULT 'DRAFT' COMMENT '审批状态（DRAFT-草稿,SUBMITTED-已提交,APPROVED-已批准,REJECTED-已驳回）',
    approver VARCHAR(64) COMMENT '审批人ID',
    approver_name VARCHAR(255) COMMENT '审批人名称',
    approval_time DATETIME COMMENT '审批时间',
    approval_comment TEXT COMMENT '审批意见',
    status INT DEFAULT 0 COMMENT '状态（0-草稿,1-已发布,2-已作��）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(64) COMMENT '创建人ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(64) COMMENT '更新人ID',
    company_id VARCHAR(64) COMMENT '公司ID',
    workshop_id VARCHAR(64) COMMENT '车间ID',
    remarks TEXT COMMENT '备注',
    INDEX idx_book_code (book_code),
    INDEX idx_approval_status (approval_status),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业指导书主表';

-- 2. 作业指导书章节表
CREATE TABLE bu_job_guide_book_section (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键',
    book_id VARCHAR(64) NOT NULL COMMENT '指导书ID',
    section_code VARCHAR(100) COMMENT '章节编码',
    section_name VARCHAR(255) NOT NULL COMMENT '章节名称',
    section_order INT COMMENT '章节顺序',
    section_type VARCHAR(50) COMMENT '章节类型（文本/图片/视频/步骤/表格）',
    content LONGTEXT COMMENT '章节内容',
    media_urls LONGTEXT COMMENT '多媒体URLs JSON',
    parent_section_id VARCHAR(64) COMMENT '父章节ID（支持多级结构）',
    status INT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(64) COMMENT '创建人ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(64) COMMENT '更新人ID',
    FOREIGN KEY (book_id) REFERENCES bu_job_guide_book(id) ON DELETE CASCADE,
    INDEX idx_book_id (book_id),
    INDEX idx_section_order (section_order),
    INDEX idx_section_type (section_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业指导书章节表';

-- 3. 作业指导书步骤表
CREATE TABLE bu_job_guide_book_step (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键',
    section_id VARCHAR(64) NOT NULL COMMENT '章节ID',
    step_order INT COMMENT '步骤顺序',
    step_title VARCHAR(255) COMMENT '步骤标题',
    step_content TEXT COMMENT '步骤内容',
    operation_time INT COMMENT '操作时长（分钟）',
    is_critical TINYINT DEFAULT 0 COMMENT '是否关键步骤',
    images_urls LONGTEXT COMMENT '图片URLs JSON',
    video_url VARCHAR(500) COMMENT '视频URL',
    tools_required LONGTEXT COMMENT '所需工具JSON',
    materials_required LONGTEXT COMMENT '所需物料JSON',
    safety_notes TEXT COMMENT '安全注意事项',
    quality_checkpoints LONGTEXT COMMENT '质量检查点JSON',
    status INT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(64) COMMENT '创建人ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(64) COMMENT '更新人ID',
    FOREIGN KEY (section_id) REFERENCES bu_job_guide_book_section(id) ON DELETE CASCADE,
    INDEX idx_section_id (section_id),
    INDEX idx_step_order (step_order),
    INDEX idx_is_critical (is_critical)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业指导书步骤表';

-- 4. 质量检查项表
CREATE TABLE bu_job_guide_book_quality_check (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键',
    book_id VARCHAR(64) COMMENT '指导书ID',
    step_id VARCHAR(64) COMMENT '步骤ID',
    check_order INT COMMENT '检查项顺序',
    check_item VARCHAR(255) NOT NULL COMMENT '检查项目',
    check_method TEXT COMMENT '检查方法',
    acceptance_criteria TEXT COMMENT '验收标准',
    is_mandatory TINYINT DEFAULT 1 COMMENT '是否必检',
    reference_standard VARCHAR(500) COMMENT '参考标准',
    status INT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(64) COMMENT '创建人ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(64) COMMENT '更新人ID',
    FOREIGN KEY (book_id) REFERENCES bu_job_guide_book(id) ON DELETE CASCADE,
    FOREIGN KEY (step_id) REFERENCES bu_job_guide_book_step(id) ON DELETE SET NULL,
    INDEX idx_book_id (book_id),
    INDEX idx_step_id (step_id),
    INDEX idx_is_mandatory (is_mandatory)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='质量检查项表';

-- 创建索引以优化查询性能
CREATE INDEX idx_job_guide_book_book_code ON bu_job_guide_book(book_code);
CREATE INDEX idx_job_guide_book_approval_status ON bu_job_guide_book(approval_status);
CREATE INDEX idx_job_guide_book_status ON bu_job_guide_book(status);
CREATE INDEX idx_job_guide_book_section_book_id ON bu_job_guide_book_section(book_id);
CREATE INDEX idx_job_guide_book_step_section_id ON bu_job_guide_book_step(section_id);
CREATE INDEX idx_job_guide_book_quality_check_book_id ON bu_job_guide_book_quality_check(book_id);
