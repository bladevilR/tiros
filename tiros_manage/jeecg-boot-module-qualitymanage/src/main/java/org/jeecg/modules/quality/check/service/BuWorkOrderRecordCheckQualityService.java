package org.jeecg.modules.quality.check.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.quality.check.bean.BuWorkOrderRecordCheck;

/**
 * <p>
 * 作业记录明细检查信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-21
 */
public interface BuWorkOrderRecordCheckQualityService extends IService<BuWorkOrderRecordCheck> {

    /**
     * 提交检查信息 如果是专检时，则需要将检查结果填写到明细表
     *
     * @param buWorkOrderRecordCheck 检查信息
     * @return 是否操作成功
     * @throws Exception 异常信息
     */
    boolean submitCheck(BuWorkOrderRecordCheck buWorkOrderRecordCheck) throws Exception;

}
