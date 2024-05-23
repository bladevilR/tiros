package org.jeecg.modules.quality.measurealert.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.quality.measurealert.bean.BuPlanFormValues;
import org.jeecg.modules.quality.measurealert.bean.vo.BuWorkMeasureAlertQueryVO;

/**
 * <p>
 * 记录表数据值记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-23
 */
public interface BuPlanFormValuesQualityService extends IService<BuPlanFormValues> {

    /**
     * 根据条件分页查询测量预警记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuPlanFormValues> page(BuWorkMeasureAlertQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

}
