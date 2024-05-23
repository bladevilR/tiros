package org.jeecg.common.tiros.serialnumber;

/**
 * <p>
 * 流水号生成 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/15
 */
public interface SerialNumberGenerate {

    /**
     * 根据模块编码生成流水号
     *
     * @param moduleCode 模块编码
     * @return 流水号
     */
    String generateSerialNumberByCode(String moduleCode);

    /**
     * 根据线路生成工单号
     *
     * @param line 线路
     * @return 工单号
     */
    String generateOrderCode(String line);

}
