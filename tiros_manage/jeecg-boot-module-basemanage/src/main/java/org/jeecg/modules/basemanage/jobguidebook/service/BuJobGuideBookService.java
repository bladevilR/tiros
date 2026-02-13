package org.jeecg.modules.basemanage.jobguidebook.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.jobguidebook.bean.BuJobGuideBook;
import org.jeecg.modules.basemanage.jobguidebook.bean.vo.BuJobGuideBookQueryVO;

import java.util.List;

public interface BuJobGuideBookService extends IService<BuJobGuideBook> {

    IPage<BuJobGuideBook> pageJobGuideBook(BuJobGuideBookQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    BuJobGuideBook getJobGuideBookById(String id) throws Exception;

    String saveJobGuideBook(BuJobGuideBook book) throws Exception;

    boolean deleteBatch(String ids) throws Exception;

    boolean saveContent(String id, String contentHtml) throws Exception;

    boolean updateStatus(String id, Integer status) throws Exception;

    String cloneAsTemplate(String sourceId) throws Exception;

    boolean submitReview(String id, String reviewerId, String reviewerName) throws Exception;

    boolean submitReviewBatch(String ids, String reviewerId, String reviewerName) throws Exception;

    boolean reviewDecision(String id, Integer reviewStatus, String reviewComment) throws Exception;

    boolean reviewDecisionBatch(String ids, Integer reviewStatus, String reviewComment) throws Exception;

    boolean submitApprove(String id, String approverId, String approverName) throws Exception;

    boolean submitApproveBatch(String ids, String approverId, String approverName) throws Exception;

    boolean approveDecision(String id, Integer approveStatus, String approveComment) throws Exception;

    boolean approveDecisionBatch(String ids, Integer approveStatus, String approveComment) throws Exception;

    String cloneAsDraft(String sourceId, BuJobGuideBook draft) throws Exception;

    String reviseWithNewVersion(String id, String newVersion) throws Exception;

    List<BuJobGuideBook> listForExport(String ids) throws Exception;
}
