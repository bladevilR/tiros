package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuMaterialAssignDetail;
import org.jeecg.modules.third.maximo.bean.JdxMatusetransIn;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 物料分配明细 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
public interface BuMaterialAssignDetailThirdService extends IService<BuMaterialAssignDetail> {

    /**
     * 根据领用明细id获取需写入到maximo的物料消耗
     *
     * @param applyDetailId 领用明细id
     * @return 需写入到maximo的物料消耗
     * @throws Exception 异常
     */
    List<JdxMatusetransIn> getMaximoMaterialConsumeNeedWriteByApplyDetailId(String applyDetailId) throws Exception;

    /**
     * 根据分配明细列表获取需写入到maximo的物料消耗
     *
     * @param assignDetail 分配明细列表
     * @return 需写入到maximo的物料消耗
     * @throws Exception 异常
     */
    JdxMatusetransIn getMaximoMaterialConsumeNeedWriteByAssignDetail(BuMaterialAssignDetail assignDetail) throws Exception;

    /**
     * 获取需写入到maximo的物料消耗
     *
     * @param date 时间点，获取该时间点后的物料消耗
     * @return 需写入到maximo的物料消耗
     * @throws Exception 异常
     */
    List<JdxMatusetransIn> listMaximoMaterialConsumeNeedWrite(Date date) throws Exception;

    /**
     * 更新领用单的同步时间
     *
     * @param assignDetailIdList 分配明细id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateApplySyncTime(List<String> assignDetailIdList) throws Exception;

    /**
     * 更新领用单的同步返回状态和时间时间
     *
     * @param assignDetailIdList 分配明细id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateApplySyncResult(List<String> assignDetailIdList) throws Exception;

}
