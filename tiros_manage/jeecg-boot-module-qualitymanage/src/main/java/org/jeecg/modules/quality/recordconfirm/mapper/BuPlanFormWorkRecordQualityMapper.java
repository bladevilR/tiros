package org.jeecg.modules.quality.recordconfirm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quality.recordconfirm.bean.BuPlanFormWorkRecord;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormRecordQueryVO;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormInstanceVO;

/**
 * <p>
 * 作业记录表 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-11
 */
public interface BuPlanFormWorkRecordQualityMapper extends BaseMapper<BuPlanFormWorkRecord> {

    /**
     * 根据条件分页查询表单记录
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuFormInstanceVO> selectFormWorkAndDataRecordVOPageByFormRecordQueryVO(IPage<BuFormInstanceVO> page, @Param("queryVO") BuFormRecordQueryVO queryVO);

    /**
     * 根据作业记录实列id查询作业记录、分类、明细、检查、计量器具
     *
     * @param recordId 作业记录实列id
     * @return 作业记录、分类、明细、检查、计量器具
     */
    BuPlanFormWorkRecord selectFormWorkRecordById(@Param("recordId") String recordId);

}
