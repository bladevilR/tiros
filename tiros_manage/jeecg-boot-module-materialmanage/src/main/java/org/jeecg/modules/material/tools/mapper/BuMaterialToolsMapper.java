package org.jeecg.modules.material.tools.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.tools.bean.BuMaterialTools;
import org.jeecg.modules.material.tools.bean.vo.BuMaterialToolsQueryVO;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 工具信息，包括工器具、工装等信息，一种物资类型可能存在多条记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
public interface BuMaterialToolsMapper extends BaseMapper<BuMaterialTools> {

    /**
     * 批量插入
     *
     * @param list  工器具类型
     */
    void insertBatch(@Param("list") List<BuMaterialTools> list);

    /**
     * 根据条件查询分页
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuMaterialTools> selectPageByCondition(IPage<BuMaterialTools> page, @Param("queryVO") BuMaterialToolsQueryVO queryVO);

    /**
     * 根据id查询工具详情
     *
     * @param id 工具id
     * @return 工具详情
     */
    BuMaterialTools selectToolById(String id);

    /**
     * 根据工班id和工器具物资类型id查询工器具集合，过滤报废的
     *
     * @param toolTypeId  工器具类型id（物资类型id）
     * @param workGroupId 工班id
     * @return 工器具集合
     */
    List<BuMaterialTools> selectToolListByTypeIdAndGroupId(@Param("toolTypeId") String toolTypeId, @Param("workGroupId") String workGroupId);

    List<Map<String, Object>> selectToolUserGroupList();

    void updateListForGroup(@Param("list") List<BuMaterialTools> list);

    void updateListForWorkshop(@Param("list") List<BuMaterialTools> list);

    /**
     * 根据工器具物资类型id查询工器具集合，过滤报废的
     *
     * @param toolTypeIdList 工器具类型id（物资类型id）
     * @return 工器具集合
     */
    List<BuMaterialTools> selectUseableToolListByTypeIdList(@Param("toolTypeIdList") List<String> toolTypeIdList);

    List<BuMaterialTools> selectListByAssetCode(@Param("list") Set<String> assetCodeList);

}
