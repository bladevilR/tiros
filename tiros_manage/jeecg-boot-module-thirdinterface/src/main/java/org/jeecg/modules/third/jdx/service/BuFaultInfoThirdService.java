package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.modules.third.common.bean.bo.FaultCompareBO;
import org.jeecg.modules.third.jdx.bean.BuFaultInfo;
import org.jeecg.modules.third.jdx.bean.vo.FaultDiff;
import org.jeecg.modules.third.maximo.bean.JdxSrIn;
import org.jeecg.modules.third.workshopdb.bean.WSFault;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 故障信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultInfoThirdService extends IService<BuFaultInfo> {

    /**
     * 获取需写入到maximo的故障信息
     *
     * @param date 时间点，获取该时间点后的故障信息
     * @return 需写入到maximo的故障信息
     * @throws Exception 异常
     */
    List<JdxSrIn> listMaximoFaultNeedWrite(Date date) throws Exception;

    /**
     * 根据id获取需写入到maximo的故障
     *
     * @param faultId 故障id
     * @return 需写入到maximo的故障
     * @throws Exception 异常
     */
    JdxSrIn getMaximoFaultNeedWriteByFaultId(String faultId) throws Exception;

    /**
     * 根据故障获取需写入到maximo的故障
     *
     * @param fault 故障
     * @return 需写入到maximo的故障
     * @throws Exception 异常
     */
    JdxSrIn getMaximoFaultNeedWriteByFault(BuFaultInfo fault) throws Exception;

    /**
     * 获取架大修系统和maximo系统的不同故障
     *
     * @param maximoFaultSnList maximo故障号列表
     * @return 故障差异
     * @throws Exception 异常
     */
    FaultDiff getFaultDiffOfJdxAndMaximo(List<String> maximoFaultSnList) throws Exception;

    /**
     * 导出架大修系统和maximo系统的不同故障
     *
     * @param faultSnBOMap maximo故障号状态
     * @return excel文件
     */
    HSSFWorkbook exportFaultDiffOfJdxAndMaximo(Map<String, FaultCompareBO> faultSnBOMap);

    /**
     * 保存从车间车辆故障库中获取的质保期故障信息
     *
     * @param wsFaultList 从车间车辆故障库中获取的质保期故障信息
     * @return 保存结果
     */
    String initSaveWorkshopFaultData(List<WSFault> wsFaultList, Map<String, String> lineNameIdMap);

}
