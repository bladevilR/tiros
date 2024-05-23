package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuRptMaterialUseRecord;
import org.jeecg.modules.third.maximo.bean.JdxMatusetransOut;

import java.util.List;

/**
 * <p>
 * 物料消耗明细 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-13
 */
public interface BuRptMaterialUseRecordThirdService extends IService<BuRptMaterialUseRecord> {

    /**
     * 通过maximo工单物料数据插入工单物料
     *
     * @param maximoOrderMaterialList maximo工单物料数据
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean insertOrderMaterialFromMaximoData(List<JdxMatusetransOut> maximoOrderMaterialList) throws Exception;

}
