package org.jeecg.modules.board.progress.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.board.homepage.bean.vo.BuRepairPlanVOWithTaskGantt;
import org.jeecg.modules.board.progress.bean.BuRepairPlan;

import java.util.List;

/**
 * <p>
 * 列计划 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-30
 */
public interface BuRepairPlanBoardService extends IService<BuRepairPlan> {

    /**
     * 查询所有架修计划基本信息及当前完成进度
     *
     * @return 所有架修计划基本信息及当前完成进度
     * @throws Exception 异常信息
     */
    List<BuRepairPlanVOWithTaskGantt> listAllRepairPlanBasicGantt() throws Exception;

}
