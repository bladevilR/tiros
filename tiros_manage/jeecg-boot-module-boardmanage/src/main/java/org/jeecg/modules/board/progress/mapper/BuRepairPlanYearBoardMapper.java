package org.jeecg.modules.board.progress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.progress.bean.BuRepairPlanYear;
import org.jeecg.modules.board.progress.bean.BuRepairPlanYearDetail;
import org.jeecg.modules.board.progress.bean.vo.BuBoardProgressQueryVO;

import java.util.List;

/**
 * <p>
 * 年计划 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-02
 */
public interface BuRepairPlanYearBoardMapper extends BaseMapper<BuRepairPlanYear> {

    /**
     * 根据条件查询年计划
     *
     * @param year    年份
     * @param queryVO 查询条件
     * @return 年计划及明细信息
     */
    List<BuRepairPlanYearDetail> selectRepairPlanYearDetailListByYearCondition(@Param("year") Integer year, @Param("queryVO") BuBoardProgressQueryVO queryVO);

}
