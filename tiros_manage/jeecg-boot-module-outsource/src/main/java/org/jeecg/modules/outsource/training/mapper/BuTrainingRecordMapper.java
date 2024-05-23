package org.jeecg.modules.outsource.training.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.outsource.bean.vo.BuContractPayQueryVO;
import org.jeecg.modules.outsource.training.bean.BuTrainingRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.outsource.training.bean.vo.BuTrainingRecordQueryVO;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2021 -05-02
 */
public interface BuTrainingRecordMapper extends BaseMapper<BuTrainingRecord> {

    /**
     * Select page by condition page.
     *
     * @param page    the page
     * @param queryVO the query vo
     * @return the page
     */
    IPage<BuTrainingRecord> selectPageByCondition(Page<BuTrainingRecord> page,@Param("queryVO") BuTrainingRecordQueryVO queryVO);

    IPage<BuTrainingRecord> selectUserPageByCondition(Page<BuTrainingRecord> page, @Param("queryVO")BuTrainingRecordQueryVO queryVO);
}
