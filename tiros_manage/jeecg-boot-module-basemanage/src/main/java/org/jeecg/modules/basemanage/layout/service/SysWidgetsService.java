package org.jeecg.modules.basemanage.layout.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.layout.bean.SysWidgets;
import org.jeecg.modules.basemanage.layout.bean.vo.BuSysWidgetsQueryVO;

/**
 * <p>
 * 桌面部件 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-27
 */
public interface SysWidgetsService extends IService<SysWidgets> {

    /**
     * 根据条件分页查询桌面部件
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<SysWidgets> pageWidgets(BuSysWidgetsQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 批量删除桌面部件
     *
     * @param ids 桌面部件ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

}
