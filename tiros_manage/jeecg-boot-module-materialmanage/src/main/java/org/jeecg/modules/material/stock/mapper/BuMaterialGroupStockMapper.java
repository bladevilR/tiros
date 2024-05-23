package org.jeecg.modules.material.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.stock.bean.BuMaterialGroupStock;
import org.jeecg.modules.material.stock.bean.vo.BuMaterialGroupStockQueryVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 班组库存 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-03
 */
public interface BuMaterialGroupStockMapper extends BaseMapper<BuMaterialGroupStock> {

    /**
     * 批量插入
     *
     * @param list 班组库存列表
     */
    void insertList(@Param("list") List<BuMaterialGroupStock> list);

    /**
     * 批量更新库存量
     *
     * @param list 班组库存列表
     */
    void updateList(@Param("list") List<BuMaterialGroupStock> list);

    /**
     * 批量更新班组库存库存量
     *
     * @param list 班组库存列表
     */
    void updateListAmount(@Param("list") List<BuMaterialGroupStock> list);

    /**
     * 根据条件分页查询班组库存
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuMaterialGroupStock> selectPageByQueryVO(IPage<BuMaterialGroupStock> page, @Param("queryVO") BuMaterialGroupStockQueryVO queryVO);

    /**
     * 根据班组id和物资类型id查询班组库存列表
     *
     * @param groupId        班组id
     * @param materialTypeId 物资类型id
     * @return 班组库存列表
     */
    List<BuMaterialGroupStock> selectListByGroupAndMaterialType(@Param("groupId") String groupId, @Param("materialTypeId") String materialTypeId);

    /**
     * 根据班组id查询公司、车间、线路
     * @param groupId 班组id
     * @return 公司id、车间id、线路id
     */
    List<Map<String, Object>> selectCompanyWorkshopLineByGroupId(@Param("groupId") String groupId);

}
