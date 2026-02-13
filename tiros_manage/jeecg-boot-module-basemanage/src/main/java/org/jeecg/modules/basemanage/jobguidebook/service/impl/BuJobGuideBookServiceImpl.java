package org.jeecg.modules.basemanage.jobguidebook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.jobguidebook.bean.*;
import org.jeecg.modules.basemanage.jobguidebook.bean.vo.BuJobGuideBookQueryVO;
import org.jeecg.modules.basemanage.jobguidebook.mapper.*;
import org.jeecg.modules.basemanage.jobguidebook.service.BuJobGuideBookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuJobGuideBookServiceImpl extends ServiceImpl<BuJobGuideBookMapper, BuJobGuideBook> implements BuJobGuideBookService {

    @Resource
    private BuJobGuideBookMapper buJobGuideBookMapper;
    @Resource
    private BuJobGuideBookDetailMapper buJobGuideBookDetailMapper;
    @Resource
    private BuJobGuideBookMaterialMapper buJobGuideBookMaterialMapper;
    @Resource
    private BuJobGuideBookToolsMapper buJobGuideBookToolsMapper;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuJobGuideBook> pageJobGuideBook(BuJobGuideBookQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buJobGuideBookMapper.selectPageBySearchText(new Page<>(pageNo, pageSize), queryVO);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuJobGuideBook getJobGuideBookById(String id) throws Exception {
        return buJobGuideBookMapper.selectJobGuideBookById(id);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuJobGuideBook> listForExport(String ids) throws Exception {
        List<String> idList = parseIdList(ids);
        if (CollectionUtils.isEmpty(idList)) {
            throw new JeecgBootException("作业指导书ID不能为空");
        }
        List<BuJobGuideBook> result = new ArrayList<>();
        for (String id : idList) {
            BuJobGuideBook book = buJobGuideBookMapper.selectJobGuideBookById(id);
            if (book == null) {
                continue;
            }
            if (Integer.valueOf(9).equals(book.getStatus())) {
                continue;
            }
            result.add(book);
        }
        if (CollectionUtils.isEmpty(result)) {
            throw new JeecgBootException("未找到可导出的作业指导书");
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveJobGuideBook(BuJobGuideBook book) throws Exception {
        if (StringUtils.isBlank(book.getId())) {
            String id = UUIDGenerator.generate();
            book.setId(id);
            if (book.getStatus() == null) {
                book.setStatus(0);
            }
            if (book.getTemplateFlag() == null) {
                book.setTemplateFlag(0);
            }
            if (book.getReviewStatus() == null) {
                book.setReviewStatus(0);
            }
            buJobGuideBookMapper.insert(book);
            return id;
        } else {
            BuJobGuideBook existing = buJobGuideBookMapper.selectById(book.getId());
            if (existing != null) {
                if (book.getContentHtml() == null) {
                    book.setContentHtml(existing.getContentHtml());
                }
                if (book.getTemplateFlag() == null) {
                    book.setTemplateFlag(existing.getTemplateFlag());
                }
                if (book.getReviewStatus() == null) {
                    book.setReviewStatus(existing.getReviewStatus());
                }
                if (book.getReviewerId() == null) {
                    book.setReviewerId(existing.getReviewerId());
                }
                if (book.getReviewerName() == null) {
                    book.setReviewerName(existing.getReviewerName());
                }
                if (book.getReviewComment() == null) {
                    book.setReviewComment(existing.getReviewComment());
                }
                if (book.getReviewTime() == null) {
                    book.setReviewTime(existing.getReviewTime());
                }
                if (book.getApproverId() == null) {
                    book.setApproverId(existing.getApproverId());
                }
                if (book.getApproverName() == null) {
                    book.setApproverName(existing.getApproverName());
                }
                if (book.getApproveComment() == null) {
                    book.setApproveComment(existing.getApproveComment());
                }
                if (book.getApproveTime() == null) {
                    book.setApproveTime(existing.getApproveTime());
                }
            }
            buJobGuideBookMapper.updateById(book);
            return book.getId();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = parseIdList(ids);
        if (CollectionUtils.isEmpty(idList)) {
            throw new JeecgBootException("作业指导书ID不能为空");
        }

        List<BuJobGuideBook> books = buJobGuideBookMapper.selectBatchIds(idList);
        if (CollectionUtils.isNotEmpty(books)) {
            for (BuJobGuideBook book : books) {
                if (book == null) continue;
                if (Integer.valueOf(1).equals(book.getStatus())) {
                    throw new JeecgBootException("已发布状态不允许删除");
                }
                if (Integer.valueOf(9).equals(book.getStatus())) {
                    throw new JeecgBootException("已作废状态不允许删除");
                }
            }
        }

        LambdaQueryWrapper<BuJobGuideBookDetail> detailWrapper = new LambdaQueryWrapper<BuJobGuideBookDetail>()
                .in(BuJobGuideBookDetail::getBookId, idList);
        List<BuJobGuideBookDetail> detailList = buJobGuideBookDetailMapper.selectList(detailWrapper);
        if (CollectionUtils.isNotEmpty(detailList)) {
            List<String> detailIdList = detailList.stream()
                    .map(BuJobGuideBookDetail::getId)
                    .collect(Collectors.toList());
            deleteDetailRelation(detailIdList);
            buJobGuideBookDetailMapper.delete(detailWrapper);
        }

        buJobGuideBookMapper.update(null,
                new UpdateWrapper<BuJobGuideBook>()
                        .set("status", 9)
                        .set("update_time", new java.util.Date())
                        .in("id", idList));
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveContent(String id, String contentHtml) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("作业指导书ID不能为空");
        }
        getByIdOrThrow(id);
        return buJobGuideBookMapper.update(null,
                new UpdateWrapper<BuJobGuideBook>()
                        .set("content_html", contentHtml)
                        .eq("id", id)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(String id, Integer status) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("作业指导书ID不能为空");
        }
        if (status == null || (status != 0 && status != 1 && status != 2 && status != 3 && status != 9)) {
            throw new JeecgBootException("状态参数不合法");
        }
        BuJobGuideBook book = getByIdOrThrow(id);
        if (status == 1) {
            if (!Integer.valueOf(3).equals(book.getStatus())) {
                throw new JeecgBootException("作业指导书需先审批通过后才能发布");
            }
            if (StringUtils.isBlank(book.getContentHtml())) {
                throw new JeecgBootException("作业指导书正文不能为空，无法发布");
            }
        }
        if (status == 2 && !Integer.valueOf(2).equals(book.getReviewStatus())) {
            throw new JeecgBootException("审核通过后才能提交审批");
        }
        if (status == 3 && !Integer.valueOf(2).equals(book.getStatus())) {
            throw new JeecgBootException("仅审批中状态可更新为审批通过");
        }
        boolean updated = buJobGuideBookMapper.update(null,
                new UpdateWrapper<BuJobGuideBook>()
                        .set("status", status)
                        .set("update_time", new java.util.Date())
                        .eq("id", id)) > 0;

        if (updated && status == 1) {
            buJobGuideBookMapper.update(null,
                    new UpdateWrapper<BuJobGuideBook>()
                            .set("status", 9)
                            .set("update_time", new java.util.Date())
                            .eq("template_flag", 0)
                            .eq("line_id", book.getLineId())
                            .eq("train_type_id", book.getTrainTypeId())
                            .eq("repair_program_id", book.getRepairProgramId())
                            .eq("file_no", book.getFileNo())
                            .eq("status", 1)
                            .ne("id", id));
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String cloneAsTemplate(String sourceId) throws Exception {
        BuJobGuideBook source = getByIdOrThrow(sourceId);
        String newId = UUIDGenerator.generate();
        BuJobGuideBook template = new BuJobGuideBook();
        template.setId(newId);
        template.setFileNo(source.getFileNo() + "-T");
        template.setFileName(source.getFileName());
        template.setFileVer(source.getFileVer());
        template.setLineId(source.getLineId());
        template.setTrainTypeId(source.getTrainTypeId());
        template.setRepairProgramId(source.getRepairProgramId());
        template.setProject(source.getProject());
        template.setExeTime(source.getExeTime());
        template.setStatus(0);
        template.setTemplateFlag(1);
        template.setReviewStatus(0);
        template.setReviewerId(null);
        template.setReviewerName(null);
        template.setReviewComment(null);
        template.setReviewTime(null);
        template.setApproverId(null);
        template.setApproverName(null);
        template.setApproveComment(null);
        template.setApproveTime(null);
        template.setRemark(source.getRemark());
        template.setCompanyId(source.getCompanyId());
        template.setWorkshopId(source.getWorkshopId());
        template.setContentHtml(source.getContentHtml());
        buJobGuideBookMapper.insert(template);

        cloneDetails(sourceId, newId);
        return newId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitReview(String id, String reviewerId, String reviewerName) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("作业指导书ID不能为空");
        }
        if (StringUtils.isBlank(reviewerId)) {
            throw new JeecgBootException("审核人不能为空");
        }
        BuJobGuideBook book = getByIdOrThrow(id);
        if (!Integer.valueOf(0).equals(book.getStatus())) {
            throw new JeecgBootException("仅草稿状态可提交审核");
        }
        if (Integer.valueOf(1).equals(book.getReviewStatus())) {
            throw new JeecgBootException("该作业指导书已在待审状态，请勿重复提交");
        }
        return buJobGuideBookMapper.update(null,
                new UpdateWrapper<BuJobGuideBook>()
                        .set("status", 0)
                        .set("review_status", 1)
                        .set("reviewer_id", reviewerId)
                        .set("reviewer_name", reviewerName)
                        .set("review_comment", null)
                        .set("review_time", null)
                        .eq("id", id)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitReviewBatch(String ids, String reviewerId, String reviewerName) throws Exception {
        List<String> idList = parseIdList(ids);
        if (CollectionUtils.isEmpty(idList)) {
            throw new JeecgBootException("作业指导书ID不能为空");
        }
        for (String id : idList) {
            submitReview(id, reviewerId, reviewerName);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewDecision(String id, Integer reviewStatus, String reviewComment) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("作业指导书ID不能为空");
        }
        if (reviewStatus == null || (reviewStatus != 2 && reviewStatus != 3)) {
            throw new JeecgBootException("审核状态不合法");
        }
        if (StringUtils.isBlank(reviewComment)) {
            throw new JeecgBootException("审核意见不能为空");
        }
        BuJobGuideBook book = getByIdOrThrow(id);
        if (!Integer.valueOf(1).equals(book.getReviewStatus())) {
            throw new JeecgBootException("仅待审状态可执行审核结论");
        }
        return buJobGuideBookMapper.update(null,
                new UpdateWrapper<BuJobGuideBook>()
                        .set("status", 0)
                        .set("review_status", reviewStatus)
                        .set("review_comment", reviewComment)
                        .set("review_time", new java.util.Date())
                        .set("update_time", new java.util.Date())
                        .eq("id", id)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewDecisionBatch(String ids, Integer reviewStatus, String reviewComment) throws Exception {
        List<String> idList = parseIdList(ids);
        if (CollectionUtils.isEmpty(idList)) {
            throw new JeecgBootException("作业指导书ID不能为空");
        }
        for (String id : idList) {
            reviewDecision(id, reviewStatus, reviewComment);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitApprove(String id, String approverId, String approverName) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("作业指导书ID不能为空");
        }
        if (StringUtils.isBlank(approverId)) {
            throw new JeecgBootException("审批人不能为空");
        }
        BuJobGuideBook book = getByIdOrThrow(id);
        if (!Integer.valueOf(0).equals(book.getStatus())) {
            throw new JeecgBootException("仅草稿状态可提交审批");
        }
        if (!Integer.valueOf(2).equals(book.getReviewStatus())) {
            throw new JeecgBootException("仅审核通过状态可提交审批");
        }
        return buJobGuideBookMapper.update(null,
                new UpdateWrapper<BuJobGuideBook>()
                        .set("status", 2)
                        .set("approver_id", approverId)
                        .set("approver_name", approverName)
                        .set("approve_comment", null)
                        .set("approve_time", null)
                        .set("update_time", new java.util.Date())
                        .eq("id", id)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitApproveBatch(String ids, String approverId, String approverName) throws Exception {
        List<String> idList = parseIdList(ids);
        if (CollectionUtils.isEmpty(idList)) {
            throw new JeecgBootException("作业指导书ID不能为空");
        }
        for (String id : idList) {
            submitApprove(id, approverId, approverName);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveDecision(String id, Integer approveStatus, String approveComment) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("作业指导书ID不能为空");
        }
        if (approveStatus == null || (approveStatus != 1 && approveStatus != 2)) {
            throw new JeecgBootException("审批状态不合法");
        }
        if (approveStatus == 2 && StringUtils.isBlank(approveComment)) {
            throw new JeecgBootException("审批退回时需填写原因");
        }
        BuJobGuideBook book = getByIdOrThrow(id);
        if (!Integer.valueOf(2).equals(book.getStatus())) {
            throw new JeecgBootException("仅审批中状态可执行审批结论");
        }
        UpdateWrapper<BuJobGuideBook> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .set("update_time", new java.util.Date());
        if (approveStatus == 1) {
            wrapper.set("status", 3);
        } else {
            wrapper.set("status", 0)
                    .set("review_status", 0);
        }
        if (StringUtils.isNotBlank(approveComment)) {
            wrapper.set("approve_comment", approveComment);
        }
        wrapper.set("approve_time", new java.util.Date());
        return buJobGuideBookMapper.update(null, wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveDecisionBatch(String ids, Integer approveStatus, String approveComment) throws Exception {
        List<String> idList = parseIdList(ids);
        if (CollectionUtils.isEmpty(idList)) {
            throw new JeecgBootException("作业指导书ID不能为空");
        }
        for (String id : idList) {
            approveDecision(id, approveStatus, approveComment);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String cloneAsDraft(String sourceId, BuJobGuideBook draft) throws Exception {
        if (draft == null) {
            throw new JeecgBootException("复用参数不能为空");
        }
        String newId = cloneAsTemplate(sourceId);
        BuJobGuideBook cloned = getByIdOrThrow(newId);
        if (StringUtils.isNotBlank(draft.getFileNo())) {
            cloned.setFileNo(draft.getFileNo().trim());
        }
        if (StringUtils.isNotBlank(draft.getFileName())) {
            cloned.setFileName(draft.getFileName().trim());
        }
        if (StringUtils.isNotBlank(draft.getFileVer())) {
            cloned.setFileVer(draft.getFileVer().trim());
        }
        if (StringUtils.isNotBlank(draft.getLineId())) {
            cloned.setLineId(draft.getLineId());
        }
        if (StringUtils.isNotBlank(draft.getTrainTypeId())) {
            cloned.setTrainTypeId(draft.getTrainTypeId());
        }
        if (StringUtils.isNotBlank(draft.getRepairProgramId())) {
            cloned.setRepairProgramId(draft.getRepairProgramId());
        }
        if (StringUtils.isNotBlank(draft.getProject())) {
            cloned.setProject(draft.getProject());
        }
        if (draft.getExeTime() != null) {
            cloned.setExeTime(draft.getExeTime());
        }
        if (StringUtils.isNotBlank(draft.getRemark())) {
            cloned.setRemark(draft.getRemark());
        }
        cloned.setTemplateFlag(0);
        cloned.setStatus(0);
        cloned.setReviewStatus(0);
        cloned.setReviewerId(null);
        cloned.setReviewerName(null);
        cloned.setReviewComment(null);
        cloned.setReviewTime(null);
        cloned.setApproverId(null);
        cloned.setApproverName(null);
        cloned.setApproveComment(null);
        cloned.setApproveTime(null);
        buJobGuideBookMapper.updateById(cloned);
        return newId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String reviseWithNewVersion(String id, String newVersion) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("作业指导书ID不能为空");
        }
        if (StringUtils.isBlank(newVersion)) {
            throw new JeecgBootException("新版本号不能为空");
        }
        getByIdOrThrow(id);
        String newId = cloneAsTemplate(id);
        BuJobGuideBook revised = getByIdOrThrow(newId);
        revised.setTemplateFlag(0);
        revised.setFileVer(newVersion.trim());
        revised.setStatus(0);
        revised.setReviewStatus(0);
        revised.setReviewerId(null);
        revised.setReviewerName(null);
        revised.setReviewComment(null);
        revised.setReviewTime(null);
        revised.setApproverId(null);
        revised.setApproverName(null);
        revised.setApproveComment(null);
        revised.setApproveTime(null);
        buJobGuideBookMapper.updateById(revised);
        return newId;
    }

    // ---- private helpers ----

    private void cloneDetails(String sourceBookId, String newBookId) {
        LambdaQueryWrapper<BuJobGuideBookDetail> detailWrapper = new LambdaQueryWrapper<BuJobGuideBookDetail>()
                .eq(BuJobGuideBookDetail::getBookId, sourceBookId);
        List<BuJobGuideBookDetail> detailList = buJobGuideBookDetailMapper.selectList(detailWrapper);
        if (CollectionUtils.isEmpty(detailList)) {
            return;
        }
        List<BuJobGuideBookDetail> newDetails = new ArrayList<>();
        for (BuJobGuideBookDetail detail : detailList) {
            String newDetailId = UUIDGenerator.generate();
            BuJobGuideBookDetail nd = new BuJobGuideBookDetail();
            nd.setId(newDetailId);
            nd.setBookId(newBookId);
            nd.setStepNum(detail.getStepNum());
            nd.setStepTitle(detail.getStepTitle());
            nd.setStepContent(detail.getStepContent());
            newDetails.add(nd);

            LambdaQueryWrapper<BuJobGuideBookMaterial> matWrapper = new LambdaQueryWrapper<BuJobGuideBookMaterial>()
                    .eq(BuJobGuideBookMaterial::getBookDetailId, detail.getId());
            List<BuJobGuideBookMaterial> mats = buJobGuideBookMaterialMapper.selectList(matWrapper);
            for (BuJobGuideBookMaterial mat : mats) {
                BuJobGuideBookMaterial nm = new BuJobGuideBookMaterial();
                nm.setId(UUIDGenerator.generate());
                nm.setBookDetailId(newDetailId);
                nm.setMaterialTypeId(mat.getMaterialTypeId());
                nm.setAmount(mat.getAmount());
                buJobGuideBookMaterialMapper.insert(nm);
            }

            LambdaQueryWrapper<BuJobGuideBookTools> toolWrapper = new LambdaQueryWrapper<BuJobGuideBookTools>()
                    .eq(BuJobGuideBookTools::getBookDetailId, detail.getId());
            List<BuJobGuideBookTools> tools = buJobGuideBookToolsMapper.selectList(toolWrapper);
            for (BuJobGuideBookTools tool : tools) {
                BuJobGuideBookTools nt = new BuJobGuideBookTools();
                nt.setId(UUIDGenerator.generate());
                nt.setBookDetailId(newDetailId);
                nt.setToolTypeId(tool.getToolTypeId());
                nt.setAmount(tool.getAmount());
                buJobGuideBookToolsMapper.insert(nt);
            }
        }
        for (BuJobGuideBookDetail nd : newDetails) {
            buJobGuideBookDetailMapper.insert(nd);
        }
    }

    private void deleteDetailRelation(List<String> detailIdList) {
        if (CollectionUtils.isEmpty(detailIdList)) {
            return;
        }
        LambdaQueryWrapper<BuJobGuideBookMaterial> matWrapper = new LambdaQueryWrapper<BuJobGuideBookMaterial>()
                .in(BuJobGuideBookMaterial::getBookDetailId, detailIdList);
        buJobGuideBookMaterialMapper.delete(matWrapper);
        LambdaQueryWrapper<BuJobGuideBookTools> toolWrapper = new LambdaQueryWrapper<BuJobGuideBookTools>()
                .in(BuJobGuideBookTools::getBookDetailId, detailIdList);
        buJobGuideBookToolsMapper.delete(toolWrapper);
    }

    private BuJobGuideBook getByIdOrThrow(String id) {
        BuJobGuideBook book = buJobGuideBookMapper.selectById(id);
        if (book == null) {
            throw new JeecgBootException("作业指导书不存在");
        }
        return book;
    }

    private List<String> parseIdList(String ids) {
        if (StringUtils.isBlank(ids)) {
            return new ArrayList<>();
        }
        return Arrays.stream(ids.split(","))
                .map(StringUtils::trimToNull)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }
}
