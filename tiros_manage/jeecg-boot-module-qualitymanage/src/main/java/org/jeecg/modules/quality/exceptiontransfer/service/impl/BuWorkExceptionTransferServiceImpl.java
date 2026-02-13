package org.jeecg.modules.quality.exceptiontransfer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.modules.quality.exceptiontransfer.bean.BuWorkExceptionTransfer;
import org.jeecg.modules.quality.exceptiontransfer.bean.vo.BuWorkExceptionTransferQueryVO;
import org.jeecg.modules.quality.exceptiontransfer.mapper.BuWorkExceptionTransferMapper;
import org.jeecg.modules.quality.exceptiontransfer.service.BuWorkExceptionTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BuWorkExceptionTransferServiceImpl extends ServiceImpl<BuWorkExceptionTransferMapper, BuWorkExceptionTransfer>
        implements BuWorkExceptionTransferService {

    @Resource
    private BuWorkExceptionTransferMapper mapper;
    @Resource
    private SerialNumberGenerate serialNumberGenerate;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private ISysBaseAPI sysBaseAPI;

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public IPage<BuWorkExceptionTransfer> pageTransfer(BuWorkExceptionTransferQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        IPage<BuWorkExceptionTransfer> page = mapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
        Map<String, String> userMap = sysBaseAPI.mapAllUserIdRealName();
        page.getRecords().forEach(item -> {
            item.setSubmitUserName(userMap.get(item.getSubmitUserId()));
            item.setDecisionUserName(userMap.get(item.getDecisionUserId()));
        });
        return page;
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public BuWorkExceptionTransfer getByIdWithDetail(String id) {
        BuWorkExceptionTransfer transfer = this.getById(id);
        if (transfer == null) {
            return null;
        }
        Map<String, String> userMap = sysBaseAPI.mapAllUserIdRealName();
        transfer.setSubmitUserName(userMap.get(transfer.getSubmitUserId()));
        transfer.setDecisionUserName(userMap.get(transfer.getDecisionUserId()));
        return transfer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createByFault(BuWorkExceptionTransfer transfer) {
        if (transfer == null || StringUtils.isBlank(transfer.getFaultId()) || StringUtils.isBlank(transfer.getOrderId())) {
            throw new JeecgBootException("故障ID和工单ID不能为空");
        }

        LambdaQueryWrapper<BuWorkExceptionTransfer> query = new LambdaQueryWrapper<>();
        query.eq(BuWorkExceptionTransfer::getFaultId, transfer.getFaultId())
                .eq(BuWorkExceptionTransfer::getOrderId, transfer.getOrderId())
                .eq(BuWorkExceptionTransfer::getDelFlag, 0)
                .orderByDesc(BuWorkExceptionTransfer::getCreateTime);
        BuWorkExceptionTransfer exist = this.getOne(query, false);
        if (exist != null && (exist.getStatus() == null || exist.getStatus() == 0)) {
            throw new JeecgBootException("当前故障已存在待处理例外转序记录");
        }

        fillByFaultInfo(transfer);
        LoginUser user = currentUser();
        Date now = new Date();
        transfer.setTransferCode(generateTransferCode());
        transfer.setStatus(0);
        transfer.setDecisionType(null);
        transfer.setDecisionUserId(null);
        transfer.setDecisionTime(null);
        transfer.setSubmitTime(now);
        transfer.setSubmitUserId(user == null ? null : user.getId());
        transfer.setDelFlag(0);
        transfer.setCreateTime(now);
        transfer.setUpdateTime(now);
        this.save(transfer);

        refreshWorkOrderExceptionFlag(transfer.getOrderId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTransfer(BuWorkExceptionTransfer transfer) {
        if (transfer == null || StringUtils.isBlank(transfer.getId())) {
            throw new JeecgBootException("例外转序ID不能为空");
        }
        BuWorkExceptionTransfer db = this.getById(transfer.getId());
        if (db == null || db.getDelFlag() != null && db.getDelFlag() == 1) {
            throw new JeecgBootException("例外转序记录不存在");
        }
        if (db.getStatus() != null && db.getStatus() != 0) {
            throw new JeecgBootException("已完成或驳回的记录不允许修改");
        }
        db.setProcessName(transfer.getProcessName());
        db.setStepName(transfer.getStepName());
        db.setNextProcess(transfer.getNextProcess());
        db.setTransferDesc(transfer.getTransferDesc());
        db.setOrderTaskId(transfer.getOrderTaskId());
        db.setUpdateTime(new Date());
        return this.updateById(db);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean decideTransfer(String id, Integer status, Integer decisionType, String decisionRemark) {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("例外转序ID不能为空");
        }
        if (status == null || (status != 1 && status != 2)) {
            throw new JeecgBootException("状态必须为放行(1)或驳回(2)");
        }
        BuWorkExceptionTransfer db = this.getById(id);
        if (db == null || db.getDelFlag() != null && db.getDelFlag() == 1) {
            throw new JeecgBootException("例外转序记录不存在");
        }
        if (db.getStatus() != null && db.getStatus() != 0) {
            throw new JeecgBootException("该例外转序已处理");
        }

        LoginUser user = currentUser();
        Date now = new Date();
        db.setStatus(status);
        db.setDecisionType(decisionType == null ? 2 : decisionType);
        db.setDecisionRemark(decisionRemark);
        db.setDecisionTime(now);
        db.setDecisionUserId(user == null ? null : user.getId());
        db.setUpdateTime(now);
        this.updateById(db);

        refreshWorkOrderExceptionFlag(db.getOrderId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatch(String ids) {
        List<String> idList = parseIds(ids);
        if (idList.isEmpty()) {
            throw new JeecgBootException("例外转序ID不能为空");
        }
        Set<String> orderIdSet = new HashSet<>();
        List<BuWorkExceptionTransfer> transferList = this.list(new LambdaQueryWrapper<BuWorkExceptionTransfer>()
                .in(BuWorkExceptionTransfer::getId, idList)
                .eq(BuWorkExceptionTransfer::getDelFlag, 0));
        transferList.stream()
                .map(BuWorkExceptionTransfer::getOrderId)
                .filter(StringUtils::isNotBlank)
                .forEach(orderIdSet::add);

        Date now = new Date();
        LambdaUpdateWrapper<BuWorkExceptionTransfer> update = new LambdaUpdateWrapper<>();
        update.in(BuWorkExceptionTransfer::getId, idList)
                .eq(BuWorkExceptionTransfer::getDelFlag, 0)
                .set(BuWorkExceptionTransfer::getDelFlag, 1)
                .set(BuWorkExceptionTransfer::getUpdateTime, now);
        this.update(update);

        orderIdSet.forEach(this::refreshWorkOrderExceptionFlag);
        return true;
    }

    private void fillByFaultInfo(BuWorkExceptionTransfer transfer) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "SELECT f.plan_id, f.line_id, f.train_no, f.sys_id, f.fault_asset_id, f.report_user_id, f.happen_time, f.fault_desc, " +
                        "o.order_code, o.order_name, t.task_name, l.line_name, s.name AS sys_name, a.asset_name AS fault_asset_name " +
                        "FROM bu_fault_info f " +
                        "LEFT JOIN bu_work_order o ON o.id = f.work_order_id " +
                        "LEFT JOIN bu_work_order_task t ON t.id = f.order_task_id " +
                        "LEFT JOIN bu_mtr_line l ON l.line_id = f.line_id " +
                        "LEFT JOIN bu_train_asset_type s ON s.id = f.sys_id " +
                        "LEFT JOIN bu_train_asset a ON a.id = f.fault_asset_id " +
                        "WHERE f.id = ?",
                transfer.getFaultId());
        if (list.isEmpty()) {
            throw new JeecgBootException("未找到故障记录");
        }
        Map<String, Object> row = list.get(0);
        transfer.setPlanId((String) row.get("plan_id"));
        transfer.setLineId((String) row.get("line_id"));
        transfer.setTrainNo((String) row.get("train_no"));
        transfer.setSysId((String) row.get("sys_id"));
        transfer.setFaultAssetId((String) row.get("fault_asset_id"));
        transfer.setReportUserId((String) row.get("report_user_id"));
        transfer.setHappenTime((Date) row.get("happen_time"));
        transfer.setFaultDesc((String) row.get("fault_desc"));
        transfer.setOrderCode((String) row.get("order_code"));
        transfer.setOrderName((String) row.get("order_name"));
        transfer.setTaskName((String) row.get("task_name"));
        transfer.setLineName((String) row.get("line_name"));
        transfer.setSysName((String) row.get("sys_name"));
        transfer.setFaultAssetName((String) row.get("fault_asset_name"));
    }

    private void markWorkOrderExceptionFlag(String orderId, Integer flagValue) {
        if (StringUtils.isBlank(orderId)) {
            return;
        }
        try {
            jdbcTemplate.update("UPDATE bu_work_order SET exception_transfer_flag = ? WHERE id = ?", flagValue, orderId);
        } catch (Exception e) {
            log.warn("更新工单例外转序标记失败, orderId={}, flag={}", orderId, flagValue, e);
        }
    }

    private void refreshWorkOrderExceptionFlag(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return;
        }
        try {
            Integer flagValue = jdbcTemplate.queryForObject(
                    "SELECT CASE " +
                            "WHEN SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) > 0 THEN 1 " +
                            "WHEN SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) > 0 THEN 2 " +
                            "ELSE 0 END " +
                            "FROM bu_work_exception_transfer WHERE order_id = ? AND del_flag = 0",
                    Integer.class,
                    orderId);
            markWorkOrderExceptionFlag(orderId, flagValue == null ? 0 : flagValue);
        } catch (Exception e) {
            log.warn("refresh exception transfer flag failed, orderId={}", orderId, e);
        }
    }

    private String generateTransferCode() {
        try {
            return serialNumberGenerate.generateSerialNumberByCode("JDXLWZS");
        } catch (Exception e) {
            String fallback = "LWZS" + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(100, 1000);
            log.warn("生成例外转序编号失败，使用回退编号: {}", fallback, e);
            return fallback;
        }
    }

    private LoginUser currentUser() {
        try {
            return (LoginUser) SecurityUtils.getSubject().getPrincipal();
        } catch (Exception ignored) {
            return null;
        }
    }

    private List<String> parseIds(String ids) {
        if (StringUtils.isBlank(ids)) {
            return java.util.Collections.emptyList();
        }
        return Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }
}
