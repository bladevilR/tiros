package org.jeecg.modules.basemanage.workstation.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.workstation.entity.BuBaseRefStationForm;
import org.jeecg.modules.basemanage.workstation.entity.BuBaseWorkstation;
import org.jeecg.modules.basemanage.workstation.entity.CompanyTree;
import org.jeecg.modules.basemanage.workstation.entity.vo.BuBaseWorkstationQueryVO;
import org.jeecg.modules.basemanage.workstation.entity.vo.UnRelatedFormQueryVO;
import org.jeecg.modules.basemanage.workstation.entity.vo.WorkstationFormInfoVO;
import org.jeecg.modules.basemanage.workstation.service.BuBaseRefStationFormService;
import org.jeecg.modules.basemanage.workstation.service.IBuBaseWorkstationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 工位信息 前端控制器
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-08
 */
@Api(tags = "工位管理")
@Slf4j
@RestController
@RequestMapping("/workstation")
public class BuBaseWorkstationController {

    private final IBuBaseWorkstationService buBaseWorkstationService;
    private final BuBaseRefStationFormService buBaseRefStationFormService;

    public BuBaseWorkstationController(IBuBaseWorkstationService buBaseWorkstationService,
                                       BuBaseRefStationFormService buBaseRefStationFormService) {
        this.buBaseWorkstationService = buBaseWorkstationService;
        this.buBaseRefStationFormService = buBaseRefStationFormService;
    }


    @GetMapping(value = "/list")
    @ApiOperation(value = "查询工位信息")
    @OperationLog()
    public Result<Page<BuBaseWorkstation>> queryPageList(@Validated BuBaseWorkstationQueryVO buBaseWorkstationQueryVO,
                                                         @RequestParam(defaultValue = "1") Integer pageNo,
                                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<BuBaseWorkstation> page = buBaseWorkstationService.selectWorkstationPage(new Page<>(pageNo, pageSize), buBaseWorkstationQueryVO);
        return new Result<Page<BuBaseWorkstation>>().successResult(page);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增工位")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> save(@RequestBody @Validated BuBaseWorkstation buBaseWorkstation) throws Exception {
        boolean flag = buBaseWorkstationService.saveWorkstation(buBaseWorkstation);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改工位")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@Validated @RequestBody BuBaseWorkstation buBaseWorkstation) throws Exception {
        boolean flag = buBaseWorkstationService.updateWorkstation(buBaseWorkstation);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "批量删除工位")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "工位id，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buBaseWorkstationService.deleteWorkstationBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/workstationTree")
    @ApiOperation(value = "工位分类目录")
    @OperationLog()
    public Result<List<CompanyTree>> selectTreeForWorkstation() throws Exception {
        List<CompanyTree> companyTreeList = buBaseWorkstationService.selectTreeForWorkstation();
        return new Result<List<CompanyTree>>().successResult(companyTreeList);
    }

    @GetMapping(value = "/fromGroupId/{id}")
    @ApiOperation(value = "根据班组找工位")
    @OperationLog()
    public Result<List<BuBaseWorkstation>> selectByWorkGroupId(@PathVariable @ApiParam(value = "班组id") String id) throws Exception {
        List<BuBaseWorkstation> workstationList = buBaseWorkstationService.selectByWorkGroupId(id);
        return new Result<List<BuBaseWorkstation>>().successResult(workstationList);
    }

    @GetMapping(value = "/form/list-related")
    @ApiOperation(value = "关联表单-查询已关联(列表)")
    @OperationLog()
    public Result<List<BuBaseRefStationForm>> listRelatedFormByWorkstationId(@Validated UnRelatedFormQueryVO queryVO) throws Exception {
        List<BuBaseRefStationForm> refStationFormList = buBaseRefStationFormService.listRelatedFormByWorkstationId(queryVO);
        return new Result<List<BuBaseRefStationForm>>().successResult(refStationFormList);
    }

    @GetMapping(value = "/form/page-unrelated")
    @ApiOperation(value = "关联表单-查询未关联(分页)")
    @OperationLog()
    public Result<IPage<WorkstationFormInfoVO>> pageUnRelatedForm(@Validated UnRelatedFormQueryVO queryVO,
                                                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                                                  @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<WorkstationFormInfoVO> page = buBaseRefStationFormService.pageUnRelatedForm(queryVO, pageNo, pageSize);
        return new Result<IPage<WorkstationFormInfoVO>>().successResult(page);
    }

    @PostMapping("/form/relate")
    @ApiOperation(value = "关联表单-关联表单到工位")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> correlationForm(@RequestBody @Validated List<BuBaseRefStationForm> refStationFormList) throws Exception {
        boolean flag = buBaseRefStationFormService.saveBatch(refStationFormList);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/form/cancel-relate")
    @ApiOperation(value = "关联表单-取消关联")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> correlationFormDelete(@RequestParam @ApiParam(value = "工位表单关联信息表id,多个逗号分割", required = true) String ids) {
        boolean flag = buBaseRefStationFormService.removeByIds(CollUtil.toList(ids.split(",")));
        return new Result<Boolean>().successResult(flag);
    }

}
