package org.jeecg.modules.basemanage.org.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.org.bean.BuTraining;
import org.jeecg.modules.basemanage.org.bean.vo.BuTrainingQueryVO;

/**
 * <p>
 * 培训记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-19
 */
public interface BuTrainingService extends IService<BuTraining> {

    /**
     * 根据条件分页查询培训记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuTraining> pageTraining(BuTrainingQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 新增或修改培训记录
     *
     * @param training 培训记录
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean saveTraining(BuTraining training) throws Exception;

    /**
     * 批量删除培训记录
     *
     * @param ids 培训记录ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

}
