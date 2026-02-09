-- 工单关联生产通知单（技术类）
ALTER TABLE bu_work_order ADD production_notice_id VARCHAR2(36);

COMMENT ON COLUMN bu_work_order.production_notice_id IS '关联生产通知单ID';

CREATE INDEX idx_work_order_notice_id ON bu_work_order(production_notice_id);

