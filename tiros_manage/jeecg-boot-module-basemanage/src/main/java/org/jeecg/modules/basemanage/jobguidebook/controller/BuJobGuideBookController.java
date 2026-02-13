package org.jeecg.modules.basemanage.jobguidebook.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.basemanage.jobguidebook.bean.BuJobGuideBook;
import org.jeecg.modules.basemanage.jobguidebook.bean.vo.BuJobGuideBookQueryVO;
import org.jeecg.modules.basemanage.jobguidebook.service.BuJobGuideBookService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Api(tags = "作业指导书")
@Slf4j
@RestController
@RequestMapping("/base/job-guide-book")
public class BuJobGuideBookController extends JeecgController<BuJobGuideBook, BuJobGuideBookService> {

    private final BuJobGuideBookService buJobGuideBookService;

    public BuJobGuideBookController(BuJobGuideBookService buJobGuideBookService) {
        this.buJobGuideBookService = buJobGuideBookService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "作业指导书-查询(分页)")
    @OperationLog()
    public Result<IPage<BuJobGuideBook>> page(@Validated BuJobGuideBookQueryVO queryVO,
                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuJobGuideBook> page = buJobGuideBookService.pageJobGuideBook(queryVO, pageNo, pageSize);
        return new Result<IPage<BuJobGuideBook>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "作业指导书-根据id查询")
    @OperationLog()
    public Result<BuJobGuideBook> get(@RequestParam @ApiParam(value = "作业指导书id", required = true) String id) throws Exception {
        BuJobGuideBook book = buJobGuideBookService.getJobGuideBookById(id);
        return new Result<BuJobGuideBook>().successResult(book);
    }

    @PostMapping("/save")
    @ApiOperation(value = "作业指导书-新增/修改")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<String> save(@RequestBody @Validated BuJobGuideBook book) throws Exception {
        String id = buJobGuideBookService.saveJobGuideBook(book);
        return new Result<String>().successResult(id);
    }

