package org.jeecg.modules.outsource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.outsource.bean.BuContractAnnex;
import org.jeecg.modules.outsource.bean.vo.BuContractAnnexSaveVO;

import java.util.List;

/**
 * <p>
 * 合同附件 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-23
 */
public interface BuContractAnnexService extends IService<BuContractAnnex> {

    /**
     * 查询合同附件列表
     *
     * @param contractId 合同id
     * @return 合同附件列表
     * @throws Exception 异常
     */
    List<BuContractAnnex> listAnnex(String contractId) throws Exception;

    /**
     * 增加合同附件列表
     *
     * @param contractAnnexSaveVO 增加合同附件信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveAnnex(BuContractAnnexSaveVO contractAnnexSaveVO) throws Exception;

    /**
     * 批量删除合同附件
     *
     * @param ids 合同附件ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

}
