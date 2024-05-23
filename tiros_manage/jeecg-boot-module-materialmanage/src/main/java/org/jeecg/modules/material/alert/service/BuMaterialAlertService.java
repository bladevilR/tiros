package org.jeecg.modules.material.alert.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.modules.material.alert.bean.BuMaterialAlertQueryVO;
import org.jeecg.modules.material.alert.bean.BuMaterialAlertVO;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 物资预警查看 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/9/18
 */
public interface BuMaterialAlertService {

    /**
     * 根据条件查询物资预警分页 默认预警
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuMaterialAlertVO> pageDefault(BuMaterialAlertQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;


    /**
     * 根据条件查询物资预警分页 默认预警
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuMaterialAlertVO> appPageDefault(BuMaterialAlertQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;


    /**
     * 周物资预警导出excel，仅库存预警
     *
     * @return 周物资预警excel
     * @throws Exception 异常信息
     */
    HSSFWorkbook exportWeekMaterialAlert() throws Exception;

    /**
     * 月物资预警导出excel，仅库存预警
     *
     * @return 月物资预警excel
     * @throws Exception 异常信息
     */
    HSSFWorkbook exportMonthMaterialAlert() throws Exception;
    /**
     * 3列车物资预警导出excel，仅库存预警
     *
     * @return 3列车物资预警excel
     * @throws Exception 异常信息
     */
    HSSFWorkbook exportThesholdMaterialAlert();

    /**
     * 设置预警为已读
     * @param id
     * @return
     * @throws Exception
     */
    boolean settingRead(String id) throws  Exception;
}
