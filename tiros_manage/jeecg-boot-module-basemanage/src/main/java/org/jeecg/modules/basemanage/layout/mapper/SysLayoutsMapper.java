package org.jeecg.modules.basemanage.layout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.layout.bean.SysLayouts;
import org.jeecg.modules.basemanage.layout.bean.vo.BuSysLayoutsQueryVO;

/**
 * <p>
 * 界面布局 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-27
 */
public interface SysLayoutsMapper extends BaseMapper<SysLayouts> {

    /**
     * 根据条件分页查询界面布局
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<SysLayouts> selectPageByCondition(IPage<SysLayouts> page, @Param("queryVO") BuSysLayoutsQueryVO queryVO);

    /**
     * 根据id查询界面布局
     *
     * @param id 界面布局id
     * @return 界面布局，包括布局下组件列表
     */
    SysLayouts selectLayoutsById(@Param("id") String id);

    /**
     * 根据用户id查询查询界面布局id
     *
     * @param userId 用户id
     * @return 界面布局id
     */
    String selectDefaultLayoutsIdByUserId(@Param("userId") String userId);

}
