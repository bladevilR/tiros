package org.jeecg.modules.dispatch.fault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCodeLevel;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yfy
 * @since 2021-05-11
 */
public interface BuFaultCodeLevelService extends IService<BuFaultCodeLevel> {

    /**
     * 根据条件查询故障代码等级信息
     *
     * @param id  查询条件
     * @return list
     * @throws Exception 异常信息
     */
    BuFaultCodeLevel FindFaultCodeLevelById(String id);
}
