package org.jeecg.modules.outsource.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.outsource.bean.BuOutsourceSupervise;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceSuperviseQueryVO;

/**
 * <p>
 * 委外监修 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
public interface BuOutsourceSuperviseService extends IService<BuOutsourceSupervise> {

    /**
     * 分页查询
     *
     * @param queryVO
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<BuOutsourceSupervise> page(BuOutsourceSuperviseQueryVO queryVO, Integer pageNo, Integer pageSize);

    /**
     * 根据id查询委外监修任务
     *
     * @param id 委外监修任务id
     * @return 委外监修任务
     * @throws Exception 异常
     */
    BuOutsourceSupervise getSuperviseById(String id) throws Exception;

    /**
     * 根据工单id和工单任务id，获取一条监修任务
     *
     * @param orderId     工单id
     * @param orderTaskId 工单任务id
     * @return 委外监修任务
     * @throws Exception 异常
     */
    BuOutsourceSupervise getSuperviseByOrderAndTask(String orderId, String orderTaskId) throws Exception;

    /**
     * 新增
     *
     * @param supplier
     * @return
     */
    boolean saveSupervise(BuOutsourceSupervise supplier) throws Exception;

}
