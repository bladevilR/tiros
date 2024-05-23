package org.jeecg.modules.group.tool.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.group.tool.bean.BuMaterialTools;
import org.jeecg.modules.group.tool.bean.vo.BuToolUsageRecordVO;
import org.jeecg.modules.group.toolequipment.bean.vo.BuMaterialToolsQueryVO;

/**
 * <p>
 * 工器具 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-14
 */
public interface BuMaterialToolService extends IService<BuMaterialTools> {

    /**
     * 分页查询工器具记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuMaterialTools> page(BuMaterialToolsQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询工器具信息
     *
     * @param id 工器具id
     * @return 工器具信息
     * @throws Exception 异常信息
     */
    BuMaterialTools getById(String id) throws Exception;

    /**
     * 设置工器具责任人员
     *
     * @param id     工器具id
     * @param userId 责任人员id
     * @return 是否设置成功
     * @throws Exception 异常信息
     */
    boolean setDutyUser(String id, String userId) throws Exception;

    /**
     * 分页查询工器具使用记录
     *
     * @param id       工器具id
     * @param trainNo  车号
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuToolUsageRecordVO> pageUsage(String id, String trainNo, Integer pageNo, Integer pageSize) throws Exception;

}
