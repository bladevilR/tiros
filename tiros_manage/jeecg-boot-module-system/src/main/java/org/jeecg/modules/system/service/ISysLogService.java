package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.modules.system.entity.SysLog;
import org.jeecg.modules.system.vo.LoginStatisticVO;
import org.jeecg.modules.system.vo.query.LoginStatisticQueryVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统日志表 服务类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
public interface ISysLogService extends IService<SysLog> {

    /**
     * @功能：清空所有日志记录
     */
    public void removeAll();

    /**
     * 获取系统总访问次数
     *
     * @return Long
     */
    Long findTotalVisitCount();

    //update-begin--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数

    /**
     * 获取系统今日访问次数
     *
     * @return Long
     */
    Long findTodayVisitCount(Date dayStart, Date dayEnd);

    /**
     * 获取系统今日访问 IP数
     *
     * @return Long
     */
    Long findTodayIp(Date dayStart, Date dayEnd);
    //update-end--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数

    /**
     * 首页：根据时间统计访问数量/ip数量
     *
     * @param dayStart
     * @param dayEnd
     * @return
     */
    List<Map<String, Object>> findVisitCount(Date dayStart, Date dayEnd);

    /**
     * 查询用户系统访问统计
     *
     * @param queryVO 查询条件
     * @return 统计数据
     * @throws Exception 异常
     */
    List<LoginStatisticVO> listLoginStatistic(LoginStatisticQueryVO queryVO) throws Exception;

    /**
     * excel方式导出系统访问统计数据
     *
     * @param queryVO 查询条件
     * @return excel数据
     * @throws Exception 异常
     */
    HSSFWorkbook exportLoginStatisticExcel(LoginStatisticQueryVO queryVO) throws Exception;

}
