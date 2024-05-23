package org.jeecg.modules.dispatch.exchange.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchang;
import org.jeecg.modules.dispatch.exchange.bean.vo.BuRepairExchangeQueryVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 交接车记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020 -09-24
 */
public interface BuRepairExchangMapper extends BaseMapper<BuRepairExchang> {
    /**
     * 根据条件分页查询交接车记录
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuRepairExchang> selectPageByCondition(IPage<BuRepairExchang> page, @Param("queryVO") BuRepairExchangeQueryVO queryVO);

    /**
     * 根据id查询交接车记录
     *
     * @param id 交接车记录id
     * @return 交接车记录
     */
    BuRepairExchang selectRepairExchangeById(@Param("id") String id);

    /**
     * 根据trainNo查询交接车记录
     *
     * @param trainNo 车号
     * @return 交接车记录
     */
    BuRepairExchang selectRepairedReceiveExchangeByTrainNo(@Param("trainNo") String trainNo);

    /**
     * 查询可交车的接车记录
     *
     * @return 已维修且未关联交车记录的接车记录
     */
    List<BuRepairExchang> selectReceiveListDeliverable();

    /**
     * 根据年份，查询对应接车的计划完成时间在年份内，已审批的交车记录
     *
     * @param year 接车的年份
     * @return 交车记录
     */
    List<BuRepairExchang> selectApprovedDeliverListByYear(@Param("year") Integer year);

    /**
     * Gets user depart list.
     *
     * @param userId the user id
     * @return the user depart list
     */
    List<Map<String, Object>> getUserDepartList(@Param("userId") String userId);

    /**
     * Gets train number.
     *
     * @return the train number
     */
    Integer getTrainNumber();

    /**
     * Gets item no.
     *
     * @param programId the program id
     * @return the item no
     */
    Integer getItemNo(@Param("programId") String programId);

    /**
     * Select list train index list.
     *
     * @param trainNo the train no
     * @return the list
     */
    List<Integer> selectListTrainIndex(@Param("trainNo") String trainNo);

}
