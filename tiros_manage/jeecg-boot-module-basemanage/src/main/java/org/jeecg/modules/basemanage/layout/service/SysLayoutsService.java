package org.jeecg.modules.basemanage.layout.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.layout.bean.SysLayouts;
import org.jeecg.modules.basemanage.layout.bean.vo.BuSysLayoutsQueryVO;

/**
 * <p>
 * 界面布局 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-27
 */
public interface SysLayoutsService extends IService<SysLayouts> {

    /**
     * 根据条件分页查询界面布局
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<SysLayouts> pageLayouts(BuSysLayoutsQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询界面布局
     *
     * @param id 界面布局id
     * @return 界面布局，包括布局下组件列表
     * @throws Exception 异常信息
     */
    SysLayouts getLayouts(String id) throws Exception;

    /**
     * 根据登录人查询查询界面布局
     *
     * @return 界面布局，包括布局下组件列表
     * @throws Exception 异常信息
     */
    SysLayouts getUserDefaultLayouts() throws Exception;

    /**
     * 界面布局新增/修改
     *
     * @param layouts 界面布局
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean saveLayouts(SysLayouts layouts) throws Exception;

    /**
     * 批量删除界面布局
     *
     * @param ids 界面布局ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

}