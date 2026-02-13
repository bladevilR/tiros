package org.jeecg.modules.basemanage.productionnotice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.basemanage.productionnotice.entity.BuProductionNotice;
import org.jeecg.modules.basemanage.productionnotice.entity.vo.BuProductionNoticeFormProgressVO;
import org.jeecg.modules.basemanage.productionnotice.entity.vo.BuProductionNoticeProgressDetailVO;
import org.jeecg.modules.basemanage.productionnotice.entity.vo.BuProductionNoticeQueryVO;
import org.jeecg.modules.basemanage.productionnotice.entity.vo.BuProductionNoticeRelationPayloadVO;
import org.jeecg.modules.basemanage.productionnotice.service.IBuProductionNoticeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "生产通知单管理")
@Slf4j
@RestController
@RequestMapping("/base/production-notice")
public class BuProductionNoticeController extends JeecgController<BuProductionNotice, IBuProductionNoticeService> {

    @Resource
    private IBuProductionNoticeService productionNoticeService;

    @GetMapping("/page")
    @ApiOperation(value = "生产通知单-分页查询")
    @OperationLog()
    public Result<IPage<BuProductionNotice>> queryPage(@Validated BuProductionNoticeQueryVO queryVO,
                                                        @RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BuProductionNotice> page = productionNoticeService.queryPage(queryVO, pageNo, pageSize);
        return new Result<IPage<BuProductionNotice>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "生产通知单-根据id查询")
    @OperationLog()
    public Result<BuProductionNotice> get(@RequestParam @ApiParam(value = "通知单id", required = true) String id) {
        BuProductionNotice notice = productionNoticeService.getById(id);
        return new Result<BuProductionNotice>().successResult(notice);
    }

    @PostMapping("/save")
    @ApiOperation(value = "生产通知单-新增")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody @Validated BuProductionNotice notice) {
        boolean flag = productionNoticeService.saveNotice(notice);
        return new Result<Boolean>().successResult(flag);
    }

    @PutMapping("/update")
    @ApiOperation(value = "生产通知单-修改")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> update(@RequestBody @Validated BuProductionNotice notice) {
        boolean flag = productionNoticeService.updateNotice(notice);
        return new Result<Boolean>().successResult(flag);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "生产通知单-批量删除")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "通知单ids，多个逗号分隔", required = true) String ids) {
        boolean flag = productionNoticeService.deleteNotice(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/submit")
    @ApiOperation(value = "生产通知单-提交审核")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> submit(@RequestParam @ApiParam(value = "通知单id", required = true) String id) {
        boolean flag = productionNoticeService.submitNotice(id);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/publish")
    @ApiOperation(value = "生产通知单-发布")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> publish(@RequestParam @ApiParam(value = "通知单id", required = true) String id) {
        boolean flag = productionNoticeService.publishNotice(id);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/close")
    @ApiOperation(value = "生产通知单-关闭")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> close(@RequestParam @ApiParam(value = "通知单id", required = true) String id) {
        boolean flag = productionNoticeService.closeNotice(id);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/pending-technical")
    @ApiOperation(value = "生产通知单-待派工技术通知单")
    @OperationLog()
    public Result<List<BuProductionNotice>> listPendingTechnical(@RequestParam(required = false) String lineId,
                                                                  @RequestParam(required = false) String trainNo) {
        List<BuProductionNotice> list = productionNoticeService.listPendingTechnicalNotices(lineId, trainNo);
        return new Result<List<BuProductionNotice>>().successResult(list);
    }

    @GetMapping("/progress-detail")
    @ApiOperation(value = "生产通知单-进度明细")
    @OperationLog()
    public Result<List<BuProductionNoticeProgressDetailVO>> progressDetail(@RequestParam @ApiParam(value = "通知单id", required = true) String id) {
        List<BuProductionNoticeProgressDetailVO> details = productionNoticeService.listProgressDetails(id);
        return new Result<List<BuProductionNoticeProgressDetailVO>>().successResult(details);
    }

    @GetMapping("/form-progress")
    @ApiOperation(value = "生产通知单-作业记录表填报明细")
    @OperationLog()
    public Result<List<BuProductionNoticeFormProgressVO>> formProgress(@RequestParam @ApiParam(value = "通知单id", required = true) String id) {
        List<BuProductionNoticeFormProgressVO> details = productionNoticeService.listFormProgress(id);
        return new Result<List<BuProductionNoticeFormProgressVO>>().successResult(details);
    }

    @GetMapping("/relation-payload")
    @ApiOperation(value = "生产通知单-关联作业记录表与附件")
    @OperationLog()
    public Result<BuProductionNoticeRelationPayloadVO> relationPayload(@RequestParam @ApiParam(value = "通知单id", required = true) String id) {
        BuProductionNoticeRelationPayloadVO payload = productionNoticeService.getNoticeRelationPayload(id);
        return new Result<BuProductionNoticeRelationPayloadVO>().successResult(payload);
    }

    @RequestMapping("/exportXls")
    @ApiOperation(value = "生产通知单-导出")
    public ModelAndView exportXls(HttpServletRequest request, BuProductionNotice notice) {
        return super.exportXls(request, notice, BuProductionNotice.class, "生产通知单");
    }

    @PostMapping("/importExcel")
    @ApiOperation(value = "生产通知单-导入")
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BuProductionNotice.class);
    }
}
