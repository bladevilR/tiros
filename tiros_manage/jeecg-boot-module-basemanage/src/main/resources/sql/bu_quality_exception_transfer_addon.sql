-- 质量模块例外转序补充脚本（适配已存在库）

-- 1) 工单增加例外转序标记
ALTER TABLE bu_work_order ADD exception_transfer_flag NUMBER(2) DEFAULT 0;
COMMENT ON COLUMN bu_work_order.exception_transfer_flag IS '例外转序标记 0无 1待处理 2已放行';

