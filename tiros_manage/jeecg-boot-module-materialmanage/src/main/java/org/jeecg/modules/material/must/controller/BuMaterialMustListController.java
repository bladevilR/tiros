package org.jeecg.modules.material.must.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.must.bean.BuMaterialMustList;
import org.jeecg.modules.material.must.bean.vo.BuMaterialMustListSetGroupVO;
import org.jeecg.modules.material.must.bean.vo.BuMaterialMustQueryVO;
import org.jeecg.modules.material.must.service.BuMaterialMustListService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2021-04-30
 */
@Slf4j
@Api(tags = "必换件")
@RestController
@RequestMapping("/material/must")
public class BuMaterialMustListController {


    private final BuMaterialMustListService materialMustListService;

    public BuMaterialMustListController(BuMaterialMustListService materialMustListService) {
        this.materialMustListService = materialMustListService;
    }

    @GetMapping("/list")
    @ApiOperation("查询必换(分页)")
    @OperationLog()
    public Result<Page<BuMaterialMustList>> list(@Validated BuMaterialMustQueryVO queryVO,
                                                 @RequestParam(defaultValue = "1") Integer pageNo,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        Page<BuMaterialMustList> page = materialMustListService.pageMaterialMustList(queryVO, new Page<>(pageNo, pageSize));
        return new Result<Page<BuMaterialMustList>>().successResult(page);
    }

    @PostMapping("/add")
    @ApiOperation("新增必换件")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuMaterialMustList materialMustList) throws Exception {
        boolean flag = materialMustListService.addMaterialMustList(materialMustList);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation("编辑必换件")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuMaterialMustList materialMustList) throws Exception {
        boolean flag = materialMustListService.updateMaterialMustList(materialMustList);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除必换件（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "必换件ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = materialMustListService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/set-group")
    @ApiOperation("设置必换件清单的工班")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> setMustListGroup(@RequestBody @Validated BuMaterialMustListSetGroupVO setGroupVO) throws Exception {
        boolean flag = materialMustListService.setMustListGroup(setGroupVO);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/valid")
    @ApiOperation(value = "设置有效")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> validMustList(@RequestParam @ApiParam(value = "必换件ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = materialMustListService.validMustList(ids);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/invalid")
    @ApiOperation(value = "设置无效")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> invalidMustList(@RequestParam @ApiParam(value = "必换件ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = materialMustListService.invalidMustList(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

