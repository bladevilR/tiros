package org.jeecg.modules.tiros.rpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.tiros.rpt.service.AlertCheckService;
import org.jeecg.common.tiros.rpt.service.AlertRecordService;
import org.jeecg.modules.basemanage.alertaccept.entity.BuBaseAlert;
import org.jeecg.modules.basemanage.alertaccept.mapper.BuBaseAlertMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 预警信息记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-25
 */
@Service
public class AlertRecordServiceImpl implements AlertRecordService {

    @Resource
    private BuBaseAlertMapper buBaseAlertMapper;
    @Resource
    private AlertCheckService alertCheckService;


    /**
     * @see AlertRecordService#setAlertRecordHandled(Integer, List)
     */
    @Override
    public boolean setAlertRecordHandled(Integer alertType, List<String> alertObjIdList) {
        if (CollectionUtils.isEmpty(alertObjIdList)) {
            return true;
        }

        LambdaQueryWrapper<BuBaseAlert> wrapper = new LambdaQueryWrapper<BuBaseAlert>()
                .in(BuBaseAlert::getAlertObjId, alertObjIdList);
        if (null != alertType) {
            wrapper.eq(BuBaseAlert::getAlertType, alertType);
        }

        BuBaseAlert alert = new BuBaseAlert()
                .setStatus(1);
        buBaseAlertMapper.update(alert, wrapper);

        // 更新生成预警信息
        alertCheckService.generateAlertInfo(alertType);

        return true;
    }

    /**
     * @see AlertRecordService#deleteAlertRecord(Integer, List)
     */
    @Override
    public boolean deleteAlertRecord(Integer alertType, List<String> alertObjIdList) {
        if (CollectionUtils.isEmpty(alertObjIdList)) {
            return true;
        }

        LambdaQueryWrapper<BuBaseAlert> wrapper = new LambdaQueryWrapper<BuBaseAlert>()
                .in(BuBaseAlert::getAlertObjId, alertObjIdList);
        if (null != alertType) {
            wrapper.eq(BuBaseAlert::getAlertType, alertType);
        }
        buBaseAlertMapper.delete(wrapper);

        // 更新生成预警信息
        alertCheckService.generateAlertInfo(alertType);

        return true;
    }

}
