package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuFaultCodeNew;
import org.jeecg.modules.third.maximo.bean.JdxFailurelistOut;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 故障编码(新) 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-30
 */
public interface BuFaultCodeNewThirdService extends IService<BuFaultCodeNew> {

    /**
     * 同步所有故障分类、代码、原因、措施
     *
     * @param maximoFaultCodeList maximo故障代码
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean insertAllFaultCodeFromMaximoData(List<JdxFailurelistOut> maximoFaultCodeList) throws Exception;

    /**
     * 消费maximo更新数据
     *
     * @param changeList maximo更新数据
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean consumeMaximoTransChangeData(List<JdxFailurelistOut> changeList) throws Exception;

}
