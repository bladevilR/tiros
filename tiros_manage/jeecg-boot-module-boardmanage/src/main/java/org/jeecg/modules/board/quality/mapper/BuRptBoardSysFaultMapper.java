package org.jeecg.modules.board.quality.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.quality.bean.BuRptBoardSysFault;
import org.jeecg.modules.board.quality.bean.BuRptBoardTrainFault;
import org.jeecg.modules.board.quality.bean.vo.BuBoardQualityQueryVO;

import java.util.List;

/**
 * <p>
 * 故障统计-中间表-系统维度 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-12
 */
public interface BuRptBoardSysFaultMapper extends BaseMapper<BuRptBoardSysFault> {

    /**
     * 批量插入
     *
     * @param list 列表
     */
    void insertList(@Param("list") List<BuRptBoardSysFault> list);

    /**
     * 根据条件查询故障统计列表
     *
     * @param queryVO 查询条件
     * @param year    年份
     * @return 故障统计列表
     */
    List<BuRptBoardSysFault> listByCondition(@Param("queryVO") BuBoardQualityQueryVO queryVO, @Param("year") Integer year);

}
