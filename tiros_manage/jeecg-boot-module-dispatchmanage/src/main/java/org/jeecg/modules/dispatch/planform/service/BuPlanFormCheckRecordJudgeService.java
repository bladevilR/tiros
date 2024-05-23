package org.jeecg.modules.dispatch.planform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.planform.bean.BuPlanFormCheckRecordJudge;
import org.jeecg.modules.dispatch.planform.bean.vo.CheckRecordJudgeQueryVO;

import java.util.List;

/**
 * <p>
 * 质量评定 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
public interface BuPlanFormCheckRecordJudgeService extends IService<BuPlanFormCheckRecordJudge> {

    /**
     * 根据条件查询质量评定
     *
     * @param queryVO 查询条件
     * @return 质量评定
     * @throws Exception 异常
     */
    List<BuPlanFormCheckRecordJudge> listJudge(CheckRecordJudgeQueryVO queryVO) throws Exception;

    /**
     * 根据id查询质量评定
     *
     * @param id 质量评定id
     * @return 质量评定
     * @throws Exception 异常
     */
    BuPlanFormCheckRecordJudge getJudgeById(String id) throws Exception;

    /**
     * 新增或修改质量评定
     *
     * @param checkRecordJudge 质量评定
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveOrUpdateJudge(BuPlanFormCheckRecordJudge checkRecordJudge) throws Exception;

    /**
     * 批量删除质量评定
     *
     * @param ids 检查表单质量评定ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

}
