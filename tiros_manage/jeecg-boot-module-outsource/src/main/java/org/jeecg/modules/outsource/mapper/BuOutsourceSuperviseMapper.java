package org.jeecg.modules.outsource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.outsource.bean.BuOutsourceSupervise;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceSuperviseQueryVO;

import java.util.List;

/**
 * <p>
 * 委外监修 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
public interface BuOutsourceSuperviseMapper extends BaseMapper<BuOutsourceSupervise> {
    /**
     * @param page
     * @param supervise
     * @return
     */
    IPage<BuOutsourceSupervise> selectSupervisePage(Page<BuOutsourceSupervise> page, BuOutsourceSuperviseQueryVO supervise);

    /**
     * 根据id查询委外监修任务
     *
     * @param id 委外监修任务id
     * @return 委外监修任务
     */
    BuOutsourceSupervise selectSuperviseById(@Param("id") String id);

    /**
     * 根据工单id和工单任务id查询监修任务
     *
     * @param orderId     工单id
     * @param orderTaskId 工单任务id
     * @return 监修任务
     */
    List<BuOutsourceSupervise> selectOneByOrderIdAndOrderTaskId(@Param("orderId") String orderId, @Param("orderTaskId") String orderTaskId);

}
