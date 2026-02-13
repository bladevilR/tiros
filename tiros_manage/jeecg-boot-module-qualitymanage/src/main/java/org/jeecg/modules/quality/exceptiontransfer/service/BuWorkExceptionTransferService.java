package org.jeecg.modules.quality.exceptiontransfer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.quality.exceptiontransfer.bean.BuWorkExceptionTransfer;
import org.jeecg.modules.quality.exceptiontransfer.bean.vo.BuWorkExceptionTransferQueryVO;

public interface BuWorkExceptionTransferService extends IService<BuWorkExceptionTransfer> {

    IPage<BuWorkExceptionTransfer> pageTransfer(BuWorkExceptionTransferQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    BuWorkExceptionTransfer getByIdWithDetail(String id) throws Exception;

    boolean createByFault(BuWorkExceptionTransfer transfer) throws Exception;

    boolean updateTransfer(BuWorkExceptionTransfer transfer) throws Exception;

    boolean decideTransfer(String id, Integer status, Integer decisionType, String decisionRemark) throws Exception;

    boolean deleteBatch(String ids) throws Exception;
}

