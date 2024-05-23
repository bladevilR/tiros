package org.jeecg.modules.quality.check.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.quality.check.bean.BuWorkOrderRecordCheck;
import org.jeecg.modules.quality.check.mapper.BuWorkOrderRecordCheckQualityMapper;
import org.jeecg.modules.quality.check.service.BuWorkOrderRecordCheckQualityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 作业记录明细检查信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-21
 */
@Slf4j
@Service
public class BuWorkOrderRecordCheckQualityServiceImpl extends ServiceImpl<BuWorkOrderRecordCheckQualityMapper, BuWorkOrderRecordCheck> implements BuWorkOrderRecordCheckQualityService {

    @Resource
    private BuWorkOrderRecordCheckQualityMapper buWorkOrderRecordCheckQualityMapper;

    /**
     * @see BuWorkOrderRecordCheckQualityService#submitCheck(BuWorkOrderRecordCheck)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean submitCheck(BuWorkOrderRecordCheck buWorkOrderRecordCheck) throws Exception {
        // 如果是专检时，则需要将检查结果填写到明细表
        if (buWorkOrderRecordCheck.getCheckType() == 3) {
            String recordDetailId = buWorkOrderRecordCheck.getRecordDetailId();
            // 检查结果 1正常0存在问题
            if (buWorkOrderRecordCheck.getResult() == 0) {
                // 作业记录明细中 结果result=（是否异常 0无1有 默认0）
                Integer recordDetailResult = 1;
                buWorkOrderRecordCheckQualityMapper.writeCheckResultToRecordDetail(recordDetailId, recordDetailResult);
            }
        }

        buWorkOrderRecordCheckQualityMapper.insert(buWorkOrderRecordCheck);

        return true;
    }

}
