package org.jeecg.modules.basemanage.line.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.line.entity.BuMtrLine;

import java.util.List;

/**
 * <p>
 * 线路 服务类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-13
 */
public interface IBuMtrLineService extends IService<BuMtrLine> {

    /**
     * 获取所有线路
     *
     * @return 所有线路列表
     * @throws Exception 异常
     */
    List<BuMtrLine> listAll() throws Exception;

    /**
     * 查询线路列表，根据当前人员所属车辆段过滤
     *
     * @return 线路列表
     */
    List<BuMtrLine> listByCurrentUser() throws Exception;

    /**
     * 根据id获取线路信息
     *
     * @param lineId 线路id
     * @return 线路信息
     * @throws Exception 异常信息
     */
    BuMtrLine getLineById(String lineId) throws Exception;

}
