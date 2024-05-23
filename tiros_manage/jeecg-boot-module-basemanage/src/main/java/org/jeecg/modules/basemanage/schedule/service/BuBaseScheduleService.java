package org.jeecg.modules.basemanage.schedule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.schedule.bean.BuBaseSchedule;
import org.jeecg.modules.basemanage.schedule.bean.vo.BuBaseScheduleCheckVO;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 日程信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-22
 */
public interface BuBaseScheduleService extends IService<BuBaseSchedule> {

    /**
     * 查询指定月份每日是否有日程安排
     *
     * @param year  年份
     * @param month 月份
     * @return 是否有日程安排结果
     * @throws Exception 异常信息
     */
    List<BuBaseScheduleCheckVO> listScheduleCheck(Integer year, Integer month) throws Exception;

    /**
     * 查询指定日期日程明细(列表)
     *
     * @param date 指定日期
     * @return 日程明细列表
     * @throws Exception 异常信息
     */
    List<BuBaseSchedule> listSchedule(Date date) throws Exception;

}
