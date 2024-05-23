package org.jeecg.modules.board.quality.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.board.quality.bean.BuFaultInfo;
import org.jeecg.modules.board.quality.bean.vo.BuBoardQualityQueryVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 故障信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-17
 */
public interface BuFaultInfoBoardMapper extends BaseMapper<BuFaultInfo> {

    /**
     * 根据条件查询故障信息
     *
     * @param queryVO 查询条件
     * @return 故障信息列表
     */
    List<BuFaultInfo> selectListByCondition(@Param("queryVO") BuBoardQualityQueryVO queryVO);

    /**
     * 根据条件查询故障信息
     *
     * @param queryVO 查询条件
     * @return 故障信息列表
     */
    List<BuFaultInfo> selectOutsourceListByCondition(@Param("queryVO") BuBoardQualityQueryVO queryVO);

    /**
     * 获取最新故障信息
     *
     * @param num 故障条数
     * @return 最新故障信息结果
     */
    List<BuFaultInfo> listLatestFaultByNum(Integer num);

    /**
     * 根据id查询故障详细信息
     *
     * @param id 故障id
     * @return 故障详细信息
     */
    BuFaultInfo selectById(@Param("id") String id);

}
