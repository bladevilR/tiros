package org.jeecg.modules.quality.recordconfirm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quality.recordconfirm.bean.BuPlanFormCheckRecord;
import org.jeecg.modules.quality.recordconfirm.bean.BuPlanFormCheckRecordJudge;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormCheckRecordVO;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormRecordQueryVO;

import java.util.List;


/**
 * <p>
 * 作业检查记录表（实例） Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
public interface BuPlanFormCheckRecordQualityMapper extends BaseMapper<BuPlanFormCheckRecord> {

    /**
     * 根据条件分页查询表单记录
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuFormCheckRecordVO> selectFormCheckRecordVOPageByFormRecordQueryVO(IPage<BuFormCheckRecordVO> page, @Param("queryVO") BuFormRecordQueryVO queryVO);

    /**
     * 根据检查表实例id查询质量评定列表
     *
     * @param checkRecordIdList 检查表实例id列表
     * @return 质量评定列表
     */
    List<BuPlanFormCheckRecordJudge> selectJudgeListByCheckRecordIdList(@Param("checkRecordIdList") List<String> checkRecordIdList);

    /**
     * 根据作业检查记录表（实例）id查询作业检查记录表（实例）信息及检查项
     *
     * @param checkRecordId 作业检查记录表（实例）id
     * @return 作业检查记录表（实例）信息及检查项
     */
    BuPlanFormCheckRecord selectWorkRecordWithItemById(@Param("checkRecordId") String checkRecordId);

    /**
     * 根据作业检查记录表（实例）id查询作业检查记录表（实例）信息及检查项及关联整改
     *
     * @param checkRecordId 作业检查记录表（实例）id
     * @return 作业检查记录表（实例）信息及检查项及关联整改
     */
    BuPlanFormCheckRecord selectCheckRecordWithItemRectifyById(@Param("checkRecordId") String checkRecordId);

}
