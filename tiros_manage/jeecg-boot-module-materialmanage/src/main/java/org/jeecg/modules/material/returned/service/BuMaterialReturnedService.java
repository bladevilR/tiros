package org.jeecg.modules.material.returned.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.returned.bean.BuMaterialReturned;
import org.jeecg.modules.material.returned.bean.vo.BuMaterialReturnedQueryVO;

/**
 * <p>
 * 退料 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
public interface BuMaterialReturnedService extends IService<BuMaterialReturned> {

    /**
     * 根据条件分页查询退料单
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuMaterialReturned> pageReturned(BuMaterialReturnedQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据退料单id查询退料单及明细信息
     *
     * @param id 退料单id
     * @return 退料单及明细信息
     * @throws Exception 异常
     */
    BuMaterialReturned getReturnedById(String id) throws Exception;

    /**
     * 新增或修改退料单
     *
     * @param returned 退料单及明细信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveReturned(BuMaterialReturned returned) throws Exception;

    /**
     * 批量删除退料单及明细
     *
     * @param ids 退料单ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 批量确认退料
     *
     * @param ids 退料单ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean confirmBatch(String ids) throws Exception;

    /**
     * 批量确认退料，不扣减班组库存不处理退料回写
     *
     * @param ids 退料单ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean confirmBatchOnlyToEbs(String ids) throws Exception;

}
