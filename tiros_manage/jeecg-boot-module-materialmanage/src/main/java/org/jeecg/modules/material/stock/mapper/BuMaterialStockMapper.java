package org.jeecg.modules.material.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.alert.bean.BuMaterialAlertQueryVO;
import org.jeecg.modules.material.alert.bean.BuMaterialAlertVO;
import org.jeecg.modules.material.stock.bean.BuMaterialStock;
import org.jeecg.modules.material.stock.bean.vo.BuMaterialStockQueryVO;

import java.util.List;

/**
 * <p>
 * 物资库存  一种物资类型可能存在多条，所以在查询库存时要注意汇总，汇总时价格用最新的，且只要显示 物资类型 的kind为 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
public interface BuMaterialStockMapper extends BaseMapper<BuMaterialStock> {

    /**
     * 批量插入
     *
     * @param list 物资库存列表
     */
    void insertList(@Param("list") List<BuMaterialStock> list);

    /**
     * 清理物资库存表中的数量为0的数据
     */
    void deleteZeroAmount();

    /**
     * 清理物质类型表中没有，库存中有的物质类型的数据
     */
    void deleteNotExistMaterialType();

    /**
     * 根据条件分页查询物资库存信息
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuMaterialStock> selectPageByCondition(IPage<BuMaterialStock> page, @Param("queryVO") BuMaterialStockQueryVO queryVO);

    /**
     * 根据物资类型id查询物资库存
     *
     * @param materialTypeId 物资类型id
     * @return 物资库存列表
     */
    List<BuMaterialStock> selectListByMaterialTypeId(@Param("materialTypeId") String materialTypeId);

    /**
     * 根据物资类型id列表查询物资库存
     *
     * @param materialTypeIdList 物资类型id列表
     * @return 物资库存列表
     */
    List<BuMaterialStock> selectListByMaterialTypeIdList(@Param("materialTypeIdList") List<String> materialTypeIdList);

    /**
     * 查询作为预警信息
     *
     * @return 预警信息列表
     */
    IPage<BuMaterialAlertVO> selectBuMaterialAlertVOPageByCondition(IPage<BuMaterialAlertVO> page, @Param("queryVO") BuMaterialAlertQueryVO queryVO);

    /**
     * 查询作为预警信息
     *
     * @return 预警信息列表
     */
    IPage<BuMaterialAlertVO> selectBuMaterialAlertVOPageByConditionApp(IPage<BuMaterialAlertVO> page, @Param("queryVO") BuMaterialAlertQueryVO queryVO);

    /**
     * 查询周物资预警列表，仅库存预警
     *
     * @return 周物资预警列表
     */
    List<BuMaterialAlertVO> selectWeekBuMaterialAlertVOList();

    /**
     * 查询月物资预警列表，仅库存预警
     *
     * @return 月物资预警列表
     */
    List<BuMaterialAlertVO> selectMonthBuMaterialAlertVOList();

    /**
     * 查询3列车物资预警列表，仅库存预警
     *
     * @return 月物资预警列表
     */
    List<BuMaterialAlertVO> selectThresholdBuMaterialAlertVOList();

    List<BuMaterialStock> selectPriceValidList();

}
