package org.jeecg.modules.group.toolequipment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.group.tool.bean.BuMaterialTools;
import org.jeecg.modules.group.tool.bean.vo.BuToolUsageRecordVO;
import org.jeecg.modules.group.toolequipment.bean.vo.BuMaterialToolsQueryVO;

/**
 * <p>
 * 工装信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-14
 */
public interface BuMaterialToolEquipmentService extends IService<BuMaterialTools> {

    /**
     * 分页查询工装记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuMaterialTools> page(BuMaterialToolsQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询工装信息
     *
     * @param id 工装id
     * @return 工装信息
     * @throws Exception 异常信息
     */
    BuMaterialTools getById(String id) throws Exception;

    /**
     * 设置工装状态
     *
     * @param id     工装id
     * @param status 状态
     * @return 是否设置成功
     * @throws Exception 异常信息
     */
    boolean setStatus(String id, Integer status) throws Exception;

    /**
     * 分页查询工装使用记录
     *
     * @param id       工装id
     * @param trainNo  车号
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuToolUsageRecordVO> pageUsage(String id, String trainNo, Integer pageNo, Integer pageSize) throws Exception;

}
