package org.jeecg.modules.material.returned.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.returned.bean.BuMaterialReturned;
import org.jeecg.modules.material.returned.bean.vo.BuMaterialReturnedQueryVO;

/**
 * <p>
 * 退料 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
public interface BuMaterialReturnedMapper extends BaseMapper<BuMaterialReturned> {

    /**
     * 根据条件分页查询退料单
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuMaterialReturned> selectPageByQueryVO(IPage<BuMaterialReturned> page, @Param("queryVO") BuMaterialReturnedQueryVO queryVO);

    /**
     * 根据退料单id查询退料单
     *
     * @param id 退料单id
     * @return 退料单
     */
    BuMaterialReturned selectReturnedById(@Param("id") String id);

    /**
     * 根据工单id,查找班组id
     *
     * @param workOrderId 工单id
     * @return 班组id
     */
    String selectGroupIdByWorkOrder(@Param("workOrderId") String workOrderId);

}
