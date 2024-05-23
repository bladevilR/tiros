package org.jeecg.modules.third.jdx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.third.jdx.bean.BuRepairTaskStaffArrange;
import org.jeecg.modules.third.maximo.bean.JdxLabtransIn;

/**
 * <p>
 * 任务人员安排 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-07-11
 */
public interface BuRepairTaskStaffArrangeThirdService extends IService<BuRepairTaskStaffArrange> {

    /**
     * 根据任务人员安排获取需写入到maximo的人员工时
     *
     * @param staffArrange 任务人员安排
     * @return 需写入到maximo的人员工时
     * @throws Exception 异常
     */
    JdxLabtransIn getMaximoWorkTimeNeedWriteByStaffArrange(BuRepairTaskStaffArrange staffArrange) throws Exception;

}
