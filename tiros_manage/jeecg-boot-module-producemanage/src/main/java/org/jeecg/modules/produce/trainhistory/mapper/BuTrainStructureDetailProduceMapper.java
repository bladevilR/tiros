package org.jeecg.modules.produce.trainhistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.plan.bean.bo.BuTrainStructureDetailBO;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainStructureDetail;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainStructureDetailTreeVO;

import java.util.List;

/**
 * <p>
 * 车辆结构明细 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-28
 */
public interface BuTrainStructureDetailProduceMapper extends BaseMapper<BuTrainStructureDetail> {

    /**
     * 根据车辆结构id查询车辆结构明细
     *
     * @param structId 车辆结构id
     * @return 所有结构明细列表
     */
    List<BuTrainStructureDetailTreeVO> selectListForTreeByStructId(@Param("structId") String structId);

    /**
     * 根据车辆结构id查询车辆结构明细
     *
     * @param structId 车辆结构id
     * @return 所有结构明细列表
     */
    List<BuTrainStructureDetailBO> selectSimpleListByStructId(@Param("structId") String structId);

    /**
     * 根据车辆结构明细id获取结构明细信息
     *
     * @param id 车辆结构明细id
     * @return 结构明细信息
     */
    BuTrainStructureDetail selectStructureDetailById(@Param("id") String id);

    /**
     * 根据车辆结构明细wbs求所有子节点列表
     *
     * @param wbs 车辆结构明细wbs
     * @return 所有子节点列表
     */
    List<String> selectChildIdListByWbs(String wbs);

}