    @PostMapping("/saveByReuse")
    @ApiOperation(value = "作业指导书-复用新建")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<String> saveByReuse(@RequestParam @ApiParam(value = "源指导书id", required = true) String sourceId,
                                      @RequestBody @Validated BuJobGuideBook draft) throws Exception {
        String id = buJobGuideBookService.cloneAsDraft(sourceId, draft);
        return new Result<String>().successResult(id);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "作业指导书-删除(批量)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buJobGuideBookService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/content/save")
    @ApiOperation(value = "作业指导书-保存正文(HTML)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7, saveParam = false)
    public Result<Boolean> saveContent(@RequestParam @ApiParam(value = "作业指导书id", required = true) String id,
                                       @RequestBody java.util.Map<String, String> body) throws Exception {
        String contentHtml = body.getOrDefault("contentHtml", "");
        boolean flag = buJobGuideBookService.saveContent(id, contentHtml);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/status")
    @ApiOperation(value = "作业指导书-更新状态")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> updateStatus(@RequestParam @ApiParam(value = "作业指导书id", required = true) String id,
                                        @RequestParam @ApiParam(value = "状态", required = true) Integer status) throws Exception {
        boolean flag = buJobGuideBookService.updateStatus(id, status);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/saveTemplate")
    @ApiOperation(value = "作业指导书-另存为模板")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<String> saveAsTemplate(@RequestParam @ApiParam(value = "作业指导书id", required = true) String id) throws Exception {
        String newId = buJobGuideBookService.cloneAsTemplate(id);
        return new Result<String>().successResult(newId);
    }

    @PostMapping("/review/submit")
    @ApiOperation(value = "作业指导书-提交审核")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> submitReview(@RequestParam(required = false) @ApiParam(value = "作业指导书id") String id,
                                        @RequestParam(required = false) @ApiParam(value = "作业指导书ids，多个逗号分隔") String ids,
                                        @RequestParam @ApiParam(value = "审核人ID", required = true) String reviewerId,
                                        @RequestParam @ApiParam(value = "审核人姓名", required = true) String reviewerName) throws Exception {
        boolean flag = buJobGuideBookService.submitReviewBatch(resolveTargetIds(id, ids), reviewerId, reviewerName);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/review/decision")
    @ApiOperation(value = "作业指导书-审核结论")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> reviewDecision(@RequestParam(required = false) @ApiParam(value = "作业指导书id") String id,
                                          @RequestParam(required = false) @ApiParam(value = "作业指导书ids，多个逗号分隔") String ids,
                                          @RequestParam @ApiParam(value = "审核状态 2-通过 3-驳回", required = true) Integer reviewStatus,
                                          @RequestParam @ApiParam(value = "审核意见", required = true) String reviewComment) throws Exception {
        boolean flag = buJobGuideBookService.reviewDecisionBatch(resolveTargetIds(id, ids), reviewStatus, reviewComment);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/approve/submit")
    @ApiOperation(value = "作业指导书-提交审批")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> submitApprove(@RequestParam(required = false) @ApiParam(value = "作业指导书id") String id,
                                         @RequestParam(required = false) @ApiParam(value = "作业指导书ids，多个逗号分隔") String ids,
                                         @RequestParam @ApiParam(value = "审批人ID", required = true) String approverId,
                                         @RequestParam @ApiParam(value = "审批人姓名", required = true) String approverName) throws Exception {
        boolean flag = buJobGuideBookService.submitApproveBatch(resolveTargetIds(id, ids), approverId, approverName);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/approve/decision")
    @ApiOperation(value = "作业指导书-审批结论")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> approveDecision(@RequestParam(required = false) @ApiParam(value = "作业指导书id") String id,
                                           @RequestParam(required = false) @ApiParam(value = "作业指导书ids，多个逗号分隔") String ids,
                                           @RequestParam @ApiParam(value = "审批结论 1-通过 2-退回", required = true) Integer approveStatus,
                                           @RequestParam(required = false) @ApiParam(value = "审批意见") String approveComment) throws Exception {
        boolean flag = buJobGuideBookService.approveDecisionBatch(resolveTargetIds(id, ids), approveStatus, approveComment);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/revise")
    @ApiOperation(value = "作业指导书-修订升版")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<String> revise(@RequestParam @ApiParam(value = "作业指导书id", required = true) String id,
                                 @RequestParam @ApiParam(value = "新版本号", required = true) String newVersion) throws Exception {
        String newId = buJobGuideBookService.reviseWithNewVersion(id, newVersion);
        return new Result<String>().successResult(newId);
    }

    @GetMapping("/export/pdf")
    @ApiOperation(value = "作业指导书-服务端导出PDF（单个PDF或批量ZIP）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6, saveParam = false)
    public void exportPdf(@RequestParam @ApiParam(value = "ids，多个逗号分隔", required = true) String ids,
                          HttpServletResponse response) throws Exception {
        List<BuJobGuideBook> books = buJobGuideBookService.listForExport(ids);
        if (books.isEmpty()) {
            throw new JeecgBootException("未找到可导出的作业指导书");
        }
        if (books.size() == 1) {
            BuJobGuideBook book = books.get(0);
            byte[] pdf = renderBookPdf(book);
            String fileName = safeFileName(defaultFileName(book)) + ".pdf";
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment;filename=" + encodeFileName(fileName));
            response.setContentLength(pdf.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(pdf);
            outputStream.flush();
            return;
        }
        String zipName = "作业指导书批量导出_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".zip";
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodeFileName(zipName));
        ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
        int index = 1;
        for (BuJobGuideBook book : books) {
            byte[] pdf = renderBookPdf(book);
            String baseName = safeFileName(defaultFileName(book));
            if (StringUtils.isBlank(baseName)) {
                baseName = "作业指导书";
            }
            String entryName = String.format("%02d_%s.pdf", index, baseName);
            zipOutputStream.putNextEntry(new ZipEntry(entryName));
            zipOutputStream.write(pdf);
            zipOutputStream.closeEntry();
            index++;
        }
        zipOutputStream.finish();
        zipOutputStream.flush();
        zipOutputStream.close();
    }

    @RequestMapping("/exportXls")
    @ApiOperation(value = "作业指导书目录-导出")
    public ModelAndView exportXls(HttpServletRequest request, BuJobGuideBook record) {
        return super.exportXls(request, record, BuJobGuideBook.class, "作业指导书目录");
    }

    private byte[] renderBookPdf(BuJobGuideBook book) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(buildPdfHtml(book), null);
        builder.toStream(outputStream);
        builder.run();
        return outputStream.toByteArray();
    }

    private String buildPdfHtml(BuJobGuideBook book) {
        String exeDate = "";
        if (book.getExeTime() != null) {
            exeDate = new SimpleDateFormat("yyyy-MM-dd").format(book.getExeTime());
        }
        String updateTime = "";
        if (book.getUpdateTime() != null) {
            updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(book.getUpdateTime());
        } else if (book.getCreateTime() != null) {
            updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(book.getCreateTime());
        }
        String contentHtml = StringUtils.defaultIfBlank(book.getContentHtml(), "<p>暂无正文</p>");
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'/>" +
                "<style>" +
                "@page { size: A4 landscape; margin: 12mm 12mm 16mm 12mm; @bottom-center { content: '第 ' counter(page) ' / ' counter(pages) ' 页'; font-size: 10px; color: #666; } }" +
                "body { font-family: SimSun, \"Microsoft YaHei\", sans-serif; font-size: 12px; color: #222; line-height: 1.6; }" +
                ".title { text-align: center; font-size: 22px; font-weight: bold; margin-bottom: 14px; }" +
                ".meta { width: 100%; border-collapse: collapse; margin-bottom: 12px; }" +
                ".meta td { border: 1px solid #666; padding: 4px 6px; font-size: 11px; }" +
                ".content img { max-width: 100%; }" +
                "</style></head><body>" +
                "<div class='title'>" + escapeHtml(StringUtils.defaultIfBlank(book.getFileName(), "作业指导书")) + "</div>" +
                "<table class='meta'>" +
                "<tr><td style='width:14%'>文件编号</td><td style='width:36%'>" + escapeHtml(book.getFileNo()) + "</td>" +
                "<td style='width:14%'>版本号</td><td style='width:36%'>" + escapeHtml(book.getFileVer()) + "</td></tr>" +
                "<tr><td>线路</td><td>" + escapeHtml(book.getLineName()) + "</td><td>车型</td><td>" + escapeHtml(book.getTrainTypeName()) + "</td></tr>" +
                "<tr><td>修程</td><td>" + escapeHtml(book.getRepairProgramName()) + "</td><td>项目</td><td>" + escapeHtml(book.getProject()) + "</td></tr>" +
                "<tr><td>编制人</td><td>" + escapeHtml(book.getCreatorName()) + "</td><td>实施日期</td><td>" + escapeHtml(exeDate) + "</td></tr>" +
                "<tr><td>最近修改</td><td colspan='3'>" + escapeHtml(updateTime) + "</td></tr>" +
                "</table>" +
                "<div class='content'>" + contentHtml + "</div>" +
                "</body></html>";
    }

    private String defaultFileName(BuJobGuideBook book) {
        String fileNo = StringUtils.defaultString(book.getFileNo());
        String fileName = StringUtils.defaultString(book.getFileName());
        String fileVer = StringUtils.defaultString(book.getFileVer());
        String merged = (fileNo + "_" + fileName + "_" + fileVer).replaceAll("^_+|_+$", "");
        return StringUtils.defaultIfBlank(merged, "作业指导书");
    }

    private String safeFileName(String fileName) {
        String val = StringUtils.defaultString(fileName);
        val = val.replaceAll("[\\\\/:*?\"<>|]", "_");
        val = val.replaceAll("\\s+", " ").trim();
        return StringUtils.defaultIfBlank(val, "作业指导书");
    }

    private String encodeFileName(String fileName) throws Exception {
        return URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String resolveTargetIds(String id, String ids) {
        if (StringUtils.isNotBlank(ids)) {
            return ids;
        }
        return id;
    }
}
