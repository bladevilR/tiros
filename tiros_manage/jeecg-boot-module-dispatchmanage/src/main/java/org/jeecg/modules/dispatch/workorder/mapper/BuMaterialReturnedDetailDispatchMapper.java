package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuMaterialReturnedDetail;

import java.util.List;

/**
 * <p>
 * 退料明细 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
public interface BuMaterialReturnedDetailDispatchMapper extends BaseMapper<BuMaterialReturnedDetail> {

    /**
     * 批量插入
     *
     * @param list 退料明细列表
     */
    void insertList(@Param("list") List<BuMaterialReturnedDetail> list);

    /**
     * 根据退料单id查询退料明细
     *
     * @param returnedId 退料单id
     * @return 退料明细
     */
    List<BuMaterialReturnedDetail> selectListByReturnedId(@Param("returnedId") String returnedId);

    /**
     * 根据工单物料id列表查询退料明细简单信息
     *
     * @param orderMaterialIdList 工单物料id列表
     * @return 退料明细简单信息
     */
    List<BuMaterialReturnedDetail> selectUnConfirmListByOrderMaterialIdList(@Param("orderMaterialIdList") List<String> orderMaterialIdList);

}
