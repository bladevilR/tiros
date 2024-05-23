package org.jeecg.modules.material.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.tiros.cache.warehouse.CacheWarehouseBO;
import org.jeecg.modules.material.warehouse.bean.BuMtrWarehouse;
import org.jeecg.modules.material.warehouse.bean.vo.BuMtrWarehouseQueryVO;

import java.util.List;

/**
 * <p>
 * 仓库信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-07
 */
public interface BuMtrWarehouseMapper extends BaseMapper<BuMtrWarehouse> {

    /**
     * 批量插入
     *
     * @param list 仓库信息列表
     */
    void insertList(@Param("list") List<BuMtrWarehouse> list);

    /**
     * 根据条件分页查询仓库信息
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuMtrWarehouse> selectPageByCondition(IPage<BuMtrWarehouse> page, @Param("queryVO") BuMtrWarehouseQueryVO queryVO);

    /**
     * 根据id获取仓库信息
     *
     * @param id 仓库id
     * @return 仓库信息
     */
    BuMtrWarehouse selectWarehouseById(@Param("id") String id);

    /**
     * 根据上级id获取下级列表
     *
     * @param parentId   上级id
     * @param workshopId 车间id
     * @return 下级仓库列表
     */
    List<BuMtrWarehouse> selectListByParentId(@Param("parentId") String parentId, @Param("workshopId") String workshopId);

    /**
     * Select all trees list.
     *
     * @return the list
     */
    List<BuMtrWarehouse> selectAllTrees();

    /**
     * 查询所有有效的仓库
     *
     * @param workshopId 车间id
     * @return 有效的仓库列表
     */
    List<BuMtrWarehouse> selectValidList(@Param("workshopId") String workshopId);

    /**
     * 根据上级id获取所有下级(树形)
     *
     * @param parentId 上级id
     * @return 所有下级列表(树形)
     */
    List<BuMtrWarehouse> selectAllChildrenTreeByParentId(@Param("parentId") String parentId);

    /**
     * 根据上级id获取所有下级的id，包含自身
     *
     * @param parentId 上级id
     * @return 所有下级的id，包含自身
     */
    List<String> selectIdAndAllChildrenIdListByParentId(@Param("parentId") String parentId);

    /**
     * 根据仓库id列表获取仓库wbs列表
     *
     * @param idList 仓库id列表
     * @return 仓库wbs列表
     */
    List<String> selectWbsListByIdList(@Param("idList") List<String> idList);

    /**
     * 根据仓库wbs列表获取仓库及所有子仓库列表 通过wbs like查询
     *
     * @param wbsList 仓库wbs列表
     * @return 仓库及所有子仓库列表
     */
    List<BuMtrWarehouse> selectListAndAllChildrenListByWbsList(@Param("wbsList") List<String> wbsList);

    /**
     * 根据用户id获取用户所属车间：通过用户找到用户部门，部门为班组的，找到班组，获取到车间id
     * 此方法可能报错：当一个人员属于多个车间时
     *
     * @param userId 用户id
     * @return 班组id
     */
    String selectUserWorkshopIdByUserId(@Param("userId") String userId);

    /**
     * 查询所有仓库简单信息，用于缓存服务
     *
     * @return 所有仓库简单信息
     */
    List<CacheWarehouseBO> selectAllSimpleInfoListForCache();

}
