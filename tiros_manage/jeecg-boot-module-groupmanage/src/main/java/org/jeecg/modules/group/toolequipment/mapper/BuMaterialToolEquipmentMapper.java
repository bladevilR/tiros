package org.jeecg.modules.group.toolequipment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.group.tool.bean.BuMaterialTools;
import org.jeecg.modules.group.tool.bean.vo.BuToolUsageRecordVO;
import org.jeecg.modules.group.toolequipment.bean.vo.BuMaterialToolsQueryVO;

/**
 * <p>
 * 工装信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-14
 */
public interface BuMaterialToolEquipmentMapper extends BaseMapper<BuMaterialTools> {

    /**
     * 根据条件查询分页工装信息
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuMaterialTools> selectPageByCondition(IPage<BuMaterialTools> page, @Param("queryVO") BuMaterialToolsQueryVO queryVO);

    /**
     * 根据id查询工装信息
     * @param id 工装id
     * @return 工装信息
     */
    BuMaterialTools getById(String id);

    /**
     * 分页查询工装使用记录
     *
     * @param page    分页信息
     * @param id      工装id
     * @param trainNo 车号
     * @return 分页结果
     */
    IPage<BuToolUsageRecordVO> selectUsageRecordList(IPage<BuToolUsageRecordVO> page, @Param("id") String id, @Param("trainNo") String trainNo);

}
