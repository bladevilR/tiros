package org.jeecg.modules.basemanage.traininfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.tiros.cache.assettype.BuTrainAssetTypeBO;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAssetType;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetTypeQueryVO;

import java.util.List;

/**
 * <p>
 * 设备类型结构 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-15
 */
public interface BuTrainAssetTypeMapper extends BaseMapper<BuTrainAssetType> {

    /**
     * 批量插入
     *
     * @param list 设备类型列表
     */
    void insertList(@Param("list") List<BuTrainAssetType> list);

    /**
     * 批量更新编码和wbs
     *
     * @param list 设备类型列表
     */
    void updateListCodeAndWbs(@Param("list") List<BuTrainAssetType> list);

    /**
     * 根据条件查询设备类型子节点
     *
     * @param queryVO 查询条件
     * @return 设备类型列表
     */
    List<BuTrainAssetType> selectListByBuTrainAssetTypeQueryVO(@Param("queryVO") BuTrainAssetTypeQueryVO queryVO);

    /**
     * 根据id查询设备类型信息
     *
     * @param id 设备类型id
     * @return 设备类型信息
     */
    BuTrainAssetType selectAssetTypeById(@Param("id") String id);

    /**
     * 根据上级id查询兄弟节点的最大code
     *
     * @param parentId 上级id
     * @return 兄弟节点的最大code
     */
    String selectBrotherMaxCodeByParentIdAndTrainTypeId(@Param("parentId") String parentId, @Param("trainTypeId") String trainTypeId);

    /**
     * 根据设备类型id列表获取设备类型wbs列表
     *
     * @param assetTypeIdList 设备类型id列表
     * @return 设备类型wbs列表
     */
    List<String> selectWbsListByIdList(@Param("assetTypeIdList") List<String> assetTypeIdList);

    /**
     * 根据设备类型wbs列表获取设备类型及其所有子节点，做为列表返回
     *
     * @param assetTypeWbsList 设备类型wbs列表
     * @return 设备类型及所有其子节点，做为列表返回
     */
    List<BuTrainAssetType> selectAssetTypeAndChildrenAsListByWbsList(@Param("assetTypeWbsList") List<String> assetTypeWbsList);

    /**
     * 根据设备类型wbs列表删除设备类型及其所有子节点
     *
     * @param assetTypeWbsList 设备类型wbs列表
     */
    void deleteAssetTypeAndChildrenByWbsList(@Param("assetTypeWbsList") List<String> assetTypeWbsList);

    /**
     * 根据车号获取车辆系统（设备类型中属于系统的第一级）
     *
     * @param trainNo 车号
     * @return 设备类型中属于系统的第一级 列表
     */
    List<BuTrainAssetType> selectTopSystemListByTrainNo(@Param("trainNo") String trainNo, @Param("parentId") String parentId);

    /**
     * 查询所有设备类型简单信息，用于缓存服务
     *
     * @return 所有设备类型简单信息
     */
    List<BuTrainAssetTypeBO> selectAllSimpleInfoListForCache();

}
