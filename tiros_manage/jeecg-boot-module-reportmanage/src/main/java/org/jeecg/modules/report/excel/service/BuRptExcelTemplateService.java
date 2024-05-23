package org.jeecg.modules.report.excel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.report.excel.bean.BuRptExcelTemplate;

/**
 * <p>
 * 报表中心 表格模板 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-08
 */
public interface BuRptExcelTemplateService extends IService<BuRptExcelTemplate> {

    /**
     * 分页查询表格模板
     *
     * @param searchText 名称或编码
     * @param pageNo     页码
     * @param pageSize   页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuRptExcelTemplate> pageExcelTemplate(String searchText, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据编码查询表格模板信息
     *
     * @param tempCode 编码
     * @return 表格模板信息
     * @throws Exception 异常信息
     */
    BuRptExcelTemplate getExcelTemplateByCode(String tempCode) throws Exception;

//    /**
//     * 新增或修改表格模板
//     *
//     * @param excelTemplate 表格模板
//     * @return 是否成功
//     * @throws Exception 异常信息
//     */
//    boolean saveExcelTemplate(BuRptExcelTemplate excelTemplate) throws Exception;

    /**
     * 批量删除表格模板
     *
     * @param ids 表格模板ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

}
