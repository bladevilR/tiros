package org.jeecg.modules.basemanage.schedule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.basemanage.schedule.bean.BuBaseSchedule;
import org.jeecg.modules.basemanage.schedule.bean.vo.BuBaseScheduleCheckVO;
import org.jeecg.modules.basemanage.schedule.mapper.BuBaseScheduleMapper;
import org.jeecg.modules.basemanage.schedule.service.BuBaseScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 日程信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-22
 */
@Service
public class BuBaseScheduleServiceImpl extends ServiceImpl<BuBaseScheduleMapper, BuBaseSchedule> implements BuBaseScheduleService {

    @Resource
    private BuBaseScheduleMapper buBaseScheduleMapper;


    /**
     * @see BuBaseScheduleService#listScheduleCheck(Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuBaseScheduleCheckVO> listScheduleCheck(Integer year, Integer month) throws Exception {
        Calendar calendar = Calendar.getInstance();
        if (null != year) {
            calendar.set(Calendar.YEAR, year);
        }
        if (null != month) {
            calendar.set(Calendar.MONTH, month - 1);
        }

        Date endTime = DateUtils.transToDayEnd(DateUtils.transToMonthEndDay(calendar)).getTime();
        Date startTime = DateUtils.transToDayStart(DateUtils.transToMonthStartDay(calendar)).getTime();

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // 查询日程信息
        List<BuBaseSchedule> scheduleList = buBaseScheduleMapper.selectListByUserIdAndTime(sysUser.getId(), startTime, endTime);

        return transformResult(scheduleList, startTime, endTime);
    }

    /**
     * @see BuBaseScheduleService#listSchedule(Date)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuBaseSchedule> listSchedule(Date date) throws Exception {
        if (null == date) {
            date = new Date();
        }

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        return buBaseScheduleMapper.selectListByUserIdAndDate(sysUser.getId(), date);
    }


    private List<BuBaseScheduleCheckVO> transformResult(List<BuBaseSchedule> scheduleList, Date startTime, Date endTime) {
        if (null == scheduleList) {
            scheduleList = new ArrayList<>();
        }

        Calendar start = Calendar.getInstance();
        start.setTime(startTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        List<BuBaseScheduleCheckVO> checkVOList = new ArrayList<>();
        while (start.before(end)) {
            Date date = start.getTime();
            long count = scheduleList.stream()
                    .filter(schedule -> schedule.getStartTime().compareTo(date) >= 0 && schedule.getEndTime().compareTo(date) <= 0)
                    .count();

            BuBaseScheduleCheckVO checkVO = new BuBaseScheduleCheckVO()
                    .setDate(date)
                    .setHasSchedule(count > 0)
                    .setNumber(new Long(count).intValue());
            checkVOList.add(checkVO);

            start.add(Calendar.DATE, 1);
        }

        return checkVOList;
    }

}
