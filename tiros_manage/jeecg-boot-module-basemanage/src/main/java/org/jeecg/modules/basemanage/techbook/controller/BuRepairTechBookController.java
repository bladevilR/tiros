package org.jeecg.modules.basemanage.techbook.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.basemanage.techbook.bean.BuRepairTechBook;
import org.jeecg.modules.basemanage.techbook.bean.vo.BuRepairTechBookQueryVO;
import org.jeecg.modules.basemanage.techbook.service.BuRepairTechBookService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 作业指导书(工艺) 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Api(tags = "作业指导书")
@Slf4j
@RestController
@RequestMapping("/base/tech-book")
public class BuRepairTechBookController extends JeecgController<BuRepairTechBook, BuRepairTechBookService> {

    private final BuRepairTechBookService buRepairTechBookService;

    public BuRepairTechBookController(BuRepairTechBookService buRepairTechBookService) {
        this.buRepairTechBookService = buRepairTechBookService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "指导书-查询(分页)")
    @OperationLog()
    public Result<IPage<BuRepairTechBook>> pageTechBook(@Validated BuRepairTechBookQueryVO queryVO,
                                                        @RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuRepairTechBook> page = buRepairTechBookService.pageTechBook(queryVO, pageNo, pageSize);
        return new Result<IPage<BuRepairTechBook>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "指导书-根据id查询")
    @OperationLog()
    public Result<BuRepairTechBook> getTechBook(@RequestParam @ApiParam(value = "作业指导书id", required = true) String id) throws Exception {
        BuRepairTechBook techBook = buRepairTechBookService.getTechBookById(id);
        return new Result<BuRepairTechBook>().successResult(techBook);
    }

    @PostMapping("/save")
    @ApiOperation(value = "指导书-新增/修改", notes = "作业指导书基本信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<String> save(@RequestBody @Validated BuRepairTechBook BuRepairTechBook) throws Exception {
        String id = buRepairTechBookService.saveTechBook(BuRepairTechBook);
        return new Result<String>().successResult(id);
    }

    @PostMapping("/saveByReuse")
    @ApiOperation(value = "指导书-复用新建", notes = "基于既有指导书复制并生成新草稿")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<String> saveByReuse(@RequestParam @ApiParam(value = "源指导书id", required = true) String sourceId,
                                      @RequestBody @Validated BuRepairTechBook draft) throws Exception {
        String id = buRepairTechBookService.cloneAsDraft(sourceId, draft);
        return new Result<String>().successResult(id);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "指导书-删除(批量)", notes = "包括明细、物料及工器具")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "作业指导书ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairTechBookService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/content/save")
    @ApiOperation(value = "指导书-保存正文(HTML)")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7, saveParam = false)
    public Result<Boolean> saveContent(@RequestParam @ApiParam(value = "作业指导书id", required = true) String id,
                                       @RequestBody java.util.Map<String, String> body) throws Exception {
        String contentHtml = body.getOrDefault("contentHtml", "");
        boolean flag = buRepairTechBookService.saveContent(id, contentHtml);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/status")
    @ApiOperation(value = "指导书-更新状态", notes = "status: 0-草稿, 1-发布, 2-审批中, 3-审批通过, 9-作废")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> updateStatus(@RequestParam @ApiParam(value = "作业指导书id", required = true) String id,
                                        @RequestParam @ApiParam(value = "状态", required = true) Integer status) throws Exception {
        boolean flag = buRepairTechBookService.updateStatus(id, status);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/saveTemplate")
    @ApiOperation(value = "指导书-另存为模板", notes = "复制当前指导书及明细")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<String> saveAsTemplate(@RequestParam @ApiParam(value = "作业指导书id", required = true) String id) throws Exception {
        String newId = buRepairTechBookService.cloneAsTemplate(id);
        return new Result<String>().successResult(newId);
    }

    @PostMapping("/review/submit")
    @ApiOperation(value = "指导书-提交审阅")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> submitReview(@RequestParam @ApiParam(value = "作业指导书id", required = true) String id,
                                        @RequestParam @ApiParam(value = "审阅人ID", required = true) String reviewerId,
                                        @RequestParam @ApiParam(value = "审阅人姓名", required = true) String reviewerName) throws Exception {
        boolean flag = buRepairTechBookService.submitReview(id, reviewerId, reviewerName);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/review/decision")
    @ApiOperation(value = "指导书-审阅结论", notes = "reviewStatus: 2-通过 3-驳回")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> reviewDecision(@RequestParam @ApiParam(value = "作业指导书id", required = true) String id,
                                          @RequestParam @ApiParam(value = "审阅状态", required = true) Integer reviewStatus,
                                          @RequestParam @ApiParam(value = "审阅意见", required = true) String reviewComment) throws Exception {
        boolean flag = buRepairTechBookService.reviewDecision(id, reviewStatus, reviewComment);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/approve/submit")
    @ApiOperation(value = "指导书-提交审批")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> submitApprove(@RequestParam @ApiParam(value = "作业指导书id", required = true) String id,
                                         @RequestParam @ApiParam(value = "审批人ID", required = true) String approverId,
                                         @RequestParam @ApiParam(value = "审批人姓名", required = true) String approverName) throws Exception {
        boolean flag = buRepairTechBookService.submitApprove(id, approverId, approverName);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/approve/decision")
    @ApiOperation(value = "指导书-审批结论", notes = "approveStatus: 1-通过 2-退回")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> approveDecision(@RequestParam @ApiParam(value = "作业指导书id", required = true) String id,
                                           @RequestParam @ApiParam(value = "审批结论", required = true) Integer approveStatus,
                                           @RequestParam(required = false) @ApiParam(value = "审批意见") String approveComment) throws Exception {
        boolean flag = buRepairTechBookService.approveDecision(id, approveStatus, approveComment);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/revise")
    @ApiOperation(value = "指导书-修订升版", notes = "复制当前指导书并生成新版本草稿")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<String> revise(@RequestParam @ApiParam(value = "作业指导书id", required = true) String id,
                                 @RequestParam @ApiParam(value = "新版本号", required = true) String newVersion) throws Exception {
        String newId = buRepairTechBookService.reviseWithNewVersion(id, newVersion);
        return new Result<String>().successResult(newId);
    }

    @RequestMapping("/exportXls")
    @ApiOperation(value = "指导书目录-导出")
    public ModelAndView exportXls(HttpServletRequest request, BuRepairTechBook record) {
        return super.exportXls(request, record, BuRepairTechBook.class, "作业指导书目录");
    }

}
