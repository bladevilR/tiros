package org.jeecg.modules.basemanage.techbook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguTechBookDetail;
import org.jeecg.modules.basemanage.regu.mapper.BuRepairReguTechBookDetailMapper;
import org.jeecg.modules.basemanage.techbook.bean.*;
import org.jeecg.modules.basemanage.techbook.bean.vo.BuRepairTechBookQueryVO;
import org.jeecg.modules.basemanage.techbook.mapper.*;
import org.jeecg.modules.basemanage.techbook.service.BuRepairTechBookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 作业指导书(工艺) 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Service
public class BuRepairTechBookServiceImpl extends ServiceImpl<BuRepairTechBookMapper, BuRepairTechBook> implements BuRepairTechBookService {

    @Resource
    private BuRepairTechBookMapper buRepairTechBookMapper;
    @Resource
    private BuRepairTechBookDetailMapper buRepairTechBookDetailMapper;
    @Resource
    private BuRepairTechBookDetailMaterialMapper buRepairTechBookDetailMaterialMapper;
    @Resource
    private BuRepairTechBookDetailToolsMapper buRepairTechBookDetailToolsMapper;
    @Resource
    private BuRepairReguTechBookDetailMapper buRepairReguTechBookDetailMapper;


    /**
     * @see BuRepairTechBookService
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuRepairTechBook> pageTechBook(BuRepairTechBookQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buRepairTechBookMapper.selectPageBySearchText(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuRepairTechBookService#getTechBookById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairTechBook getTechBookById(String id) throws Exception {
        return buRepairTechBookMapper.selectTechBookById(id);
    }

    /**
     * @see BuRepairTechBookService#saveTechBook(BuRepairTechBook)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveTechBook(BuRepairTechBook techBook) throws Exception {
        if (StringUtils.isBlank(techBook.getId())) {
            String id = UUIDGenerator.generate();
            techBook.setId(id);
            if (techBook.getStatus() == null) {
                techBook.setStatus(0);
            }
            if (techBook.getTemplateFlag() == null) {
                techBook.setTemplateFlag(0);
            }
            if (techBook.getReviewStatus() == null) {
                techBook.setReviewStatus(0);
            }
            buRepairTechBookMapper.insert(techBook);
            return id;
        } else {
            BuRepairTechBook existing = buRepairTechBookMapper.selectById(techBook.getId());
            if (existing != null) {
                if (techBook.getContentHtml() == null) {
                    techBook.setContentHtml(existing.getContentHtml());
                }
                if (techBook.getTemplateFlag() == null) {
                    techBook.setTemplateFlag(existing.getTemplateFlag());
                }
                if (techBook.getReviewStatus() == null) {
                    techBook.setReviewStatus(existing.getReviewStatus());
                }
                if (techBook.getReviewerId() == null) {
                    techBook.setReviewerId(existing.getReviewerId());
                }
                if (techBook.getReviewerName() == null) {
                    techBook.setReviewerName(existing.getReviewerName());
                }
                if (techBook.getReviewComment() == null) {
                    techBook.setReviewComment(existing.getReviewComment());
                }
                if (techBook.getReviewTime() == null) {
                    techBook.setReviewTime(existing.getReviewTime());
                }
            }
            buRepairTechBookMapper.updateById(techBook);
            return techBook.getId();
        }
    }

    /**
     * @see BuRepairTechBookService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = parseIdList(ids);
        if (CollectionUtils.isEmpty(idList)) {
            throw new JeecgBootException("指导书ID不能为空");
        }

        List<BuRepairTechBook> techBooks = buRepairTechBookMapper.selectBatchIds(idList);
        if (CollectionUtils.isNotEmpty(techBooks)) {
            for (BuRepairTechBook book : techBooks) {
                if (book == null) {
                    continue;
                }
                if (Integer.valueOf(1).equals(book.getStatus())) {
                    throw new JeecgBootException("已发布状态不允许删除");
                }
                if (Integer.valueOf(9).equals(book.getStatus())) {
                    throw new JeecgBootException("已作废状态不允许删除");
                }
            }
        }

        // 检查是否关联规程
        checkReguRelation(idList);

        LambdaQueryWrapper<BuRepairTechBookDetail> detailWrapper = new LambdaQueryWrapper<BuRepairTechBookDetail>()
                .in(BuRepairTechBookDetail::getBookId, idList);
        List<BuRepairTechBookDetail> detailList = buRepairTechBookDetailMapper.selectList(detailWrapper);
        if (CollectionUtils.isNotEmpty(detailList)) {
            List<String> detailIdList = detailList.stream()
                    .map(BuRepairTechBookDetail::getId)
                    .collect(Collectors.toList());
            // 删除明细关联信息
            deleteDetailRelation(detailIdList);
            // 删除明细
            buRepairTechBookDetailMapper.delete(detailWrapper);
        }

        // 删除指导书（逻辑删除标记为作废）
        buRepairTechBookMapper.update(null,
                new UpdateWrapper<BuRepairTechBook>()
                        .set("status", 9)
                        .set("update_time", new java.util.Date())
                        .in("id", idList));

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveContent(String id, String contentHtml) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("指导书ID不能为空");
        }
        getByIdOrThrow(id);
        return buRepairTechBookMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<BuRepairTechBook>()
                        .set("content_html", contentHtml)
                        .eq("id", id)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(String id, Integer status) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("指导书ID不能为空");
        }
        if (status == null || (status != 0 && status != 1 && status != 2 && status != 3 && status != 9)) {
            throw new JeecgBootException("状态参数不合法");
        }
        BuRepairTechBook techBook = getByIdOrThrow(id);
        if (status == 1) {
            if (!Integer.valueOf(3).equals(techBook.getStatus())) {
                throw new JeecgBootException("指导书需先审批通过后才能发布");
            }
            if (StringUtils.isBlank(techBook.getContentHtml())) {
                throw new JeecgBootException("指导书正文不能为空，无法发布");
            }
        }
        if (status == 2 && !Integer.valueOf(2).equals(techBook.getReviewStatus())) {
            throw new JeecgBootException("审阅通过后才能提交审批");
        }
        if (status == 3 && !Integer.valueOf(2).equals(techBook.getStatus())) {
            throw new JeecgBootException("仅审批中状态可更新为审批通过");
        }
        boolean updated = buRepairTechBookMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<BuRepairTechBook>()
                        .set("status", status)
                        .set("update_time", new java.util.Date())
                        .eq("id", id)) > 0;

        if (updated && status == 1) {
            // 新版发布后，将同线路、同修程、同文件编号的其他已发布版本自动作废
            buRepairTechBookMapper.update(null,
                    new UpdateWrapper<BuRepairTechBook>()
                            .set("status", 9)
                            .set("update_time", new java.util.Date())
                            .eq("template_flag", 0)
                            .eq("line_id", techBook.getLineId())
                            .eq("repair_program_id", techBook.getRepairProgramId())
                            .eq("file_no", techBook.getFileNo())
                            .eq("status", 1)
                            .ne("id", id));
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String cloneAsTemplate(String sourceId) throws Exception {
        BuRepairTechBook source = getByIdOrThrow(sourceId);
        String newId = UUIDGenerator.generate();
        BuRepairTechBook template = new BuRepairTechBook();
        template.setId(newId);
        template.setFileNo(source.getFileNo() + "-T");
        template.setFileName(source.getFileName());
        template.setFileVer(source.getFileVer());
        template.setLineId(source.getLineId());
        template.setRepairProgramId(source.getRepairProgramId());
        template.setExeTime(source.getExeTime());
        template.setStatus(0);
        template.setTemplateFlag(1);
        template.setReviewStatus(0);
        template.setReviewerId(null);
        template.setReviewerName(null);
        template.setReviewComment(null);
        template.setReviewTime(null);
        template.setCompanyId(source.getCompanyId());
        template.setWorkshopId(source.getWorkshopId());
        template.setContentHtml(source.getContentHtml());
        buRepairTechBookMapper.insert(template);

        // 复制明细及物料/工器具
        LambdaQueryWrapper<BuRepairTechBookDetail> detailWrapper = new LambdaQueryWrapper<BuRepairTechBookDetail>()
                .eq(BuRepairTechBookDetail::getBookId, sourceId);
        List<BuRepairTechBookDetail> detailList = buRepairTechBookDetailMapper.selectList(detailWrapper);
        if (CollectionUtils.isNotEmpty(detailList)) {
            List<BuRepairTechBookDetail> newDetails = new ArrayList<>();
            for (BuRepairTechBookDetail detail : detailList) {
                String newDetailId = UUIDGenerator.generate();
                BuRepairTechBookDetail nd = new BuRepairTechBookDetail();
                nd.setId(newDetailId);
                nd.setBookId(newId);
                nd.setStepNum(detail.getStepNum());
                nd.setStepTitle(detail.getStepTitle());
                nd.setStepContent(detail.getStepContent());
                newDetails.add(nd);

                // 复制物料
                LambdaQueryWrapper<BuRepairTechBookDetailMaterial> matWrapper = new LambdaQueryWrapper<BuRepairTechBookDetailMaterial>()
                        .eq(BuRepairTechBookDetailMaterial::getBookDetailId, detail.getId());
                List<BuRepairTechBookDetailMaterial> mats = buRepairTechBookDetailMaterialMapper.selectList(matWrapper);
                for (BuRepairTechBookDetailMaterial mat : mats) {
                    BuRepairTechBookDetailMaterial nm = new BuRepairTechBookDetailMaterial();
                    nm.setId(UUIDGenerator.generate());
                    nm.setBookDetailId(newDetailId);
                    nm.setMaterialTypeId(mat.getMaterialTypeId());
                    nm.setAmount(mat.getAmount());
                    buRepairTechBookDetailMaterialMapper.insert(nm);
                }

                // 复制工器具
                LambdaQueryWrapper<BuRepairTechBookDetailTools> toolWrapper = new LambdaQueryWrapper<BuRepairTechBookDetailTools>()
                        .eq(BuRepairTechBookDetailTools::getBookDetailId, detail.getId());
                List<BuRepairTechBookDetailTools> tools = buRepairTechBookDetailToolsMapper.selectList(toolWrapper);
                for (BuRepairTechBookDetailTools tool : tools) {
                    BuRepairTechBookDetailTools nt = new BuRepairTechBookDetailTools();
                    nt.setId(UUIDGenerator.generate());
                    nt.setBookDetailId(newDetailId);
                    nt.setToolTypeId(tool.getToolTypeId());
                    nt.setAmount(tool.getAmount());
                    buRepairTechBookDetailToolsMapper.insert(nt);
                }
            }
            for (BuRepairTechBookDetail nd : newDetails) {
                buRepairTechBookDetailMapper.insert(nd);
            }
        }
        return newId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitReview(String id, String reviewerId, String reviewerName) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("指导书ID不能为空");
        }
        if (StringUtils.isBlank(reviewerId)) {
            throw new JeecgBootException("审阅人不能为空");
        }
        BuRepairTechBook techBook = getByIdOrThrow(id);
        if (!Integer.valueOf(0).equals(techBook.getStatus())) {
            throw new JeecgBootException("仅草稿状态可提交审阅");
        }
        if (Integer.valueOf(1).equals(techBook.getStatus())) {
            throw new JeecgBootException("已发布的指导书无需提交审阅");
        }
        if (Integer.valueOf(1).equals(techBook.getReviewStatus())) {
            throw new JeecgBootException("该指导书已在待审状态，请勿重复提交");
        }
        return buRepairTechBookMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<BuRepairTechBook>()
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
    public boolean reviewDecision(String id, Integer reviewStatus, String reviewComment) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("指导书ID不能为空");
        }
        if (reviewStatus == null || (reviewStatus != 2 && reviewStatus != 3)) {
            throw new JeecgBootException("审阅状态不合法");
        }
        if (StringUtils.isBlank(reviewComment)) {
            throw new JeecgBootException("审阅意见不能为空");
        }
        BuRepairTechBook techBook = getByIdOrThrow(id);
        if (!Integer.valueOf(1).equals(techBook.getReviewStatus())) {
            throw new JeecgBootException("仅待审状态可执行审阅结论");
        }
        return buRepairTechBookMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<BuRepairTechBook>()
                        .set("status", 0)
                        .set("review_status", reviewStatus)
                        .set("review_comment", reviewComment)
                        .set("review_time", new java.util.Date())
                        .set("update_time", new java.util.Date())
                        .eq("id", id)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitApprove(String id, String approverId, String approverName) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("指导书ID不能为空");
        }
        if (StringUtils.isBlank(approverId)) {
            throw new JeecgBootException("审批人不能为空");
        }
        BuRepairTechBook techBook = getByIdOrThrow(id);
        if (!Integer.valueOf(0).equals(techBook.getStatus())) {
            throw new JeecgBootException("仅草稿状态可提交审批");
        }
        if (!Integer.valueOf(2).equals(techBook.getReviewStatus())) {
            throw new JeecgBootException("仅审阅通过状态可提交审批");
        }
        if (Integer.valueOf(1).equals(techBook.getStatus())) {
            throw new JeecgBootException("已发布指导书不可再次提交审批");
        }
        return buRepairTechBookMapper.update(null,
                new UpdateWrapper<BuRepairTechBook>()
                        .set("status", 2)
                        .set("reviewer_id", approverId)
                        .set("reviewer_name", approverName)
                        .set("review_comment", null)
                        .set("update_time", new java.util.Date())
                        .eq("id", id)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveDecision(String id, Integer approveStatus, String approveComment) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("指导书ID不能为空");
        }
        if (approveStatus == null || (approveStatus != 1 && approveStatus != 2)) {
            throw new JeecgBootException("审批状态不合法");
        }
        if (approveStatus == 2 && StringUtils.isBlank(approveComment)) {
            throw new JeecgBootException("审批退回时需填写原因");
        }
        BuRepairTechBook techBook = getByIdOrThrow(id);
        if (!Integer.valueOf(2).equals(techBook.getStatus())) {
            throw new JeecgBootException("仅审批中状态可执行审批结论");
        }
        UpdateWrapper<BuRepairTechBook> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .set("update_time", new java.util.Date());
        if (approveStatus == 1) {
            wrapper.set("status", 3);
        } else {
            wrapper.set("status", 0)
                    .set("review_status", 0);
        }
        if (StringUtils.isNotBlank(approveComment)) {
            wrapper.set("review_comment", approveComment)
                    .set("review_time", new java.util.Date());
        }
        return buRepairTechBookMapper.update(null, wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String cloneAsDraft(String sourceId, BuRepairTechBook draft) throws Exception {
        if (draft == null) {
            throw new JeecgBootException("复用参数不能为空");
        }
        String newId = cloneAsTemplate(sourceId);
        BuRepairTechBook cloned = getByIdOrThrow(newId);
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
        if (StringUtils.isNotBlank(draft.getRepairProgramId())) {
            cloned.setRepairProgramId(draft.getRepairProgramId());
        }
        if (draft.getExeTime() != null) {
            cloned.setExeTime(draft.getExeTime());
        }
        cloned.setTemplateFlag(0);
        cloned.setStatus(0);
        cloned.setReviewStatus(0);
        cloned.setReviewerId(null);
        cloned.setReviewerName(null);
        cloned.setReviewComment(null);
        cloned.setReviewTime(null);
        buRepairTechBookMapper.updateById(cloned);
        return newId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String reviseWithNewVersion(String id, String newVersion) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new JeecgBootException("指导书ID不能为空");
        }
        if (StringUtils.isBlank(newVersion)) {
            throw new JeecgBootException("新版本号不能为空");
        }
        BuRepairTechBook source = getByIdOrThrow(id);
        String newId = cloneAsTemplate(id);
        BuRepairTechBook revised = getByIdOrThrow(newId);
        revised.setTemplateFlag(0);
        revised.setFileVer(newVersion.trim());
        revised.setStatus(0);
        revised.setReviewStatus(0);
        revised.setReviewerId(null);
        revised.setReviewerName(null);
        revised.setReviewComment(null);
        revised.setReviewTime(null);
        buRepairTechBookMapper.updateById(revised);
        return newId;
    }


    private void checkReguRelation(List<String> idList) {
        LambdaQueryWrapper<BuRepairReguTechBookDetail> reguTechBookQueryWrapper = new LambdaQueryWrapper<BuRepairReguTechBookDetail>()
                .in(BuRepairReguTechBookDetail::getTechBookId, idList);
        Integer reguRelationCount = buRepairReguTechBookDetailMapper.selectCount(reguTechBookQueryWrapper);
        if (reguRelationCount > 0) {
            throw new JeecgBootException("作业指导书已关联规程，不能删除");
        }
    }

    private void deleteDetailRelation(List<String> detailIdList) {
        if (CollectionUtils.isEmpty(detailIdList)) {
            return;
        }

        LambdaQueryWrapper<BuRepairTechBookDetailMaterial> detailMaterialWrapper = new LambdaQueryWrapper<BuRepairTechBookDetailMaterial>()
                .in(BuRepairTechBookDetailMaterial::getBookDetailId, detailIdList);
        buRepairTechBookDetailMaterialMapper.delete(detailMaterialWrapper);
        LambdaQueryWrapper<BuRepairTechBookDetailTools> detailToolsWrapper = new LambdaQueryWrapper<BuRepairTechBookDetailTools>()
                .in(BuRepairTechBookDetailTools::getBookDetailId, detailIdList);
        buRepairTechBookDetailToolsMapper.delete(detailToolsWrapper);
    }

    private BuRepairTechBook getByIdOrThrow(String id) {
        BuRepairTechBook techBook = buRepairTechBookMapper.selectById(id);
        if (techBook == null) {
            throw new JeecgBootException("指导书不存在");
        }
        return techBook;
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
