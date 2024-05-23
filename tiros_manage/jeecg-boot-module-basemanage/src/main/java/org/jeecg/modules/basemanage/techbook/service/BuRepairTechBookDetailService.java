package org.jeecg.modules.basemanage.techbook.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.techbook.bean.BuRepairTechBookDetail;

import java.util.List;

/**
 * <p>
 * 作业指导书(工艺)明细 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
public interface BuRepairTechBookDetailService extends IService<BuRepairTechBookDetail> {

    /**
     * 根据作业指导书id查询明细列表
     *
     * @param bookId 作业指导书id
     * @return 作业指导书明细列表
     * @throws Exception 异常
     */
    List<BuRepairTechBookDetail> listDetail(String bookId) throws Exception;

    /**
     * 根据明细id查询明细及关联信息
     *
     * @param detailId 明细id
     * @return 明细和管理信息
     * @throws Exception 异常
     */
    BuRepairTechBookDetail getDetailAndRelation(String detailId) throws Exception;

    /**
     * 新增或修改明细
     *
     * @param detail 明细信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveDetail(BuRepairTechBookDetail detail) throws Exception;

    /**
     * 批量删除明细
     *
     * @param ids 明细ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

}
