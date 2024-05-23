package org.jeecg.modules.material.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.bean.BuMaterialTypeTool;
import org.jeecg.modules.material.material.bean.vo.BuMaterialTypeQueryVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 物资类型 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020 -08-07
 */
public interface BuMaterialTypeMapper extends BaseMapper<BuMaterialType> {

    /**
     * 批量插入
     *
     * @param list 物资类型列表
     */
    void insertList(@Param("list") List<BuMaterialType> list);

    /**
     * 批量更新物资类型价格
     *
     * @param list 物资类型列表
     */
    void updatePriceBatch(@Param("list") List<BuMaterialType> list);

    /**
     * 批量更新物资类型id等于code
     *
     * @param list 物资类型列表
     */
    void updateIdEqualsCodeBatch(@Param("list") List<BuMaterialType> list);

    /**
     * 批量更新物资类型重复的业务数据表数据
     *
     * @param oldMaterialTypeIdsNewMaterialTypeIdMapList 重复的物资类型ids和新的物资类型id
     */
    void updateOldNewMaterialTypeIdBusinessTableData(@Param("oldIdsNewIdMapList") List<Map<String, Object>> oldMaterialTypeIdsNewMaterialTypeIdMapList);

    /**
     * 批量更新物资类型价格到业务数据表数据
     *
     * @param idPriceMapList 物资类型id和价格
     */
    void updateMaterialTypePriceBusinessTableData(@Param("idPriceMapList") List<Map<String, Object>> idPriceMapList);

    /**
     * 根据条件分页查询物资类型
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 物质类型分页结果
     */
    Page<BuMaterialType> selectMaterialTypePage(Page<BuMaterialType> page, @Param("queryVO") BuMaterialTypeQueryVO queryVO);

    /**
     * 根据条件分页查询工器具类型
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 工器具类型分页结果
     */
    Page<BuMaterialTypeTool> selectMaterialTypeToolPage(Page<BuMaterialType> page, @Param("queryVO") BuMaterialTypeQueryVO queryVO);

    /**
     * 根据条件分页查询必换件
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 必换件物质类型分页结果
     */
    Page<BuMaterialType> selectMustMaterialTypePage(Page<BuMaterialType> page, @Param("queryVO") BuMaterialTypeQueryVO queryVO);

    /**
     * 根据id查询物质类型
     *
     * @param id 物质类型id
     * @return 物质类型信息
     */
    BuMaterialType selectMaterialTypeById(@Param("id") String id);

    /**
     * Select sys id by part id string.
     *
     * @param id the id
     * @return the string
     */
    String selectSysIdByPartId(String id);

    List<BuMaterialType> selectAll();

    /**
     * Update batch.
     *
     * @param list the list
     */
    void updateBatch(@Param("list") List<BuMaterialType> list);

    /**
     * Insert batch.
     *
     * @param list the list
     */
    void insertBatch(@Param("list") List<BuMaterialType> list);

    void saveTypeList(@Param("list") List<BuMaterialType> typeList);

    List<BuMaterialType> selectListByCode(@Param("list") Set<String> codeList);

    List<BuMaterialType> selectRepeatMaterialTypeList();

    List<BuMaterialType> selectCodeIdNotEqualsMaterialTypeList();

}
