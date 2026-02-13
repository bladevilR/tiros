package org.jeecg.modules.basemanage.techbook.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.techbook.bean.BuRepairTechBook;
import org.jeecg.modules.basemanage.techbook.bean.vo.BuRepairTechBookQueryVO;

/**
 * <p>
 * 作业指导书(工艺) 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
public interface BuRepairTechBookService extends IService<BuRepairTechBook> {

    /**
     * 分页查询作业指导书
     *
     * @param queryVO
     * @param pageNo     页码
     * @param pageSize   页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuRepairTechBook> pageTechBook(BuRepairTechBookQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询作业指导书
     *
     * @param id 作业指导书id
     * @return 作业指导书
     * @throws Exception 异常
     */
    BuRepairTechBook getTechBookById(String id) throws Exception;

    /**
     * 新增或修改作业指导书
     *
     * @param techBook 作业指导书信息
     * @return 是否成功
     * @throws Exception 异常
     */
    String saveTechBook(BuRepairTechBook techBook) throws Exception;

    /**
     * 批量删除作业指导书
     *
     * @param ids 作业指导书ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 保存/更新正文
     */
    boolean saveContent(String id, String contentHtml) throws Exception;

    /**
     * 审阅/发布等状态更新
     */
    boolean updateStatus(String id, Integer status) throws Exception;

    /**
     * 以现有指导书另存为模板（复制基本信息与明细）
     */
    String cloneAsTemplate(String sourceId) throws Exception;

    /**
     * 提交审阅
     */
    boolean submitReview(String id, String reviewerId, String reviewerName) throws Exception;

    /**
     * 审阅结论
     */
    boolean reviewDecision(String id, Integer reviewStatus, String reviewComment) throws Exception;

    /**
     * 提交审批（审阅通过后）
     */
    boolean submitApprove(String id, String approverId, String approverName) throws Exception;

    /**
     * 审批结论 approveStatus: 1-通过 2-退回
     */
    boolean approveDecision(String id, Integer approveStatus, String approveComment) throws Exception;

    /**
     * 基于既有指导书复用创建草稿
     */
    String cloneAsDraft(String sourceId, BuRepairTechBook draft) throws Exception;

    String reviseWithNewVersion(String id, String newVersion) throws Exception;


}
