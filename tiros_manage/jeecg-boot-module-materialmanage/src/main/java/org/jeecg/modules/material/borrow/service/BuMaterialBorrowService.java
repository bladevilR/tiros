package org.jeecg.modules.material.borrow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.modules.material.borrow.bean.BuMaterialBorrow;
import org.jeecg.modules.material.borrow.bean.BuMaterialBorrowDetail;
import org.jeecg.modules.material.borrow.bean.vo.BuMaterialBorrowQueryVO;
import org.jeecg.modules.material.borrow.bean.vo.BuMaterialBorrowReturnVO;

import java.util.List;

/**
 * <p>
 * 物资借用 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020 -09-17
 */
public interface BuMaterialBorrowService extends IService<BuMaterialBorrow> {

    /**
     * 分页查询物资借用记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果 page
     * @throws Exception 异常信息
     */
    IPage<BuMaterialBorrow> page(BuMaterialBorrowQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 分页查询物资借用记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果 page
     * @throws Exception 异常信息
     */
    IPage<BuMaterialBorrow> appPage(BuMaterialBorrowQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 批量删除物资借用记录
     *
     * @param ids 物资借用记录ids 多个逗号分隔
     * @return 是否删除成功 boolean
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 批量设置物资借用记录状态为已归还
     *
     * @param borrowReturnVOs 物资借用记录
     * @return 是否操作成功 return
     * @throws Exception 异常信息
     */
    boolean setReturn( List<BuMaterialBorrowReturnVO> borrowReturnVOs) throws Exception;

    /**
     * Borrow detail list list.
     *
     * @param id the id
     * @return the list
     * @throws Exception the exception
     */
    List<BuMaterialBorrowDetail> borrowDetailList(String id) throws Exception;

    /**
     * Save material borrow boolean.
     *
     * @param buMaterialBorrow the bu material borrow
     * @return the boolean
     * @throws Exception the exception
     */
    boolean saveMaterialBorrow(BuMaterialBorrow buMaterialBorrow) throws Exception;

    /**
     * Update material borrow boolean.
     *
     * @param buMaterialBorrow the bu material borrow
     * @return the boolean
     * @throws Exception the exception
     */
    boolean updateMaterialBorrow(BuMaterialBorrow buMaterialBorrow) throws Exception;

    /**
     * Export material borrow hssf workbook.
     *
     * @param queryVO the query vo
     * @return the hssf workbook
     */
    HSSFWorkbook exportMaterialBorrow(BuMaterialBorrowQueryVO queryVO) throws Exception;

    /**
     * Export exportDetailList hssf workbook.
     *
     * @param queryVO the query vo
     * @return the hssf workbook
     */
    HSSFWorkbook exportDetailList(BuMaterialBorrowQueryVO queryVO);

    /**
     * 设置明细归还
     * @param borrowReturnVO
     * @return
     */
    boolean setDetailReturn(BuMaterialBorrowReturnVO borrowReturnVO) throws Exception;
}
