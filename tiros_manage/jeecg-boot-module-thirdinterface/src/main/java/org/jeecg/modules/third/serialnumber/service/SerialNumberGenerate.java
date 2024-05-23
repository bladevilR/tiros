package org.jeecg.modules.third.serialnumber.service;

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

}
