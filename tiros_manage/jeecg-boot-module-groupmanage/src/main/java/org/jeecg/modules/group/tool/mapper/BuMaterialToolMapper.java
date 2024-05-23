package org.jeecg.modules.group.tool.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.group.tool.bean.BuMaterialTools;
import org.jeecg.modules.group.tool.bean.vo.BuToolUsageRecordVO;
import org.jeecg.modules.group.toolequipment.bean.vo.BuMaterialToolsQueryVO;

/**
 * <p>
 * 工器具 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-14
 */
public interface BuMaterialToolMapper extends BaseMapper<BuMaterialTools> {

    /**
     * 根据条件查询分页工器具信息
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuMaterialTools> selectPageByCondition(IPage<BuMaterialTools> page, @Param("queryVO") BuMaterialToolsQueryVO queryVO);

    /**
     * 根据id查询工器具信息
     *
     * @param id 工器具id
     * @return 工器具信息
     */
    BuMaterialTools getById(@Param("id") String id);

    /**
     * 分页查询工器具使用记录
     *
     * @param page    分页信息
     * @param id      工器具id
     * @param trainNo 车号
     * @return 分页结果
     */
    IPage<BuToolUsageRecordVO> selectUsageRecordList(IPage<BuToolUsageRecordVO> page, @Param("id") String id, @Param("trainNo") String trainNo);

}
