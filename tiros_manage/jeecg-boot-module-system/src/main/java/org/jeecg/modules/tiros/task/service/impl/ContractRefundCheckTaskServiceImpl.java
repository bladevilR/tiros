package org.jeecg.modules.tiros.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.tiros.task.service.ContractRefundCheckTaskService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.basemanage.alertaccept.mapper.BuBaseAlertAcceptMapper;
import org.jeecg.modules.outsource.bean.BuContractInfo;
import org.jeecg.modules.outsource.mapper.BuContractInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 委外合同质保金退款检查 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/17
 */
@Service
public class ContractRefundCheckTaskServiceImpl implements ContractRefundCheckTaskService {

    @Resource
    private BuContractInfoMapper buContractInfoMapper;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private BuBaseAlertAcceptMapper buBaseAlertAcceptMapper;


    /**
     * @see ContractRefundCheckTaskService#checkContractRefund()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean checkContractRefund() throws RuntimeException {
        Date now = new Date();

        List<BuContractInfo> contractList = buContractInfoMapper.selectListNeedRemindRefund(now);
        if (CollectionUtils.isNotEmpty(contractList)) {
            // 查询预警接收人username列表
            List<String> usernameList = buBaseAlertAcceptMapper.selectUsernameListByAlertType(9);
            if (CollectionUtils.isNotEmpty(usernameList)) {
                String toUser = String.join(",", usernameList);

                for (BuContractInfo contract : contractList) {
                    String warrantyStartDate = DateUtils.date_sdf.get().format(contract.getWarrantyStartDate());
                    String warrantyFinishDate = DateUtils.date_sdf.get().format(contract.getWarrantyFinishDate());
                    String title = String.format("委外合同质保金退款：合同%s已过质保期", contract.getContractName());
                    String content = String.format("线路：%s，合同编号：%s，合同名称：%s。该合同质保开始日期%s，质保结束日期%s，已过质保期，可以进行质保金退款，请及时处理。",
                            contract.getLineName(), contract.getContractNo(), contract.getContractName(),
                            warrantyStartDate, warrantyFinishDate);
                    sysBaseAPI.sendSysAnnouncement("admin", toUser, title, content);
                }
            }

            // 更新是否需要提醒退款
            List<String> contractIdList = contractList.stream()
                    .map(BuContractInfo::getId)
                    .collect(Collectors.toList());
            List<List<String>> contractIdBatchSubList = DatabaseBatchSubUtil.batchSubList(contractIdList);
            for (List<String> contractIdBatchSub : contractIdBatchSubList) {
                BuContractInfo updateContract = new BuContractInfo()
                        .setNeedRemindRefund(0);
                LambdaQueryWrapper<BuContractInfo> updateContractWrapper = new LambdaQueryWrapper<BuContractInfo>()
                        .in(BuContractInfo::getId, contractIdBatchSub);
                buContractInfoMapper.update(updateContract, updateContractWrapper);
            }
        }

        return true;
    }

}
