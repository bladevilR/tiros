-- 质量可视化表
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

-- 添加注释
COMMENT ON TABLE bu_quality_visual IS '质量可视化表';
COMMENT ON COLUMN bu_quality_visual.id IS '主键ID';
COMMENT ON COLUMN bu_quality_visual.plan_id IS '关联列计划ID';
COMMENT ON COLUMN bu_quality_visual.train_no IS '车号';
COMMENT ON COLUMN bu_quality_visual.train_type IS '车型';
COMMENT ON COLUMN bu_quality_visual.project_name IS '项目名称';
COMMENT ON COLUMN bu_quality_visual.process_flow IS '工序流程数据JSON';
COMMENT ON COLUMN bu_quality_visual.quality_points IS '质量检查点数据JSON';
COMMENT ON COLUMN bu_quality_visual.completed_processes IS '已完成工序数';
COMMENT ON COLUMN bu_quality_visual.total_processes IS '总工序数';
COMMENT ON COLUMN bu_quality_visual.progress IS '完成进度百分比';
COMMENT ON COLUMN bu_quality_visual.quality_issues IS '质量问题数';
COMMENT ON COLUMN bu_quality_visual.open_issues IS '未关闭问题数';
COMMENT ON COLUMN bu_quality_visual.quality_level IS '质量等级：A-优秀 B-良好 C-合格 D-不合格';
COMMENT ON COLUMN bu_quality_visual.remark IS '备注';
COMMENT ON COLUMN bu_quality_visual.create_by IS '创建人';
COMMENT ON COLUMN bu_quality_visual.create_time IS '创建时间';
COMMENT ON COLUMN bu_quality_visual.update_by IS '更新人';
COMMENT ON COLUMN bu_quality_visual.update_time IS '更新时间';
COMMENT ON COLUMN bu_quality_visual.del_flag IS '删除标志：0-未删除 1-已删除';

-- 创建索引
CREATE INDEX idx_quality_plan_id ON bu_quality_visual(plan_id);
CREATE INDEX idx_quality_train_no ON bu_quality_visual(train_no);
CREATE INDEX idx_quality_create_time ON bu_quality_visual(create_time);
