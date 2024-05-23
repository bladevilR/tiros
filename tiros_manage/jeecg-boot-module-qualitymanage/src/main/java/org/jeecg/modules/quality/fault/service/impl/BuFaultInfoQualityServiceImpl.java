package org.jeecg.modules.quality.fault.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.modules.quality.fault.bean.BuFaultHandleRecord;
import org.jeecg.modules.quality.fault.bean.BuFaultInfo;
import org.jeecg.modules.quality.fault.bean.vo.BuFaultInfoQueryVO;
import org.jeecg.modules.quality.fault.mapper.BuFaultInfoQualityMapper;
import org.jeecg.modules.quality.fault.service.BuFaultInfoQualityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
@Slf4j
@Service
public class BuFaultInfoQualityServiceImpl extends ServiceImpl<BuFaultInfoQualityMapper, BuFaultInfo> implements BuFaultInfoQualityService {

    @Resource
    private BuFaultInfoQualityMapper buFaultInfoQualityMapper;
    @Resource
    private ISysBaseAPI sysBaseAPI;


    /**
     * @see BuFaultInfoQualityService#page(BuFaultInfoQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuFaultInfo> page(BuFaultInfoQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        IPage<BuFaultInfo> page = buFaultInfoQualityMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);

        List<BuFaultInfo> faultInfoList = page.getRecords();
        if (CollectionUtils.isNotEmpty(faultInfoList)) {
            Map<String, String> userIdNameMap = sysBaseAPI.mapAllUserIdRealName();

            for (BuFaultInfo faultInfo : faultInfoList) {
                faultInfo.setReportUserName(userIdNameMap.get(faultInfo.getReportUserId()));
                faultInfo.setDutyEngineerName(userIdNameMap.get(faultInfo.getDutyEngineer()));
                faultInfo.setCloseUserName(userIdNameMap.get(faultInfo.getCloseUserId()));
            }
        }

        return page;
    }

    /**
     * @see BuFaultInfoQualityService#getFaultById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuFaultInfo getFaultById(String id) throws Exception {
        BuFaultInfo faultInfo = buFaultInfoQualityMapper.selectFaultById(id);

        if (null != faultInfo) {
            Map<String, String> userIdNameMap = sysBaseAPI.mapAllUserIdRealName();

            faultInfo.setReportUserName(userIdNameMap.get(faultInfo.getReportUserId()));
            faultInfo.setDutyEngineerName(userIdNameMap.get(faultInfo.getDutyEngineer()));
            faultInfo.setCloseUserName(userIdNameMap.get(faultInfo.getCloseUserId()));

            List<BuFaultHandleRecord> handleRecordList = faultInfo.getHandleRecordList();
            if (CollectionUtils.isNotEmpty(handleRecordList)) {
                handleRecordList.sort(Comparator.comparing(BuFaultHandleRecord::getSolutionTime, Comparator.nullsLast(Comparator.naturalOrder())));
                faultInfo.setHandleRecordList(handleRecordList.subList(0, 1));
            }
        }

        return faultInfo;
    }

    /**
     * @see BuFaultInfoQualityService#listHandleRecordById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuFaultHandleRecord> listHandleRecordById(String id) throws Exception {
        return buFaultInfoQualityMapper.selectHandleRecordListByFaultId(id);
    }

}
