package org.jeecg.modules.basemanage.workrecord.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecord;
import org.jeecg.modules.basemanage.workrecord.bean.bo.BuWorkOrderRecordCheckBO;
import org.jeecg.modules.basemanage.workrecord.bean.vo.BuWorkRecordQueryVO;

import java.util.List;

/**
 * <p>
 * 作业记录表 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-10
 */
public interface BuWorkRecordMapper extends BaseMapper<BuWorkRecord> {

    /**
     * 根据条件分页查询作业记录表
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    Page<BuWorkRecord> selectWorkRecordPage(Page<BuWorkRecord> page, @Param("queryVO") BuWorkRecordQueryVO queryVO);

    /**
     * 根据id查询作业记录表(含明细分类和明细)
     *
     * @param id 作业记录表id
     * @return 作业记录表(含明细分类和明细)
     */
    BuWorkRecord selectWorkRecordById(String id);

    /**
     * 根据作业记录明细id列表查询作业记录检查信息
     *
     * @param workRecordDetailIdList 作业记录明细id列表
     * @return 作业记录检查信息
     */
    List<BuWorkOrderRecordCheckBO> selectCheckListByDetailIdList(@Param("workRecordDetailIdList") List<String> workRecordDetailIdList);

}
