package org.jeecg.modules.group.partchange.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.group.partchange.bean.BuWorkPartChange;
import org.jeecg.modules.group.partchange.bean.vo.BuWorkPartChangeQueryVO;

/**
 * <p>
 * 部件更换 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
public interface BuWorkPartChangeService extends IService<BuWorkPartChange> {

    /**
     * 根据条件分页查询部件更换记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuWorkPartChange> page(BuWorkPartChangeQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询部件更换记录详情
     *
     * @param id 部件更换记录id
     * @return 部件更换记录详情
     * @throws Exception 异常信息
     */
    BuWorkPartChange selectById(String id) throws Exception;

    /**
     * 批量删除部件更换记录
     *
     * @param ids 部件更换记录ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

}
