package org.jeecg.modules.quality.rectify.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quality.rectify.bean.BuWorkRectify;
import org.jeecg.modules.quality.rectify.bean.vo.BuWorkRectifyQueryVO;

/**
 * <p>
 * 整改信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-15
 */
public interface BuWorkRectifyMapper extends BaseMapper<BuWorkRectify> {

    /**
     * 根据条件分页查询整改信息
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuWorkRectify> selectPageByCondition(IPage<BuWorkRectify> page, @Param("queryVO") BuWorkRectifyQueryVO queryVO);

    /**
     * 根据id查询整改详细信息
     *
     * @param id 整改id
     * @return 整改详细信息
     */
    BuWorkRectify selectRectifyById(@Param("id") String id);

}
