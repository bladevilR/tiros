package org.jeecg.modules.basemanage.traininfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAsset;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetListQueryVOForApp;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetQueryVO;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetTreeQueryVO;

import java.util.List;

/**
 * <p>
 * 车辆设备，按车辆分区存放，如果后期设备太多，可以线路分表，设备更换时，如果这个设备对应的是周转件，从车上换下的设备需要在 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-15
 */
public interface BuTrainAssetMapper extends BaseMapper<BuTrainAsset> {

    /**
     * 批量插入
     *
     * @param list 车辆设备列表
     */
    void insertList(@Param("list") List<BuTrainAsset> list);

    /**
     * 查找子节点
     *
     * @param queryVO
     * @return
     */
    List<BuTrainAsset> selectListByBuTrainAssetQueryVO(@Param("queryVO") BuTrainAssetQueryVO queryVO);

    /**
     * 根据条件查询车辆设备树形结构-app使用
     *
     * @param queryVO 查询条件
     * @return 车辆设备树形结构
     */
    List<BuTrainAsset> selectTreeListByListQueryVOForApp(@Param("queryVO") BuTrainAssetListQueryVOForApp queryVO);

    /**
     * 根据条件查询车辆设备
     *
     * @param queryVO 查询条件
     * @return 车辆设备
     */
    List<BuTrainAsset> selectListByTreeQueryVO(@Param("queryVO") BuTrainAssetTreeQueryVO queryVO);

    /**
     * 根据条件查询车辆设备-app使用
     *
     * @param queryVO 查询条件
     * @return 车辆设备
     */
    List<BuTrainAsset> selectListByListQueryVOForApp(@Param("queryVO") BuTrainAssetListQueryVOForApp queryVO);

    /**
     * 根据车辆id查询设备编码列表
     *
     * @param trainId 车辆id
     * @return 设备编码列表
     */
    List<String> selectAssetNoListByTrainId(@Param("trainId") String trainId);

    /**
     * 根据车辆设备wbs列表获取所有子设备列表 通过wbs like查询
     *
     * @param wbsList 车辆设备wbs列表
     * @return 所有子设备列表
     */
    List<BuTrainAsset> selectAllChildrenListByWbsList(@Param("wbsList") List<String> wbsList);

    /**
     * 根据车辆设备上级id列表获取下级设备列表
     *
     * @param parentIdList 上级id列表
     * @return 下级设备列表
     */
    List<BuTrainAsset> selectListByParentIdList(@Param("parentIdList") List<String> parentIdList);

    /**
     * 根据车辆id和车辆结构明细id查询车辆设备
     *
     * @param trainId        车辆id
     * @param structDetailId 车辆结构明细id
     * @return 车辆设备
     */
    List<BuTrainAsset> selectListByTrainIdAndStructDetailId(@Param("trainId") String trainId, @Param("structDetailId") String structDetailId);

    /**
     * 根据车辆id查询顶级车辆设备
     *
     * @param trainId 车辆id
     * @return 顶级车辆设备
     */
    List<BuTrainAsset> selectTopListByTrainId(@Param("trainId") String trainId);

    /**
     * 根据上级id查询兄弟节点的最大code
     *
     * @param parentId 上级id
     * @return 兄弟节点的最大code
     */
    String selectBrotherMaxCodeByParentId(@Param("parentId") String parentId);

}
