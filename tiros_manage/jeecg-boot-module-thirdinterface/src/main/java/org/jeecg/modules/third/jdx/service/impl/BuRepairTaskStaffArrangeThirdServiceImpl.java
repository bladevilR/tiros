package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.bean.BuRepairTaskStaffArrange;
import org.jeecg.modules.third.jdx.mapper.BuRepairTaskStaffArrangeThirdMapper;
import org.jeecg.modules.third.jdx.service.BuRepairTaskStaffArrangeThirdService;
import org.jeecg.modules.third.maximo.bean.JdxLabtransIn;
import org.jeecg.modules.third.utils.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 任务人员安排 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-07-11
 */
@Service
public class BuRepairTaskStaffArrangeThirdServiceImpl extends ServiceImpl<BuRepairTaskStaffArrangeThirdMapper, BuRepairTaskStaffArrange> implements BuRepairTaskStaffArrangeThirdService {

    @Resource
    private BuRepairTaskStaffArrangeThirdMapper buRepairTaskStaffArrangeThirdMapper;


    /**
     * @see BuRepairTaskStaffArrangeThirdService#getMaximoWorkTimeNeedWriteByStaffArrange(BuRepairTaskStaffArrange)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public JdxLabtransIn getMaximoWorkTimeNeedWriteByStaffArrange(BuRepairTaskStaffArrange staffArrange) throws Exception {
        JdxLabtransIn maximoWorkTime = transformToJdxLabtransIn(staffArrange);

        // 如果工时时长小于等于0.01，先不传，等下次再传
        boolean endTimeValid = checkWorkEndTimeValid(maximoWorkTime);
        if (endTimeValid) {
            return maximoWorkTime;
        } else {
//            log.error("人员工时结束时间不能是未来时间，此次同步不进行写入人员工时，等待下次再同步，人员工时信息：" + JSON.toJSONString(staffArrange));
            return null;
        }
    }

    private JdxLabtransIn transformToJdxLabtransIn(BuRepairTaskStaffArrange staffArrange) {
        // 处理时间
        Date startTime = staffArrange.getTaskStart();
        Date startDate = DateUtils.transToDayStart(startTime);
        BigDecimal workTime = staffArrange.getWorkTime();
        // 最小0.01
        BigDecimal minHours = BigDecimal.valueOf(0.01D);
        if (workTime.compareTo(minHours) < 0) {
            workTime = minHours;
        }

        JdxLabtransIn labtransIn = new JdxLabtransIn()
                .setContractnum(null)
                .setCraft("500")
                .setExternalrefid(staffArrange.getId())
                .setFcprojectid(staffArrange.getFdProjectCode())
                .setFctaskid(staffArrange.getFdTaskCode())
                .setLaborcode(staffArrange.getWorkNo())
                .setLabtransid(null)
                .setOrgid(null)
                .setRefwo(staffArrange.getOrderCode())
                .setRegularhrs(workTime.floatValue())
                .setSiteid(StringUtils.isBlank(staffArrange.getLineId()) ? null : MaximoThirdConstant.SITE_PREFIX + staffArrange.getLineId())
                .setSkilllevel(null)
                .setSourcesysid("JDX")
                .setStartdate(startDate)
                .setStarttime(startTime)
                .setTransdate(new Date())
                .setTranstype("WORK")
                .setVendor(null)
                .setTransid(null)
                .setTransseq(1L);

        return labtransIn;
    }

    private boolean checkWorkEndTimeValid(JdxLabtransIn maximoWorkTime) {
        Date starttime = maximoWorkTime.getStarttime();
        Float regularhrs = maximoWorkTime.getRegularhrs();

        Date endTime = new Date(starttime.getTime() + (long) (3600000 * regularhrs));
        return endTime.before(new Date());
    }

}
