package org.jeecg.modules.group.turnoverchange.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.group.turnoverchange.bean.BuWorkTurnoverChange;
import org.jeecg.modules.group.turnoverchange.bean.vo.BuWorkTurnoverChangeQueryVO;

/**
 * <p>
 * 周转件更换，如果换上件没有记录，应先建立记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
public interface BuWorkTurnoverChangeService extends IService<BuWorkTurnoverChange> {

    /**
     * 根据条件分页查询周转件更换记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuWorkTurnoverChange> page(BuWorkTurnoverChangeQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询周转件更换记录详情
     *
     * @param id 周转件更换记录id
     * @return 周转件更换记录详情
     * @throws Exception 异常信息
     */
    BuWorkTurnoverChange selectById(String id) throws Exception;

    /**
     * 批量删除周转件更换记录
     *
     * @param ids 周转件更换记录ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

}
