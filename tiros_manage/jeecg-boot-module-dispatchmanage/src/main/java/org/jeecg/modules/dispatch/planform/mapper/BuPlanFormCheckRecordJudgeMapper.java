package org.jeecg.modules.dispatch.planform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.planform.bean.BuPlanFormCheckRecordJudge;
import org.jeecg.modules.dispatch.planform.bean.vo.CheckRecordJudgeQueryVO;

import java.util.List;

/**
 * <p>
 * 质量评定 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
public interface BuPlanFormCheckRecordJudgeMapper extends BaseMapper<BuPlanFormCheckRecordJudge> {

    /**
     * 根据条件查询质量评定
     *
     * @param queryVO 查询条件
     * @return 质量评定列表
     */
    List<BuPlanFormCheckRecordJudge> selectListByQueryVO(@Param("queryVO") CheckRecordJudgeQueryVO queryVO);

    /**
     * 根据检查表单id查询质量评定
     *
     * @param checkInstId 查询条件
     * @return 质量评定列表
     */
    List<BuPlanFormCheckRecordJudge> selectListByCheckInstId(@Param("checkInstId") String checkInstId);

    /**
     * 根据id查询质量评定
     *
     * @param id 质量评定id
     * @return 质量评定
     */
    BuPlanFormCheckRecordJudge selectJudgeById(@Param("id") String id);

}
