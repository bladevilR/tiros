package org.jeecg.modules.report.cost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.report.cost.bean.BuMaterialAssignDetail;
import org.jeecg.modules.report.cost.bean.vo.BuMaterialReceiveQueryVO;

import java.util.List;

/**
 * <p>
 * 物料分配明细 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
public interface BuMaterialAssignDetailReportMapper extends BaseMapper<BuMaterialAssignDetail> {

    /**
     * 根据领用明细id查询物料分配明细
     *
     * @param applyDetailId 领用明细id
     * @return 物料分配明细列表
     */
    List<BuMaterialAssignDetail> selectListByApplyDetailId(@Param("applyDetailId") String applyDetailId);

    /**
     * 根据条件分页查询物料领用明细
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuMaterialAssignDetail> selectPageByReceiveQueryVO(IPage<BuMaterialAssignDetail> page, @Param("queryVO") BuMaterialReceiveQueryVO queryVO);

    /**
     * 根据条件查询物料领用明细列表
     *
     * @param queryVO 查询条件
     * @return 物料领用明细列表
     */
    List<BuMaterialAssignDetail> selectListByReceiveQueryVO(@Param("queryVO") BuMaterialReceiveQueryVO queryVO);

}
