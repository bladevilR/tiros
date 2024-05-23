package org.jeecg.modules.dispatch.plan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYearDetail;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanYearAutoGenerateVO;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanYearDetailVOWithTaskGantt;

import java.util.List;

/**
 * <p>
 * 年计划明细 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
public interface BuRepairPlanYearDetailService extends IService<BuRepairPlanYearDetail> {

    /**
     * 根据年份查询年计划明细列表
     *
     * @param year   年份
     * @param status 年计划明细状态
     * @return 年计划明细列表
     * @throws Exception 异常
     */
    List<BuRepairPlanYearDetail> listByYearAndStatus(Integer year, Integer status) throws Exception;

    /**
     * 根据架修数、大修数据、选择的计划模版、首列时间，自动对年计划进行排程。 年计划自动生成时，自动跳过节假日
     *
     * @param generateVO 自动生成条件
     * @return 年计划明细列表
     * @throws Exception 异常
     */
    List<BuRepairPlanYearDetail> autoGenerate(BuRepairPlanYearAutoGenerateVO generateVO) throws Exception;

    /**
     * 保存年计划明细列表(甘特)
     *
     * @param ganttList 年计划明细列表(甘特图对象)
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveGanttList(List<BuRepairPlanYearDetailVOWithTaskGantt> ganttList) throws Exception;

}
