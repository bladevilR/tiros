-- 例外转序记录表
CREATE TABLE bu_work_exception_transfer (
    id VARCHAR2(36) NOT NULL,
    transfer_code VARCHAR2(64) NOT NULL,
    fault_id VARCHAR2(36) NOT NULL,
    order_id VARCHAR2(36) NOT NULL,
    order_task_id VARCHAR2(36),
    plan_id VARCHAR2(36),
    line_id VARCHAR2(36),
    train_no VARCHAR2(50),
    sys_id VARCHAR2(36),
    fault_asset_id VARCHAR2(36),
    report_user_id VARCHAR2(36),
    happen_time DATE,
    fault_desc VARCHAR2(2000),
    process_name VARCHAR2(500),
    step_name VARCHAR2(500),
    next_process VARCHAR2(1000),
    transfer_desc VARCHAR2(2000),
    status NUMBER(2) DEFAULT 0,
    decision_type NUMBER(2),
    decision_user_id VARCHAR2(36),
    decision_time DATE,
    decision_remark VARCHAR2(1000),
    submit_time DATE,
    submit_user_id VARCHAR2(36),
    company_id VARCHAR2(36),
    workshop_id VARCHAR2(36),
    del_flag NUMBER(1) DEFAULT 0,
    create_time DATE DEFAULT SYSDATE,
    create_by VARCHAR2(36),
    update_time DATE,
    update_by VARCHAR2(36),
    CONSTRAINT pk_work_exception_transfer PRIMARY KEY (id)
);

COMMENT ON TABLE bu_work_exception_transfer IS '例外转序记录';
COMMENT ON COLUMN bu_work_exception_transfer.transfer_code IS '转序编号';
COMMENT ON COLUMN bu_work_exception_transfer.fault_id IS '关联故障ID';
COMMENT ON COLUMN bu_work_exception_transfer.order_id IS '关联工单ID';
COMMENT ON COLUMN bu_work_exception_transfer.order_task_id IS '关联工单任务ID';
COMMENT ON COLUMN bu_work_exception_transfer.status IS '状态 0待处理 1已放行 2已驳回';
COMMENT ON COLUMN bu_work_exception_transfer.decision_type IS '完成方式 1审批流 2一键放行';
COMMENT ON COLUMN bu_work_exception_transfer.del_flag IS '删除标记 0否1是';

CREATE INDEX idx_ex_transfer_fault ON bu_work_exception_transfer(fault_id);
CREATE INDEX idx_ex_transfer_order ON bu_work_exception_transfer(order_id);
CREATE INDEX idx_ex_transfer_plan_train ON bu_work_exception_transfer(plan_id, train_no);

-- 工单增加例外转序标记
ALTER TABLE bu_work_order ADD exception_transfer_flag NUMBER(2) DEFAULT 0;
COMMENT ON COLUMN bu_work_order.exception_transfer_flag IS '例外转序标记 0无 1待处理 2已放行';

-- 可选：流水号模块（若未配置JDXLWZS，可执行）
-- INSERT INTO sys_serial_number(
--   id,module_code,module_name,prefix,infix_date,suffix_length,suffix_fill_zero,prepare_batch_size,current_suffix_max_value,clear_suffix_every_day,clear_suffix_last_day,create_time,create_by,update_time,update_by
-- )
-- SELECT REPLACE(SYS_GUID(),'-',''),'JDXLWZS','例外转序编号','LWZS',1,4,1,50,'0',1,NULL,SYSDATE,'admin',SYSDATE,'admin' FROM dual
-- WHERE NOT EXISTS (SELECT 1 FROM sys_serial_number WHERE module_code='JDXLWZS');
