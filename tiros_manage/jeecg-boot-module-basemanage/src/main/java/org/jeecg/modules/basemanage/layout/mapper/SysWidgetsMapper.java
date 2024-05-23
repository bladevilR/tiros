package org.jeecg.modules.basemanage.layout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.layout.bean.SysWidgets;
import org.jeecg.modules.basemanage.layout.bean.vo.BuSysWidgetsQueryVO;

/**
 * <p>
 * 桌面部件 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-27
 */
public interface SysWidgetsMapper extends BaseMapper<SysWidgets> {

    /**
     * 根据条件分页查询桌面部件
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<SysWidgets> selectPageByCondition(IPage<SysWidgets> page, @Param("queryVO") BuSysWidgetsQueryVO queryVO);

}
