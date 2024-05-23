package org.jeecg.modules.dispatch.workorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderAnnex;
import org.jeecg.modules.dispatch.workorder.bean.vo.BuWorkOrderAnnexSaveVO;

import java.util.List;

/**
 * <p>
 * 工单附件 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-19
 */
public interface BuWorkOrderAnnexService extends IService<BuWorkOrderAnnex> {

    /**
     * 查询工单附件列表
     *
     * @param orderId 工单id
     * @param taskId  工单任务id
     * @return 工单附件列表
     * @throws Exception 异常
     */
    List<BuWorkOrderAnnex> listAnnex(String orderId, String taskId) throws Exception;

    /**
     * 增加工单附件列表
     *
     * @param orderAnnexSaveVO 增加工单附件信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveAnnex(BuWorkOrderAnnexSaveVO orderAnnexSaveVO) throws Exception;

    /**
     * 批量删除工单附件
     *
     * @param ids 工单附件ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

}
