package org.jeecg.modules.group.safeassume.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.group.safeassume.bean.BuWorkSafeAssume;
import org.jeecg.modules.group.safeassume.bean.vo.BuWorkSafeAssumeQueryVO;

/**
 * <p>
 * 安全预想 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
public interface BuWorkSafeAssumeService extends IService<BuWorkSafeAssume> {

    /**
     * 根据条件分页查询安全预想
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuWorkSafeAssume> pageSafeAssume(BuWorkSafeAssumeQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据根据id查询安全预想详情
     *
     * @param id 安全预想id
     * @return 安全预想详情
     * @throws Exception 异常信息
     */
    BuWorkSafeAssume selectById(String id) throws Exception;

    /**
     * 批量删除安全预想
     *
     * @param ids 安全预想ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

}
