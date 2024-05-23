package org.jeecg.modules.produce.summary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.summary.bean.outsource.BuOutsourceOutinDetail;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 委外出入库明细 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-10-14
 */
@Mapper
@Repository(value = "summaryOutsourceOutinDetail")
public interface BuOutsourceOutinDetailMapper extends BaseMapper<BuOutsourceOutinDetail> {

    /**
     * 通过列计划id找委外交接部件详情
     * @param planId  列计划id
     * @param type  0 出库单，1：入库单
     * @return 部件详情
     */
    List<BuOutsourceOutinDetail> selectOutInDetailListByPlanId(@Param("planId") String planId,@Param("type")Integer type);
    /**
     * 通过线路id,修程Id,截止时间找委外交接部件详情
     * @param lineId  线路id
     * @param repairProgramId 修程Id
     * @param actFinish  截止时间
     * @return 部件详情
     */
    List<BuOutsourceOutinDetail> selectProblemPart(@Param("lineId")String lineId, @Param("repairProgramId")String repairProgramId,@Param("actFinish")Date actFinish);

    /**
     *
     * @param lineId   线路id
     * @param repairProgramId 修程Id
     * @param actFinish  截止时间
     * @return 委外项目数量
     */
    int selectOutInCount(@Param("lineId")String lineId, @Param("repairProgramId")String repairProgramId, @Param("actFinish")Date actFinish);
}
