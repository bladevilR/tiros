package org.jeecg.modules.report.cost.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.modules.report.cost.bean.BuMaterialAssignDetail;
import org.jeecg.modules.report.cost.bean.vo.BuMaterialReceiveQueryVO;

import java.util.List;

/**
 * <p>
 * 物料分配明细 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
public interface BuMaterialAssignDetailReportService extends IService<BuMaterialAssignDetail> {

    /**
     * 根据领用明细id查询物流分配明细
     *
     * @param applyDetailId 领用明细id
     * @return 物料分配明细列表
     * @throws Exception 异常
     */
    List<BuMaterialAssignDetail> listAssignDetail(String applyDetailId) throws Exception;

    /**
     * 根据条件查询物料领用明细
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 物料领用明细列表
     * @throws Exception 异常
     */
    IPage<BuMaterialAssignDetail> pageMaterialReceive(BuMaterialReceiveQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据条件导出领用明细excel
     *
     * @param queryVO 查询条件
     * @return 领用明细excel
     * @throws Exception 异常
     */
    HSSFWorkbook exportMaterialReceive(BuMaterialReceiveQueryVO queryVO) throws Exception;

}
