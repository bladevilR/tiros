package org.jeecg.modules.basemanage.org.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.org.bean.BuTraining;
import org.jeecg.modules.basemanage.org.bean.vo.BuTrainingQueryVO;

/**
 * <p>
 * 培训记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-19
 */
public interface BuTrainingMapper extends BaseMapper<BuTraining> {

    /**
     * 根据条件分页查询培训记录
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuTraining> selectPageByCondition(Page<BuTraining> page, @Param("queryVO") BuTrainingQueryVO queryVO);

}
